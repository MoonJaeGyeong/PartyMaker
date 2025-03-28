package com.partymaker.party;

import com.partymaker.party.dto.CharacterInfo;
import com.partymaker.party.dto.GetResponse;
import com.partymaker.party.dto.response.GetCharacterResponse;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.List;
import java.util.UUID;


@Service
@Slf4j
public class PartyService {

    @Value("${DNF.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();


    public Object craeteParty(List<Character> request) {
        String apiUrl = getUrl("애기븝미", "diregie");

        // HTTP GET 요청 보내기
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(apiUrl, String.class);

        // 응답 값
        String responseBody = responseEntity.getBody();
        System.out.println("GET Response: " + responseBody);

        return "";
    }

    public GetCharacterResponse getCharacter(String serverId, String characterName){
        String apiUrl = getUrl(characterName, serverId);

        // HTTP GET 요청 보내기
        GetResponse response = restTemplate.getForObject(apiUrl, GetResponse.class);

        if(response == null){
            return null;
        }
        CharacterInfo info = response.rows().get(0);

        long power = getPower(info.characterId(), serverId, info.jobGrowName());

        return GetCharacterResponse.of(response, power);
    }

    private String getUrl(String characterName, String serverId) {
        String url = "https://api.neople.co.kr/df/servers/"+ serverId +"/characters?characterName=" + characterName +"&apikey=" + apiKey;
        return url;
    }

    private long getPower(String characterId, String serverId, String jobName) {
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

        return Long.parseLong(power.replaceAll(",", ""));
    }

    private String crawl(WebDriver driver, String url, String jobName){
        String power = "0";

        try {
            // 웹 페이지 접속
            driver.get(url);

            // WebDriverWait을 사용해 요소가 나타날 때까지 기다리기
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(1));

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
