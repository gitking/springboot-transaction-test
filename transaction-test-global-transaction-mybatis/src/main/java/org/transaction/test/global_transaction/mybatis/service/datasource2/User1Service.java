package org.transaction.test.global_transaction.mybatis.service.datasource2;

import org.transaction.test.global_transaction.mybatis.bean.datasource2.DataSource2User1;

public interface User1Service {

    void addRequired(DataSource2User1 user);

    void addRequiredException(DataSource2User1 user1);

    void truncate();

    void addSupports(DataSource2User1 user);

    void addSupportsException(DataSource2User1 user1);

    void addRequiresNew(DataSource2User1 user);

    void addRequiresNewException(DataSource2User1 user1);

    void addNotSupported(DataSource2User1 user1);

    void addNotSupportedException(DataSource2User1 user1);

    void add(DataSource2User1 user1);

    void addException(DataSource2User1 user1);

    void addMandatory(DataSource2User1 user1);

    void addMandatoryException(DataSource2User1 user1);

    void addNever(DataSource2User1 user1);

    void addNeverException(DataSource2User1 user1);

    void addNested(DataSource2User1 user1);

    void addNestedException(DataSource2User1 user1);

}
