package org.transaction.test.global_transaction.mybatis.service.datasource2;

import org.transaction.test.global_transaction.mybatis.bean.datasource2.DataSource2User2;

public interface User2Service {

    void addRequired(DataSource2User2 user2);

    void addRequiredException(DataSource2User2 user2);

    void truncate();

    void addSupports(DataSource2User2 user2);

    void addSupportsException(DataSource2User2 user2);

    void addRequiresNew(DataSource2User2 user2);

    void addRequiresNewException(DataSource2User2 user2);

    void addNotSupported(DataSource2User2 user2);

    void addNotSupportedException(DataSource2User2 user2);

    void add(DataSource2User2 user2);

    void addException(DataSource2User2 user2);

    void addMandatory(DataSource2User2 user2);

    void addMandatoryException(DataSource2User2 user2);

    void addNever(DataSource2User2 user2);

    void addNeverException(DataSource2User2 user2);

    void addNested(DataSource2User2 user2);

    void addNestedException(DataSource2User2 user2);

}
