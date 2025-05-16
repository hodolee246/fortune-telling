package com.hodolee.example.infra.fortune.external;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class NaverFortuneClient {

    public String getFortune(String birthDate) throws IOException {
        String query = birthDate + " 운세";
        // TODO 추후 외부 주입으로 변경
        String url = "https://search.naver.com/search.naver?query=" + query;
        Document doc = Jsoup.connect(url).get();

        String fortune = doc.selectFirst(".detail").text();
        // TODO else
        return fortune;
    }

}
