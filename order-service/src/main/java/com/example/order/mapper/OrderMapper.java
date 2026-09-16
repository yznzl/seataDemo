package com.example.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.order.entity.OrderDO;
import org.apache.ibatis.annotations.Mapper;

/** 订单数据访问接口。 */
@Mapper
public interface OrderMapper extends BaseMapper<OrderDO> {
}
