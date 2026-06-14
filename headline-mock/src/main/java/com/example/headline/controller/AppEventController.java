package com.example.headline.controller;

import com.example.headline.service.AppEventService;
import com.example.headline.validator.AppEventValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/app/event", produces = "application/json;charset=UTF-8")
public class AppEventController {

    private final AppEventService appEventService;
    private final AppEventValidator appEventValidator;

    public AppEventController(AppEventService appEventService, AppEventValidator appEventValidator) {
        this.appEventService = appEventService;
        this.appEventValidator = appEventValidator;
    }

    // 事件时间线：根据主题名称和时间粒度返回事件发展脉络。
    @GetMapping("/timeline")
    public ResponseEntity<Map<String, Object>> timeline(@RequestParam Map<String, Object> request) {
        ResponseEntity<Map<String, Object>> error = appEventValidator.validateTimeline(request);
        if (error != null) {
            return error;
        }
        return ResponseEntity.ok(appEventService.timeline(request));
    }

    // 事件结构化信息：根据 topic_id 或 topic_name 返回事件背景、参与方、影响范围等信息。
    @GetMapping("/structured_info")
    public ResponseEntity<Map<String, Object>> structuredInfo(@RequestParam Map<String, Object> request) {
        ResponseEntity<Map<String, Object>> error = appEventValidator.validateTopicSelector(request);
        if (error != null) {
            return error;
        }
        return ResponseEntity.ok(appEventService.structuredInfo(request));
    }

    // 事件主体关系图：返回事件相关主体节点与关系边。
    @GetMapping("/relation_graph")
    public ResponseEntity<Map<String, Object>> relationGraph(@RequestParam Map<String, Object> request) {
        ResponseEntity<Map<String, Object>> error = appEventValidator.validateTopicSelector(request);
        if (error != null) {
            return error;
        }
        return ResponseEntity.ok(appEventService.relationGraph(request));
    }

    // 事件溯源文档：返回支撑该事件或主题的来源文档列表。
    @GetMapping("/source_docs")
    public ResponseEntity<Map<String, Object>> sourceDocs(@RequestParam Map<String, Object> request) {
        ResponseEntity<Map<String, Object>> error = appEventValidator.validateTopicSelectorWithPage(request);
        if (error != null) {
            return error;
        }
        return ResponseEntity.ok(appEventService.sourceDocs(request));
    }

    // 文档反向溯源：根据 document_id 查询该文档所属的主题列表。
    @GetMapping("/source_topics")
    public ResponseEntity<Map<String, Object>> sourceTopics(@RequestParam Map<String, Object> request) {
        ResponseEntity<Map<String, Object>> error = appEventValidator.validateSourceTopics(request);
        if (error != null) {
            return error;
        }
        return ResponseEntity.ok(appEventService.sourceTopics(request));
    }
}
