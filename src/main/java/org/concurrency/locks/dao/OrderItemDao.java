package org.concurrency.locks.dao;

import org.apache.ibatis.annotations.Insert;
import org.concurrency.locks.model.OrderItem;

/**
 * @author tianbo
 * @date 2018-11-27
 */
public interface OrderItemDao {
    String TABLE = "order_item";
    String INSERT_COLUMNS = "user_id, product_id, number, source, create_time, update_time";
    String FIELD_VALUES = "#{userId}, #{productId}, #{number}, #{source}, #{createTime}, #{updateTime}";

    @Insert("insert into " + TABLE + " (" + INSERT_COLUMNS + ") values(" + FIELD_VALUES + ")")
    int insert(OrderItem one);
}