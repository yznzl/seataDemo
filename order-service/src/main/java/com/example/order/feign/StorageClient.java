package com.example.order.feign;

import com.example.order.config.SeataFeignConfig;
import com.example.order.fallback.StorageClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/** 库存服务 Feign 客户端。 */
@FeignClient(
        name = "storage-service",
        url = "${storage.service.url:http://127.0.0.1:8082}",
        configuration = SeataFeignConfig.class,
        fallback = StorageClientFallback.class)
public interface StorageClient {
    /**
     * 扣减库存。
     *
     * @param count 扣减数量
     * @return 扣减结果
     */
    @PostMapping("/storage/deduct")
    Map<String, Object> deduct(@RequestParam("count") int count);

    /**
     * 查询库存状态。
     *
     * @return 库存结果
     */
    @GetMapping("/storage/state")
    Map<String, Object> state();
}
