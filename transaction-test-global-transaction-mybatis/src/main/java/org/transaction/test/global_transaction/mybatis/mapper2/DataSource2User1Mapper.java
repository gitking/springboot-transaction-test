package org.transaction.test.global_transaction.mybatis.mapper2;

import org.transaction.test.global_transaction.mybatis.bean.datasource2.DataSource2User1;

public interface DataSource2User1Mapper {

    int deleteByPrimaryKey(Integer id);

    int insert(DataSource2User1 record);

    int insertSelective(DataSource2User1 record);

    DataSource2User1 selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DataSource2User1 record);

    int updateByPrimaryKey(Integer id);

    int truncated();
}
