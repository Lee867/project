package com.example.headline.controller;

import com.example.headline.service.AppReportService;
import com.example.headline.validator.AppReportValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/app/report", produces = "application/json;charset=UTF-8")
public class AppReportController {

    private final AppReportService appReportService;
    private final AppReportValidator appReportValidator;

    public AppReportController(AppReportService appReportService, AppReportValidator appReportValidator) {
        this.appReportService = appReportService;
        this.appReportValidator = appReportValidator;
    }

    // 报告元数据：根据 document_id 返回标题、摘要、来源、作者等结构化信息。
    @PostMapping("/meta")
    public ResponseEntity<Map<String, Object>> meta(@RequestBody Map<String, Object> request) {
        ResponseEntity<Map<String, Object>> error = appReportValidator.validateDocumentId(request);
        if (error != null) {
            return error;
        }
        return ResponseEntity.ok(appReportService.meta(request));
    }

    // 报告全文访问：返回源文档文件 URL，供 APP 打开或预览。
    @PostMapping("/file_url")
    public ResponseEntity<Map<String, Object>> fileUrl(@RequestBody Map<String, Object> request) {
        ResponseEntity<Map<String, Object>> error = appReportValidator.validateDocumentId(request);
        if (error != null) {
            return error;
        }
        return ResponseEntity.ok(appReportService.fileUrl(request));
    }

    // 报告排行榜：按时间粒度和排序字段返回热门或高引报告。
    @PostMapping("/rank")
    public ResponseEntity<Map<String, Object>> rank(@RequestBody Map<String, Object> request) {
        ResponseEntity<Map<String, Object>> error = appReportValidator.validateRank(request);
        if (error != null) {
            return error;
        }
        return ResponseEntity.ok(appReportService.rank(request));
    }

    // 个性化推荐：根据 user_id 和分页参数返回推荐报告列表。
    @PostMapping("/recommend")
    public ResponseEntity<Map<String, Object>> recommend(@RequestBody Map<String, Object> request) {
        ResponseEntity<Map<String, Object>> error = appReportValidator.validateUserWithPage(request);
        if (error != null) {
            return error;
        }
        return ResponseEntity.ok(appReportService.recommend(request));
    }

    // 报告版本列表：根据 document_id 返回该报告的版本历史。
    @PostMapping("/versions")
    public ResponseEntity<Map<String, Object>> versions(@RequestBody Map<String, Object> request) {
        ResponseEntity<Map<String, Object>> error = appReportValidator.validateDocumentId(request);
        if (error != null) {
            return error;
        }
        return ResponseEntity.ok(appReportService.versions(request));
    }

    // 版本对比：根据两个版本 ID 返回差异片段和摘要。
    @PostMapping("/version_diff")
    public ResponseEntity<Map<String, Object>> versionDiff(@RequestBody Map<String, Object> request) {
        ResponseEntity<Map<String, Object>> error = appReportValidator.validateVersionDiff(request);
        if (error != null) {
            return error;
        }
        return ResponseEntity.ok(appReportService.versionDiff(request));
    }

    // 更新提醒：返回用户关注报告中的新版本提示。
    @PostMapping("/update_notice")
    public ResponseEntity<Map<String, Object>> updateNotice(@RequestBody Map<String, Object> request) {
        ResponseEntity<Map<String, Object>> error = appReportValidator.validateUserId(request);
        if (error != null) {
            return error;
        }
        return ResponseEntity.ok(appReportService.updateNotice(request));
    }
}
