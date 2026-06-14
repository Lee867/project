package com.example.headline.mock;

import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class AppQaMockData {

    // QA 模块固定模板、图表和引用：启动时创建一次，请求时只做格式化和选择。
    private final Map<String, String> answerTemplates;
    private final String thinkingAnswerTemplate;
    private final Map<String, Object> trendChart;
    private final List<Map<String, Object>> citations;
    private final List<Map<String, Object>> emptyCitations;

    public AppQaMockData() {
        this.answerTemplates = createAnswerTemplates();
        this.thinkingAnswerTemplate = "Thinking-mode answer for: %s. "
                + "The reasoning path checks reports, events, "
                + "and research evidence before giving a conclusion.";
        this.trendChart = createTrendChart();
        this.citations = createCitations();
        this.emptyCitations = List.of();
    }

    public String getAnswerTemplate(String verbosity) {
        // verbosity 已由 Validator 校验；这里保留异常是为了防止内部调用传错。
        String template = answerTemplates.get(verbosity);
        if (template == null) {
            throw new IllegalArgumentException("Unknown verbosity: " + verbosity);
        }
        return template;
    }

    public String getThinkingAnswerTemplate() {
        return thinkingAnswerTemplate;
    }

    public Map<String, Object> getTrendChart() {
        return trendChart;
    }

    public List<Map<String, Object>> getCitations() {
        return citations;
    }

    public List<Map<String, Object>> getEmptyCitations() {
        return emptyCitations;
    }

    private Map<String, String> createAnswerTemplates() {
        Map<String, String> templates = new LinkedHashMap<>();
        templates.put(
                "auto",
                "Mock answer for: %s. "
                        + "The current evidence suggests continued attention "
                        + "to AI infrastructure and edge computing applications."
        );
        templates.put(
                "summary",
                "Summary answer for: %s. "
                        + "Key signals point to AI infrastructure and edge devices."
        );
        templates.put(
                "structured",
                "Structured answer for: %s\n"
                        + "1. Main trend: edge AI adoption.\n"
                        + "2. Evidence: mock reports and research results.\n"
                        + "3. Risk: supply-chain uncertainty."
        );
        templates.put(
                "full",
                "Full answer for: %s. "
                        + "This response combines report clues, event dynamics, "
                        + "and research results for APP frontend testing."
        );
        return templates;
    }

    private Map<String, Object> createTrendChart() {
        Map<String, Object> title = new LinkedHashMap<>();
        title.put("text", "AI Infrastructure Trend");

        Map<String, Object> xAxis = new LinkedHashMap<>();
        xAxis.put("type", "category");
        xAxis.put("data", List.of("2024", "2025", "2026"));

        Map<String, Object> yAxis = new LinkedHashMap<>();
        yAxis.put("type", "value");

        Map<String, Object> seriesItem = new LinkedHashMap<>();
        seriesItem.put("name", "hotness");
        seriesItem.put("type", "line");
        seriesItem.put("data", List.of(30, 55, 86));

        Map<String, Object> chart = new LinkedHashMap<>();
        chart.put("title", title);
        chart.put("xAxis", xAxis);
        chart.put("yAxis", yAxis);
        chart.put("series", List.of(seriesItem));
        return chart;
    }

    private List<Map<String, Object>> createCitations() {
        return List.of(
                citation("doc_001", "clue_001"),
                citation("research_001", "research_chunk_001")
        );
    }

    private Map<String, Object> citation(String documentId, String chunkId) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("document_id", documentId);
        item.put("chunk_id", chunkId);
        return item;
    }
}
