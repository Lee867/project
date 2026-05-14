package com.example.headline.controller;

import com.example.headline.service.AppHeadlineService;
import com.example.headline.validator.AppHeadlineValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/app/headline")
public class AppHeadlineController {

    private final AppHeadlineService appHeadlineService;
    private final AppHeadlineValidator appHeadlineValidator;

    public AppHeadlineController(AppHeadlineService appHeadlineService, AppHeadlineValidator appHeadlineValidator) {
        this.appHeadlineService = appHeadlineService;
        this.appHeadlineValidator = appHeadlineValidator;
    }

    // 主题线索聚合：根据主题、文档类型、国家和时间范围返回 APP 首页线索列表。
    @PostMapping("/topic_clues")
    public ResponseEntity<Map<String, Object>> topicClues(@RequestBody Map<String, Object> request) {
        ResponseEntity<Map<String, Object>> error = appHeadlineValidator.validateTopicClues(request);
        if (error != null) {
            return error;
        }
        return ResponseEntity.ok(appHeadlineService.topicClues(request));
    }

    // 多维筛选线索：按 conditions 等高级检索条件返回筛选后的线索。
    @PostMapping("/filter_clues")
    public ResponseEntity<Map<String, Object>> filterClues(@RequestBody Map<String, Object> request) {
        ResponseEntity<Map<String, Object>> error = appHeadlineValidator.validateFilterClues(request);
        if (error != null) {
            return error;
        }
        return ResponseEntity.ok(appHeadlineService.filterClues(request));
    }

    // 线索详情：根据 clue_id 返回不同线索的详情和关联实体。
    @PostMapping("/clue_detail")
    public ResponseEntity<Map<String, Object>> clueDetail(@RequestBody Map<String, Object> request) {
        ResponseEntity<Map<String, Object>> error = appHeadlineValidator.validateClueDetail(request);
        if (error != null) {
            return error;
        }
        return ResponseEntity.ok(appHeadlineService.clueDetail(request));
    }
}
