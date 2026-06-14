package com.example.headline.service;

import com.example.headline.mock.AppResearchMockData;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AppResearchService {

    private final AppResearchMockData mockData;

    public AppResearchService(AppResearchMockData mockData) {
        this.mockData = mockData;
    }

    public Map<String, Object> meta(Map<String, Object> request) {
        String documentId = request.get("document_id").toString().trim();
        return apiResponse(mockData.getResearchMeta(documentId));
    }

    public Map<String, Object> citationList(Map<String, Object> request) {
        String documentId = request.get("document_id").toString().trim();
        String citationType = request.get("citation_type").toString().trim();
        List<Map<String, Object>> items = mockData.getCitationDocuments(documentId, citationType);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("document_list", page(items, request));
        data.put("total", items.size());
        return apiResponse(data);
    }

    public Map<String, Object> authorCollab(Map<String, Object> request) {
        return apiResponse(mockData.getAuthorCollabGraph());
    }

    public Map<String, Object> institutionCollab(Map<String, Object> request) {
        return apiResponse(mockData.getInstitutionCollabGraph());
    }

    public Map<String, Object> techEvolution(Map<String, Object> request) {
        return apiResponse(mockData.getTechEvolutionGraph());
    }

    public Map<String, Object> citationTrend(Map<String, Object> request) {
        String granularity = textOrDefault(request.get("granularity"), "year");
        return apiResponse(mockData.getCitationTrend(granularity));
    }

    public Map<String, Object> collabStrength(Map<String, Object> request) {
        String targetType = request.get("target_type").toString().trim();
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("partner_list", mockData.getCollabPartners(targetType));
        return apiResponse(data);
    }

    public Map<String, Object> briefList(Map<String, Object> request) {
        // brief_list 必须按请求中的 document_ids 顺序返回，不返回未请求的成果。
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Object documentId : (List<?>) request.get("document_ids")) {
            resultList.add(mockData.getResearchBrief(documentId.toString().trim()));
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("result_list", resultList);
        data.put("total", resultList.size());
        return apiResponse(data);
    }

    public Map<String, Object> export(Map<String, Object> request) {
        String exportFormat = request.get("export_format").toString().trim();
        return apiResponse(mockData.getExportResponse(exportFormat));
    }

    private List<Map<String, Object>> page(List<Map<String, Object>> items, Map<String, Object> request) {
        int pageNo = positiveQueryInt(request.get("page_no"), 1);
        int pageSize = positiveQueryInt(request.get("page_size"), 20);
        long offset = (long) (pageNo - 1) * pageSize;
        int from = (int) Math.min(offset, items.size());
        int to = Math.min(from + pageSize, items.size());
        return items.subList(from, to);
    }

    private int positiveQueryInt(Object value, int defaultValue) {
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

    private Map<String, Object> apiResponse(Object data) {
        // Research 模块成功响应统一包装，字段顺序保持 data/detail/status_code。
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("data", data);
        response.put("detail", "\u6210\u529f");
        response.put("status_code", 200);
        return response;
    }
}
