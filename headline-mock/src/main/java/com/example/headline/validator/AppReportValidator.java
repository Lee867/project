package com.example.headline.validator;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class AppReportValidator {

    private static final Set<String> DOCUMENT_IDS = Set.of(
            "report_001", "report_002", "report_003", "report_004", "report_005", "report_006", "report_007",
            "report_week_001", "report_week_002", "report_week_003",
            "report_month_001", "report_month_002", "report_month_003",
            "report_year_001", "report_year_002", "report_year_003"
    );
    private static final Set<String> USER_IDS = Set.of("user_001", "user_002");
    private static final Set<String> VERSION_IDS = Set.of("report_001_v1", "report_001_v2", "report_001_v3", "report_003_v2");
    private static final Set<String> GRANULARITIES = Set.of("week", "month", "year");
    private static final Set<String> SORT_BY = Set.of("hotness", "citation");

    // 通用报告 ID 校验：meta、file_url、versions 都复用这里。
    public ResponseEntity<Map<String, Object>> validateDocumentId(Map<String, Object> request) {
        return validateRequiredAllowed(request, "document_id", DOCUMENT_IDS, "document_id must be one of mock report ids");
    }

    // 排行榜校验：granularity 必填，sort_by 和分页参数必须合法。
    public ResponseEntity<Map<String, Object>> validateRank(Map<String, Object> request) {
        if (isBlank(request.get("granularity"))) {
            return missing("granularity");
        }
        if (!isIn(request.get("granularity"), GRANULARITIES)) {
            return valueError("granularity", "granularity must be week, month, or year");
        }
        if (!isOptionalIn(request.get("sort_by"), SORT_BY)) {
            return valueError("sort_by", "sort_by must be hotness or citation");
        }
        return validatePage(request);
    }

    // 用户 ID 校验：用于推荐和更新提醒接口。
    public ResponseEntity<Map<String, Object>> validateUserId(Map<String, Object> request) {
        return validateRequiredAllowed(request, "user_id", USER_IDS, "user_id must be one of " + USER_IDS);
    }

    public ResponseEntity<Map<String, Object>> validateUserWithPage(Map<String, Object> request) {
        ResponseEntity<Map<String, Object>> userError = validateUserId(request);
        return userError == null ? validatePage(request) : userError;
    }

    // 版本对比校验：两个版本都必须存在，且不能是同一个版本。
    public ResponseEntity<Map<String, Object>> validateVersionDiff(Map<String, Object> request) {
        if (isBlank(request.get("version_id_a"))) {
            return missing("version_id_a");
        }
        if (isBlank(request.get("version_id_b"))) {
            return missing("version_id_b");
        }
        if (!isIn(request.get("version_id_a"), VERSION_IDS)) {
            return valueError("version_id_a", "version_id_a must be one of " + VERSION_IDS);
        }
        if (!isIn(request.get("version_id_b"), VERSION_IDS)) {
            return valueError("version_id_b", "version_id_b must be one of " + VERSION_IDS);
        }
        if (request.get("version_id_a").equals(request.get("version_id_b"))) {
            return valueError("version_id_b", "version_id_b must be different from version_id_a");
        }
        return null;
    }

    // 必填字段和白名单合法值的通用校验，减少 document_id、user_id 等重复逻辑。
    private ResponseEntity<Map<String, Object>> validateRequiredAllowed(Map<String, Object> request, String field, Set<String> allowed, String message) {
        if (isBlank(request.get(field))) {
            return missing(field);
        }
        if (!isIn(request.get(field), allowed)) {
            return valueError(field, message);
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
