package com.example.headline.validator;

import com.example.headline.mock.AppResearchMockData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class AppResearchValidator {

    private static final Set<String> CITATION_TYPES = Set.of("reference", "secondary_reference", "co_citation", "cited_by", "secondary_cited_by", "co_cited");
    private static final Set<String> GRANULARITIES = Set.of("year", "quarter");
    private static final Set<String> NODE_SIZE_METRICS = Set.of("PAPER_COUNT", "CITATION", "PATENT_COUNT");
    private static final Set<String> TARGET_TYPES = Set.of("author", "institution");
    private static final Set<String> EXPORT_FORMATS = Set.of("word", "pdf", "excel");

    private final AppResearchMockData mockData;

    public AppResearchValidator(AppResearchMockData mockData) {
        this.mockData = mockData;
    }

    public ResponseEntity<Map<String, Object>> validateDocumentIdQuery(Map<String, Object> request) {
        ResponseEntity<Map<String, Object>> error = validateQueryDocumentId(request);
        return error;
    }

    public ResponseEntity<Map<String, Object>> validateCitationList(Map<String, Object> request) {
        ResponseEntity<Map<String, Object>> documentError = validateQueryDocumentId(request);
        if (documentError != null) {
            return documentError;
        }
        ResponseEntity<Map<String, Object>> citationError = validateRequiredQueryString(request, "citation_type");
        if (citationError != null) {
            return citationError;
        }
        String citationType = ((String) request.get("citation_type")).trim();
        if (!mockData.hasCitationType(citationType)) {
            return valueError("citation_type", "citation_type is not supported", "query");
        }
        return validateQueryPage(request);
    }

    public ResponseEntity<Map<String, Object>> validateAuthorCollab(Map<String, Object> request) {
        ResponseEntity<Map<String, Object>> idsError = validateRequiredStringList(request, "author_ids", "body");
        if (idsError != null) {
            return idsError;
        }
        for (Object authorId : (List<?>) request.get("author_ids")) {
            if (!mockData.hasAuthorId(authorId.toString().trim())) {
                return valueError("author_ids", "author_ids contains unsupported author id", "body");
            }
        }
        return validateHops(request);
    }

    public ResponseEntity<Map<String, Object>> validateInstitutionCollab(Map<String, Object> request) {
        ResponseEntity<Map<String, Object>> idsError = validateRequiredStringList(request, "institution_ids", "body");
        if (idsError != null) {
            return idsError;
        }
        for (Object institutionId : (List<?>) request.get("institution_ids")) {
            if (!mockData.hasInstitutionId(institutionId.toString().trim())) {
                return valueError("institution_ids", "institution_ids contains unsupported institution id", "body");
            }
        }
        return validateHops(request);
    }

    public ResponseEntity<Map<String, Object>> validateTechEvolution(Map<String, Object> request) {
        if (request.get("keyword") != null && !isNonBlankString(request.get("keyword"))) {
            return valueError("keyword", "keyword must be a non-empty string", "body");
        }
        if (request.get("ipc_code") != null && !isNonBlankString(request.get("ipc_code"))) {
            return valueError("ipc_code", "ipc_code must be a non-empty string", "body");
        }
        if (!isOptionalIn(request.get("granularity"), GRANULARITIES)) {
            return valueError("granularity", "granularity must be year or quarter", "body");
        }
        if (request.get("max_nodes") != null) {
            Integer maxNodes = positiveBodyInt(request.get("max_nodes"));
            if (maxNodes == null || maxNodes > 500) {
                return valueError("max_nodes", "max_nodes must be a JSON integer from 1 to 500", "body");
            }
        }
        if (!isOptionalIn(request.get("node_size_metric"), NODE_SIZE_METRICS)) {
            return valueError("node_size_metric", "node_size_metric must be PAPER_COUNT, CITATION, or PATENT_COUNT", "body");
        }
        return validateDateRange(request, "body");
    }

    public ResponseEntity<Map<String, Object>> validateCitationTrend(Map<String, Object> request) {
        ResponseEntity<Map<String, Object>> idsError = validateRequiredStringList(request, "document_ids", "body");
        if (idsError != null) {
            return idsError;
        }
        for (Object documentId : (List<?>) request.get("document_ids")) {
            if (!mockData.hasDocumentId(documentId.toString().trim())) {
                return valueError("document_ids", "document_ids contains unsupported document id", "body");
            }
        }
        if (!isOptionalIn(request.get("granularity"), GRANULARITIES)) {
            return valueError("granularity", "granularity must be year or quarter", "body");
        }
        return null;
    }

    public ResponseEntity<Map<String, Object>> validateCollabStrength(Map<String, Object> request) {
        ResponseEntity<Map<String, Object>> targetError = validateRequiredBodyString(request, "target_type");
        if (targetError != null) {
            return targetError;
        }
        String targetType = ((String) request.get("target_type")).trim();
        if (!TARGET_TYPES.contains(targetType)) {
            return valueError("target_type", "target_type must be author or institution", "body");
        }
        if (request.get("target_ids") != null) {
            ResponseEntity<Map<String, Object>> idsError = validateOptionalStringList(request, "target_ids", "body");
            if (idsError != null) {
                return idsError;
            }
        }
        if (request.get("tech_keyword") != null && !isNonBlankString(request.get("tech_keyword"))) {
            return valueError("tech_keyword", "tech_keyword must be a non-empty string", "body");
        }
        return validateDateRange(request, "body");
    }

    public ResponseEntity<Map<String, Object>> validateBriefList(Map<String, Object> request) {
        ResponseEntity<Map<String, Object>> idsError = validateRequiredStringList(request, "document_ids", "body");
        if (idsError != null) {
            return idsError;
        }
        for (Object documentId : (List<?>) request.get("document_ids")) {
            if (!mockData.hasDocumentId(documentId.toString().trim())) {
                return valueError("document_ids", "document_ids contains unsupported document id", "body");
            }
        }
        return null;
    }

    public ResponseEntity<Map<String, Object>> validateExport(Map<String, Object> request) {
        ResponseEntity<Map<String, Object>> idsError = validateBriefList(request);
        if (idsError != null) {
            return idsError;
        }
        ResponseEntity<Map<String, Object>> formatError = validateRequiredBodyString(request, "export_format");
        if (formatError != null) {
            return formatError;
        }
        String exportFormat = ((String) request.get("export_format")).trim();
        if (!EXPORT_FORMATS.contains(exportFormat)) {
            return valueError("export_format", "export_format must be word, pdf, or excel", "body");
        }
        return null;
    }

    private ResponseEntity<Map<String, Object>> validateQueryDocumentId(Map<String, Object> request) {
        ResponseEntity<Map<String, Object>> error = validateRequiredQueryString(request, "document_id");
        if (error != null) {
            return error;
        }
        String documentId = ((String) request.get("document_id")).trim();
        if (!mockData.hasDocumentId(documentId)) {
            return valueError("document_id", "document_id is not supported", "query");
        }
        return null;
    }

    private ResponseEntity<Map<String, Object>> validateRequiredQueryString(Map<String, Object> request, String field) {
        Object value = request.get(field);
        if (value == null) {
            return missing(field, "query");
        }
        if (!isNonBlankString(value)) {
            return valueError(field, field + " must be a non-empty string", "query");
        }
        return null;
    }

    private ResponseEntity<Map<String, Object>> validateRequiredBodyString(Map<String, Object> request, String field) {
        Object value = request.get(field);
        if (value == null) {
            return missing(field, "body");
        }
        if (!isNonBlankString(value)) {
            return valueError(field, field + " must be a non-empty string", "body");
        }
        return null;
    }

    private ResponseEntity<Map<String, Object>> validateRequiredStringList(Map<String, Object> request, String field, String location) {
        Object value = request.get(field);
        if (value == null) {
            return missing(field, location);
        }
        if (!(value instanceof List<?> values)) {
            return valueError(field, field + " must be an array", location);
        }
        if (values.isEmpty()) {
            return valueError(field, field + " must not be empty", location);
        }
        return validateStringListItems(values, field, location);
    }

    private ResponseEntity<Map<String, Object>> validateOptionalStringList(Map<String, Object> request, String field, String location) {
        Object value = request.get(field);
        if (!(value instanceof List<?> values)) {
            return valueError(field, field + " must be an array", location);
        }
        return validateStringListItems(values, field, location);
    }

    private ResponseEntity<Map<String, Object>> validateStringListItems(List<?> values, String field, String location) {
        for (Object item : values) {
            if (!isNonBlankString(item)) {
                return valueError(field, field + " must contain only non-empty strings", location);
            }
        }
        return null;
    }

    private ResponseEntity<Map<String, Object>> validateHops(Map<String, Object> request) {
        if (request.get("hops") == null) {
            return null;
        }
        Integer hops = positiveBodyInt(request.get("hops"));
        if (hops == null || hops > 3) {
            return valueError("hops", "hops must be a JSON integer from 1 to 3", "body");
        }
        return null;
    }

    private ResponseEntity<Map<String, Object>> validateQueryPage(Map<String, Object> request) {
        // GET 查询参数进入 Controller 后是字符串，这里按十进制正整数字符串严格校验。
        if (request.get("page_no") != null && positiveQueryInt(request.get("page_no")) == null) {
            return valueError("page_no", "page_no must be a positive integer", "query");
        }
        Integer pageSize = positiveQueryInt(request.get("page_size"));
        if (request.get("page_size") != null && (pageSize == null || pageSize > 100)) {
            return valueError("page_size", "page_size must be a positive integer no greater than 100", "query");
        }
        return null;
    }

    private ResponseEntity<Map<String, Object>> validateDateRange(Map<String, Object> request, String location) {
        LocalDate startDate = parseDate(request.get("start_date"));
        LocalDate endDate = parseDate(request.get("end_date"));
        if (request.get("start_date") != null && startDate == null) {
            return valueError("start_date", "start_date must use YYYY-MM-DD format", location);
        }
        if (request.get("end_date") != null && endDate == null) {
            return valueError("end_date", "end_date must use YYYY-MM-DD format", location);
        }
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            return valueError("end_date", "end_date must be greater than or equal to start_date", location);
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
        Map<String, Object> errorItem = new LinkedHashMap<>();
        errorItem.put("loc", List.of(location, field));
        errorItem.put("msg", message);
        errorItem.put("type", type);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("detail", List.of(errorItem));
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }

    private boolean isBlank(Object value) {
        return !(value instanceof String text) || text.trim().isEmpty();
    }

    private boolean isNonBlankString(Object value) {
        return value instanceof String text && !text.trim().isEmpty();
    }

    private boolean isOptionalIn(Object value, Set<String> allowed) {
        return value == null || value instanceof String text && allowed.contains(text.trim());
    }

    private Integer positiveQueryInt(Object value) {
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

    private Integer positiveBodyInt(Object value) {
        // POST JSON body 中的 integer 只接受整数类型，不接受 "2" 或 2.5。
        if (!(value instanceof Byte || value instanceof Short || value instanceof Integer || value instanceof Long)) {
            return null;
        }
        long parsed = ((Number) value).longValue();
        if (parsed <= 0 || parsed > Integer.MAX_VALUE) {
            return null;
        }
        return (int) parsed;
    }

    private LocalDate parseDate(Object value) {
        if (value == null) {
            return null;
        }
        if (!(value instanceof String text) || text.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(text.trim());
        } catch (DateTimeParseException ignored) {
            return null;
        }
    }
}
