package com.example.headline.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AppHeadlineService {

    // 主题线索列表：使用请求中的 start_date/end_date 对线索做简单时间过滤。
    public Map<String, Object> topicClues(Map<String, Object> request) {
        List<Map<String, Object>> items = new ArrayList<>();
        for (Map<String, Object> clue : List.of(aiChipClue(), lowAltitudeClue())) {
            if (inDateRange(clue.get("publication_date").toString(), request)) {
                items.add(clue);
            }
        }
        return clueListResponse(items, request);
    }

    // 筛选线索列表：模拟高级检索后的结果，只返回一条更匹配筛选条件的线索。
    public Map<String, Object> filterClues(Map<String, Object> request) {
        return clueListResponse(List.of(aiChipClue()), request);
    }

    // 线索详情：根据 clue_id 返回不同详情，方便前端验证详情页展示。
    public Map<String, Object> clueDetail(Map<String, Object> request) {
        String clueId = request.get("clue_id").toString();
        if ("clue_002".equals(clueId)) {
            return apiResponse(Map.of(
                    "clue_id", "clue_002",
                    "document_id", "doc_002",
                    "document_title", "Low-altitude economy application outlook",
                    "doc_type", "report",
                    "publication_date", "2026-04-20",
                    "summary", "Low-altitude economy pilots are expanding demand for drones and navigation systems.",
                    "entities", List.of("low-altitude economy", "drones", "navigation systems"),
                    "source_url", "https://example.com/mock/doc_002"
            ));
        }
        return apiResponse(Map.of(
                "clue_id", "clue_001",
                "document_id", "doc_001",
                "document_title", "AI chip and edge computing trend report",
                "doc_type", "report",
                "publication_date", "2026-05-01",
                "summary", "AI chips and edge computing are moving into large-scale applications.",
                "entities", List.of("AI chips", "edge computing"),
                "source_url", "https://example.com/mock/doc_001"
        ));
    }

    private Map<String, Object> clueListResponse(List<Map<String, Object>> clueList, Map<String, Object> request) {
        return apiResponse(Map.of(
                "clue_list", clueList,
                "total", clueList.size(),
                "time_range", Map.of(
                        "start_date", textOrDefault(request.get("start_date"), "unbounded"),
                        "end_date", textOrDefault(request.get("end_date"), "unbounded")
                )
        ));
    }

    // AI 芯片线索：highlight 中的实体位置由原文自动计算，避免手写偏移不一致。
    private Map<String, Object> aiChipClue() {
        String text = "AI chips and edge computing are moving into large-scale applications.";
        return Map.of(
                "clue_id", "clue_001",
                "document_id", "doc_001",
                "document_title", "AI chip and edge computing trend report",
                "doc_type", "report",
                "publication_date", "2026-05-01",
                "text", text,
                "highlight", List.of(highlight(text, "AI chips"), highlight(text, "edge computing"))
        );
    }

    // 低空经济线索：用于和 topic_clues 的第一条形成差异，方便前端观察筛选效果。
    private Map<String, Object> lowAltitudeClue() {
        String text = "The low-altitude economy is accelerating drones and navigation systems deployment.";
        return Map.of(
                "clue_id", "clue_002",
                "document_id", "doc_002",
                "document_title", "Low-altitude economy application outlook",
                "doc_type", "report",
                "publication_date", "2026-04-20",
                "text", text,
                "highlight", List.of(
                        highlight(text, "low-altitude economy"),
                        highlight(text, "drones"),
                        highlight(text, "navigation systems")
                )
        );
    }

    private Map<String, Object> highlight(String text, String keyword) {
        int start = text.indexOf(keyword);
        return Map.of("keyword", keyword, "start", start, "end", start + keyword.length());
    }

    private Map<String, Object> apiResponse(Object data) {
        return Map.of("data", data, "detail", "success", "status_code", 200);
    }

    private boolean inDateRange(String publicationDate, Map<String, Object> request) {
        LocalDate date = LocalDate.parse(publicationDate);
        Object startDate = request.get("start_date");
        Object endDate = request.get("end_date");
        if (startDate != null && date.isBefore(LocalDate.parse(startDate.toString()))) {
            return false;
        }
        return endDate == null || !date.isAfter(LocalDate.parse(endDate.toString()));
    }

    private String textOrDefault(Object value, String defaultValue) {
        return value == null || value.toString().trim().isEmpty() ? defaultValue : value.toString();
    }
}
