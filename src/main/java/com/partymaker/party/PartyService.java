package com.partymaker.party;

import com.partymaker.party.dto.CharacterInfo;
import com.partymaker.party.dto.request.Character;
import com.partymaker.party.dto.response.CreatePartyResponse;
import com.partymaker.party.dto.response.GetCharacterResponse;
import com.partymaker.party.dto.response.GetResponse;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
public class PartyService {

    @Value("${DNF.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();


    public List<CreatePartyResponse> createParty(List<Character> characters) {
        List<Character> buffers = characters.stream().filter(Character::isBuffer).toList();
        List<Character> dealers = characters.stream().filter(c -> !c.isBuffer()).collect(Collectors.toList());

        List<CreatePartyResponse> allValidParties = new ArrayList<>();

        for (Character buffer : buffers) {
            List<List<Character>> dealerCombos = getValidDealerCombos(dealers, buffer.ownedName());

            for (List<Character> dealerCombo : dealerCombos) {
                Set<String> owners = dealerCombo.stream().map(Character::ownedName).collect(Collectors.toSet());
                if (owners.size() < 3) continue;

                long dealerPower = dealerCombo.stream().mapToLong(Character::power).sum();
                long totalPower = dealerPower * buffer.power();

                List<Character> partyMembers = new ArrayList<>(dealerCombo);
                partyMembers.add(buffer);

                allValidParties.add(new CreatePartyResponse(partyMembers, Long.toString(totalPower)));
            }
        }

        return selectBestBalancedParties(allValidParties);
    }

    private static List<List<Character>> getValidDealerCombos(List<Character> dealers, String bufferOwner) {
        List<List<Character>> result = new ArrayList<>();
        int n = dealers.size();
        for (int i = 0; i < n; i++) {
            Character d1 = dealers.get(i);
            if (d1.ownedName().equals(bufferOwner)) continue;
            for (int j = i + 1; j < n; j++) {
                Character d2 = dealers.get(j);
                if (d2.ownedName().equals(bufferOwner)) continue;
                for (int k = j + 1; k < n; k++) {
                    Character d3 = dealers.get(k);
                    if (d3.ownedName().equals(bufferOwner)) continue;

                    Set<String> ownerSet = Set.of(d1.ownedName(), d2.ownedName(), d3.ownedName());
                    if (ownerSet.size() < 3) continue;

                    result.add(List.of(d1, d2, d3));
                }
            }
        }
        return result;
    }

    private static List<CreatePartyResponse> selectBestBalancedParties(List<CreatePartyResponse> parties) {
        parties.sort(Comparator.comparing(p -> Long.parseLong(p.totalPower())));

        List<CreatePartyResponse> result = new ArrayList<>();
        Set<Integer> usedIndexes = new HashSet<>();

        for (int i = 0; i < parties.size(); i++) {
            if (usedIndexes.contains(i)) continue;
            CreatePartyResponse p1 = parties.get(i);

            int bestMatch = -1;
            long minDiff = Long.MAX_VALUE;

            for (int j = i + 1; j < parties.size(); j++) {
                if (usedIndexes.contains(j)) continue;
                CreatePartyResponse p2 = parties.get(j);

                long diff = Math.abs(Long.parseLong(p1.totalPower()) - Long.parseLong(p2.totalPower()));
                if (diff < minDiff) {
                    minDiff = diff;
                    bestMatch = j;
                }
            }

            if (bestMatch != -1) {
                result.add(p1);
                result.add(parties.get(bestMatch));
                usedIndexes.add(i);
                usedIndexes.add(bestMatch);
            }
        }

        return result;
    }

    public GetCharacterResponse getCharacter(String serverId, String characterName){
        String apiUrl = getUrl(characterName, serverId);

        // HTTP GET 요청 보내기
        GetResponse response = restTemplate.getForObject(apiUrl, GetResponse.class);

        if(response == null){
            return null;
        }
        CharacterInfo info = response.rows().get(0);

        BigInteger power = getPower(info.characterId(), serverId, info.jobGrowName());
        if(isBuffer(info.jobGrowName())){
            int castInt = power.divide(BigInteger.valueOf(10000)).intValue() ;
            return GetCharacterResponse.of(info, castInt);
        }

        int castInt = power.divide(BigInteger.valueOf(100000000)).intValue() ;
        return GetCharacterResponse.of(info, castInt);
    }

    private String getUrl(String characterName, String serverId) {
        String url = "https://api.neople.co.kr/df/servers/"+ serverId +"/characters?characterName=" + characterName +"&apikey=" + apiKey;
        return url;
    }

    private BigInteger getPower(String characterId, String serverId, String jobName) {
        String os = System.getProperty("os.name").toLowerCase();
        WebDriver driver;

        if (os.contains("win")) {
            System.setProperty("webdriver.chrome.driver", "driver/chromedriver.exe");
            driver = new ChromeDriver();
        } else {
            System.setProperty("webdriver.chrome.driver", "/home/ubuntu/drivers/chromedriver");
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless=new");
            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");

            options.addArguments("--user-data-dir=/tmp/chrome-profile-" + UUID.randomUUID());
            driver = new ChromeDriver(options);
        }

        String url = "https://dundam.xyz/character?server=" + serverId + "&key=" + characterId;
        String power = crawl(driver, url, jobName);

        return new BigInteger(power.replaceAll(",", ""));
    }

    private String crawl(WebDriver driver, String url, String jobName){
        String power = "0";

        try {
            // 웹 페이지 접속
            driver.get(url);

            // WebDriverWait을 사용해 요소가 나타날 때까지 기다리기
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));

            if(isBuffer(jobName)){
                WebElement dvalElement = wait.until(
                        ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#content-container > div.new-cinfo > div.c-aba-stat > div > div.abas-bottom > div.abbot-alldeal > div > div > div > div > div > span.dval"))
                );
                power = dvalElement.getText();
            }

            else{
                WebElement dvalElement = wait.until(
                        ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#content-container > div.new-cinfo > div.c-aba-stat > div > div.abas-bottom > div.abbot-alldeal > div > div > div:nth-child(8) > div > div > span.dval"))
                );
                power = dvalElement.getText();
            }

            log.info("크롤링 값: " + power);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 브라우저 종료
            driver.quit();
        }

        return power;
    }

    private boolean isBuffer(String jobName){
        return jobName.matches(".*(인챈|뮤즈|크루).*");
    }

}
