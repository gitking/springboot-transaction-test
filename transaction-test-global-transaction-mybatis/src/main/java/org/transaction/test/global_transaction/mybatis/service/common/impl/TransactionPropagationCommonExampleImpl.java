package org.transaction.test.global_transaction.mybatis.service.common.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.transaction.test.global_transaction.mybatis.bean.datasource1.DataSource1User1;
import org.transaction.test.global_transaction.mybatis.bean.datasource2.DataSource2User2;
import org.transaction.test.global_transaction.mybatis.service.common.TransactionPropagationCommonExample;
import org.transaction.test.global_transaction.mybatis.service.datasource1.User1Service;
import org.transaction.test.global_transaction.mybatis.service.datasource2.User2Service;

@Service
public class TransactionPropagationCommonExampleImpl implements TransactionPropagationCommonExample {

    @Autowired
    private User1Service user1ServiceDataSource1;
    
    @Autowired
    private User2Service user2ServiceDataSource1;

    @Autowired
    private org.transaction.test.global_transaction.mybatis.service.datasource2.User1Service user1ServiceDataSource2;
    
    @Autowired
    private org.transaction.test.global_transaction.mybatis.service.datasource2.User2Service user2ServiceDataSource2;

    @Override
    public void truncated() {
        user1ServiceDataSource1.truncate();
        user2ServiceDataSource1.truncate();
        
        user1ServiceDataSource2.truncate();
        user2ServiceDataSource2.truncate();
    }

    @Override
    public void notransaction_exception_notransaction_notransaction() {
        DataSource1User1 user1 = new DataSource1User1();
        user1.setName("张三");
        user1ServiceDataSource1.add(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2ServiceDataSource2.add(user2);
        throw new RuntimeException();
    }

    @Override
    public void notransaction_notransaction_notransaction_exception() {
        DataSource1User1 user1 = new DataSource1User1();
        user1.setName("张三");
        user1ServiceDataSource1.add(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2ServiceDataSource2.addException(user2);
    }

    @Override
    @Transactional
    public void transaction_exception_notransaction_notransaction() {
        DataSource1User1 user1 = new DataSource1User1();
        user1.setName("张三");
        user1ServiceDataSource1.add(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2ServiceDataSource2.add(user2);
        throw new RuntimeException();
    }

    /**
     * 对use1数据源开启事务
     */
    @Override
    @Transactional(value = "user1TM")
    public void transaction_exception_notransaction_notransaction_user1() {
        DataSource1User1 user1 = new DataSource1User1();
        user1.setName("张三");
        user1ServiceDataSource1.add(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2ServiceDataSource2.add(user2);
        throw new RuntimeException();
    }

    /**
     * 针对user2数据源开启事务
     */
    @Override
    @Transactional(value = "user2TM")
    public void transaction_exception_notransaction_notransaction_user2() {
        DataSource1User1 user1 = new DataSource1User1();
        user1.setName("张三");
        user1ServiceDataSource1.add(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2ServiceDataSource2.add(user2);
        throw new RuntimeException();
    }

    @Override
    @Transactional
    public void transaction_notransaction_notransaction_exception() {
        DataSource1User1 user1 = new DataSource1User1();
        user1.setName("张三");
        user1ServiceDataSource1.add(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2ServiceDataSource2.addException(user2);
    }

    /**
     * 注意没有事务注解
     *
     */
    @Override
    public void notransaction_required_required() {
        DataSource1User1 user1 = new DataSource1User1();
        user1.setName("张三");
        user1ServiceDataSource1.addRequired(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2ServiceDataSource2.addRequired(user2);
    }

    /**
     * 注意没有事务注解并且方法内部还抛出了异常
     *
     */
    @Override
    public void notransaction_exception_required_required() {
        DataSource1User1 user1 = new DataSource1User1();
        user1.setName("张三");
        user1ServiceDataSource1.addRequired(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2ServiceDataSource2.addRequired(user2);
        throw new RuntimeException();
    }


    /**
     * 注意没有事务注解，但是调用内部方法抛出异常
     *
     */
    @Override
    public void notransaction_required_required_exception() {
        DataSource1User1 user1 = new DataSource1User1();
        user1.setName("张三");
        user1ServiceDataSource1.addRequired(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2ServiceDataSource2.addRequiredException(user2);
    }

    /**
     *
     *
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void transaction_exception_required_required() {
        DataSource1User1 user1 = new DataSource1User1();
        user1.setName("张三");
        user1ServiceDataSource1.addRequired(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2ServiceDataSource2.addRequired(user2);

        throw new RuntimeException();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void transaction_required_required_exception() {
        DataSource1User1 user1 = new DataSource1User1();
        user1.setName("张三");
        user1ServiceDataSource1.addRequired(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2ServiceDataSource2.addRequiredException(user2);
    }

    @Override
    public void notransaction_supports_supports_exception() {
        DataSource1User1 user1 = new DataSource1User1();
        user1.setName("张三");
        user1ServiceDataSource1.addSupports(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2ServiceDataSource2.addSupportsException(user2);
    }

    @Override
    public void notransaction_exception_supports_supports() {
        DataSource1User1 user1 = new DataSource1User1();
        user1.setName("张三");
        user1ServiceDataSource1.addSupports(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2ServiceDataSource2.addSupports(user2);
        throw new RuntimeException();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void transaction_supports_supports_exception() {
        DataSource1User1 user1 = new DataSource1User1();
        user1.setName("张三");
        user1ServiceDataSource1.addSupports(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2ServiceDataSource2.addSupportsException(user2);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void transaction_exception_supports_supports() {
        DataSource1User1 user1 = new DataSource1User1();
        user1.setName("张三");
        user1ServiceDataSource1.addSupports(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2ServiceDataSource2.addSupports(user2);

        throw new RuntimeException();
    }

    @Override
    public void notransaction_requiresNew_requiresNew_exception() {
        DataSource1User1 user1 = new DataSource1User1();
        user1.setName("张三");
        user1ServiceDataSource1.addRequiresNew(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2ServiceDataSource2.addRequiresNewException(user2);
    }

    @Override
    public void notransaction_exception_requiresNew_requiresNew() {
        DataSource1User1 user1 = new DataSource1User1();
        user1.setName("张三");
        user1ServiceDataSource1.addRequiresNew(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2ServiceDataSource2.addRequiresNew(user2);
        throw new RuntimeException();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void transaction_required_requiresNew_requiresNew_exception() {
        DataSource1User1 user1 = new DataSource1User1();
        user1.setName("张三");
        user1ServiceDataSource1.addRequired(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2ServiceDataSource2.addRequiresNew(user2);

        DataSource2User2 user21 = new DataSource2User2();
        user21.setName("王五");
        user2ServiceDataSource2.addRequiresNewException(user21);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void transaction_required_requiresNew_requiresNew_exception_try() {
        DataSource1User1 user1 = new DataSource1User1();
        user1.setName("张三");
        user1ServiceDataSource1.addRequired(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2ServiceDataSource2.addRequiresNew(user2);

        DataSource2User2 user21 = new DataSource2User2();
        user21.setName("王五");
        try {
            user2ServiceDataSource2.addRequiresNewException(user21);
        } catch (Exception e) {
            System.out.println("回滚");
        }
    }

    @Override
    @Transactional(propagation =  Propagation.REQUIRED)
    public void transaction_exception_required_requiresNew_requiresNew() {
        DataSource1User1 user1 = new DataSource1User1();
        user1.setName("张三");
        user1ServiceDataSource1.addRequired(user1);

        DataSource1User1 user12 = new DataSource1User1();
        user12.setName("李四");
        user1ServiceDataSource1.addRequiresNew(user12);

        DataSource2User2 user21 = new DataSource2User2();
        user21.setName("王五");
        user2ServiceDataSource2.addRequiresNew(user21);
        throw new RuntimeException();
    }

    @Override
    public void notransaction_exception_required_notSupport() {
        DataSource1User1 user1 = new DataSource1User1();
        user1.setName("张三");
        user1ServiceDataSource1.addRequired(user1);

        DataSource1User1 user11 = new DataSource1User1();
        user11.setName("王五");
        user1ServiceDataSource1.addNotSupported(user11);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2ServiceDataSource2.addNotSupported(user2);
        throw new RuntimeException();
    }

    @Override
    public void notransaction_required_notSupport_exception() {
        DataSource1User1 user1 = new DataSource1User1();
        user1.setName("张三");
        user1ServiceDataSource1.addRequired(user1);

        DataSource1User1 user11 = new DataSource1User1();
        user11.setName("王五");
        user1ServiceDataSource1.addNotSupported(user11);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2ServiceDataSource2.addNotSupportedException(user2);
    }

    @Override
    @Transactional
    public void transaction_exception_required_notSuppored() {
        DataSource1User1 user1 = new DataSource1User1();
        user1.setName("张三");
        user1ServiceDataSource1.addRequired(user1);

        DataSource1User1 user11 = new DataSource1User1();
        user11.setName("王五");
        user1ServiceDataSource1.addNotSupported(user11);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2ServiceDataSource2.addNotSupported(user2);
        throw new RuntimeException();
    }

    @Override
    @Transactional
    public void transaction_required_notSupported_exception() {
        DataSource1User1 user1 = new DataSource1User1();
        user1.setName("张三");
        user1ServiceDataSource1.addRequired(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2ServiceDataSource2.addNotSupportedException(user2);
    }

    @Override
    public void notransaction_mandatory() {
        DataSource1User1 user1 = new DataSource1User1();
        user1.setName("张三");
        user1ServiceDataSource1.addMandatory(user1);
    }

    @Override
    @Transactional
    public void transaction_exception_mandatory_mandatory() {
        DataSource1User1 user1 = new DataSource1User1();
        user1.setName("张三");
        user1ServiceDataSource1.addMandatory(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2ServiceDataSource2.addMandatory(user2);
        throw new RuntimeException();
    }

    @Override
    @Transactional
    public void transaction_mandatory_mandatory_exception() {
        DataSource1User1 user1 = new DataSource1User1();
        user1.setName("张三");
        user1ServiceDataSource1.addMandatory(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2ServiceDataSource2.addMandatoryException(user2);
    }

    @Override
    public void notransaction_exception_never_never() {
        DataSource1User1 user1 = new DataSource1User1();
        user1.setName("张三");
        user1ServiceDataSource1.addNever(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2ServiceDataSource2.addNever(user2);
        throw new RuntimeException();
    }

    @Override
    public void notransaction_never_never_exception() {
        DataSource1User1 user1 = new DataSource1User1();
        user1.setName("张三");
        user1ServiceDataSource1.addNever(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2ServiceDataSource2.addNeverException(user2);
    }

    @Override
    @Transactional
    public void transaction_never() {
        DataSource1User1 user1 = new DataSource1User1();
        user1.setName("张三");
        user1ServiceDataSource1.addNever(user1);
    }

    @Override
    @Transactional
    public void transaction_exception_nested_nested() {
        DataSource1User1 user1 = new DataSource1User1();
        user1.setName("张三");
        user1ServiceDataSource1.addNested(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2ServiceDataSource2.addNested(user2);
        throw new RuntimeException();
    }

    @Override
    @Transactional
    public void transaction_nested_nested_exception() {
        DataSource1User1 user1 = new DataSource1User1();
        user1.setName("张三");
        user1ServiceDataSource1.addNested(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2ServiceDataSource2.addNestedException(user2);
    }

    @Override
    @Transactional
    public void transaction_nested_nested_exception_try() {
        DataSource1User1 user1 = new DataSource1User1();
        user1.setName("张三");
        user1ServiceDataSource1.addNested(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        try {
            user2ServiceDataSource2.addNestedException(user2);
        } catch (Exception e) {
            System.out.println("方法回滚");
        }
    }

    @Override
    @Transactional
    public void transaction_required_required_exception_try() {
        DataSource1User1 user1 = new DataSource1User1();
        user1.setName("张三");
        user1ServiceDataSource1.addRequired(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        try {
            user2ServiceDataSource2.addRequiredException(user2);
        } catch (Exception e){
            System.out.println("方法回滚");
        }
    }

    @Override
    @Transactional
    public void transaction_noTransaction_noTransaction_exception_try() {
        DataSource1User1 user1 = new DataSource1User1();
        user1.setName("张三");
        user1ServiceDataSource1.add(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        try {
            user2ServiceDataSource2.addException(user2);
        } catch (Exception e) {
            System.out.println("方法回滚");
        }
    }
}
