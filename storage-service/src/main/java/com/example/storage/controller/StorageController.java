package com.example.storage.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.example.storage.service.StorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/** 库存 HTTP 接口。 */
@RestController
@RequestMapping("/storage")
public class StorageController {
    private final StorageService storageService;

    /**
     * 创建库存控制器。
     *
     * @param storageService 库存业务服务
     */
    public StorageController(StorageService storageService) { this.storageService = storageService; }

    private static final AtomicInteger SLOW_CALL_COUNTER = new AtomicInteger(0);

    /**
     * 扣减库存。
     *
     * @param count 扣减数量
     * @return 扣减结果或错误信息
     */
    @PostMapping("/deduct")
    @SentinelResource(
            value = "deduct"
    )
    public ResponseEntity<Map<String, Object>> deduct(@RequestParam("count") int count) {
        applyArtificialDelay();
        try {
            return ResponseEntity.ok(storageService.deduct(count));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(errorResponse("INVALID_REQUEST", exception.getMessage()));
        } catch (RuntimeException exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse("STORAGE_ERROR", exception.getMessage()));
        }
    }

    /**
     * 查询库存状态。
     *
     * @return 当前库存
     */
    @GetMapping("/state")
    public Map<String, Object> state() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("stock", storageService.stock());
        return result;
    }

    /**
     * 前 5 次请求人工延迟 1s，用于演示 Sentinel 慢调用比例熔断。
     * 放在本地事务之外，避免延迟期间持有 InnoDB 行锁和 Seata 全局锁。
     */
    private void applyArtificialDelay() {
        int currentCount = SLOW_CALL_COUNTER.incrementAndGet();
        if (currentCount <= 5) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } else if (currentCount > 6) {
            SLOW_CALL_COUNTER.set(0);
        }
    }

    private Map<String, Object> errorResponse(String code, String message) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", code);
        result.put("error", message);
        return result;
    }
}
