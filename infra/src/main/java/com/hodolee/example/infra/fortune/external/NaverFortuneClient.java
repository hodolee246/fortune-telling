package com.hodolee.example.infra.fortune.external;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class NaverFortuneClient {

    @Value("${naver.fortune.apiUri}")
    private String apiUri;

    public String getFortune(String birthDate) {
        String query = birthDate + " 운세";
        String requestUrl = apiUri + query;

        try {
            Document document = Jsoup.connect(requestUrl)
                    .userAgent("Mozilla/5.0")
                    .get();
            Element fortuneElement = document.selectFirst("div.source_dsc");

            if (fortuneElement == null) {
                throw new IllegalStateException("크롤링한 정보를 찾을 수 없습니다.");
            }

            return fortuneElement.text();
        } catch (IOException e) {
            throw new IllegalStateException("네이버 운세 크롤링에 실패하였습니다.");
        }
    }

}
