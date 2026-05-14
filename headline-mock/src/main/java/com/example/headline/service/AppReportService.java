package com.example.headline.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
public class AppReportService {

    // 报告元数据：返回 APP 报告详情页需要展示的结构化字段。
    public Map<String, Object> meta(Map<String, Object> request) {
        Object documentId = request.get("document_id");
        return apiResponse(Map.of(
                "document_id", documentId,
                "title", "Global AI Infrastructure Trend Report 2026",
                "abstract", "Mock metadata for APP report detail integration testing.",
                "source", "Mock Research Institute",
                "authors", List.of(
                        Map.of("name", "Alex Chen", "organization", "Mock Research Institute"),
                        Map.of("name", "Jamie Liu", "organization", "Mock Industry Lab")
                ),
                "publication_date", "2026-05-01",
                "document_type", "report",
                "keywords", List.of("AI infrastructure", "semiconductor", "edge computing"),
                "is_public", true
        ));
    }

    // 源文件地址：按 document_id 拼出一个可预览的测试 URL。
    public Map<String, Object> fileUrl(Map<String, Object> request) {
        Object documentId = request.get("document_id");
        return apiResponse(Map.of("document_id", documentId, "file_url", "https://example.com/mock/reports/" + documentId + ".pdf"));
    }

    // 排行榜：先按时间粒度选择数据集，再按 hotness/citation 排序并分页。
    public Map<String, Object> rank(Map<String, Object> request) {
        List<Map<String, Object>> sorted = sortedReports(reportItems(request.get("granularity").toString()), request.get("sort_by"));
        return apiResponse(Map.of(
                "report_list", page(sorted, request),
                "total", sorted.size(),
                "granularity", request.get("granularity"),
                "sort_by", request.getOrDefault("sort_by", "hotness")
        ));
    }

    // 推荐报告：按 page_no/page_size 对推荐池分页，total 保留推荐池总数。
    public Map<String, Object> recommend(Map<String, Object> request) {
        List<Map<String, Object>> items = reportItems("recommend");
        return apiResponse(Map.of(
                "report_list", page(items, request),
                "total", items.size(),
                "page_no", positiveInt(request.get("page_no"), 1),
                "page_size", positiveInt(request.get("page_size"), 10)
        ));
    }

    // 版本列表：模拟报告从 v1 到最新版本的演进历史。
    public Map<String, Object> versions(Map<String, Object> request) {
        return apiResponse(Map.of(
                "document_id", request.get("document_id"),
                "version_list", List.of(
                        versionItem("report_001_v1", "v1.0", "2025-11-01", "Initial release."),
                        versionItem("report_001_v2", "v2.0", "2026-02-01", "Added industry cases."),
                        versionItem("report_001_v3", "v3.0", "2026-05-01", "Latest update.")
                )
        ));
    }

    // 版本差异：返回 added/removed/unchanged 三类片段。
    public Map<String, Object> versionDiff(Map<String, Object> request) {
        return apiResponse(Map.of(
                "version_id_a", request.get("version_id_a"),
                "version_id_b", request.get("version_id_b"),
                "added", List.of("Added edge AI deployment section.", "Added supply-chain risk chart."),
                "removed", List.of("Removed outdated 2024 forecast paragraph."),
                "unchanged", List.of("Core semiconductor demand conclusion remains unchanged.")
        ));
    }

    // 更新提醒：模拟用户收藏报告中有新版本的情况。
    public Map<String, Object> updateNotice(Map<String, Object> request) {
        return apiResponse(Map.of(
                "user_id", request.get("user_id"),
                "notice_list", List.of(
                        Map.of("document_id", "report_001", "latest_version_id", "report_001_v3", "update_date", "2026-05-01"),
                        Map.of("document_id", "report_003", "latest_version_id", "report_003_v2", "update_date", "2026-04-15")
                ),
                "total", 2
        ));
    }

    // 不同时间粒度返回不同榜单数据，模拟本周热门、年度高引等榜单。
    private List<Map<String, Object>> reportItems(String granularity) {
        String prefix = switch (granularity) {
            case "week" -> "report_week_";
            case "month" -> "report_month_";
            case "year" -> "report_year_";
            default -> "report_";
        };
        return List.of(
                rankItem(prefix + "001", "AI Infrastructure Investment Outlook", 98, 120),
                rankItem(prefix + "002", "Low-altitude Economy Industry Report", 87, 86),
                rankItem(prefix + "003", "Advanced Semiconductor Supply Chain", 76, 150)
        );
    }

    private Map<String, Object> rankItem(String documentId, String title, int hotness, int citationCount) {
        return Map.of(
                "document_id", documentId,
                "document_title", title,
                "doc_type", "report",
                "publication_date", "2026-05-01",
                "hotness", hotness,
                "citation_count", citationCount
        );
    }

    private Map<String, Object> versionItem(String versionId, String versionName, String updateDate, String summary) {
        return Map.of("version_id", versionId, "version_name", versionName, "update_date", updateDate, "summary", summary);
    }

    private List<Map<String, Object>> sortedReports(List<Map<String, Object>> items, Object sortBy) {
        List<Map<String, Object>> sortedItems = new ArrayList<>(items);
        String sortField = "citation".equals(sortBy) ? "citation_count" : "hotness";
        sortedItems.sort(Comparator.<Map<String, Object>>comparingInt(item -> ((Number) item.get(sortField)).intValue()).reversed());
        return sortedItems;
    }

    private List<Map<String, Object>> page(List<Map<String, Object>> items, Map<String, Object> request) {
        int pageNo = positiveInt(request.get("page_no"), 1);
        int pageSize = positiveInt(request.get("page_size"), 10);
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
        return Map.of("data", data, "detail", "success", "status_code", 200);
    }
}
