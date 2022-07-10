package org.transaction.test.global_transaction.mybatis.service.datasource1;

import org.transaction.test.global_transaction.mybatis.bean.datasource1.DataSource1User2;

public interface User2Service {

    void addRequired(DataSource1User2 user2);

    void addRequiredException(DataSource1User2 user2);

    void truncate();

    void addSupports(DataSource1User2 user2);

    void addSupportsException(DataSource1User2 user2);

    void addRequiresNew(DataSource1User2 user2);

    void addRequiresNewException(DataSource1User2 user2);

    void addNotSupported(DataSource1User2 user2);

    void addNotSupportedException(DataSource1User2 user2);

    void add(DataSource1User2 user2);

    void addException(DataSource1User2 user2);

    void addMandatory(DataSource1User2 user2);

    void addMandatoryException(DataSource1User2 user2);

    void addNever(DataSource1User2 user2);

    void addNeverException(DataSource1User2 user2);

    void addNested(DataSource1User2 user2);

    void addNestedException(DataSource1User2 user2);

}
