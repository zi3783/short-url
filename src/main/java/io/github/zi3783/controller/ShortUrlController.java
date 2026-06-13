package io.github.zi3783.controller;

import io.github.zi3783.common.Result;
import io.github.zi3783.service.ShortUrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/shortUrl")
@Slf4j
@Tag(name = "短链接相关接口")
public class ShortUrlController {

    @Autowired
    private ShortUrlService shortUrlService;


    @PostMapping("/create")
    @Operation(summary = "创建短链")
    public Result<String> createShortUrl(@RequestParam String targetUrl) {

        String shortUrl = shortUrlService.createShortUrl(targetUrl);

        return Result.success(shortUrl);
    }

    @Operation(summary = "访问短链")
    @GetMapping("/{shortUrl}")
    public ResponseEntity<Void> redirect(@PathVariable String shortUrl) {

        String targetUrl = shortUrlService.getTargetUrl(shortUrl);

        if(targetUrl == null){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(targetUrl))
                .build();
    }
}
