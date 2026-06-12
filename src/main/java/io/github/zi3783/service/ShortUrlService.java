package io.github.zi3783.service;

public interface ShortUrlService {

    /**
     * 创建短链
     * @param targetUrl
     * @return
     */
    String createShortUrl(String targetUrl);
}
