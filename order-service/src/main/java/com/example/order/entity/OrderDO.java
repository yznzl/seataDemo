package com.example.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/** 订单持久化对象。 */
@TableName("demo_order")
public class OrderDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Integer decisionNumber;
    private String status;

    /** @return 订单编号 */
    public Long getId() { return id; }

    /** @param id 订单编号 */
    public void setId(Long id) { this.id = id; }

    /** @return 判定数字 */
    public Integer getDecisionNumber() { return decisionNumber; }

    /** @param decisionNumber 判定数字 */
    public void setDecisionNumber(Integer decisionNumber) { this.decisionNumber = decisionNumber; }

    /** @return 订单状态 */
    public String getStatus() { return status; }

    /** @param status 订单状态 */
    public void setStatus(String status) { this.status = status; }
}
