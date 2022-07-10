package org.transaction.test.global_transaction.mybatis.mapper1;

import org.transaction.test.global_transaction.mybatis.bean.datasource1.DataSource1User2;

public interface DataSource1User2Mapper {

    int deleteByPrimaryKey(Integer id);

    int insert(DataSource1User2 record);

    int insertSelective(DataSource1User2 record);

    DataSource1User2 selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DataSource1User2 record);

    int updateByPrimaryKey(DataSource1User2 record);

    int truncated();
}
