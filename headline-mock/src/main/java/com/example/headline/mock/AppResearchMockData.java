package com.example.headline.mock;

import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class AppResearchMockData {

    // Research 模块固定成果、图谱、趋势和导出响应：全部在构造器初始化一次。
    private final Map<String, Map<String, Object>> researchMetaMap;
    private final Map<String, List<Map<String, Object>>> citationDocumentsMap;
    private final Map<String, Object> authorCollabGraph;
    private final Map<String, Object> institutionCollabGraph;
    private final Map<String, Object> techEvolutionGraph;
    private final Map<String, Map<String, Object>> citationTrendMap;
    private final Map<String, List<Map<String, Object>>> collabPartnersMap;
    private final Map<String, Map<String, Object>> researchBriefMap;
    private final Map<String, Map<String, Object>> exportResponseMap;
    private final List<String> citationTypes;
    private final List<String> authorIds;
    private final List<String> institutionIds;

    public AppResearchMockData() {
        this.researchMetaMap = createResearchMetaMap();
        this.citationTypes = List.of("reference", "secondary_reference", "co_citation", "cited_by", "secondary_cited_by", "co_cited");
        this.authorIds = List.of("author_001", "author_002", "author_003");
        this.institutionIds = List.of("inst_001", "inst_002", "inst_003");
        this.citationDocumentsMap = createCitationDocumentsMap();
        this.authorCollabGraph = createAuthorCollabGraph();
        this.institutionCollabGraph = createInstitutionCollabGraph();
        this.techEvolutionGraph = createTechEvolutionGraph();
        this.citationTrendMap = createCitationTrendMap();
        this.collabPartnersMap = createCollabPartnersMap();
        this.researchBriefMap = createResearchBriefMap();
        this.exportResponseMap = createExportResponseMap();
    }

    public Map<String, Object> getResearchMeta(String documentId) {
        return required(researchMetaMap, documentId, "document_id");
    }

    public List<Map<String, Object>> getCitationDocuments(String documentId, String citationType) {
        // 引用列表按 document_id + citation_type 组合查询，避免返回错类型数据。
        String key = citationKey(documentId, citationType);
        List<Map<String, Object>> data = citationDocumentsMap.get(key);
        if (data == null) {
            throw new IllegalArgumentException("Unknown citation list: " + key);
        }
        return data;
    }

    public Map<String, Object> getAuthorCollabGraph() {
        return authorCollabGraph;
    }

    public Map<String, Object> getInstitutionCollabGraph() {
        return institutionCollabGraph;
    }

    public Map<String, Object> getTechEvolutionGraph() {
        return techEvolutionGraph;
    }

    public Map<String, Object> getCitationTrend(String granularity) {
        return required(citationTrendMap, granularity, "granularity");
    }

    public List<Map<String, Object>> getCollabPartners(String targetType) {
        List<Map<String, Object>> data = collabPartnersMap.get(targetType);
        if (data == null) {
            throw new IllegalArgumentException("Unknown target_type: " + targetType);
        }
        return data;
    }

    public Map<String, Object> getResearchBrief(String documentId) {
        return required(researchBriefMap, documentId, "document_id");
    }

    public Map<String, Object> getExportResponse(String exportFormat) {
        return required(exportResponseMap, exportFormat, "export_format");
    }

    public boolean hasDocumentId(String documentId) {
        return researchMetaMap.containsKey(documentId);
    }

    public boolean hasAuthorId(String authorId) {
        return authorIds.contains(authorId);
    }

    public boolean hasInstitutionId(String institutionId) {
        return institutionIds.contains(institutionId);
    }

    public boolean hasCitationType(String citationType) {
        return citationTypes.contains(citationType);
    }

    private Map<String, Map<String, Object>> createResearchMetaMap() {
        Map<String, Map<String, Object>> data = new LinkedHashMap<>();
        data.put("research_001", metaItem(
                "research_001",
                "Edge AI Computing Architecture Research",
                "Research result metadata for APP frontend integration testing.",
                List.of("Alex Chen", "Jamie Liu"),
                List.of("Mock Research Institute", "Mock Industry Lab"),
                "2026-04-10",
                73,
                8.6,
                List.of("edge AI", "chip architecture", "computing")
        ));
        data.put("research_002", metaItem(
                "research_002",
                "AI Chip Scheduling Method",
                "Mock patent metadata for AI chip scheduling and inference workloads.",
                List.of("Morgan Wu"),
                List.of("Mock Industry Lab"),
                "2026-03-18",
                41,
                6.2,
                List.of("AI chip", "scheduling", "inference")
        ));
        data.put("research_003", metaItem(
                "research_003",
                "Low-altitude Navigation Optimization",
                "Research metadata for low-altitude navigation optimization testing.",
                List.of("Taylor Wang", "Morgan Li"),
                List.of("Mock Navigation Center"),
                "2026-02-08",
                29,
                5.7,
                List.of("navigation", "drones", "route planning")
        ));
        data.put("research_004", metaItem(
                "research_004",
                "Advanced Semiconductor Packaging Reliability",
                "Research metadata for semiconductor packaging reliability testing.",
                List.of("Jamie Liu"),
                List.of("Mock Technology Center"),
                "2026-01-15",
                35,
                7.1,
                List.of("semiconductor", "packaging", "reliability")
        ));
        return data;
    }

    private Map<String, List<Map<String, Object>>> createCitationDocumentsMap() {
        Map<String, List<Map<String, Object>>> data = new LinkedHashMap<>();
        for (String documentId : researchMetaMap.keySet()) {
            for (String citationType : citationTypes) {
                data.put(citationKey(documentId, citationType), List.of(
                        citationItem("research_002", "AI Chip Scheduling Method", List.of("Morgan Wu"), "2026-03-18", "Mock Journal"),
                        citationItem("research_003", "Low-altitude Navigation Optimization", List.of("Taylor Wang", "Morgan Li"), "2026-02-08", "Mock Conference"),
                        citationItem("research_004", "Advanced Semiconductor Packaging Reliability", List.of("Jamie Liu"), "2026-01-15", "Mock Journal")
                ));
            }
        }
        return data;
    }

    private Map<String, Object> createAuthorCollabGraph() {
        return graph(
                List.of(
                        node("author_001", "Alex Chen", "author"),
                        node("author_002", "Jamie Liu", "author"),
                        node("author_003", "Morgan Wu", "author")
                ),
                List.of(
                        edge("author_001", "author_002", "co_author", 0.91),
                        edge("author_001", "author_003", "co_author", 0.76)
                )
        );
    }

    private Map<String, Object> createInstitutionCollabGraph() {
        return graph(
                List.of(
                        node("inst_001", "Mock Research Institute", "institution"),
                        node("inst_002", "Mock Industry Lab", "institution"),
                        node("inst_003", "Mock Technology Center", "institution")
                ),
                List.of(
                        edge("inst_001", "inst_002", "joint_research", 0.88),
                        edge("inst_001", "inst_003", "technology_transfer", 0.72)
                )
        );
    }

    private Map<String, Object> createTechEvolutionGraph() {
        return graph(
                List.of(
                        node("tech_001", "AI chip", "technology"),
                        node("tech_002", "edge computing", "technology"),
                        node("tech_003", "on-device model", "technology")
                ),
                List.of(
                        edge("tech_001", "tech_002", "evolves_to", 0.88),
                        edge("tech_002", "tech_003", "evolves_to", 0.81)
                )
        );
    }

    private Map<String, Map<String, Object>> createCitationTrendMap() {
        Map<String, Map<String, Object>> data = new LinkedHashMap<>();
        data.put("year", trendResponse(List.of(
                trendPoint("2024", 18, List.of(citedDoc("research_001", "Edge AI Computing Architecture Research"))),
                trendPoint("2025", 42, List.of(citedDoc("research_002", "AI Chip Scheduling Method"))),
                trendPoint("2026", 73, List.of(citedDoc("research_001", "Edge AI Computing Architecture Research")))
        )));
        data.put("quarter", trendResponse(List.of(
                trendPoint("2026-Q1", 21, List.of(citedDoc("research_004", "Advanced Semiconductor Packaging Reliability"))),
                trendPoint("2026-Q2", 32, List.of(citedDoc("research_001", "Edge AI Computing Architecture Research")))
        )));
        return data;
    }

    private Map<String, List<Map<String, Object>>> createCollabPartnersMap() {
        Map<String, List<Map<String, Object>>> data = new LinkedHashMap<>();
        data.put("author", List.of(
                partner("author_002", "Jamie Liu", 0.84, 12),
                partner("author_003", "Morgan Wu", 0.76, 8)
        ));
        data.put("institution", List.of(
                partner("inst_002", "Mock Industry Lab", 0.87, 16),
                partner("inst_003", "Mock Technology Center", 0.73, 9)
        ));
        return data;
    }

    private Map<String, Map<String, Object>> createResearchBriefMap() {
        Map<String, Map<String, Object>> data = new LinkedHashMap<>();
        data.put("research_001", briefItem(
                "research_001",
                "Edge AI Computing Architecture Research",
                List.of("Alex Chen", "Jamie Liu"),
                List.of("Mock Research Institute"),
                "Mock Journal",
                List.of("edge AI", "chip", "inference"),
                List.of("AI infrastructure", "semiconductor"),
                "Sample research brief item for APP integration testing."
        ));
        data.put("research_002", briefItem(
                "research_002",
                "AI Chip Scheduling Method",
                List.of("Morgan Wu"),
                List.of("Mock Industry Lab"),
                "Mock Journal",
                List.of("AI chip", "scheduling"),
                List.of("semiconductor", "computing"),
                "Sample patent-style research brief item for APP integration testing."
        ));
        data.put("research_003", briefItem(
                "research_003",
                "Low-altitude Navigation Optimization",
                List.of("Taylor Wang", "Morgan Li"),
                List.of("Mock Navigation Center"),
                "Mock Conference",
                List.of("navigation", "drones"),
                List.of("low-altitude economy"),
                "Sample navigation research brief item for APP integration testing."
        ));
        data.put("research_004", briefItem(
                "research_004",
                "Advanced Semiconductor Packaging Reliability",
                List.of("Jamie Liu"),
                List.of("Mock Technology Center"),
                "Mock Journal",
                List.of("semiconductor", "packaging"),
                List.of("supply chain", "manufacturing"),
                "Sample semiconductor research brief item for APP integration testing."
        ));
        return data;
    }

    private Map<String, Map<String, Object>> createExportResponseMap() {
        Map<String, Map<String, Object>> data = new LinkedHashMap<>();
        data.put("word", exportResponse("export_task_001", "word", "https://example.com/mock/research/export/export_task_001.docx"));
        data.put("pdf", exportResponse("export_task_001", "pdf", "https://example.com/mock/research/export/export_task_001.pdf"));
        data.put("excel", exportResponse("export_task_001", "excel", "https://example.com/mock/research/export/export_task_001.xlsx"));
        return data;
    }

    private Map<String, Object> metaItem(
            String documentId,
            String title,
            String abstractText,
            List<String> authors,
            List<String> affiliations,
            String publishDate,
            int citationCount,
            double impactFactor,
            List<String> techClassifications
    ) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("document_id", documentId);
        item.put("title", title);
        item.put("abstract", abstractText);
        item.put("authors", authors);
        item.put("affiliations", affiliations);
        item.put("publish_date", publishDate);
        item.put("citation_count", citationCount);
        item.put("impact_factor", impactFactor);
        item.put("tech_classifications", techClassifications);
        return item;
    }

    private Map<String, Object> citationItem(String documentId, String title, List<String> authors, String publishDate, String source) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("document_id", documentId);
        item.put("title", title);
        item.put("authors", authors);
        item.put("publish_date", publishDate);
        item.put("source", source);
        return item;
    }

    private Map<String, Object> graph(List<Map<String, Object>> nodes, List<Map<String, Object>> edges) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("nodes", nodes);
        data.put("edges", edges);
        return data;
    }

    private Map<String, Object> node(String nodeId, String label, String nodeType) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("node_id", nodeId);
        item.put("label", label);
        item.put("node_type", nodeType);
        return item;
    }

    private Map<String, Object> edge(String sourceId, String targetId, String relationType, double weight) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("source_id", sourceId);
        item.put("target_id", targetId);
        item.put("relation_type", relationType);
        item.put("weight", weight);
        return item;
    }

    private Map<String, Object> trendResponse(List<Map<String, Object>> trendData) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("trend_data", trendData);
        return data;
    }

    private Map<String, Object> trendPoint(String time, int citationCount, List<Map<String, Object>> citedDocs) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("time", time);
        item.put("citation_count", citationCount);
        item.put("cited_docs", citedDocs);
        return item;
    }

    private Map<String, Object> citedDoc(String documentId, String title) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("document_id", documentId);
        item.put("title", title);
        return item;
    }

    private Map<String, Object> partner(String partnerId, String partnerName, double strengthScore, int collabOutputCount) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("partner_id", partnerId);
        item.put("partner_name", partnerName);
        item.put("strength_score", strengthScore);
        item.put("collab_output_count", collabOutputCount);
        return item;
    }

    private Map<String, Object> briefItem(
            String documentId,
            String title,
            List<String> authors,
            List<String> affiliations,
            String source,
            List<String> keywords,
            List<String> themes,
            String abstractText
    ) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("document_id", documentId);
        item.put("title", title);
        item.put("authors", authors);
        item.put("affiliations", affiliations);
        item.put("source", source);
        item.put("keywords", keywords);
        item.put("themes", themes);
        item.put("abstract", abstractText);
        return item;
    }

    private Map<String, Object> exportResponse(String taskId, String exportFormat, String fileUrl) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("task_id", taskId);
        item.put("export_format", exportFormat);
        item.put("file_url", fileUrl);
        return item;
    }

    private String citationKey(String documentId, String citationType) {
        return documentId + "::" + citationType;
    }

    private Map<String, Object> required(Map<String, Map<String, Object>> dataMap, String key, String fieldName) {
        Map<String, Object> data = dataMap.get(key);
        if (data == null) {
            throw new IllegalArgumentException("Unknown " + fieldName + ": " + key);
        }
        return data;
    }
}
