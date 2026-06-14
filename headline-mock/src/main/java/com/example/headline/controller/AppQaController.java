package com.example.headline.controller;

import com.example.headline.service.AppQaService;
import com.example.headline.validator.AppQaValidator;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/app/qa")
public class AppQaController {

    private static final MediaType JSON_UTF8 =
            MediaType.parseMediaType("application/json;charset=UTF-8");

    private static final MediaType SSE_UTF8 =
            MediaType.parseMediaType("text/event-stream;charset=UTF-8");

    private final AppQaService appQaService;
    private final AppQaValidator appQaValidator;

    public AppQaController(AppQaService appQaService, AppQaValidator appQaValidator) {
        this.appQaService = appQaService;
        this.appQaValidator = appQaValidator;
    }

    // 智能问答：is_stream=true 返回 SSE，is_stream=false 返回普通 JSON。
    @PostMapping("/ask")
    public ResponseEntity<?> ask(@RequestBody Map<String, Object> request) {
        ResponseEntity<Map<String, Object>> error = appQaValidator.validateAsk(request);
        if (error != null) {
            return error;
        }

        boolean isStream = request.get("is_stream") == null || Boolean.TRUE.equals(request.get("is_stream"));
        if (isStream) {
            return ResponseEntity.ok()
                    .contentType(SSE_UTF8)
                    .body(appQaService.streamAsk(request));
        }

        return ResponseEntity.ok()
                .contentType(JSON_UTF8)
                .body(appQaService.ask(request));
    }
}
