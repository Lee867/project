package com.example.headline.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AppEventService {

    // 事件时间线：根据 granularity 返回年度或月度的事件脉络。
    public Map<String, Object> timeline(Map<String, Object> request) {
        String granularity = textOrDefault(request.get("granularity"), "year");
        List<Map<String, Object>> items = "month".equals(granularity) ? monthTimeline() : yearTimeline();
        return apiResponse(Map.of("timeline_list", items, "total", items.size()));
    }

    // 结构化信息：模拟事件详情页中的背景、参与方、影响范围和研判。
    public Map<String, Object> structuredInfo(Map<String, Object> request) {
        return apiResponse(Map.of(
                "event_id", textOrDefault(request.get("topic_id"), "event_001"),
                "name", textOrDefault(request.get("topic_name"), "AI infrastructure trend"),
                "event_time", "2026-01-01 to 2026-05-01",
                "location", "Global",
                "participants", List.of("Mock Research Institute", "Mock Industry Lab"),
                "background", "Major technology and industry activity tracked for APP testing.",
                "core_actions", "Policy releases, investment updates, and application pilot projects are aggregated.",
                "impact_scope", "Semiconductor supply chain, AI infrastructure, and low-altitude economy sectors.",
                "assessment", "The event remains active and shows continuing cross-industry impact."
        ));
    }

    // 关系图：返回节点和边，字段与关系图响应结构保持一致。
    public Map<String, Object> relationGraph(Map<String, Object> request) {
        return apiResponse(Map.of(
                "nodes", List.of(
                        node("org_001", "Mock Research Institute", "organization"),
                        node("org_002", "Mock Industry Lab", "organization"),
                        node("tech_001", "AI infrastructure", "technology")
                ),
                "edges", List.of(
                        edge("org_001", "tech_001", "research"),
                        edge("org_002", "tech_001", "application")
                )
        ));
    }

    // 事件来源文档：支持分页，total 始终表示全部来源文档数量。
    public Map<String, Object> sourceDocs(Map<String, Object> request) {
        List<Map<String, Object>> docs = List.of(
                document("doc_001", "AI chip and edge computing trend report", "report"),
                document("doc_002", "Low-altitude economy application outlook", "report"),
                document("doc_003", "Advanced semiconductor supply chain note", "news")
        );
        return apiResponse(Map.of("document_list", page(docs, request), "total", docs.size()));
    }

    // 文档反向溯源：根据 document_id 模拟所属主题列表，也支持分页。
    public Map<String, Object> sourceTopics(Map<String, Object> request) {
        List<Map<String, Object>> topics = List.of(
                Map.of("topic_id", "topic_001", "topic_name", "AI infrastructure trend", "score", 0.96),
                Map.of("topic_id", "topic_003", "topic_name", "Advanced semiconductor supply chain", "score", 0.88)
        );
        return apiResponse(Map.of("topic_list", page(topics, request), "total", topics.size(), "document_id", request.get("document_id")));
    }

    private List<Map<String, Object>> yearTimeline() {
        return List.of(
                Map.of("time", "2024", "event_count", 8, "summary", "Early pilots and policy signals emerged."),
                Map.of("time", "2025", "event_count", 18, "summary", "Industrial deployment accelerated."),
                Map.of("time", "2026", "event_count", 26, "summary", "Large-scale applications expanded.")
        );
    }

    private List<Map<String, Object>> monthTimeline() {
        return List.of(
                Map.of("time", "2026-03", "event_count", 5, "summary", "Pilot projects increased."),
                Map.of("time", "2026-04", "event_count", 7, "summary", "Investment activity grew."),
                Map.of("time", "2026-05", "event_count", 9, "summary", "Application scenarios widened.")
        );
    }

    private Map<String, Object> node(String id, String name, String type) {
        return Map.of("id", id, "name", name, "type", type);
    }

    private Map<String, Object> edge(String source, String target, String relation) {
        return Map.of("source", source, "target", target, "relation", relation);
    }

    private Map<String, Object> document(String documentId, String title, String docType) {
        return Map.of("document_id", documentId, "document_title", title, "doc_type", docType, "publication_date", "2026-05-01");
    }

    private List<Map<String, Object>> page(List<Map<String, Object>> items, Map<String, Object> request) {
        int pageNo = positiveInt(request.get("page_no"), 1);
        int pageSize = positiveInt(request.get("page_size"), 10);
        int from = Math.min((pageNo - 1) * pageSize, items.size());
        int to = Math.min(from + pageSize, items.size());
        return items.subList(from, to);
    }

    private int positiveInt(Object value, int defaultValue) {
        try {
            return value == null ? defaultValue : Math.max(Integer.parseInt(value.toString().trim()), 1);
        } catch (NumberFormatException ignored) {
            return defaultValue;
        }
    }

    private String textOrDefault(Object value, String defaultValue) {
        return value == null || value.toString().trim().isEmpty() ? defaultValue : value.toString();
    }

    private Map<String, Object> apiResponse(Object data) {
        return Map.of("data", data, "detail", "success", "status_code", 200);
    }
}
