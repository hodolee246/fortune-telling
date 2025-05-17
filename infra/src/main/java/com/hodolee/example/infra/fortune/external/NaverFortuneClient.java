package com.hodolee.example.infra.fortune.external;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class NaverFortuneClient {

    @Value("${naver.fortune.apiUri}")
    private String apiUri;

    public String getFortune(String birthDate) {
        try {
            String query = birthDate + " 운세";
            String url = apiUri + query;
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .get();
            // TODO 구조 맞게 잘라야함
            String fortune = doc.selectFirst(".detail").text();

            return fortune;
        } catch (IOException e) {
            throw new RuntimeException("naver api error");
        }
    }

}
