package org.transaction.test.global_transaction.mybatis.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.transaction.test.global_transaction.mybatis.service.common.TransactionPropagationCommonExample;

/**
 * 测试各种多数据源下，不使用JTA事务的情况下，Spring事务传播性
 * 这个类要跟TransactionPropagetionExampleImpJtaTest这个类对比观察。
 *
 * https://segmentfault.com/a/1190000013341344 《Spring事务传播行为详解》  JerryTse 发布于 2018-02-23
 * 鉴于文章篇幅问题，其他事务传播行为的测试就不在此一一描述了，感兴趣的读者可以去源码中自己寻找相应测试代码和结果解释。
 * 传送门：https://github.com/TmTse/transaction-test
 *
 * Spring官方文档: https://docs.spring.io/spring-framework/docs/5.0.9.RELEASE/spring-framework-reference/data-access.html#tx-propagation-required
 *
 * 2.2 场景二 外围方法开启事务。张三”未插入成功，亲测不行 求解？？ spring date jpa 不支持保存点事物 @Transactional(propagation = Propagation.NESTED)。
 *
 * 多数据源测试
 * @author JerryTse
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/applicationContext.xml"})
public class TransactionPropagationCommonExampleImplTest {

    @Autowired
    private TransactionPropagationCommonExample transactionPropagationCommonExampleExample;

    @Before
    public void setUpBeforeClass() throws Exception {
        transactionPropagationCommonExampleExample.truncated();
    }


    /**
     * 结果： 张三（插入），李四（插入） </br>
     */
    @Test
    public void testNotransaction_exception_notransaction_notransaction() {
        transactionPropagationCommonExampleExample.notransaction_exception_notransaction_notransaction();
    }

    /**
     * 结果：张三（插入），李四（插入）</br>
     */
    @Test
    public void testNotransaction_notransaction_notransaction_exception() {
        transactionPropagationCommonExampleExample.notransaction_notransaction_notransaction_exception();
    }


    /**
     * 结果：张三（未插入），李四（插入）</br>
     *
     * SpringBoot多数据源，默认只有第一个数据源事务生效。但是JTA多数据源俩个事务都会生效。可以参考TransactionPropagetionExampleImpJtaTest这个类的测试方法
     */
    @Test
    public void testTransaction_exception_notransaction_notransaction() {
        transactionPropagationCommonExampleExample.transaction_exception_notransaction_notransaction();
    }

    /**
     * 结果: 张三(未插入), 李四(插入) </br>
     *
     * 对user1数据源开启事务，事务回滚只针对user1表。
     */
    @Test
    public void testTransaction_exception_notransaction_notransaction_user1() {
        transactionPropagationCommonExampleExample.transaction_exception_notransaction_notransaction_user1();
    }

    /**
     * 结果： 张三(插入),李四(未插入) </br>
     *
     * 对user2数据源开启事务，事务回滚只针对user2表。
     */
    @Test
    public void testTransaction_exception_notransaction_notransaction_user2() {
        transactionPropagationCommonExampleExample.transaction_exception_notransaction_notransaction_user2();
    }

    // ————————————————————————————————————————————————————————————————————————————————
    // 由上可知，在多数源的情况下，我们对哪个数据源开启事务，事务使用情况就可以和单数据源事务的情况一致，未开启事务的数据源可当做不支持事务处理，在事务的各种情况中不做考虑。
    // ————————————————————————————————————————————————————————————————————————————————

    // ————————————————————————————————————————————————————————————————————————————————
    // 以下内容均为测试，结果可以根据单数据源的情况推测
    // ————————————————————————————————————————————————————————————————————————————————

    /**
     * 结果：张三（未插入），李四（插入）</br>
     *
     * 应该是我猜的，多数据源的情况下，外围方法开启事务，“张三”方法加入外围方法事务，“李四”方法没有跟“张三”公用一个事务，因为不是一个数据源。
     * 所以，“李四”方法抛出异常，被外围方法感知，外围方法事务回滚，“张三”也被回滚了。但是“李四”方法用的自己独立的数据源，没有开启事务，即使“李四”方法抛出异常，“李四”也不会被回滚。
     *
     * 在transaction_notransaction_notransaction_exception方法上面指定使用数据源2，那结果就会变成张三（插入），李四（未插入）了。
     * 所以根据结果反推，在多数据源情况下，@Transactional 这个注解默认对在执行的过程中碰到的第一个数据源生效。可以看下源码，TODO
     *
     * 但是JTA多数源的事务的结果就是，张三（未插入），李四（未插入）,可以参考这个类TransactionPropagetionExampleImpJtaTest
     */
    @Test
    public void testTransaction_notransaction_notransaction_exception() {
        transactionPropagationCommonExampleExample.transaction_notransaction_notransaction_exception();
    }

    /**
     * 结果：张三（插入），李四（插入）</br>
     *
     *
     */
    @Test
    public void testTransaction_noTransaction_noTransaction_exception_try() {
        transactionPropagationCommonExampleExample.transaction_noTransaction_noTransaction_exception_try();
    }

    /**
     * 结果：张三（插入），李四（插入） </br>
     *
     */
    @Test
    public void testNoTransaction() {
        transactionPropagationCommonExampleExample.notransaction_required_required();
    }

    /**
     * 结果：张三（插入），李四（插入）</br>
     *
     */
    @Test
    public void testNotransaction_exception_required_required() {
        transactionPropagationCommonExampleExample.notransaction_exception_required_required();;
    }

    /**
     * 结果：张三（插入），李四（插入）</br>
     *
     * 这个李四应该不能插入进去的，但是李四缺插入进去了，多数据源事务控制有问题。TODO
     * 但是如果张三也抛出异常，张三和李四都插入不进去，很奇怪的，多数据源值得研究一下。TODO
     */
    @Test
    public void testNoTransaction_required_required_exception() {
        transactionPropagationCommonExampleExample.notransaction_required_required_exception();
    }

    /**
     * 结果：张三（未插入），李四（插入）</br>
     *
     * 似乎事务默认只对方法中遇见的第一个数据源生效，正常来说张三和李四都不应该插入进去的。 TODO
     * 但是JTA就可以完美控制的，可以参考这个类TransactionPropagetionExampleImpJtaTest
     */
    @Test
    public void testTransaction_exception_required_required() {
        transactionPropagationCommonExampleExample.transaction_exception_required_required();
    }

    /**
     * 结果：张三（未插入），李四（插入）</br>
     *
     */
    @Test
    public void testTransaction_required_required_exception() {
        transactionPropagationCommonExampleExample.transaction_required_required_exception();
    }

    /**
     * 结果：张三（未插入），李四（插入）</br>
     *
     * 从这个结果上可以看出来，虽然“李四”方法上面也开启了事务，但是这个事务只针对数据源1生效，
     * 在SpringBoot看来插入“李四”就是一个普通Java方法，或者在SpringBoot看来插入“李四”方法跟插入“张三”方法属于同一个数据源下的同一个事务，
     * 所以插入“李四”方法虽然抛出异常，但是李四缺插入成功了，张三竟然没有插入成功，因为插入“李四”方法的数据源根本就没有开启事务。
     * 插入“李四”方法抛出异常，在插入“张三”方法的数据源看来就是自己的方法抛出异常了，要回滚。
     * 这个方法最终会抛出这个错误：
     * org.springframework.transaction.UnexpectedRollbackException: Transaction rolled back because it has been marked as rollback-only
     */
    @Test
    public void testTransaction_required_required_exception_try() {
        transactionPropagationCommonExampleExample.transaction_required_required_exception_try();
    }
}
