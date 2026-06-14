package com.example.headline.service;

import com.example.headline.mock.AppQaMockData;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AppQaService {

    private final AppQaMockData mockData;

    public AppQaService(AppQaMockData mockData) {
        this.mockData = mockData;
    }

    public Map<String, Object> ask(Map<String, Object> request) {
        // 非流式响应：根据 verbosity/thinking 选择模板，根据 traceable 决定是否返回引用。
        String query = request.get("query").toString().trim();
        String verbosity = textOrDefault(request.get("verbosity"), "auto");
        boolean thinking = booleanValue(request.get("thinking"), false);
        boolean traceable = booleanValue(request.get("traceable"), true);

        String template = thinking
                ? mockData.getThinkingAnswerTemplate()
                : mockData.getAnswerTemplate(verbosity);
        String answer = String.format(template, query);
        Object chart = containsTrendKeyword(query) ? mockData.getTrendChart() : null;
        List<Map<String, Object>> citations = traceable
                ? mockData.getCitations()
                : mockData.getEmptyCitations();

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("answer", answer);
        data.put("chart", chart);
        data.put("citations", citations);
        return apiResponse(data);
    }

    public String streamAsk(Map<String, Object> request) {
        // Mock 阶段不做真正逐 token 推送，只返回符合 SSE 格式的两段 data。
        String query = request.get("query").toString().trim();
        String verbosity = textOrDefault(request.get("verbosity"), "auto");
        boolean thinking = booleanValue(request.get("thinking"), false);
        String template = thinking
                ? mockData.getThinkingAnswerTemplate()
                : mockData.getAnswerTemplate(verbosity);
        String answer = String.format(template, query);
        String escaped = answer
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n");
        return "data: {\"delta\":\"" + escaped + "\",\"done\":false}\n\n"
                + "data: {\"delta\":\"\",\"done\":true}\n\n";
    }

    private boolean containsTrendKeyword(String query) {
        String lower = query.toLowerCase();
        return lower.contains("trend") || query.contains("趋势");
    }

    private boolean booleanValue(Object value, boolean defaultValue) {
        return value instanceof Boolean bool ? bool : defaultValue;
    }

    private String textOrDefault(Object value, String defaultValue) {
        if (!(value instanceof String text) || text.trim().isEmpty()) {
            return defaultValue;
        }
        return text.trim();
    }

    private Map<String, Object> apiResponse(Object data) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("data", data);
        response.put("detail", "\u6210\u529f");
        response.put("status_code", 200);
        return response;
    }
}
