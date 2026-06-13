package io.github.zi3783.service;

public interface ShortUrlService {

    /**
     * 创建短链
     * @param targetUrl
     * @return
     */
    String createShortUrl(String targetUrl);

    /**
     * 解析短链
     * @param shortUrl
     * @return
     */
    String getTargetUrl(String shortUrl);
}
