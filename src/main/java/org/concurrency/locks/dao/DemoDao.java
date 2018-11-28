package org.concurrency.locks.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.concurrency.locks.model.Demo;

/**
 * @author tianbo
 * @date 2018-11-27
 */
public interface DemoDao {
    String TABLE = "demo";
    String FIELD_VALUES = "#{type}, #{content}, unix_timestamp(), unix_timestamp()";
    String INSERT_COLUMNS = "type, content, create_time, update_time";
    String SELECT_COLUMNS = "id, type, content, create_time as createTime, update_time as updateTime";

    @Insert("insert into " + TABLE + " (" + INSERT_COLUMNS + ") values(" + FIELD_VALUES + ")")
    int insert(Demo one);

    @Select("select " + SELECT_COLUMNS + " from " + TABLE + " order by create_time desc limit 1")
    Demo findNewestCreated();
}
