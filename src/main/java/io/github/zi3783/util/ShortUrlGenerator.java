package io.github.zi3783.util;

import org.springframework.stereotype.Component;

@Component
public class ShortUrlGenerator {
    private static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = ALPHABET.length();
    private static final int SHORT_LENGTH = 6;

    public String encode(long id) {
        //转换成62进制字符
        StringBuilder sb = new StringBuilder();
        long num = id;
        do {
            sb.append(ALPHABET.charAt((int) (num % BASE)));
            num /= BASE;
        }while(num > 0);

        //补成6位
        while(sb.length() < SHORT_LENGTH) {
            sb.append('0');
        }

        return sb.reverse().toString();
    }

    public long decode(String shortUrl) {
        long result = 0;
        for(char c : shortUrl.toCharArray()) {
            int i = ALPHABET.indexOf(c);
            if( i >= 0) {
                result = result * BASE + i;
            }
        }
        return result;
    }
}
