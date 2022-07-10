package org.transaction.test.global_transaction.mybatis.service.datasource1;

import org.transaction.test.global_transaction.mybatis.bean.datasource1.DataSource1User1;

public interface User1Service {

    void addRequired(DataSource1User1 user);

    void addRequiredException(DataSource1User1 user1);

    void truncate();

    void addSupports(DataSource1User1 user);

    void addSupportsException(DataSource1User1 user1);

    void addRequiresNew(DataSource1User1 user);

    void addRequiresNewException(DataSource1User1 user1);

    void addNotSupported(DataSource1User1 user1);

    void addNotSupportedException(DataSource1User1 user1);

    void add(DataSource1User1 user1);

    void addException(DataSource1User1 user1);

    void addMandatory(DataSource1User1 user1);

    void addMandatoryException(DataSource1User1 user1);

    void addNever(DataSource1User1 user1);

    void addNeverException(DataSource1User1 user1);

    void addNested(DataSource1User1 user1);

    void addNestedException(DataSource1User1 user1);

}
