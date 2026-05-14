package com.example.headline.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AppResearchService {

    // 成果元数据：返回研究成果详情页的核心字段。
    public Map<String, Object> meta(Map<String, Object> request) {
        return apiResponse(Map.of(
                "document_id", request.get("document_id"),
                "title", "Edge AI Computing Architecture Research",
                "authors", List.of("Alex Chen", "Jamie Liu"),
                "institutions", List.of("Mock Research Institute", "Mock Industry Lab"),
                "publication_date", "2026-04-10",
                "document_type", "paper",
                "abstract", "Research result metadata for APP frontend integration testing.",
                "keywords", List.of("edge AI", "chip architecture", "computing")
        ));
    }

    // 引用列表：按 page_no/page_size 分页，模拟不同引用关系下的相关文档。
    public Map<String, Object> citationList(Map<String, Object> request) {
        List<Map<String, Object>> items = researchItems();
        return apiResponse(Map.of(
                "document_id", request.get("document_id"),
                "citation_type", request.get("citation_type"),
                "document_list", page(items, request),
                "total", items.size()
        ));
    }

    // 作者合作图谱：复用 collabGraph 生成作者节点和合作边。
    public Map<String, Object> authorCollab(Map<String, Object> request) {
        return apiResponse(collabGraph("author"));
    }

    // 机构合作图谱：复用 collabGraph 生成机构节点和合作边。
    public Map<String, Object> institutionCollab(Map<String, Object> request) {
        return apiResponse(collabGraph("institution"));
    }

    // 技术演化：内部数据带日期，会按 start_date/end_date 过滤节点和边。
    public Map<String, Object> techEvolution(Map<String, Object> request) {
        return apiResponse(Map.of(
                "nodes", List.of(
                        Map.of("id", "tech_001", "name", "AI chip", "year", 2024, "value", 32),
                        Map.of("id", "tech_002", "name", "edge computing", "year", 2025, "value", 45),
                        Map.of("id", "tech_003", "name", "on-device model", "year", 2026, "value", 51)
                ),
                "edges", List.of(
                        Map.of("source", "tech_001", "target", "tech_002", "relation", "enables"),
                        Map.of("source", "tech_002", "target", "tech_003", "relation", "evolves_to")
                ),
                "time_range", Map.of(
                        "start_date", textOrDefault(request.get("start_date"), "unbounded"),
                        "end_date", textOrDefault(request.get("end_date"), "unbounded")
                )
        ));
    }

    // 引用趋势：返回 ECharts 折线图容易消费的 trend_data。
    public Map<String, Object> citationTrend(Map<String, Object> request) {
        return apiResponse(Map.of(
                "document_ids", request.get("document_ids"),
                "granularity", textOrDefault(request.get("granularity"), "year"),
                "trend_data", List.of(
                        Map.of("time", "2024", "citation_count", 18),
                        Map.of("time", "2025", "citation_count", 42),
                        Map.of("time", "2026", "citation_count", 73)
                )
        ));
    }

    // 合作强度：内部合作伙伴带最近合作日期，会按时间范围过滤。
    public Map<String, Object> collabStrength(Map<String, Object> request) {
        List<Map<String, Object>> partners = new ArrayList<>();
        for (Map<String, Object> item : partnerItems()) {
            if (inDateRange(item.get("last_collab_date").toString(), request)) {
                partners.add(Map.of("target_id", item.get("target_id"), "target_name", item.get("target_name"), "strength", item.get("strength")));
            }
        }
        return apiResponse(Map.of("target_type", request.get("target_type"), "partner_list", partners, "total", partners.size()));
    }

    // 成果摘要列表：按 document_ids 模拟返回成果基本信息。
    public Map<String, Object> briefList(Map<String, Object> request) {
        return apiResponse(Map.of("document_list", researchItems(), "total", researchItems().size()));
    }

    // 导出接口：不生成真实文件，只返回模拟任务 ID 和文件 URL。
    public Map<String, Object> export(Map<String, Object> request) {
        Object exportFormat = request.get("export_format");
        return apiResponse(Map.of(
                "task_id", "export_task_001",
                "status", "finished",
                "file_url", "https://example.com/mock/research/export/export_task_001." + exportFormat
        ));
    }

    private Map<String, Object> collabGraph(String type) {
        return Map.of(
                "nodes", List.of(
                        Map.of("id", type + "_001", "name", "Node A", "type", type),
                        Map.of("id", type + "_002", "name", "Node B", "type", type)
                ),
                "edges", List.of(Map.of("source", type + "_001", "target", type + "_002", "weight", 0.82))
        );
    }

    private List<Map<String, Object>> researchItems() {
        return List.of(
                Map.of("document_id", "research_001", "document_title", "Edge AI Computing Architecture Research", "doc_type", "paper", "publication_date", "2026-04-10"),
                Map.of("document_id", "research_002", "document_title", "AI Chip Scheduling Method", "doc_type", "patent", "publication_date", "2026-03-18"),
                Map.of("document_id", "research_003", "document_title", "Low-altitude Navigation Optimization", "doc_type", "paper", "publication_date", "2026-02-08")
        );
    }

    private List<Map<String, Object>> partnerItems() {
        return List.of(
                Map.of("target_id", "author_001", "target_name", "Alex Chen", "strength", 0.91, "last_collab_date", "2026-03-01"),
                Map.of("target_id", "author_002", "target_name", "Jamie Liu", "strength", 0.84, "last_collab_date", "2025-10-01")
        );
    }

    private List<Map<String, Object>> page(List<Map<String, Object>> items, Map<String, Object> request) {
        int pageNo = positiveInt(request.get("page_no"), 1);
        int pageSize = positiveInt(request.get("page_size"), 10);
        int from = Math.min((pageNo - 1) * pageSize, items.size());
        int to = Math.min(from + pageSize, items.size());
        return items.subList(from, to);
    }

    private boolean inDateRange(String dateText, Map<String, Object> request) {
        LocalDate date = LocalDate.parse(dateText);
        Object startDate = request.get("start_date");
        Object endDate = request.get("end_date");
        if (startDate != null && date.isBefore(LocalDate.parse(startDate.toString()))) {
            return false;
        }
        return endDate == null || !date.isAfter(LocalDate.parse(endDate.toString()));
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
