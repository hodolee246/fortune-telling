package com.hodolee.example.infra.fortune.url;

import org.hashids.Hashids;
import org.springframework.stereotype.Component;

@Component
public class UrlGenerator {

    private final Hashids hashids;

    public UrlGenerator(Hashids hashids) {
        this.hashids = hashids;
    }

    public String generateEncodeUrl(Long id) {
        String hashedId = hashids.encode(id);

        return String.format("http://localhost:8080/%s", hashedId);
    }

    public String getDecodedUrl(String encodedUrl) {
        return hashids.decodeHex(encodedUrl);
    }

}
