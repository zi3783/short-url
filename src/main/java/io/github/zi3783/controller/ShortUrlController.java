package io.github.zi3783.controller;

import io.github.zi3783.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shortUrl")
@Slf4j
@Tag(name = "短链接相关接口")
public class ShortUrlController {

    @PostMapping("/create")
    @Operation(description = "创建短链")
    public Result<String> createShortUrl(@RequestParam String targetUrl) {
        //TODO 未实现生成短链接
        return Result.success();
    }

    @Operation(description = "访问短链")
    @GetMapping("/{shortUrl}")
    public ResponseEntity<Void> redirect(@PathVariable String shortUrl) {
        //TODO 为实现解析短链接
        return ResponseEntity
                .status(HttpStatus.FOUND)
//                .location()
                .build();
    }
}
