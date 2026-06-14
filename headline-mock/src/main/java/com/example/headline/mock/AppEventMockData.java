package com.example.headline.mock;

import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class AppEventMockData {

    // 事件模块固定数据：主题映射、时间线、图谱和溯源列表都在 Bean 初始化时创建。
    private final Map<String, String> topicNameById;
    private final Map<String, String> topicIdByName;
    private final Map<String, Map<String, Object>> timelineMap;
    private final Map<String, Map<String, Object>> structuredInfoMap;
    private final Map<String, Map<String, Object>> relationGraphMap;
    private final Map<String, List<Map<String, Object>>> sourceDocsMap;
    private final Map<String, List<Map<String, Object>>> sourceTopicsMap;

    public AppEventMockData() {
        this.topicNameById = createTopicNameById();
        this.topicIdByName = createTopicIdByName();
        this.timelineMap = createTimelineMap();
        this.structuredInfoMap = createStructuredInfoMap();
        this.relationGraphMap = createRelationGraphMap();
        this.sourceDocsMap = createSourceDocsMap();
        this.sourceTopicsMap = createSourceTopicsMap();
    }

    public String resolveTopicId(String topicId, String topicName) {
        // 同时支持 topic_id 和 topic_name；Validator 会提前保证二者不冲突。
        if (isNotBlank(topicId)) {
            return topicId.trim();
        }
        if (isNotBlank(topicName)) {
            String resolved = topicIdByName.get(topicName.trim());
            if (resolved == null) {
                throw new IllegalArgumentException("Unknown topic_name: " + topicName);
            }
            return resolved;
        }
        throw new IllegalArgumentException("topic_id or topic_name is required");
    }

    public String getTopicName(String topicId) {
        String topicName = topicNameById.get(topicId);
        if (topicName == null) {
            throw new IllegalArgumentException("Unknown topic_id: " + topicId);
        }
        return topicName;
    }

    public Map<String, Object> getTimeline(String topicId, String granularity) {
        return required(timelineMap, timelineKey(topicId, granularity), "timeline");
    }

    public Map<String, Object> getStructuredInfo(String topicId) {
        return required(structuredInfoMap, topicId, "topic_id");
    }

    public Map<String, Object> getRelationGraph(String topicId) {
        return required(relationGraphMap, topicId, "topic_id");
    }

    public List<Map<String, Object>> getSourceDocs(String topicId) {
        List<Map<String, Object>> data = sourceDocsMap.get(topicId);
        if (data == null) {
            throw new IllegalArgumentException("Unknown topic_id: " + topicId);
        }
        return data;
    }

    public List<Map<String, Object>> getSourceTopics(String documentId) {
        List<Map<String, Object>> data = sourceTopicsMap.get(documentId);
        if (data == null) {
            throw new IllegalArgumentException("Unknown document_id: " + documentId);
        }
        return data;
    }

    public boolean hasTopicId(String topicId) {
        return topicNameById.containsKey(topicId);
    }

    public boolean hasTopicName(String topicName) {
        return topicIdByName.containsKey(topicName);
    }

    public boolean hasDocumentId(String documentId) {
        return sourceTopicsMap.containsKey(documentId);
    }

    public boolean topicMatches(String topicId, String topicName) {
        return topicName.equals(topicNameById.get(topicId));
    }

    private Map<String, String> createTopicNameById() {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("topic_001", "AI infrastructure trend");
        data.put("topic_002", "Low-altitude economy development");
        data.put("topic_003", "Advanced semiconductor supply chain");
        return data;
    }

    private Map<String, String> createTopicIdByName() {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("AI infrastructure trend", "topic_001");
        data.put("Low-altitude economy development", "topic_002");
        data.put("Advanced semiconductor supply chain", "topic_003");
        return data;
    }

    private Map<String, Map<String, Object>> createTimelineMap() {
        Map<String, Map<String, Object>> data = new LinkedHashMap<>();
        data.put(timelineKey("topic_001", "year"), timelineResponse(List.of(
                timelineItem("2024", 8, "Early AI infrastructure pilots and policy signals emerged."),
                timelineItem("2025", 18, "Industrial AI deployment accelerated."),
                timelineItem("2026", 26, "Large-scale AI applications expanded.")
        )));
        data.put(timelineKey("topic_001", "month"), timelineResponse(List.of(
                timelineItem("2026-03", 5, "AI chip pilot projects increased."),
                timelineItem("2026-04", 7, "Infrastructure investment activity grew."),
                timelineItem("2026-05", 9, "Edge computing scenarios widened.")
        )));
        data.put(timelineKey("topic_002", "year"), timelineResponse(List.of(
                timelineItem("2024", 6, "Low-altitude economy demonstration zones appeared."),
                timelineItem("2025", 15, "Drone logistics and navigation pilots expanded."),
                timelineItem("2026", 22, "Commercial low-altitude applications scaled.")
        )));
        data.put(timelineKey("topic_002", "month"), timelineResponse(List.of(
                timelineItem("2026-03", 4, "Drone delivery pilots increased."),
                timelineItem("2026-04", 6, "Navigation system trials expanded."),
                timelineItem("2026-05", 8, "Low-altitude route planning advanced.")
        )));
        data.put(timelineKey("topic_003", "year"), timelineResponse(List.of(
                timelineItem("2024", 9, "Semiconductor supply-chain risks became visible."),
                timelineItem("2025", 17, "Advanced manufacturing capacity planning accelerated."),
                timelineItem("2026", 24, "Supply-chain resilience programs expanded.")
        )));
        data.put(timelineKey("topic_003", "month"), timelineResponse(List.of(
                timelineItem("2026-03", 5, "Packaging capacity updates increased."),
                timelineItem("2026-04", 7, "Supply-chain risk assessments were refreshed."),
                timelineItem("2026-05", 8, "Advanced semiconductor partnerships expanded.")
        )));
        return data;
    }

    private Map<String, Map<String, Object>> createStructuredInfoMap() {
        Map<String, Map<String, Object>> data = new LinkedHashMap<>();
        data.put("topic_001", structuredInfo(
                "topic_001",
                "AI infrastructure trend",
                "2026-01-01 to 2026-05-01",
                "Global",
                List.of("Mock Research Institute", "Mock Industry Lab"),
                "Major technology and industry activity tracked for APP testing.",
                "Policy releases and investment updates.",
                "AI infrastructure and semiconductor supply chain.",
                "The event remains active."
        ));
        data.put("topic_002", structuredInfo(
                "topic_002",
                "Low-altitude economy development",
                "2026-01-20 to 2026-04-20",
                "China",
                List.of("Mock Industry Lab", "Mock Navigation Center"),
                "Drone applications and navigation systems are expanding in low-altitude scenarios.",
                "Route pilots, policy updates, and application demonstrations.",
                "Drones, navigation systems, logistics, and urban air mobility.",
                "The event is moving from pilots toward commercial deployment."
        ));
        data.put("topic_003", structuredInfo(
                "topic_003",
                "Advanced semiconductor supply chain",
                "2026-01-15 to 2026-04-15",
                "Global",
                List.of("Mock Technology Center", "Mock Research Institute"),
                "Advanced semiconductor manufacturing and supply-chain resilience remain under review.",
                "Manufacturing capacity updates and supply-chain risk tracking.",
                "Semiconductor manufacturing, packaging, and upstream materials.",
                "The event remains strategically important."
        ));
        return data;
    }

    private Map<String, Map<String, Object>> createRelationGraphMap() {
        Map<String, Map<String, Object>> data = new LinkedHashMap<>();
        data.put("topic_001", relationGraph(
                List.of(
                        node("org_001", "Mock Research Institute", "organization"),
                        node("org_002", "Mock Industry Lab", "organization"),
                        node("tech_001", "AI infrastructure", "technology")
                ),
                List.of(
                        edge("org_001", "tech_001", "research", 0.95),
                        edge("org_002", "tech_001", "application", 0.85)
                )
        ));
        data.put("topic_002", relationGraph(
                List.of(
                        node("org_002", "Mock Industry Lab", "organization"),
                        node("tech_002", "Drones", "technology"),
                        node("tech_003", "Navigation systems", "technology")
                ),
                List.of(
                        edge("org_002", "tech_002", "pilot", 0.9),
                        edge("tech_002", "tech_003", "depends_on", 0.82)
                )
        ));
        data.put("topic_003", relationGraph(
                List.of(
                        node("org_003", "Mock Technology Center", "organization"),
                        node("tech_004", "Advanced semiconductor", "technology"),
                        node("risk_001", "Supply-chain risk", "risk")
                ),
                List.of(
                        edge("org_003", "tech_004", "analysis", 0.93),
                        edge("tech_004", "risk_001", "affected_by", 0.8)
                )
        ));
        return data;
    }

    private Map<String, List<Map<String, Object>>> createSourceDocsMap() {
        Map<String, List<Map<String, Object>>> data = new LinkedHashMap<>();
        data.put("topic_001", List.of(
                document("doc_001", "AI chip and edge computing trend report"),
                document("doc_003", "Advanced semiconductor supply chain note")
        ));
        data.put("topic_002", List.of(
                document("doc_002", "Low-altitude economy application outlook"),
                document("doc_004", "Drone navigation systems field report")
        ));
        data.put("topic_003", List.of(
                document("doc_003", "Advanced semiconductor supply chain note"),
                document("doc_001", "AI chip and edge computing trend report")
        ));
        return data;
    }

    private Map<String, List<Map<String, Object>>> createSourceTopicsMap() {
        Map<String, List<Map<String, Object>>> data = new LinkedHashMap<>();
        data.put("doc_001", List.of(
                topic("topic_001", "AI infrastructure trend"),
                topic("topic_003", "Advanced semiconductor supply chain")
        ));
        data.put("doc_002", List.of(topic("topic_002", "Low-altitude economy development")));
        data.put("doc_003", List.of(
                topic("topic_003", "Advanced semiconductor supply chain"),
                topic("topic_001", "AI infrastructure trend")
        ));
        data.put("doc_004", List.of(topic("topic_002", "Low-altitude economy development")));
        return data;
    }

    private Map<String, Object> timelineResponse(List<Map<String, Object>> items) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("timeline_list", items);
        data.put("total", items.size());
        return data;
    }

    private Map<String, Object> timelineItem(String time, int eventCount, String summary) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("time", time);
        item.put("event_count", eventCount);
        item.put("summary", summary);
        return item;
    }

    private Map<String, Object> structuredInfo(
            String eventId,
            String name,
            String eventTime,
            String location,
            List<String> participants,
            String background,
            String coreActions,
            String impactScope,
            String assessment
    ) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("event_id", eventId);
        data.put("name", name);
        data.put("event_time", eventTime);
        data.put("location", location);
        data.put("participants", participants);
        data.put("background", background);
        data.put("core_actions", coreActions);
        data.put("impact_scope", impactScope);
        data.put("assessment", assessment);
        return data;
    }

    private Map<String, Object> relationGraph(List<Map<String, Object>> nodes, List<Map<String, Object>> edges) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("nodes", nodes);
        data.put("edges", edges);
        return data;
    }

    private Map<String, Object> node(String nodeId, String label, String nodeType) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("node_id", nodeId);
        data.put("label", label);
        data.put("node_type", nodeType);
        return data;
    }

    private Map<String, Object> edge(String sourceId, String targetId, String relationType, double weight) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("source_id", sourceId);
        data.put("target_id", targetId);
        data.put("relation_type", relationType);
        data.put("weight", weight);
        return data;
    }

    private Map<String, Object> document(String documentId, String title) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("document_id", documentId);
        data.put("title", title);
        return data;
    }

    private Map<String, Object> topic(String topicId, String name) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("topic_id", topicId);
        data.put("name", name);
        return data;
    }

    private String timelineKey(String topicId, String granularity) {
        return topicId + "::" + granularity;
    }

    private boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private Map<String, Object> required(Map<String, Map<String, Object>> dataMap, String key, String fieldName) {
        Map<String, Object> data = dataMap.get(key);
        if (data == null) {
            throw new IllegalArgumentException("Unknown " + fieldName + ": " + key);
        }
        return data;
    }
}
