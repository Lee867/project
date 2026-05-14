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
public class AppResearchValidator {

    private static final Set<String> DOCUMENT_IDS = Set.of("research_001", "research_002", "research_003", "research_004");
    private static final Set<String> AUTHOR_IDS = Set.of("author_001", "author_002", "author_003");
    private static final Set<String> INSTITUTION_IDS = Set.of("inst_001", "inst_002", "inst_003");
    private static final Set<String> CITATION_TYPES = Set.of("reference", "secondary_reference", "co_citation", "cited_by", "secondary_cited_by", "co_cited");
    private static final Set<String> GRANULARITIES = Set.of("year", "quarter");
    private static final Set<String> NODE_SIZE_METRICS = Set.of("PAPER_COUNT", "CITATION", "PATENT_COUNT");
    private static final Set<String> TARGET_TYPES = Set.of("author", "institution");
    private static final Set<String> EXPORT_FORMATS = Set.of("word", "pdf", "excel");

    // GET 查询中的 document_id 校验：meta 和 citation_list 复用。
    public ResponseEntity<Map<String, Object>> validateDocumentIdQuery(Map<String, Object> request) {
        return validateRequiredAllowed(request, "document_id", DOCUMENT_IDS, "query", "document_id must be one of " + DOCUMENT_IDS);
    }

    // 引用列表校验：document_id、citation_type、分页参数都要合法。
    public ResponseEntity<Map<String, Object>> validateCitationList(Map<String, Object> request) {
        ResponseEntity<Map<String, Object>> documentError = validateDocumentIdQuery(request);
        if (documentError != null) {
            return documentError;
        }
        ResponseEntity<Map<String, Object>> citationError = validateRequiredAllowed(request, "citation_type", CITATION_TYPES, "query", "citation_type must be one of " + CITATION_TYPES);
        return citationError == null ? validatePage(request, "query") : citationError;
    }

    // 作者合作图谱校验：author_ids 必须是非空数组，hops 最大为 3。
    public ResponseEntity<Map<String, Object>> validateAuthorCollab(Map<String, Object> request) {
        ResponseEntity<Map<String, Object>> idsError = validateRequiredStringListIn(request, "author_ids", AUTHOR_IDS, "author_ids must be an array using allowed author ids");
        return idsError == null ? validateHops(request) : idsError;
    }

    // 机构合作图谱校验：institution_ids 必须是非空数组，hops 最大为 3。
    public ResponseEntity<Map<String, Object>> validateInstitutionCollab(Map<String, Object> request) {
        ResponseEntity<Map<String, Object>> idsError = validateRequiredStringListIn(request, "institution_ids", INSTITUTION_IDS, "institution_ids must be an array using allowed institution ids");
        return idsError == null ? validateHops(request) : idsError;
    }

    // 技术演化校验：keyword/ipc_code 至少一个，时间范围和图谱参数必须合法。
    public ResponseEntity<Map<String, Object>> validateTechEvolution(Map<String, Object> request) {
        if (isBlank(request.get("keyword")) && isBlank(request.get("ipc_code"))) {
            return missing("keyword", "body");
        }
        if (!isOptionalIn(request.get("granularity"), GRANULARITIES)) {
            return valueError("granularity", "granularity must be year or quarter", "body");
        }
        Integer maxNodes = positiveInt(request.get("max_nodes"));
        if (request.get("max_nodes") != null && maxNodes == null) {
            return valueError("max_nodes", "max_nodes must be a positive integer", "body");
        }
        if (maxNodes != null && maxNodes > 500) {
            return valueError("max_nodes", "max_nodes must be no greater than 500", "body");
        }
        if (!isOptionalIn(request.get("node_size_metric"), NODE_SIZE_METRICS)) {
            return valueError("node_size_metric", "node_size_metric must be one of " + NODE_SIZE_METRICS, "body");
        }
        return validateDateRange(request);
    }

    // 引用趋势校验：document_ids 必须是非空数组，granularity 只支持 year/quarter。
    public ResponseEntity<Map<String, Object>> validateCitationTrend(Map<String, Object> request) {
        ResponseEntity<Map<String, Object>> idsError = validateRequiredStringListIn(request, "document_ids", DOCUMENT_IDS, "document_ids must be an array using allowed document ids");
        if (idsError != null) {
            return idsError;
        }
        if (!isOptionalIn(request.get("granularity"), GRANULARITIES)) {
            return valueError("granularity", "granularity must be year or quarter", "body");
        }
        return null;
    }

    // 合作强度校验：target_type 必填，target_ids 可选但必须是数组。
    public ResponseEntity<Map<String, Object>> validateCollabStrength(Map<String, Object> request) {
        ResponseEntity<Map<String, Object>> targetError = validateRequiredAllowed(request, "target_type", TARGET_TYPES, "body", "target_type must be author or institution");
        if (targetError != null) {
            return targetError;
        }
        if (request.get("target_ids") != null && !(request.get("target_ids") instanceof List<?>)) {
            return valueError("target_ids", "target_ids must be an array", "body");
        }
        return validateDateRange(request);
    }

    public ResponseEntity<Map<String, Object>> validateBriefList(Map<String, Object> request) {
        return validateRequiredStringListIn(request, "document_ids", DOCUMENT_IDS, "document_ids must be an array using allowed document ids");
    }

    // 导出校验：document_ids 和 export_format 都必须合法。
    public ResponseEntity<Map<String, Object>> validateExport(Map<String, Object> request) {
        ResponseEntity<Map<String, Object>> idsError = validateBriefList(request);
        if (idsError != null) {
            return idsError;
        }
        return validateRequiredAllowed(request, "export_format", EXPORT_FORMATS, "body", "export_format must be word, pdf, or excel");
    }

    private ResponseEntity<Map<String, Object>> validateRequiredAllowed(Map<String, Object> request, String field, Set<String> allowed, String location, String message) {
        if (isBlank(request.get(field))) {
            return missing(field, location);
        }
        if (!isIn(request.get(field), allowed)) {
            return valueError(field, message, location);
        }
        return null;
    }

    private ResponseEntity<Map<String, Object>> validateRequiredStringListIn(Map<String, Object> request, String field, Set<String> allowed, String message) {
        Object value = request.get(field);
        if (value == null) {
            return missing(field, "body");
        }
        if (!(value instanceof List<?> values)) {
            return valueError(field, field + " must be an array", "body");
        }
        if (values.isEmpty()) {
            return valueError(field, field + " must not be empty", "body");
        }
        for (Object item : values) {
            if (!isIn(item, allowed)) {
                return valueError(field, message, "body");
            }
        }
        return null;
    }

    private ResponseEntity<Map<String, Object>> validateHops(Map<String, Object> request) {
        Integer hops = positiveInt(request.get("hops"));
        if (request.get("hops") != null && hops == null) {
            return valueError("hops", "hops must be a positive integer", "body");
        }
        if (hops != null && hops > 3) {
            return valueError("hops", "hops must be no greater than 3", "body");
        }
        return null;
    }

    private ResponseEntity<Map<String, Object>> validatePage(Map<String, Object> request, String location) {
        if (request.get("page_no") != null && positiveInt(request.get("page_no")) == null) {
            return valueError("page_no", "page_no must be a positive integer", location);
        }
        Integer pageSize = positiveInt(request.get("page_size"));
        if (request.get("page_size") != null && pageSize == null) {
            return valueError("page_size", "page_size must be a positive integer no greater than 100", location);
        }
        if (pageSize != null && pageSize > 100) {
            return valueError("page_size", "page_size must be a positive integer no greater than 100", location);
        }
        return null;
    }

    // 时间范围校验：用于技术演化和合作强度分析。
    private ResponseEntity<Map<String, Object>> validateDateRange(Map<String, Object> request) {
        LocalDate startDate = parseDate(request.get("start_date"));
        LocalDate endDate = parseDate(request.get("end_date"));
        if (request.get("start_date") != null && startDate == null) {
            return valueError("start_date", "start_date must use YYYY-MM-DD format", "body");
        }
        if (request.get("end_date") != null && endDate == null) {
            return valueError("end_date", "end_date must use YYYY-MM-DD format", "body");
        }
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            return valueError("end_date", "end_date must be greater than or equal to start_date", "body");
        }
        return null;
    }

    private ResponseEntity<Map<String, Object>> missing(String field, String location) {
        return error(field, "Field required", "missing", location);
    }

    private ResponseEntity<Map<String, Object>> valueError(String field, String message, String location) {
        return error(field, message, "value_error", location);
    }

    private ResponseEntity<Map<String, Object>> error(String field, String message, String type, String location) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(Map.of(
                "detail", List.of(Map.of("loc", List.of(location, field), "msg", message, "type", type))
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

    private Integer positiveInt(Object value) {
        if (value == null) {
            return null;
        }
        try {
            int parsed = Integer.parseInt(value.toString().trim());
            return parsed > 0 ? parsed : null;
        } catch (NumberFormatException ignored) {
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
