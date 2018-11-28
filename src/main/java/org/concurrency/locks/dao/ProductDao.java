package org.concurrency.locks.dao;

import org.apache.ibatis.annotations.*;
import org.concurrency.locks.model.Product;

/**
 * @author tianbo
 * @date 2018-11-27
 */
public interface ProductDao {
    String TABLE = "product";
    String FIELD_VALUES = "#{type}, #{name}, #{stock}, #{price}, #{version}, #{createTime}, #{updateTime}";
    String INSERT_COLUMNS = "type, name, stock, price, version, create_time, update_time";
    String SELECT_COLUMNS = "id, type, name, stock, price, version, create_time as createTime, update_time as " +
            "updateTime";

    @Insert("insert into " + TABLE + " (" + INSERT_COLUMNS + ") values(" + FIELD_VALUES + ")")
    int insert(Product one);

    @Select("select " + SELECT_COLUMNS + " from " + TABLE + " where id=#{id}")
    Product findById(@Param("id") long id);

    @Select("select " + SELECT_COLUMNS + " from " + TABLE + " where id=#{id} for update")
    Product lockById(@Param("id") long id);

    @Update("update " + TABLE + " set stock=#{stock}, update_time=#{updateTime} where id=#{id}")
    int update(@Param("id") long id, @Param("stock") int stock, @Param("updateTime") long updateTime);

    @Update("update " + TABLE + " set stock=#{stock}, version=#{newVersion}, update_time=#{updateTime} where id=#{id}" +
            " and version=#{oldVersion}")
    int updateV2(@Param("id") long id, @Param("stock") int stock, @Param("newVersion") long newVersion, @Param
            ("oldVersion") long oldVersion, @Param("updateTime") long updateTime);
}
