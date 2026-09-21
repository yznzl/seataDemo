package com.example.order.fallback;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 订单接口的 Sentinel 降级和流控处理器。
 */
public class SentinelFallback {

    /**
     * 处理业务异常导致的降级。
     *
     * @param decisionNumber 请求中的判定数字
     * @param exception 业务异常
     * @return 降级响应
     */
    public static ResponseEntity<Map<String, Object>> fallback(
            Integer decisionNumber, Throwable exception) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", "SENTINEL_FALLBACK");
        result.put("message", "demoRun 请求被降级");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    /**
     * 处理 Sentinel 流控拒绝。
     *
     * @param decisionNumber 请求中的判定数字
     * @param exception Sentinel 流控异常
     * @return 流控响应
     */
    public static ResponseEntity<Map<String, Object>> blockHandler(
            Integer decisionNumber, BlockException exception) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", "SENTINEL_FLOW_LIMIT");
        result.put("message", "demoRun 请求过于频繁，请稍后重试");
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(result);
    }
}
