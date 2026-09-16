package com.example.storage.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.storage.entity.StorageDO;
import com.example.storage.mapper.StorageMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

/** 库存业务服务。 */
@Service
public class StorageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(StorageService.class);
    private static final long STOCK_RECORD_ID = 1L;
    private static final int MIN_DEDUCT_COUNT = 1;
    private static final String INSUFFICIENT_STOCK_ERROR = "insufficient stock";

    private final StorageMapper storageMapper;

    /**
     * 创建库存业务服务。
     *
     * @param storageMapper 库存数据访问接口
     */
    public StorageService(StorageMapper storageMapper) { this.storageMapper = storageMapper; }

    /**
     * 在当前 Seata 分支事务中扣减库存。
     *
     * @param count 扣减数量
     * @return 扣减结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> deduct(int count) {
        validateCount(count);
        try {
            if (storageMapper.deduct(count) != 1) {
                throw new IllegalStateException(INSUFFICIENT_STOCK_ERROR);
            }
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("deducted", count);
            result.put("stock", stock());
            return result;
        } catch (RuntimeException exception) {
            LOGGER.error("Stock deduction failed, count={}", count, exception);
            throw exception;
        }
    }

    /**
     * 查询当前库存。
     *
     * @return 可用库存
     */
    public int stock() {
        StorageDO storage = storageMapper.selectOne(
                new QueryWrapper<StorageDO>().eq("id", STOCK_RECORD_ID));
        if (storage == null || storage.getStock() == null) {
            throw new IllegalStateException("stock record not found");
        }
        return storage.getStock();
    }

    private void validateCount(int count) {
        if (count < MIN_DEDUCT_COUNT) {
            throw new IllegalArgumentException("count must be greater than 0");
        }
    }
}
