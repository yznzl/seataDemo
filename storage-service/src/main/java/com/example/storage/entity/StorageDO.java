package com.example.storage.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/** 库存持久化对象。 */
@TableName("demo_storage")
public class StorageDO {
    @TableId
    private Long id;
    private Integer stock;

    /** @return 库存记录编号 */
    public Long getId() { return id; }

    /** @param id 库存记录编号 */
    public void setId(Long id) { this.id = id; }

    /** @return 可用库存 */
    public Integer getStock() { return stock; }

    /** @param stock 可用库存 */
    public void setStock(Integer stock) { this.stock = stock; }
}
