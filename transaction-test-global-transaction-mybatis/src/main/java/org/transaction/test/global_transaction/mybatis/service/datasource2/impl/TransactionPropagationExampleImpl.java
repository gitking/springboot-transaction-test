package org.transaction.test.global_transaction.mybatis.service.datasource2.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.transaction.test.global_transaction.mybatis.bean.datasource2.DataSource2User1;
import org.transaction.test.global_transaction.mybatis.bean.datasource2.DataSource2User2;
import org.transaction.test.global_transaction.mybatis.service.datasource2.TransactionPropagationExample;
import org.transaction.test.global_transaction.mybatis.service.datasource2.User1Service;
import org.transaction.test.global_transaction.mybatis.service.datasource2.User2Service;

@Service
public class TransactionPropagationExampleImpl implements TransactionPropagationExample {

    @Autowired
    private User1Service user1Service;

    @Autowired
    private User2Service user2Service;

    @Override
    public void truncated() {
        user1Service.truncate();
        user2Service.truncate();
    }

    @Override
    public void notransaction_exception_notransaction_notransaction() {
        DataSource2User1 user1 = new DataSource2User1();
        user1.setName("张三");
        user1Service.add(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2Service.add(user2);
        throw new RuntimeException();
    }

    @Override
    public void notransaction_notransaction_notransaction_exception() {
        DataSource2User1 user1 = new DataSource2User1();
        user1.setName("张三");
        user1Service.add(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2Service.addException(user2);
    }

    @Override
    @Transactional
    public void transaction_exception_notransaction_notransaction() {
        DataSource2User1 user1 = new DataSource2User1();
        user1.setName("张三");
        user1Service.add(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2Service.add(user2);
        throw new RuntimeException();
    }

    /**
     * 对use1数据源开启事务
     */
    @Override
    @Transactional(value = "user1TM")
    public void transaction_exception_notransaction_notransaction_user1() {
        DataSource2User1 user1 = new DataSource2User1();
        user1.setName("张三");
        user1Service.add(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2Service.add(user2);
        throw new RuntimeException();
    }

    /**
     * 针对user2数据源开启事务
     */
    @Override
    @Transactional(value = "user2TM")
    public void transaction_exception_notransaction_notransaction_user2() {
        DataSource2User1 user1 = new DataSource2User1();
        user1.setName("张三");
        user1Service.add(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2Service.add(user2);
        throw new RuntimeException();
    }

    @Override
    @Transactional
    public void transaction_notransaction_notransaction_exception() {
        DataSource2User1 user1 = new DataSource2User1();
        user1.setName("张三");
        user1Service.add(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2Service.addException(user2);
    }

    /**
     * 注意没有事务注解
     *
     */
    @Override
    public void notransaction_required_required() {
        DataSource2User1 user1 = new DataSource2User1();
        user1.setName("张三");
        user1Service.addRequired(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2Service.addRequired(user2);
    }

    /**
     * 注意没有事务注解并且方法内部还抛出了异常
     *
     */
    @Override
    public void notransaction_exception_required_required() {
        DataSource2User1 user1 = new DataSource2User1();
        user1.setName("张三");
        user1Service.addRequired(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2Service.addRequired(user2);
        throw new RuntimeException();
    }


    /**
     * 注意没有事务注解，但是调用内部方法抛出异常
     *
     */
    @Override
    public void notransaction_required_required_exception() {
        DataSource2User1 user1 = new DataSource2User1();
        user1.setName("张三");
        user1Service.addRequired(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2Service.addRequiredException(user2);
    }

    /**
     *
     *
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void transaction_exception_required_required() {
        DataSource2User1 user1 = new DataSource2User1();
        user1.setName("张三");
        user1Service.addRequired(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2Service.addRequired(user2);

        throw new RuntimeException();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void transaction_required_required_exception() {
        DataSource2User1 user1 = new DataSource2User1();
        user1.setName("张三");
        user1Service.addRequired(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2Service.addRequiredException(user2);
    }

    @Override
    public void notransaction_supports_supports_exception() {
        DataSource2User1 user1 = new DataSource2User1();
        user1.setName("张三");
        user1Service.addSupports(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2Service.addSupportsException(user2);
    }

    @Override
    public void notransaction_exception_supports_supports() {
        DataSource2User1 user1 = new DataSource2User1();
        user1.setName("张三");
        user1Service.addSupports(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2Service.addSupports(user2);
        throw new RuntimeException();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void transaction_supports_supports_exception() {
        DataSource2User1 user1 = new DataSource2User1();
        user1.setName("张三");
        user1Service.addSupports(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2Service.addSupportsException(user2);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void transaction_exception_supports_supports() {
        DataSource2User1 user1 = new DataSource2User1();
        user1.setName("张三");
        user1Service.addSupports(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2Service.addSupports(user2);

        throw new RuntimeException();
    }

    @Override
    public void notransaction_requiresNew_requiresNew_exception() {
        DataSource2User1 user1 = new DataSource2User1();
        user1.setName("张三");
        user1Service.addRequiresNew(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2Service.addRequiresNewException(user2);
    }

    @Override
    public void notransaction_exception_requiresNew_requiresNew() {
        DataSource2User1 user1 = new DataSource2User1();
        user1.setName("张三");
        user1Service.addRequiresNew(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2Service.addRequiresNew(user2);
        throw new RuntimeException();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void transaction_required_requiresNew_requiresNew_exception() {
        DataSource2User1 user1 = new DataSource2User1();
        user1.setName("张三");
        user1Service.addRequired(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2Service.addRequiresNew(user2);

        DataSource2User2 user21 = new DataSource2User2();
        user21.setName("王五");
        user2Service.addRequiresNewException(user21);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void transaction_required_requiresNew_requiresNew_exception_try() {
        DataSource2User1 user1 = new DataSource2User1();
        user1.setName("张三");
        user1Service.addRequired(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2Service.addRequiresNew(user2);

        DataSource2User2 user21 = new DataSource2User2();
        user21.setName("王五");
        try {
            user2Service.addRequiresNewException(user21);
        } catch (Exception e) {
            System.out.println("回滚");
        }
    }

    @Override
    @Transactional(propagation =  Propagation.REQUIRED)
    public void transaction_exception_required_requiresNew_requiresNew() {
        DataSource2User1 user1 = new DataSource2User1();
        user1.setName("张三");
        user1Service.addRequired(user1);

        DataSource2User1 user12 = new DataSource2User1();
        user12.setName("李四");
        user1Service.addRequiresNew(user12);

        DataSource2User2 user21 = new DataSource2User2();
        user21.setName("王五");
        user2Service.addRequiresNew(user21);
        throw new RuntimeException();
    }

    @Override
    public void notransaction_exception_required_notSupport() {
        DataSource2User1 user1 = new DataSource2User1();
        user1.setName("张三");
        user1Service.addRequired(user1);

        DataSource2User1 user11 = new DataSource2User1();
        user11.setName("王五");
        user1Service.addNotSupported(user11);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2Service.addNotSupported(user2);
        throw new RuntimeException();
    }

    @Override
    public void notransaction_required_notSupport_exception() {
        DataSource2User1 user1 = new DataSource2User1();
        user1.setName("张三");
        user1Service.addRequired(user1);

        DataSource2User1 user11 = new DataSource2User1();
        user11.setName("王五");
        user1Service.addNotSupported(user11);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2Service.addNotSupportedException(user2);
    }

    @Override
    @Transactional
    public void transaction_exception_required_notSuppored() {
        DataSource2User1 user1 = new DataSource2User1();
        user1.setName("张三");
        user1Service.addRequired(user1);

        DataSource2User1 user11 = new DataSource2User1();
        user11.setName("王五");
        user1Service.addNotSupported(user11);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2Service.addNotSupported(user2);
        throw new RuntimeException();
    }

    @Override
    @Transactional
    public void transaction_required_notSupported_exception() {
        DataSource2User1 user1 = new DataSource2User1();
        user1.setName("张三");
        user1Service.addRequired(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2Service.addNotSupportedException(user2);
    }

    @Override
    public void notransaction_mandatory() {
        DataSource2User1 user1 = new DataSource2User1();
        user1.setName("张三");
        user1Service.addMandatory(user1);
    }

    @Override
    @Transactional
    public void transaction_exception_mandatory_mandatory() {
        DataSource2User1 user1 = new DataSource2User1();
        user1.setName("张三");
        user1Service.addMandatory(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2Service.addMandatory(user2);
        throw new RuntimeException();
    }

    @Override
    @Transactional
    public void transaction_mandatory_mandatory_exception() {
        DataSource2User1 user1 = new DataSource2User1();
        user1.setName("张三");
        user1Service.addMandatory(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2Service.addMandatoryException(user2);
    }

    @Override
    public void notransaction_exception_never_never() {
        DataSource2User1 user1 = new DataSource2User1();
        user1.setName("张三");
        user1Service.addNever(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2Service.addNever(user2);
        throw new RuntimeException();
    }

    @Override
    public void notransaction_never_never_exception() {
        DataSource2User1 user1 = new DataSource2User1();
        user1.setName("张三");
        user1Service.addNever(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2Service.addNeverException(user2);
    }

    @Override
    @Transactional
    public void transaction_never() {
        DataSource2User1 user1 = new DataSource2User1();
        user1.setName("张三");
        user1Service.addNever(user1);
    }

    @Override
    @Transactional
    public void transaction_exception_nested_nested() {
        DataSource2User1 user1 = new DataSource2User1();
        user1.setName("张三");
        user1Service.addNested(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2Service.addNested(user2);
        throw new RuntimeException();
    }

    @Override
    @Transactional
    public void transaction_nested_nested_exception() {
        DataSource2User1 user1 = new DataSource2User1();
        user1.setName("张三");
        user1Service.addNested(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2Service.addNestedException(user2);
    }

    @Override
    @Transactional
    public void transaction_nested_nested_exception_try() {
        DataSource2User1 user1 = new DataSource2User1();
        user1.setName("张三");
        user1Service.addNested(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        try {
            user2Service.addNestedException(user2);
        } catch (Exception e) {
            System.out.println("方法回滚");
        }
    }

    @Override
    @Transactional
    public void transaction_required_required_exception_try() {
        DataSource2User1 user1 = new DataSource2User1();
        user1.setName("张三");
        user1Service.addRequired(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        try {
            user2Service.addRequiredException(user2);
        } catch (Exception e){
            System.out.println("方法回滚");
        }
    }

    @Override
    @Transactional
    public void transaction_noTransaction_noTransaction_exception_try() {
        DataSource2User1 user1 = new DataSource2User1();
        user1.setName("张三");
        user1Service.add(user1);

        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        try {
            user2Service.addException(user2);
        } catch (Exception e) {
            System.out.println("方法回滚");
        }
    }
}
