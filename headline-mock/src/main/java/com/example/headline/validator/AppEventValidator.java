package com.example.headline.validator;

import com.example.headline.mock.AppEventMockData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class AppEventValidator {

    private static final Set<String> GRANULARITIES = Set.of("year", "month");

    private final AppEventMockData mockData;

    public AppEventValidator(AppEventMockData mockData) {
        this.mockData = mockData;
    }

    // 时间线校验：topic_name 必填，granularity 可选且只能是 year 或 month。
    public ResponseEntity<Map<String, Object>> validateTimeline(Map<String, Object> request) {
        if (isBlank(request.get("topic_name"))) {
            return missing("topic_name");
        }
        if (!(request.get("topic_name") instanceof String topicName)) {
            return valueError("topic_name", "topic_name must be a string");
        }
        if (!mockData.hasTopicName(topicName.trim())) {
            return valueError("topic_name", "topic_name is not supported");
        }
        if (request.get("granularity") != null) {
            if (!(request.get("granularity") instanceof String granularity) || granularity.trim().isEmpty()) {
                return valueError("granularity", "granularity must be year or month");
            }
            if (!GRANULARITIES.contains(granularity.trim())) {
                return valueError("granularity", "granularity must be year or month");
            }
        }
        return null;
    }

    // 主题选择器校验：topic_id 和 topic_name 二选一；两个都传时必须匹配同一主题。
    public ResponseEntity<Map<String, Object>> validateTopicSelector(Map<String, Object> request) {
        return validateTopicIdOrName(request);
    }

    public ResponseEntity<Map<String, Object>> validateTopicSelectorWithPage(Map<String, Object> request) {
        ResponseEntity<Map<String, Object>> selectorError = validateTopicIdOrName(request);
        return selectorError == null ? validatePage(request) : selectorError;
    }

    // 文档反向溯源校验：document_id 必填，并且必须存在于 Mock 数据中。
    public ResponseEntity<Map<String, Object>> validateSourceTopics(Map<String, Object> request) {
        if (isBlank(request.get("document_id"))) {
            return missing("document_id");
        }
        if (!(request.get("document_id") instanceof String documentId)) {
            return valueError("document_id", "document_id must be a string");
        }
        if (!mockData.hasDocumentId(documentId.trim())) {
            return valueError("document_id", "document_id is not supported");
        }
        return validatePage(request);
    }

    private ResponseEntity<Map<String, Object>> validateTopicIdOrName(Map<String, Object> request) {
        // topic_id/topic_name 二选一；两个都传时必须指向同一个 Mock 主题。
        boolean hasTopicId = !isBlank(request.get("topic_id"));
        boolean hasTopicName = !isBlank(request.get("topic_name"));
        if (!hasTopicId && !hasTopicName) {
            return missing("topic_id");
        }

        String topicId = null;
        String topicName = null;
        if (hasTopicId) {
            if (!(request.get("topic_id") instanceof String text)) {
                return valueError("topic_id", "topic_id must be a string");
            }
            topicId = text.trim();
            if (!mockData.hasTopicId(topicId)) {
                return valueError("topic_id", "topic_id is not supported");
            }
        }
        if (hasTopicName) {
            if (!(request.get("topic_name") instanceof String text)) {
                return valueError("topic_name", "topic_name must be a string");
            }
            topicName = text.trim();
            if (!mockData.hasTopicName(topicName)) {
                return valueError("topic_name", "topic_name is not supported");
            }
        }
        if (topicId != null && topicName != null && !mockData.topicMatches(topicId, topicName)) {
            return valueError("topic_name", "topic_id and topic_name do not match");
        }
        return null;
    }

    private ResponseEntity<Map<String, Object>> validatePage(Map<String, Object> request) {
        if (!isOptionalPositiveInt(request.get("page_no"))) {
            return valueError("page_no", "page_no must be a positive integer");
        }
        if (!isOptionalPageSize(request.get("page_size"))) {
            return valueError("page_size", "page_size must be a positive integer no greater than 100");
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
        errorItem.put("loc", List.of("query", field));
        errorItem.put("msg", message);
        errorItem.put("type", type);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("detail", List.of(errorItem));
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }

    private boolean isBlank(Object value) {
        return !(value instanceof String text) || text.trim().isEmpty();
    }

    private boolean isOptionalPositiveInt(Object value) {
        return value == null || positiveInt(value) != null;
    }

    private boolean isOptionalPageSize(Object value) {
        Integer pageSize = positiveInt(value);
        return value == null || (pageSize != null && pageSize <= 100);
    }

    private Integer positiveInt(Object value) {
        if (!(value instanceof String text)) {
            return null;
        }

        String normalized = text.trim();
        if (!normalized.matches("\\d+")) {
            return null;
        }

        try {
            long parsed = Long.parseLong(normalized);
            if (parsed <= 0 || parsed > Integer.MAX_VALUE) {
                return null;
            }
            return (int) parsed;
        } catch (NumberFormatException ignored) {
            return null;
        }
    }
}
