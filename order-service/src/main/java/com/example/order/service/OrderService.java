package com.example.order.service;

import com.example.order.entity.OrderDO;
import com.example.order.feign.StorageClient;
import com.example.order.mapper.OrderMapper;
import io.seata.spring.annotation.GlobalTransactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/** 订单业务服务。 */
@Service
public class OrderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);
    private static final int RANDOM_NUMBER_BOUND = 100;
    private static final int MIN_DECISION_NUMBER = 0;
    private static final int DEDUCT_COUNT = 1;
    private static final String STATUS_CREATED = "CREATED";
    private static final String STATUS_SUCCESS = "SUCCESS";
    private static final String COMMIT_RESULT = "COMMIT";
    private static final String ROLLBACK_RESULT = "ROLLBACK";
    private static final String ODD_NUMBER_ERROR = "odd number triggers rollback";

    private final OrderMapper orderMapper;
    private final StorageClient storageClient;

    /**
     * 创建订单业务服务。
     *
     * @param orderMapper 订单数据访问接口
     * @param storageClient 库存服务客户端
     */
    public OrderService(OrderMapper orderMapper, StorageClient storageClient) {
        this.orderMapper = orderMapper;
        this.storageClient = storageClient;
    }

    /**
     * 在 Seata 全局事务中创建订单并扣减库存。
     *
     * @param decisionNumber 判定数字，不传时使用随机数
     * @return 事务处理结果
     */
    @GlobalTransactional(name = "order-storage-global", rollbackFor = Exception.class)
    public Map<String, Object> run(Integer decisionNumber) {
        validateDecisionNumber(decisionNumber);
        int number = decisionNumber == null
                ? ThreadLocalRandom.current().nextInt(RANDOM_NUMBER_BOUND) : decisionNumber;
        try {
            OrderDO order = new OrderDO();
            order.setDecisionNumber(number);
            order.setStatus(STATUS_CREATED);
            orderMapper.insert(order);
            storageClient.deduct(1);
            if ((number & 1) == 1) {
                throw new IllegalStateException(ODD_NUMBER_ERROR);
            }
            order.setStatus(STATUS_SUCCESS);
            orderMapper.updateById(order);
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("number", number);
            result.put("expected", COMMIT_RESULT);
            result.put("orderId", order.getId());
            return result;
        } catch (RuntimeException exception) {
            LOGGER.error("Order transaction failed, decisionNumber={}", number, exception);
            throw exception;
        }
    }

    /**
     * 查询订单和库存状态。
     *
     * @return 当前状态
     */
    public Map<String, Object> state() {
        try {
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("orders", orderMapper.selectCount(null));
            result.put("storage", storageClient.state());
            return result;
        } catch (RuntimeException exception) {
            LOGGER.error("Failed to query order state", exception);
            throw exception;
        }
    }

    /**
     * 组装事务回滚响应。
     *
     * @param exception 事务异常
     * @return 回滚响应
     */
    public Map<String, Object> rollbackResponse(Exception exception) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("expected", ROLLBACK_RESULT);
        result.put("error", exception.getMessage());
        result.put("stateAfterRollback", state());
        return result;
    }

    private void validateDecisionNumber(Integer decisionNumber) {
        if (decisionNumber != null && decisionNumber < MIN_DECISION_NUMBER) {
            throw new IllegalArgumentException("n must be greater than or equal to 0");
        }
    }
}
