package org.transaction.test.global_transaction.mybatis.mapper2;

import org.transaction.test.global_transaction.mybatis.bean.datasource2.DataSource2User2;

public interface DataSource2User2Mapper {

    int deleteByPrimaryKey(Integer id);

    int insert(DataSource2User2 record);

    int insertSelective(DataSource2User2 record);

    DataSource2User2 selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DataSource2User2 record);

    int updateByPrimaryKey(DataSource2User2 record);

    int truncated();
}
