package com.example.storage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.storage.entity.StorageDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/** 库存数据访问接口。 */
@Mapper
public interface StorageMapper extends BaseMapper<StorageDO> {
    /**
     * 仅在库存充足时扣减库存。
     *
     * @param count 扣减数量
     * @return 受影响的行数
     */
    @Update("UPDATE demo_storage SET stock = stock - #{count} "
            + "WHERE id = 1 AND stock >= #{count}")
    int deduct(@Param("count") int count);
}
