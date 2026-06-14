package com.example.headline.service;

import com.example.headline.mock.AppHeadlineMockData;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AppHeadlineService {

    private final AppHeadlineMockData mockData;

    public AppHeadlineService(AppHeadlineMockData mockData) {
        this.mockData = mockData;
    }

    public Map<String, Object> topicClues(Map<String, Object> request) {
        // 主题线索需要根据请求中的 start_date/end_date 做简单时间过滤。
        List<Map<String, Object>> items = new ArrayList<>();
        for (Map<String, Object> clue : mockData.getAllClues()) {
            if (inDateRange(clue.get("publication_date").toString(), request)) {
                items.add(clue);
            }
        }
        return clueListResponse(items, request);
    }

    public Map<String, Object> filterClues(Map<String, Object> request) {
        return clueListResponse(List.of(mockData.getAiChipClue()), request);
    }

    public Map<String, Object> clueDetail(Map<String, Object> request) {
        String clueId = request.get("clue_id").toString();
        return apiResponse(mockData.getClueDetail(clueId));
    }

    private Map<String, Object> clueListResponse(List<Map<String, Object>> clueList, Map<String, Object> request) {
        // 列表接口统一在这里组装 data，确保 clue_list 在 total 前面。
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("clue_list", page(clueList, request));
        data.put("total", clueList.size());
        return apiResponse(data);
    }

    private Map<String, Object> apiResponse(Object data) {
        // 成功响应外层结构统一为 data/detail/status_code，方便前端统一处理。
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("data", data);
        response.put("detail", "\u6210\u529f");
        response.put("status_code", 200);
        return response;
    }

    private List<Map<String, Object>> page(List<Map<String, Object>> items, Map<String, Object> request) {
        int pageNo = positiveInt(request.get("page_no"), 1);
        int pageSize = positiveInt(request.get("page_size"), 20);
        int from = Math.min((pageNo - 1) * pageSize, items.size());
        int to = Math.min(from + pageSize, items.size());
        return items.subList(from, to);
    }

    private int positiveInt(Object value, int defaultValue) {
        if (value instanceof Integer || value instanceof Long || value instanceof Short || value instanceof Byte) {
            int parsed = ((Number) value).intValue();
            return parsed > 0 ? parsed : defaultValue;
        }
        return defaultValue;
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
}
