package com.example.headline.controller;

import com.example.headline.service.AppQaService;
import com.example.headline.validator.AppQaValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/app/qa")
public class AppQaController {

    private final AppQaService appQaService;
    private final AppQaValidator appQaValidator;

    public AppQaController(AppQaService appQaService, AppQaValidator appQaValidator) {
        this.appQaService = appQaService;
        this.appQaValidator = appQaValidator;
    }

    // 智能问答：根据 query、session_id 和可选知识来源返回模拟答案。
    @PostMapping("/ask")
    public ResponseEntity<Map<String, Object>> ask(@RequestBody Map<String, Object> request) {
        ResponseEntity<Map<String, Object>> error = appQaValidator.validateAsk(request);
        if (error != null) {
            return error;
        }
        return ResponseEntity.ok(appQaService.ask(request));
    }
}
