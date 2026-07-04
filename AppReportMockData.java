package com.example.headline.mock;

import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class AppReportMockData {

    // 报告模块固定 Mock 数据：按 document_id、user_id、版本组合预先建好，避免接口调用时重复构造。
    private final Map<String, Map<String, Object>> reportMetaMap;
    private final Map<String, Map<String, Object>> reportFileUrlMap;
    private final Map<String, Map<String, Object>> reportVersionResponseMap;
    private final Map<String, List<Map<String, Object>>> rankReports;
    private final Map<String, List<Map<String, Object>>> recommendReportsByUser;
    private final Map<String, Map<String, Object>> versionDiffMap;
    private final Map<String, Map<String, Object>> updateNoticeMap;

    public AppReportMockData() {
        // 这些 reportItem 同时用于排行榜和推荐，字段顺序需保持 OpenAPI 要求。
        Map<String, Object> report001 = reportItem(
                "report_001",
                "Global AI Infrastructure Trend Report 2026",
                "Mock Research Institute",
                "2026-05-01",
                "report",
                96,
                120
        );
        Map<String, Object> report002 = reportItem(
                "report_002",
                "Low-altitude Economy Industry Report",
                "Mock Industry Lab",
                "2026-04-20",
                "report",
                87,
                86
        );
        Map<String, Object> report003 = reportItem(
                "report_003",
                "Advanced Semiconductor Supply Chain",
                "Mock Technology Center",
                "2026-03-18",
                "report",
                82,
                150
        );

        this.reportMetaMap = createReportMetaMap();
        this.reportFileUrlMap = createReportFileUrlMap();
        this.reportVersionResponseMap = createReportVersionResponseMap();
        this.rankReports = createRankReports();
        this.recommendReportsByUser = createRecommendReportsByUser(report001, report002, report003);
        this.versionDiffMap = createVersionDiffMap();
        this.updateNoticeMap = createUpdateNoticeMap();
    }

    public Map<String, Object> getReportMeta(String documentId) {
        return required(reportMetaMap, documentId, "document_id");
    }

    public Map<String, Object> getFileUrl(String documentId) {
        return required(reportFileUrlMap, documentId, "document_id");
    }

    public List<Map<String, Object>> getRankReports(String granularity) {
        List<Map<String, Object>> data = rankReports.get(granularity);
        if (data == null) {
            throw new IllegalArgumentException("Unknown granularity: " + granularity);
        }
        return data;
    }

    public List<Map<String, Object>> getRecommendReports(String userId) {
        List<Map<String, Object>> data = recommendReportsByUser.get(userId);
        if (data == null) {
            throw new IllegalArgumentException("Unknown user_id: " + userId);
        }
        return data;
    }

    public Map<String, Object> getVersions(String documentId) {
        return required(reportVersionResponseMap, documentId, "document_id");
    }

    public Map<String, Object> getVersionDiff(String versionIdA, String versionIdB) {
        return required(versionDiffMap, versionDiffKey(versionIdA, versionIdB), "version_diff");
    }

    // Validator 用它提前判断版本组合是否支持，避免请求进入 Service 后抛 500。
    public boolean hasVersionDiff(String versionIdA, String versionIdB) {
        return versionDiffMap.containsKey(versionDiffKey(versionIdA, versionIdB));
    }

    public Map<String, Object> getUpdateNotices(String userId) {
        return required(updateNoticeMap, userId, "user_id");
    }

    private Map<String, Map<String, Object>> createReportMetaMap() {
        Map<String, Map<String, Object>> metadata = new LinkedHashMap<>();
        metadata.put(
                "report_001",
                reportMeta(
                        "report_001",
                        "Global AI Infrastructure Trend Report 2026",
                        "Mock metadata for AI infrastructure report integration testing.",
                        "Mock Research Institute",
                        List.of(
                                author("Alex Chen", "Mock Research Institute"),
                                author("Jamie Liu", "Mock Industry Lab")
                        ),
                        "2026-05-01",
                        "report",
                        List.of("AI infrastructure", "semiconductor", "edge computing"),
                        true
                )
        );
        metadata.put(
                "report_002",
                reportMeta(
                        "report_002",
                        "Low-altitude Economy Industry Report",
                        "Mock metadata about low-altitude economy, drones and navigation systems.",
                        "Mock Industry Lab",
                        List.of(author("Morgan Li", "Mock Industry Lab")),
                        "2026-04-20",
                        "report",
                        List.of("low-altitude economy", "drones", "navigation systems"),
                        true
                )
        );
        metadata.put(
                "report_003",
                reportMeta(
                        "report_003",
                        "Advanced Semiconductor Supply Chain",
                        "Mock metadata about semiconductor manufacturing and supply-chain risks.",
                        "Mock Technology Center",
                        List.of(author("Taylor Wang", "Mock Technology Center")),
                        "2026-03-18",
                        "report",
                        List.of("semiconductor", "supply chain", "manufacturing"),
                        true
                )
        );
        return metadata;
    }

    private Map<String, Map<String, Object>> createReportFileUrlMap() {
        Map<String, Map<String, Object>> fileUrls = new LinkedHashMap<>();
        fileUrls.put("report_001", fileUrl("report_001", "https://example.com/mock/reports/report_001.pdf"));
        fileUrls.put("report_002", fileUrl("report_002", "https://example.com/mock/reports/report_002.pdf"));
        fileUrls.put("report_003", fileUrl("report_003", "https://example.com/mock/reports/report_003.pdf"));
        return fileUrls;
    }

    private Map<String, Map<String, Object>> createReportVersionResponseMap() {
        Map<String, List<Map<String, Object>>> versions = new LinkedHashMap<>();
        versions.put(
                "report_001",
                List.of(
                        versionItem("report_001_v3", "v3.0", "2026-05-01", "Latest update."),
                        versionItem("report_001_v2", "v2.0", "2026-02-01", "Added industry cases."),
                        versionItem("report_001_v1", "v1.0", "2025-11-01", "Initial release.")
                )
        );
        versions.put(
                "report_002",
                List.of(
                        versionItem("report_002_v2", "v2.0", "2026-04-20", "Added drone application cases."),
                        versionItem("report_002_v1", "v1.0", "2026-01-20", "Initial release.")
                )
        );
        versions.put(
                "report_003",
                List.of(
                        versionItem("report_003_v2", "v2.0", "2026-04-15", "Added semiconductor supply-chain update."),
                        versionItem("report_003_v1", "v1.0", "2026-01-15", "Initial release.")
                )
        );

        Map<String, Map<String, Object>> responses = new LinkedHashMap<>();
        responses.put("report_001", versionResponse("report_001", versions.get("report_001")));
        responses.put("report_002", versionResponse("report_002", versions.get("report_002")));
        responses.put("report_003", versionResponse("report_003", versions.get("report_003")));
        return responses;
    }

    private Map<String, List<Map<String, Object>>> createRankReports() {
        Map<String, List<Map<String, Object>>> reports = new LinkedHashMap<>();
        reports.put(
                "week",
                List.of(
                        reportItem("report_001", "AI Infrastructure Investment Outlook", "Mock Research Institute", "2026-05-01", "report", 98, 120),
                        reportItem("report_002", "Low-altitude Economy Industry Report", "Mock Industry Lab", "2026-04-20", "report", 87, 86),
                        reportItem("report_003", "Advanced Semiconductor Supply Chain", "Mock Technology Center", "2026-03-18", "report", 76, 150)
                )
        );
        reports.put(
                "month",
                List.of(
                        reportItem("report_001", "AI Infrastructure Investment Outlook", "Mock Research Institute", "2026-05-01", "report", 92, 120),
                        reportItem("report_002", "Low-altitude Economy Industry Report", "Mock Industry Lab", "2026-04-20", "report", 84, 86),
                        reportItem("report_003", "Advanced Semiconductor Supply Chain", "Mock Technology Center", "2026-03-18", "report", 79, 150)
                )
        );
        reports.put(
                "year",
                List.of(
                        reportItem("report_003", "Advanced Semiconductor Supply Chain", "Mock Technology Center", "2026-03-18", "report", 91, 150),
                        reportItem("report_001", "AI Infrastructure Investment Outlook", "Mock Research Institute", "2026-05-01", "report", 88, 120),
                        reportItem("report_002", "Low-altitude Economy Industry Report", "Mock Industry Lab", "2026-04-20", "report", 80, 86)
                )
        );
        return reports;
    }

    private Map<String, List<Map<String, Object>>> createRecommendReportsByUser(
            Map<String, Object> report001,
            Map<String, Object> report002,
            Map<String, Object> report003
    ) {
        Map<String, List<Map<String, Object>>> reports = new LinkedHashMap<>();
        reports.put("user_001", List.of(report001, report003, report002));
        reports.put("user_002", List.of(report002, report001));
        return reports;
    }

    private Map<String, Map<String, Object>> createVersionDiffMap() {
        Map<String, Map<String, Object>> diffs = new LinkedHashMap<>();
        putVersionDiff(
                diffs,
                "report_001_v1",
                "report_001_v2",
                List.of(
                        diffSegment("added", "Added industry cases."),
                        diffSegment("unchanged", "Core AI infrastructure conclusion remains unchanged.")
                ),
                "v2.0 adds industry cases on top of the initial AI infrastructure report."
        );
        putVersionDiff(
                diffs,
                "report_001_v2",
                "report_001_v3",
                List.of(
                        diffSegment("added", "Added edge AI deployment section."),
                        diffSegment("removed", "Removed outdated 2024 forecast paragraph."),
                        diffSegment("unchanged", "Core semiconductor demand conclusion remains unchanged.")
                ),
                "v3.0 updates edge AI deployment and supply-chain risk content."
        );
        putVersionDiff(
                diffs,
                "report_002_v1",
                "report_002_v2",
                List.of(
                        diffSegment("added", "Added drone application cases."),
                        diffSegment("unchanged", "Navigation systems analysis remains unchanged.")
                ),
                "v2.0 adds low-altitude economy application cases."
        );
        putVersionDiff(
                diffs,
                "report_003_v1",
                "report_003_v2",
                List.of(
                        diffSegment("added", "Added semiconductor supply-chain update."),
                        diffSegment("unchanged", "Manufacturing capacity analysis remains unchanged.")
                ),
                "v2.0 adds semiconductor supply-chain updates."
        );
        return diffs;
    }

    private Map<String, Map<String, Object>> createUpdateNoticeMap() {
        Map<String, Map<String, Object>> notices = new LinkedHashMap<>();
        notices.put(
                "user_001",
                updateNoticeResponse(
                        List.of(
                                updateNotice("report_001", "Global AI Infrastructure Trend Report 2026", "report_001_v3", "v3.0", "2026-05-01", "Latest update."),
                                updateNotice("report_003", "Advanced Semiconductor Supply Chain", "report_003_v2", "v2.0", "2026-04-15", "Added semiconductor supply-chain update.")
                        )
                )
        );
        notices.put(
                "user_002",
                updateNoticeResponse(
                        List.of(
                                updateNotice("report_002", "Low-altitude Economy Industry Report", "report_002_v2", "v2.0", "2026-04-20", "Added drone application cases.")
                        )
                )
        );
        return notices;
    }

    private void putVersionDiff(
            Map<String, Map<String, Object>> diffs,
            String versionIdA,
            String versionIdB,
            List<Map<String, Object>> diffSegments,
            String summary
    ) {
        diffs.put(versionDiffKey(versionIdA, versionIdB), versionDiff(versionIdA, versionIdB, diffSegments, summary));
        diffs.put(versionDiffKey(versionIdB, versionIdA), versionDiff(versionIdB, versionIdA, diffSegments, summary));
    }

    private Map<String, Object> reportMeta(
            String documentId,
            String title,
            String reportAbstract,
            String source,
            List<Map<String, Object>> authors,
            String publicationDate,
            String documentType,
            List<String> keywords,
            boolean publicReport
    ) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("document_id", documentId);
        data.put("title", title);
        data.put("abstract", reportAbstract);
        data.put("source", source);
        data.put("authors", authors);
        data.put("publication_date", publicationDate);
        data.put("document_type", documentType);
        data.put("keywords", keywords);
        data.put("is_public", publicReport);
        return data;
    }

    private Map<String, Object> author(String name, String organization) {
        Map<String, Object> author = new LinkedHashMap<>();
        author.put("name", name);
        author.put("organization", organization);
        return author;
    }

    private Map<String, Object> fileUrl(String documentId, String url) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("document_id", documentId);
        data.put("file_url", url);
        return data;
    }

    private Map<String, Object> reportItem(
            String documentId,
            String title,
            String source,
            String publicationDate,
            String documentType,
            int hotness,
            int citationCount
    ) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("document_id", documentId);
        item.put("title", title);
        item.put("source", source);
        item.put("publication_date", publicationDate);
        item.put("document_type", documentType);
        item.put("hotness", hotness);
        item.put("citation_count", citationCount);
        return item;
    }

    private Map<String, Object> versionResponse(String documentId, List<Map<String, Object>> versions) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("document_id", documentId);
        response.put("versions", versions);
        return response;
    }

    private Map<String, Object> versionItem(String versionId, String versionLabel, String publicationDate, String description) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("version_id", versionId);
        item.put("version_label", versionLabel);
        item.put("publication_date", publicationDate);
        item.put("description", description);
        return item;
    }

    private Map<String, Object> versionDiff(
            String versionIdA,
            String versionIdB,
            List<Map<String, Object>> diffSegments,
            String summary
    ) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("version_id_a", versionIdA);
        data.put("version_id_b", versionIdB);
        data.put("diff_segments", diffSegments);
        data.put("summary", summary);
        return data;
    }

    private Map<String, Object> diffSegment(String diffType, String text) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("diff_type", diffType);
        item.put("text", text);
        return item;
    }

    private Map<String, Object> updateNoticeResponse(List<Map<String, Object>> noticeList) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("notice_list", noticeList);
        data.put("total", noticeList.size());
        return data;
    }

    private Map<String, Object> updateNotice(
            String documentId,
            String title,
            String latestVersionId,
            String latestVersionLabel,
            String latestPublicationDate,
            String updateDescription
    ) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("document_id", documentId);
        item.put("title", title);
        item.put("latest_version_id", latestVersionId);
        item.put("latest_version_label", latestVersionLabel);
        item.put("latest_publication_date", latestPublicationDate);
        item.put("update_description", updateDescription);
        return item;
    }

    private String versionDiffKey(String versionIdA, String versionIdB) {
        return versionIdA + "::" + versionIdB;
    }

    private Map<String, Object> required(Map<String, Map<String, Object>> dataMap, String key, String fieldName) {
        Map<String, Object> data = dataMap.get(key);
        if (data == null) {
            throw new IllegalArgumentException("Unknown " + fieldName + ": " + key);
        }
        return data;
    }
}
