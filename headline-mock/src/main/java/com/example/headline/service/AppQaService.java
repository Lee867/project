package com.example.headline.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AppQaService {

    // 智能问答：根据 verbosity、thinking、traceable 返回不同形态的答案。
    public Map<String, Object> ask(Map<String, Object> request) {
        String query = request.get("query").toString();
        boolean traceable = booleanValue(request.get("traceable"), true);
        return apiResponse(Map.of(
                "answer", answerText(query, request),
                "chart", chartData(query),
                "citations", traceable ? citations() : List.of()
        ));
    }

    // 按回答详细程度生成不同答案文本，模拟真实问答的多种输出风格。
    private String answerText(String query, Map<String, Object> request) {
        String verbosity = textOrDefault(request.get("verbosity"), "auto");
        boolean thinking = booleanValue(request.get("thinking"), false);
        if ("summary".equals(verbosity)) {
            return "Summary answer for: " + query + ". Key signals point to AI infrastructure and edge devices.";
        }
        if ("structured".equals(verbosity)) {
            return "Structured answer for: " + query + "\n1. Main trend: edge AI adoption.\n2. Evidence: mock reports and research results.\n3. Risk: supply-chain uncertainty.";
        }
        if ("full".equals(verbosity)) {
            return "Full answer for: " + query + ". This response combines report clues, event dynamics, and research results for APP frontend testing.";
        }
        if (thinking) {
            return "Thinking-mode answer for: " + query + ". The reasoning path checks reports, events, and research evidence before giving a conclusion.";
        }
        return "Mock answer for: " + query + ". The current evidence suggests continued attention to AI infrastructure and edge computing applications.";
    }

    // 当问题包含 trend 或趋势时返回一个简单 ECharts option；否则返回空对象。
    private Map<String, Object> chartData(String query) {
        String lower = query.toLowerCase();
        if (!lower.contains("trend") && !query.contains("趋势")) {
            return Map.of();
        }
        return Map.of(
                "chart_type", "line",
                "x_axis", List.of("2024", "2025", "2026"),
                "series", List.of(Map.of("name", "hotness", "data", List.of(30, 55, 86)))
        );
    }

    private List<Map<String, Object>> citations() {
        return List.of(
                Map.of("document_id", "doc_001", "document_title", "AI chip and edge computing trend report", "quote", "AI chips and edge computing are moving into large-scale applications."),
                Map.of("document_id", "research_001", "document_title", "Edge AI Computing Architecture Research", "quote", "Edge AI adoption continues to rise.")
        );
    }

    private boolean booleanValue(Object value, boolean defaultValue) {
        return value instanceof Boolean bool ? bool : defaultValue;
    }

    private String textOrDefault(Object value, String defaultValue) {
        return value == null || value.toString().trim().isEmpty() ? defaultValue : value.toString();
    }

    private Map<String, Object> apiResponse(Object data) {
        return Map.of("data", data, "detail", "success", "status_code", 200);
    }
}
