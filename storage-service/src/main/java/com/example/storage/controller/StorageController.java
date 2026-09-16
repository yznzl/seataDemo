package com.example.storage.controller;

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

    /**
     * 扣减库存。
     *
     * @param count 扣减数量
     * @return 扣减结果或错误信息
     */
    @PostMapping("/deduct")
    public ResponseEntity<Map<String, Object>> deduct(@RequestParam("count") int count) {
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

    private Map<String, Object> errorResponse(String code, String message) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", code);
        result.put("error", message);
        return result;
    }
}
