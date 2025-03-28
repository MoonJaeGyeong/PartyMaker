package com.partymaker.party;

import com.partymaker.party.dto.GetResponse;
import com.partymaker.party.dto.response.GetCharacterResponse;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.Duration;
import java.util.List;


@Service
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
        long power = getPower(response.rows().get(0).characterId(), serverId);

        return GetCharacterResponse.of(response, power);
    }

    private String getUrl(String characterName, String serverId) {
        String url = "https://api.neople.co.kr/df/servers/"+ serverId +"/characters?characterName=" + characterName +"&apikey=" + apiKey;
        return url;
    }

    private long getPower(String characterId, String serverId) {

        String power = "0";

        // 1. WebDriver와 ChromeDriver 설정
        // 프로젝트 폴더 기준으로 chromedirver.exe 파일의 위치를 작성
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            System.setProperty("webdriver.chrome.driver", "driver/chromedriver.exe");
        } else {
            System.setProperty("webdriver.chrome.driver", "/home/ubuntu/drivers/chromedriver");
        }
        WebDriver driver = new ChromeDriver();

        try {
            // 웹 페이지 접속
            String url = "https://dundam.xyz/character?server=" + serverId + "&key=" + characterId;
            driver.get(url);

            // WebDriverWait을 사용해 요소가 나타날 때까지 기다리기
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
            WebElement dvalElement = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#content-container > div.new-cinfo > div.c-aba-stat > div > div.abas-bottom > div.abbot-alldeal > div > div > div > div > div > span.dval"))
            );

            // 값 가져오기
            power = dvalElement.getText();
            System.out.println("크롤링된 값: " + power);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 브라우저 종료
            driver.quit();
        }

        return Long.parseLong(power.replaceAll(",", ""));
    }

    public static String encodeURIComponent(String component) {
        String result = null;

        try {
            result = URLEncoder.encode(component, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        return result;
    }
}
