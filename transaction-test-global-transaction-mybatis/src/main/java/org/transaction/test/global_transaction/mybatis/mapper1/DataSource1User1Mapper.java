package org.transaction.test.global_transaction.mybatis.mapper1;

import org.transaction.test.global_transaction.mybatis.bean.datasource1.DataSource1User1;

public interface DataSource1User1Mapper {

    int deleteByPrimaryKey(Integer id);

    int insert(DataSource1User1 record);

    int insertSelective(DataSource1User1 record);

    DataSource1User1 selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DataSource1User1 record);

    int updateByPrimaryKey(Integer id);

    int truncated();
}
