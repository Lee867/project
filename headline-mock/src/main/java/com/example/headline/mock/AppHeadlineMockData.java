package com.example.headline.mock;

import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class AppHeadlineMockData {

    // 头条模块固定测试数据：Bean 初始化时创建一次，接口调用时只读取这些对象。
    // AI 芯片线索的列表数据
    private final Map<String, Object> aiChipClue;
    //低空经济线索的列表数据
    private final Map<String, Object> lowAltitudeClue;
    //AI 芯片线索的详情数据
    private final Map<String, Object> aiChipClueDetail;
    //低空经济线索的详情数据
    private final Map<String, Object> lowAltitudeClueDetail;
    //所有线索组成的列表
    private final List<Map<String, Object>> allClues;
    //clue_id 到详情数据的查询表
    private final Map<String, Map<String, Object>> clueDetails;

    public AppHeadlineMockData() {
        // 构造线索时复用同一批实体对象，保证列表高亮和详情实体保持一致。
        String aiChipText = "AI chips and edge computing are moving into large-scale applications.";
        Map<String, Object> aiChipEntity = entity(aiChipText, "entity_001", "AI chips", "technology");
        Map<String, Object> edgeComputingEntity = entity(aiChipText, "entity_002", "edge computing", "technology");

        Map<String, Object> aiChipHighlight = new LinkedHashMap<>();
        aiChipHighlight.put("entities", List.of(aiChipEntity, edgeComputingEntity));
        aiChipHighlight.put("relations", List.of(relation(aiChipEntity, edgeComputingEntity, "supports")));

        Map<String, Object> aiChipClueData = new LinkedHashMap<>();
        aiChipClueData.put("clue_id", "clue_001");
        aiChipClueData.put("document_id", "doc_001");
        aiChipClueData.put("document_title", "AI chip and edge computing trend report");
        aiChipClueData.put("doc_type", "report");
        aiChipClueData.put("source", "Mock Research Institute");
        aiChipClueData.put("publication_date", "2026-05-01");
        aiChipClueData.put("text", aiChipText);
        aiChipClueData.put("highlight", aiChipHighlight);
        this.aiChipClue = aiChipClueData;

        //详情数据
        Map<String, Object> aiChipDetailData = new LinkedHashMap<>();
        aiChipDetailData.put("clue_id", "clue_001");
        aiChipDetailData.put("raw_text", aiChipText);
        aiChipDetailData.put("document_id", "doc_001");
        aiChipDetailData.put("source_url", "https://example.com/mock/doc_001");
        aiChipDetailData.put("publication_date", "2026-05-01");
        aiChipDetailData.put("source", "Mock Research Institute");
        aiChipDetailData.put("related_entities", List.of(aiChipEntity, edgeComputingEntity));
        aiChipDetailData.put("context_summary", "AI chips and edge computing are moving into large-scale applications.");
        this.aiChipClueDetail = aiChipDetailData;

        String lowAltitudeText = "The low-altitude economy is accelerating drones and navigation systems deployment.";
        Map<String, Object> lowAltitudeEntity = entity(lowAltitudeText, "entity_003", "low-altitude economy", "industry");
        Map<String, Object> dronesEntity = entity(lowAltitudeText, "entity_004", "drones", "technology");
        Map<String, Object> navigationSystemsEntity = entity(lowAltitudeText, "entity_005", "navigation systems", "technology");

        Map<String, Object> lowAltitudeHighlight = new LinkedHashMap<>();
        lowAltitudeHighlight.put("entities", List.of(lowAltitudeEntity, dronesEntity, navigationSystemsEntity));
        lowAltitudeHighlight.put("relations", List.of(
                relation(lowAltitudeEntity, dronesEntity, "drives"),
                relation(dronesEntity, navigationSystemsEntity, "depends_on")
        ));

        Map<String, Object> lowAltitudeClueData = new LinkedHashMap<>();
        lowAltitudeClueData.put("clue_id", "clue_002");
        lowAltitudeClueData.put("document_id", "doc_002");
        lowAltitudeClueData.put("document_title", "Low-altitude economy application outlook");
        lowAltitudeClueData.put("doc_type", "report");
        lowAltitudeClueData.put("source", "Mock Industry Lab");
        lowAltitudeClueData.put("publication_date", "2026-04-20");
        lowAltitudeClueData.put("text", lowAltitudeText);
        lowAltitudeClueData.put("highlight", lowAltitudeHighlight);
        this.lowAltitudeClue = lowAltitudeClueData;

        Map<String, Object> lowAltitudeDetailData = new LinkedHashMap<>();
        lowAltitudeDetailData.put("clue_id", "clue_002");
        lowAltitudeDetailData.put("raw_text", lowAltitudeText);
        lowAltitudeDetailData.put("document_id", "doc_002");
        lowAltitudeDetailData.put("source_url", "https://example.com/mock/doc_002");
        lowAltitudeDetailData.put("publication_date", "2026-04-20");
        lowAltitudeDetailData.put("source", "Mock Industry Lab");
        lowAltitudeDetailData.put("related_entities", List.of(lowAltitudeEntity, dronesEntity, navigationSystemsEntity));
        lowAltitudeDetailData.put("context_summary", "Low-altitude economy pilots are expanding demand for drones and navigation systems.");
        this.lowAltitudeClueDetail = lowAltitudeDetailData;

        this.allClues = List.of(aiChipClue, lowAltitudeClue);

        Map<String, Map<String, Object>> details = new LinkedHashMap<>();
        details.put("clue_001", aiChipClueDetail);
        details.put("clue_002", lowAltitudeClueDetail);
        this.clueDetails = details;
    }

    public List<Map<String, Object>> getAllClues() {
        return allClues;
    }

    public Map<String, Object> getAiChipClue() {
        return aiChipClue;
    }

    public Map<String, Object> getLowAltitudeClue() {
        return lowAltitudeClue;
    }

    public Map<String, Object> getClueDetail(String clueId) {
        Map<String, Object> clueDetail = clueDetails.get(clueId);
        if (clueDetail == null) {
            throw new IllegalArgumentException("Unknown clue_id: " + clueId);
        }
        return clueDetail;
    }

    private Map<String, Object> entity(
            String fullText,
            String entityId,
            String entityText,
            String entityType
    ) {
        // 根据完整文本自动计算实体位置，避免手写 start/end 时和 text 对不上。
        int start = fullText.indexOf(entityText);
        if (start < 0) {
            throw new IllegalArgumentException("Entity text not found in full text: " + entityText);
        }

        Map<String, Object> entity = new LinkedHashMap<>();
        entity.put("entity_id", entityId);
        entity.put("text", entityText);
        entity.put("entity_type", entityType);
        entity.put("start", start);
        entity.put("end", start + entityText.length());
        return entity;
    }

    private Map<String, Object> relation(Map<String, Object> headEntity, Map<String, Object> tailEntity, String relationType) {
        Map<String, Object> relation = new LinkedHashMap<>();
        relation.put("relation_id", headEntity.get("entity_id") + "_" + tailEntity.get("entity_id"));
        relation.put("head_entity", headEntity);
        relation.put("tail_entity", tailEntity);
        relation.put("relation_type", relationType);
        return relation;
    }
}
