package com.example.headline.validator;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class AppEventValidator {

    private static final Set<String> TOPIC_IDS = Set.of("topic_001", "topic_002", "topic_003");
    private static final Set<String> TOPIC_NAMES = Set.of("AI infrastructure trend", "Low-altitude economy development", "Advanced semiconductor supply chain");
    private static final Set<String> DOCUMENT_IDS = Set.of("doc_001", "doc_002", "doc_003", "doc_004");
    private static final Set<String> GRANULARITIES = Set.of("year", "month");

    // 时间线校验：topic_name 必填，granularity 只能是 year 或 month。
    public ResponseEntity<Map<String, Object>> validateTimeline(Map<String, Object> request) {
        if (isBlank(request.get("topic_name"))) {
            return missing("topic_name");
        }
        if (!isIn(request.get("topic_name"), TOPIC_NAMES)) {
            return valueError("topic_name", "topic_name must be one of " + TOPIC_NAMES);
        }
        if (!isOptionalIn(request.get("granularity"), GRANULARITIES)) {
            return valueError("granularity", "granularity must be year or month");
        }
        return null;
    }

    // 主题选择器校验：topic_id 和 topic_name 二选一即可。
    public ResponseEntity<Map<String, Object>> validateTopicSelector(Map<String, Object> request) {
        return validateTopicIdOrName(request);
    }

    public ResponseEntity<Map<String, Object>> validateTopicSelectorWithPage(Map<String, Object> request) {
        ResponseEntity<Map<String, Object>> selectorError = validateTopicIdOrName(request);
        return selectorError == null ? validatePage(request) : selectorError;
    }

    // 文档反向溯源校验：document_id 必填，并且必须是白名单中的文档。
    public ResponseEntity<Map<String, Object>> validateSourceTopics(Map<String, Object> request) {
        if (isBlank(request.get("document_id"))) {
            return missing("document_id");
        }
        if (!isIn(request.get("document_id"), DOCUMENT_IDS)) {
            return valueError("document_id", "document_id must be one of " + DOCUMENT_IDS);
        }
        return validatePage(request);
    }

    // topic_id/topic_name 至少传一个；如果两个都传，则两个都必须合法。
    private ResponseEntity<Map<String, Object>> validateTopicIdOrName(Map<String, Object> request) {
        boolean hasTopicId = !isBlank(request.get("topic_id"));
        boolean hasTopicName = !isBlank(request.get("topic_name"));
        if (!hasTopicId && !hasTopicName) {
            return missing("topic_id");
        }
        if (hasTopicId && !isIn(request.get("topic_id"), TOPIC_IDS)) {
            return valueError("topic_id", "topic_id must be one of " + TOPIC_IDS);
        }
        if (hasTopicName && !isIn(request.get("topic_name"), TOPIC_NAMES)) {
            return valueError("topic_name", "topic_name must be one of " + TOPIC_NAMES);
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
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(Map.of(
                "detail", List.of(Map.of("loc", List.of("query", field), "msg", message, "type", type))
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

    private boolean isOptionalPositiveInt(Object value) {
        return value == null || positiveInt(value) != null;
    }

    private boolean isOptionalPageSize(Object value) {
        Integer pageSize = positiveInt(value);
        return value == null || (pageSize != null && pageSize <= 100);
    }

    private Integer positiveInt(Object value) {
        try {
            int parsed = Integer.parseInt(value.toString().trim());
            return parsed > 0 ? parsed : null;
        } catch (Exception ignored) {
            return null;
        }
    }
}
