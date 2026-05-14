package com.example.headline.validator;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class AppHeadlineValidator {

    private static final Set<String> CLUE_IDS = Set.of("clue_001", "clue_002");
    private static final Set<String> DOCUMENT_TYPES = Set.of("article", "patent", "conference", "thesis", "report", "wos", "news", "unknown");
    private static final Set<String> SEARCH_FIELDS = Set.of("themes", "categories", "title", "keywords", "abstract", "authors", "affiliations", "source", "fulltext");
    private static final Set<String> MATCH_MODES = Set.of("fuzzy", "exact");
    private static final Set<String> COMBINE_MODES = Set.of("AND", "OR");
    private static final Set<String> TARGET_TYPES = Set.of("document", "chunk");
    private static final Set<String> SORT_BY = Set.of("default", "publication_date");
    private static final Set<String> SORT_ORDER = Set.of("desc", "asc");

    // 主题线索参数校验：topic 必填，筛选条件和时间范围必须合法。
    public ResponseEntity<Map<String, Object>> validateTopicClues(Map<String, Object> request) {
        if (isBlank(request.get("topic"))) {
            return missing("topic");
        }
        if (request.get("doc_types") != null && !isStringListIn(request.get("doc_types"), DOCUMENT_TYPES)) {
            return valueError("doc_types", "doc_types must be an array using allowed document types");
        }
        if (request.get("countries") != null && !isStringList(request.get("countries"))) {
            return valueError("countries", "countries must be an array of strings");
        }
        ResponseEntity<Map<String, Object>> dateError = validateDateRange(request);
        if (dateError != null) {
            return dateError;
        }
        return validatePage(request);
    }

    // 高级筛选参数校验：conditions 必须存在、必须是非空数组，内部字段也要逐项校验。
    public ResponseEntity<Map<String, Object>> validateFilterClues(Map<String, Object> request) {
        if (!request.containsKey("conditions") || request.get("conditions") == null) {
            return missing("conditions");
        }
        if (!(request.get("conditions") instanceof List<?> conditions)) {
            return valueError("conditions", "conditions must be an array");
        }
        if (conditions.isEmpty()) {
            return valueError("conditions", "conditions must not be empty");
        }
        for (int i = 0; i < conditions.size(); i++) {
            Object condition = conditions.get(i);
            if (!(condition instanceof Map<?, ?> conditionMap)) {
                return valueError("conditions", "each condition must be an object");
            }
            if (!isIn(conditionMap.get("field"), SEARCH_FIELDS)) {
                return valueError("conditions." + i + ".field", "field must be one of " + SEARCH_FIELDS);
            }
            if (isBlank(conditionMap.get("keyword"))) {
                return missing("conditions." + i + ".keyword");
            }
            if (!isOptionalIn(conditionMap.get("match_mode"), MATCH_MODES)) {
                return valueError("conditions." + i + ".match_mode", "match_mode must be fuzzy or exact");
            }
            if (!isOptionalIn(conditionMap.get("combine_mode"), COMBINE_MODES)) {
                return valueError("conditions." + i + ".combine_mode", "combine_mode must be AND or OR");
            }
        }
        if (!isOptionalIn(request.get("target_type"), TARGET_TYPES)) {
            return valueError("target_type", "target_type must be document or chunk");
        }
        if (!isOptionalIn(request.get("sort_by"), SORT_BY)) {
            return valueError("sort_by", "sort_by must be default or publication_date");
        }
        if (!isOptionalIn(request.get("sort_order"), SORT_ORDER)) {
            return valueError("sort_order", "sort_order must be desc or asc");
        }
        return validatePage(request);
    }

    // 线索详情校验：只允许访问当前白名单中的 clue_id。
    public ResponseEntity<Map<String, Object>> validateClueDetail(Map<String, Object> request) {
        if (isBlank(request.get("clue_id"))) {
            return missing("clue_id");
        }
        if (!isIn(request.get("clue_id"), CLUE_IDS)) {
            return valueError("clue_id", "clue_id must be one of " + CLUE_IDS);
        }
        return null;
    }

    // 时间范围校验：日期格式为 YYYY-MM-DD，且 end_date 不能早于 start_date。
    private ResponseEntity<Map<String, Object>> validateDateRange(Map<String, Object> request) {
        LocalDate startDate = parseDate(request.get("start_date"));
        LocalDate endDate = parseDate(request.get("end_date"));
        if (request.get("start_date") != null && startDate == null) {
            return valueError("start_date", "start_date must use YYYY-MM-DD format");
        }
        if (request.get("end_date") != null && endDate == null) {
            return valueError("end_date", "end_date must use YYYY-MM-DD format");
        }
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            return valueError("end_date", "end_date must be greater than or equal to start_date");
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

    private ResponseEntity<Map<String, Object>> missing(String fieldName) {
        return error(fieldName, "Field required", "missing");
    }

    private ResponseEntity<Map<String, Object>> valueError(String fieldName, String message) {
        return error(fieldName, message, "value_error");
    }

    private ResponseEntity<Map<String, Object>> error(String fieldName, String message, String type) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(Map.of(
                "detail", List.of(Map.of("loc", List.of("body", fieldName), "msg", message, "type", type))
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

    private boolean isStringList(Object value) {
        if (!(value instanceof List<?> values)) {
            return false;
        }
        for (Object item : values) {
            if (!(item instanceof String) || isBlank(item)) {
                return false;
            }
        }
        return true;
    }

    private boolean isStringListIn(Object value, Set<String> allowed) {
        if (!(value instanceof List<?> values)) {
            return false;
        }
        for (Object item : values) {
            if (!isIn(item, allowed)) {
                return false;
            }
        }
        return true;
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

    private LocalDate parseDate(Object value) {
        if (value == null || value.toString().trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(value.toString().trim());
        } catch (DateTimeParseException ignored) {
            return null;
        }
    }
}
