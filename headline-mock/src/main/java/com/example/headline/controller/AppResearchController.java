package com.example.headline.controller;

import com.example.headline.service.AppResearchService;
import com.example.headline.validator.AppResearchValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/app/research")
public class AppResearchController {

    private final AppResearchService appResearchService;
    private final AppResearchValidator appResearchValidator;

    public AppResearchController(AppResearchService appResearchService, AppResearchValidator appResearchValidator) {
        this.appResearchService = appResearchService;
        this.appResearchValidator = appResearchValidator;
    }

    // 成果元数据：根据 document_id 返回论文、专利等研究成果的结构化信息。
    @GetMapping("/meta")
    public ResponseEntity<Map<String, Object>> meta(@RequestParam Map<String, Object> request) {
        ResponseEntity<Map<String, Object>> error = appResearchValidator.validateDocumentIdQuery(request);
        if (error != null) {
            return error;
        }
        return ResponseEntity.ok(appResearchService.meta(request));
    }

    // 引用关系列表：按 citation_type 返回参考文献、被引文献、共引等相关成果。
    @GetMapping("/citation_list")
    public ResponseEntity<Map<String, Object>> citationList(@RequestParam Map<String, Object> request) {
        ResponseEntity<Map<String, Object>> error = appResearchValidator.validateCitationList(request);
        if (error != null) {
            return error;
        }
        return ResponseEntity.ok(appResearchService.citationList(request));
    }

    // 作者合作网络：根据作者 ID 列表返回作者合作图谱。
    @PostMapping("/author_collab")
    public ResponseEntity<Map<String, Object>> authorCollab(@RequestBody Map<String, Object> request) {
        ResponseEntity<Map<String, Object>> error = appResearchValidator.validateAuthorCollab(request);
        if (error != null) {
            return error;
        }
        return ResponseEntity.ok(appResearchService.authorCollab(request));
    }

    // 机构合作图谱：根据机构 ID 列表返回机构之间的合作关系。
    @PostMapping("/institution_collab")
    public ResponseEntity<Map<String, Object>> institutionCollab(@RequestBody Map<String, Object> request) {
        ResponseEntity<Map<String, Object>> error = appResearchValidator.validateInstitutionCollab(request);
        if (error != null) {
            return error;
        }
        return ResponseEntity.ok(appResearchService.institutionCollab(request));
    }

    // 技术演化路径：按关键词、IPC、时间范围等条件返回技术节点和演化边。
    @PostMapping("/tech_evolution")
    public ResponseEntity<Map<String, Object>> techEvolution(@RequestBody Map<String, Object> request) {
        ResponseEntity<Map<String, Object>> error = appResearchValidator.validateTechEvolution(request);
        if (error != null) {
            return error;
        }
        return ResponseEntity.ok(appResearchService.techEvolution(request));
    }

    // 引用趋势：根据成果 ID 列表返回不同时间点的引用趋势数据。
    @PostMapping("/citation_trend")
    public ResponseEntity<Map<String, Object>> citationTrend(@RequestBody Map<String, Object> request) {
        ResponseEntity<Map<String, Object>> error = appResearchValidator.validateCitationTrend(request);
        if (error != null) {
            return error;
        }
        return ResponseEntity.ok(appResearchService.citationTrend(request));
    }

    // 合作强度：按作者或机构维度返回合作伙伴和强度分数。
    @PostMapping("/collab_strength")
    public ResponseEntity<Map<String, Object>> collabStrength(@RequestBody Map<String, Object> request) {
        ResponseEntity<Map<String, Object>> error = appResearchValidator.validateCollabStrength(request);
        if (error != null) {
            return error;
        }
        return ResponseEntity.ok(appResearchService.collabStrength(request));
    }

    // 成果基本信息列表：根据 document_ids 返回一组成果摘要。
    @PostMapping("/brief_list")
    public ResponseEntity<Map<String, Object>> briefList(@RequestBody Map<String, Object> request) {
        ResponseEntity<Map<String, Object>> error = appResearchValidator.validateBriefList(request);
        if (error != null) {
            return error;
        }
        return ResponseEntity.ok(appResearchService.briefList(request));
    }

    // 成果导出：返回一个模拟导出任务和文件地址。
    @PostMapping("/export")
    public ResponseEntity<Map<String, Object>> export(@RequestBody Map<String, Object> request) {
        ResponseEntity<Map<String, Object>> error = appResearchValidator.validateExport(request);
        if (error != null) {
            return error;
        }
        return ResponseEntity.ok(appResearchService.export(request));
    }
}
