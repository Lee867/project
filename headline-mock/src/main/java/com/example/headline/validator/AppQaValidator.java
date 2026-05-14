package com.example.headline.validator;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class AppQaValidator {

    private static final Set<String> KNOWLEDGE_TYPES = Set.of("document", "entity", "relation", "event", "topic");
    private static final Set<String> KNOWLEDGE_IDS = Set.of("doc_001", "doc_002", "research_001", "report_001", "topic_001", "entity_001", "relation_001");
    private static final Set<String> VERBOSITY = Set.of("auto", "summary", "structured", "full");

    // 问答请求校验：query/session_id 必填，其余开关和枚举值按白名单校验。
    public ResponseEntity<Map<String, Object>> validateAsk(Map<String, Object> request) {
        if (isBlank(request.get("query"))) {
            return missing("query");
        }
        if (isBlank(request.get("session_id"))) {
            return missing("session_id");
        }
        if (!isOptionalBoolean(request.get("thinking"))) {
            return valueError("thinking", "thinking must be a boolean");
        }
        if (!isOptionalBoolean(request.get("traceable"))) {
            return valueError("traceable", "traceable must be a boolean");
        }
        if (!isOptionalBoolean(request.get("is_stream"))) {
            return valueError("is_stream", "is_stream must be a boolean");
        }
        if (!isOptionalIn(request.get("verbosity"), VERBOSITY)) {
            return valueError("verbosity", "verbosity must be auto, summary, structured, or full");
        }
        return validateKnowledgeSources(request.get("knowledge_sources"));
    }

    // 知识来源校验：每项都需要 knowledge_type 和 knowledge_id，且必须在白名单内。
    private ResponseEntity<Map<String, Object>> validateKnowledgeSources(Object value) {
        if (value == null) {
            return null;
        }
        if (!(value instanceof List<?> sources)) {
            return valueError("knowledge_sources", "knowledge_sources must be an array");
        }
        for (int i = 0; i < sources.size(); i++) {
            Object source = sources.get(i);
            if (!(source instanceof Map<?, ?> sourceMap)) {
                return valueError("knowledge_sources." + i, "each knowledge source must be an object");
            }
            if (isBlank(sourceMap.get("knowledge_type"))) {
                return missing("knowledge_sources." + i + ".knowledge_type");
            }
            if (!isIn(sourceMap.get("knowledge_type"), KNOWLEDGE_TYPES)) {
                return valueError("knowledge_sources." + i + ".knowledge_type", "knowledge_type must be one of " + KNOWLEDGE_TYPES);
            }
            if (isBlank(sourceMap.get("knowledge_id"))) {
                return missing("knowledge_sources." + i + ".knowledge_id");
            }
            if (!isIn(sourceMap.get("knowledge_id"), KNOWLEDGE_IDS)) {
                return valueError("knowledge_sources." + i + ".knowledge_id", "knowledge_id must be one of mock knowledge ids");
            }
        }
        return null;
    }

    private ResponseEntity<Map<String, Object>> missing(String field) {
        return error(field, "Field required", "missing");
    }

    private ResponseEntity<Map<String, Object>> valueError(String field, String message) {
        return error(field, message, "value_error");
    }

    private ResponseEntity<Map<String, Object>> error(String field, String message, String type) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(Map.of(
                "detail", List.of(Map.of("loc", List.of("body", field), "msg", message, "type", type))
        ));
    }

    private boolean isBlank(Object value) {
        return value == null || value.toString().trim().isEmpty();
    }

    private boolean isIn(Object value, Set<String> allowed) {
        return value instanceof String text && allowed.contains(text);
    }

    private boolean isOptionalIn(Object value, Set<String> allowed) {
        return value == null || isIn(value, allowed);
    }

    private boolean isOptionalBoolean(Object value) {
        return value == null || value instanceof Boolean;
    }
}
