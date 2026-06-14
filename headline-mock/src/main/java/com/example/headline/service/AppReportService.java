package com.example.headline.service;

import com.example.headline.mock.AppReportMockData;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AppReportService {

    private final AppReportMockData mockData;

    public AppReportService(AppReportMockData mockData) {
        this.mockData = mockData;
    }

    public Map<String, Object> meta(Map<String, Object> request) {
        String documentId = request.get("document_id").toString();
        return apiResponse(mockData.getReportMeta(documentId));
    }

    public Map<String, Object> fileUrl(Map<String, Object> request) {
        String documentId = request.get("document_id").toString();
        return apiResponse(mockData.getFileUrl(documentId));
    }

    public Map<String, Object> rank(Map<String, Object> request) {
        // 排行榜按 granularity 取固定列表，再根据 sort_by 做简单排序和分页。
        String granularity = request.get("granularity").toString();
        List<Map<String, Object>> sorted = sortedReports(mockData.getRankReports(granularity), request.get("sort_by"));

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("rank_list", page(sorted, request));
        data.put("total", sorted.size());
        return apiResponse(data);
    }

    public Map<String, Object> recommend(Map<String, Object> request) {
        // 推荐列表按 user_id 区分固定数据，模拟个性化推荐效果。
        String userId = request.get("user_id").toString();
        List<Map<String, Object>> items = mockData.getRecommendReports(userId);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("recommend_list", page(items, request));
        data.put("total", items.size());
        return apiResponse(data);
    }

    public Map<String, Object> versions(Map<String, Object> request) {
        String documentId = request.get("document_id").toString();
        return apiResponse(mockData.getVersions(documentId));
    }

    public Map<String, Object> versionDiff(Map<String, Object> request) {
        String versionIdA = request.get("version_id_a").toString();
        String versionIdB = request.get("version_id_b").toString();
        return apiResponse(mockData.getVersionDiff(versionIdA, versionIdB));
    }

    public Map<String, Object> updateNotice(Map<String, Object> request) {
        String userId = request.get("user_id").toString();
        return apiResponse(mockData.getUpdateNotices(userId));
    }

    private List<Map<String, Object>> sortedReports(List<Map<String, Object>> items, Object sortBy) {
        List<Map<String, Object>> sortedItems = new ArrayList<>(items);
        String sortField = "citation".equals(sortBy) ? "citation_count" : "hotness";
        sortedItems.sort(Comparator.<Map<String, Object>>comparingInt(item -> ((Number) item.get(sortField)).intValue()).reversed());
        return sortedItems;
    }

    private List<Map<String, Object>> page(List<Map<String, Object>> items, Map<String, Object> request) {
        int pageNo = positiveInt(request.get("page_no"), 1);
        int pageSize = positiveInt(request.get("page_size"), 20);
        int from = Math.min((pageNo - 1) * pageSize, items.size());
        int to = Math.min(from + pageSize, items.size());
        return items.subList(from, to);
    }

    private int positiveInt(Object value, int defaultValue) {
        if (value instanceof Number number && number.intValue() > 0) {
            return number.intValue();
        }
        if (value instanceof String text) {
            try {
                int parsed = Integer.parseInt(text.trim());
                return parsed > 0 ? parsed : defaultValue;
            } catch (NumberFormatException ignored) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    private Map<String, Object> apiResponse(Object data) {
        // 所有报告接口成功响应统一包装，保持 data/detail/status_code 顺序。
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("data", data);
        response.put("detail", "\u6210\u529f");
        response.put("status_code", 200);
        return response;
    }
}
