package org.transaction.test.global_transaction.mybatis.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.transaction.test.global_transaction.mybatis.service.datasource1.TransactionPropagationExample;

/**
 * 测试各种多数据源下，不使用JTA事务的情况下，Spring事务传播性
 *
 * https://segmentfault.com/a/1190000013341344 《Spring事务传播行为详解》  JerryTse 发布于 2018-02-23
 * 鉴于文章篇幅问题，其他事务传播行为的测试就不在此一一描述了，感兴趣的读者可以去源码中自己寻找相应测试代码和结果解释。
 * 传送门：https://github.com/TmTse/transaction-test
 *
 * Spring官方文档: https://docs.spring.io/spring-framework/docs/5.0.9.RELEASE/spring-framework-reference/data-access.html#tx-propagation-required
 *
 * 2.2 场景二 外围方法开启事务。张三”未插入成功，亲测不行 求解？？ spring date jpa 不支持保存点事物 @Transactional(propagation = Propagation.NESTED)。
 *
 * 只测试单数据源的情况,只测试DataSource1这个数据源
 *
 * @author JerryTse
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/applicationContextSingal.xml"})
public class TransactionPropagationExampleImplDataSource1Test {

    @Autowired
    private TransactionPropagationExample transactionPropagationDataSource1Example;

    @Before
    public void setUpBeforeClass() throws Exception {
        transactionPropagationDataSource1Example.truncated();
    }

    /**
     * 结果：张三(插入)， 李四(插入) </br>
     *
     * 没有开启事务的情况下，事务默认是自动提交的，SQL执行完就直接提交了
     */
    @Test
    public void testNotransaction_exception_notransaction_notransaction() {
        transactionPropagationDataSource1Example.notransaction_exception_notransaction_notransaction();
    }

    /**
     * 结果：张三(插入)， 李四(插入) </br>
     *
     * 没有开启事务的情况下，事务默认是自动提交的，SQL执行完就直接提交了
     */
    @Test
    public void testNotransaction_notransaction_notransaction_exception() {
        transactionPropagationDataSource1Example.notransaction_notransaction_notransaction_exception();
    }

    /**
     * 结果: 张三(未插入)， 李四(未插入)</br>
     *
     * 外围方法开启事务，外围方法内的方法就应该在同一个事务中，内部方法抛出异常，被外围方法捕获，整个事务回滚。
     */
    @Test
    public void testTransaction_notransaction_notransaction_excpetion() {
        transactionPropagationDataSource1Example.transaction_notransaction_notransaction_exception();
    }

    /**
     * 结果： 张三（插入）， 李四（插入）</br>
     *
     * 外围方法开启事务，内部方法在同一事务中，只要不抛出异常，事务就不会回滚
     */
    @Test
    public void testTransaction_noTransaction_notransaction_exception_try(){
        transactionPropagationDataSource1Example.transaction_noTransaction_noTransaction_exception_try();
    }

    /**
     * 结果： 张三（插入）， 李四（插入） </br>
     *
     */
    @Test
    public void testNoTransaction() {
        transactionPropagationDataSource1Example.notransaction_required_required();
    }

    /**
     * 结果： 张三（插入）， 李四（插入） </br>
     *
     * 外围方法没有事务，插入“张三”，“李四”方法在自己的事务中独立运行，外围方法异常不影响内部插入“张三”，“李四”方法独立的事务。
     */
    @Test
    public void testNoTransaction_exception_required_required() {
        transactionPropagationDataSource1Example.notransaction_exception_required_required();
    }

}
