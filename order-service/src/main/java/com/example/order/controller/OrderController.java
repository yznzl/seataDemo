package com.example.order.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.example.order.fallback.SentinelFallback;
import com.example.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/** 订单 HTTP 接口。 */
@RestController
@RequestMapping("/demo")
public class OrderController {
    private final OrderService orderService;

    /**
     * 创建订单控制器。
     *
     * @param orderService 订单业务服务
     */
    public OrderController(OrderService orderService) { this.orderService = orderService; }

    /**
     * 执行订单和库存事务。
     *
     * @param decisionNumber 可选判定数字
     * @return 事务处理结果
     */
    @GetMapping("/run")
    @SentinelResource(
            value = "demoRun",
            fallbackClass = SentinelFallback.class,
            fallback = "fallback",
            blockHandlerClass = SentinelFallback.class,
            blockHandler = "blockHandler"
    )
    public ResponseEntity<Map<String, Object>> run(
            @RequestParam(value = "n", required = false) Integer decisionNumber) throws InterruptedException {
//        try {
            return ResponseEntity.ok(orderService.run(decisionNumber));
//        } catch (IllegalArgumentException exception) {
//            return ResponseEntity.badRequest().body(errorResponse("INVALID_REQUEST", exception.getMessage()));
//        } catch (RuntimeException exception) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(orderService.rollbackResponse(exception));
//        }
    }

    /**
     * 查询订单和库存状态。
     *
     * @return 当前状态
     */
    @GetMapping("/state")
    public Map<String, Object> state() { return orderService.state(); }

    private Map<String, Object> errorResponse(String code, String message) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", code);
        result.put("error", message);
        return result;
    }
}
