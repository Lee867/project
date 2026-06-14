package com.example.headline.service;

import com.example.headline.mock.AppEventMockData;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AppEventService {

    private final AppEventMockData mockData;

    public AppEventService(AppEventMockData mockData) {
        this.mockData = mockData;
    }

    public Map<String, Object> timeline(Map<String, Object> request) {
        String topicName = request.get("topic_name").toString();
        String topicId = mockData.resolveTopicId(null, topicName);
        String granularity = textOrDefault(request.get("granularity"), "year");
        return apiResponse(mockData.getTimeline(topicId, granularity));
    }

    public Map<String, Object> structuredInfo(Map<String, Object> request) {
        String topicId = resolveTopicId(request);
        return apiResponse(mockData.getStructuredInfo(topicId));
    }

    public Map<String, Object> relationGraph(Map<String, Object> request) {
        String topicId = resolveTopicId(request);
        return apiResponse(mockData.getRelationGraph(topicId));
    }

    public Map<String, Object> sourceDocs(Map<String, Object> request) {
        // source_docs 需要分页；total 始终表示分页前的完整数量。
        String topicId = resolveTopicId(request);
        List<Map<String, Object>> docs = mockData.getSourceDocs(topicId);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("document_list", page(docs, request));
        data.put("total", docs.size());
        return apiResponse(data);
    }

    public Map<String, Object> sourceTopics(Map<String, Object> request) {
        String documentId = request.get("document_id").toString();
        List<Map<String, Object>> topics = mockData.getSourceTopics(documentId);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("topic_list", page(topics, request));
        data.put("total", topics.size());
        return apiResponse(data);
    }

    private String resolveTopicId(Map<String, Object> request) {
        String topicId = textOrNull(request.get("topic_id"));
        String topicName = textOrNull(request.get("topic_name"));
        return mockData.resolveTopicId(topicId, topicName);
    }

    private List<Map<String, Object>> page(List<Map<String, Object>> items, Map<String, Object> request) {
        int pageNo = positiveInt(request.get("page_no"), 1);
        int pageSize = positiveInt(request.get("page_size"), 20);
        int from = Math.min((pageNo - 1) * pageSize, items.size());
        int to = Math.min(from + pageSize, items.size());
        return items.subList(from, to);
    }

    private int positiveInt(Object value, int defaultValue) {
        if (!(value instanceof String text)) {
            return defaultValue;
        }

        String normalized = text.trim();
        if (!normalized.matches("\\d+")) {
            return defaultValue;
        }

        try {
            long parsed = Long.parseLong(normalized);
            if (parsed <= 0 || parsed > Integer.MAX_VALUE) {
                return defaultValue;
            }
            return (int) parsed;
        } catch (NumberFormatException ignored) {
            return defaultValue;
        }
    }

    private String textOrDefault(Object value, String defaultValue) {
        if (!(value instanceof String text) || text.trim().isEmpty()) {
            return defaultValue;
        }
        return text.trim();
    }

    private String textOrNull(Object value) {
        if (!(value instanceof String text) || text.trim().isEmpty()) {
            return null;
        }
        return text.trim();
    }

    private Map<String, Object> apiResponse(Object data) {
        // Event 模块只返回 JSON，统一使用成功响应包装。
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("data", data);
        response.put("detail", "\u6210\u529f");
        response.put("status_code", 200);
        return response;
    }
}
