package com.example.headline.validator;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class AppQaValidator {

    private static final Set<String> KNOWLEDGE_TYPES = Set.of("document", "entity", "relation", "event", "topic");
    private static final Set<String> VERBOSITY = Set.of("auto", "summary", "structured", "full");

    // 问答请求校验：Request body 字段严格按 OpenAPI 类型校验。
    public ResponseEntity<Map<String, Object>> validateAsk(Map<String, Object> request) {
        ResponseEntity<Map<String, Object>> queryError = validateRequiredString(request, "query");
        if (queryError != null) {
            return queryError;
        }

        ResponseEntity<Map<String, Object>> sessionError = validateRequiredString(request, "session_id");
        if (sessionError != null) {
            return sessionError;
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
        if (!isOptionalVerbosity(request.get("verbosity"))) {
            return valueError("verbosity", "verbosity must be auto, summary, structured, or full");
        }
        return validateKnowledgeSources(request.get("knowledge_sources"));
    }

    private ResponseEntity<Map<String, Object>> validateRequiredString(Map<String, Object> request, String field) {
        Object value = request.get(field);
        if (value == null) {
            return missing(field);
        }
        if (!(value instanceof String text)) {
            return valueError(field, field + " must be a string");
        }
        if (text.trim().isEmpty()) {
            return valueError(field, field + " must not be blank");
        }
        return null;
    }

    private ResponseEntity<Map<String, Object>> validateKnowledgeSources(Object value) {
        // knowledge_sources 允许空数组；knowledge_id 不做白名单限制，只要求非空字符串。
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

            Object knowledgeType = sourceMap.get("knowledge_type");
            if (knowledgeType == null) {
                return missing("knowledge_sources." + i + ".knowledge_type");
            }
            if (!(knowledgeType instanceof String typeText)) {
                return valueError("knowledge_sources." + i + ".knowledge_type", "knowledge_type must be a string");
            }
            if (typeText.trim().isEmpty()) {
                return valueError("knowledge_sources." + i + ".knowledge_type", "knowledge_type must not be blank");
            }
            if (!KNOWLEDGE_TYPES.contains(typeText.trim())) {
                return valueError("knowledge_sources." + i + ".knowledge_type", "knowledge_type must be one of " + KNOWLEDGE_TYPES);
            }

            Object knowledgeId = sourceMap.get("knowledge_id");
            if (knowledgeId == null) {
                return missing("knowledge_sources." + i + ".knowledge_id");
            }
            if (!(knowledgeId instanceof String idText)) {
                return valueError("knowledge_sources." + i + ".knowledge_id", "knowledge_id must be a string");
            }
            if (idText.trim().isEmpty()) {
                return valueError("knowledge_sources." + i + ".knowledge_id", "knowledge_id must not be blank");
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
        Map<String, Object> errorItem = new LinkedHashMap<>();
        errorItem.put("loc", List.of("body", field));
        errorItem.put("msg", message);
        errorItem.put("type", type);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("detail", List.of(errorItem));
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }

    private boolean isOptionalBoolean(Object value) {
        return value == null || value instanceof Boolean;
    }

    private boolean isOptionalVerbosity(Object value) {
        return value == null || value instanceof String text && VERBOSITY.contains(text.trim());
    }
}
