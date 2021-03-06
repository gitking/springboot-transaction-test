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

    /**
     * 结果： 张三（插入），李四（未插入） </br>
     *
     * 外围方法没有事务，插入“张三”、“李四”方法都在自己的事务中独立运行，所以插入“李四”方法抛出异常只会回滚插入“李四”方法，插入“张三”方法不受影响。
     */
    @Test
    public void testNoTransaction_required_required_exception() {
        transactionPropagationDataSource1Example.notransaction_required_required_exception();
    }

    /**
     * 结果： 张三（未插入）， 李四（未插入）</br>
     *
     * 外围方法开启事务，插入“张三”、“李四”方法都在外围方法的事务中运行，加入外围方法事务，所以三个方法同一个事务。
     * 外围方法或内部方法抛出异常，整个事务全部回滚。
     */
    @Test
    public void testTransaction_exception_required_required() {
        transactionPropagationDataSource1Example.transaction_exception_required_required();
    }

    /**
     * 结果： 张三（未插入）、李四（未插入） </br>
     *
     * 外围方法开启事务，插入“张三”、“李四”方法都在外围方法的事务中运行，加入外围方法事务，所以三个方法同一个事务。
     * 外围方法或内部方法抛出异常，整个事务全部回滚。
     */
    @Test
    public void testTransaction_required_required_exception() {
        transactionPropagationDataSource1Example.transaction_required_required_exception();
    }

    /**
     * 结果： 张三（未插入）， 李四（未插入） </br>
     *
     * 外围方法开启事务，插入“张三”、“李四”方法都在外围方法的事务中运行，加入外围方法事务，所以三个方法同一个事务。外围方法或内部方法抛出异常，
     * 整个事务全部回滚。虽然我们catch了插入“李四”方法的异常，使异常不会被外围方法感知，但是插入“李四”方法事务被回滚，内部方法和外围方法属于同一个事务，
     * 所以整体事务被回滚了。并且这个方法会抛出这个异常：
     * org.springframework.transaction.UnexpectedRollbackException: Transaction rolled back because it has been marked as rollback-only
     */
    @Test
    public void testTransaction_required_required_exception_try(){
        transactionPropagationDataSource1Example.transaction_required_required_exception_try();
    }

    /**
     * PROPAGATION_SUPPORTS 支持当前事务，如果当前没有事务，就以非事务方式执行。
     *
     * 结果： 张三（插入），李四（插入） </br>
     * 外围方法未开启事务，插入“张三”、“李四”方法也均未开启事务，因为不存在事务所以无论外围方法或者内部方法抛出异常都不会回滚。
     *
     * 不开启事务时，数据源的提交方式模式为true,自动提交的，即SQL执行完就自动提交了。
     * 可以看 @author why技术 的 这篇文章《当Transactional碰到锁，有个大坑，要小心。》 https://juejin.cn/post/6999856083208503333
     */
    @Test
    public void testNotransaction_supports_supports_exception() {
        transactionPropagationDataSource1Example.notransaction_supports_supports_exception();
    }

    /**
     * 结果：张三(插入)，李四（插入） </br>
     * 外围方法未开启事务，插入“张三”、“李四”方法也均未开启事务，因为不存在事务所以无论外围方法或者内部方法抛出异常都不会回滚。
     */
    @Test
    public void testNotransaction_exception_supports_spports() {
        transactionPropagationDataSource1Example.notransaction_exception_supports_supports();
    }

    /**
     * 结果： 张三（未插入），李四（未插入）</br>
     * 外围方法开启事务，插入“张三”、“李四”方法都在外围方法的的事务中运行，加入外围方法事务，所以三个方法在同一个事务。外围方法或者内部方法抛出异常，
     * 整个事务全部回滚。
     */
    @Test
    public void testTransaction_supports_supports_exception() {
        transactionPropagationDataSource1Example.transaction_supports_supports_exception();
    }

    /**
     * 结果：张三（未插入），李四（未插入）</br>
     *
     * 外围方法开启事务，插入“张三”，“李四”方法都在外围方法的事务中运行，加入外围方法事务，所以三个方法同一个事务。外围方法或内部方法抛出异常，
     * 整个事务全部回滚。
     */
    @Test
    public void testTransaction_exception_supports_supports() {
        transactionPropagationDataSource1Example.transaction_exception_supports_supports();
    }

    //-------------------------------------------------------------------------------------
    // REQUIRED和SUPPORTS在外围方法支持事务的时候没有区别，均加入外围方法的事务中。
    // 当外围方法不支持事务，REQUIRED开启新的事务而SUPPORTS不开启事务
    // REQUIRED的事务一定和外围方法事务统一。如果外围方法没有事务，每一个内部REQUIRED方法都会开启一个新的事务，互不干扰。
    //---------------------------------------------------------------------------------------

    /**
     * PROPAGATION_REQUIRES_NEW 	新建事务，如果当前存在事务，把当前事务挂起。
     * 结果：张三（插入），李四（插入）</br>
     *
     * 外围方法未开启事务，插入“张三”，“李四”方法都在自己的事务中独立运行。外围方法抛出异常，插入“张三”，“李四”事务均不回滚。
     */
    @Test
    public void testNotransaction_exception_requiresNew_requiresNew() {
        transactionPropagationDataSource1Example.notransaction_exception_requiresNew_requiresNew();
    }

    /**
     * 结果：张三（插入），李四（未插入） </br>
     *
     * 外围方法未开启事务，插入“张三”，“李四”方法都在自己的事务中独立运行。插入“李四”方法抛出异常只会导致插入“李四”方法中的事务被回滚，
     * 不会影响插入“张三”方法的事务。
     */
    @Test
    public void testNotransaction_requiresNew_requiresNew_exception() {
        transactionPropagationDataSource1Example.notransaction_requiresNew_requiresNew_exception();
    }

    /**
     * 结果： 张三（未插入）， 李四（插入），王五（插入） </br>
     *
     * 外围方法开启事务，插入“张三”方法和外围方法一个事务，插入“李四”方法，插入“王五”方法分别在独立的新建事务中，
     * 外围方法抛出异常只回滚和外围方法同一事务的方法，故插入“张三”的方法回滚。
     */
    @Test
    public void testTransaction_exception_required_requiresNew_requiresNew() {
        transactionPropagationDataSource1Example.transaction_exception_required_requiresNew_requiresNew();
    }

    /**
     * 结果：张三（未插入），李四（插入），王五（未插入） </br>
     *
     * 外围方法开启事务，插入“张三”方法和外围方法一个事务，插入“李四”方法，插入“王五”方法分别在独立的新建事务中。插入“王五”方法抛出异常，首先插入
     * “王五”方法的事务被回滚，异常继续抛出被外围方法感知，外围方法事务亦被回滚，故插入“张三”方法也被回滚。
     */
    @Test
    public void testTransaction_requires_requiresNew_requiresNew_exception() {
        transactionPropagationDataSource1Example.transaction_required_requiresNew_requiresNew_exception();;
    }

    /**
     * 结果：张三（插入），李四（插入），王五（未插入） </br>
     *
     * 外围方法开启事务，插入“张三”方法和外围方法一个事务，插入“李四”方法，插入“王五”方法分别在独立的新建事务中。插入“王五”方法抛出异常，首先插入“王五”方法的事务
     * 被回滚，异常被Catch不会被外围方法感知，外围方法事务不回滚。故插入“张三”方法插入成功。
     */
    @Test
    public void testTransaction_required_requiresNew_requiresNew_exception_try() {
        transactionPropagationDataSource1Example.transaction_required_requiresNew_requiresNew_exception_try();
    }
    //----------------------------------------------------------------------------------------------------
    // REQUIRES_NEW标注方法无论外围方法是否开启事务，内部REQUIRES_NEW方法均会开启独立事务，且和外围方法也不在同一个事务中，内部方法和外围方法、内部方法之间均不互相干扰。
    // 当外围方法不开启事务的时候，REQUIRED和REQUIRES_NEW标注的内部方法效果相同。
    // ------------------------------------------------------------------------------------------------------

    /**
     * PROPAGATION_NOT_SUPPORTED 	以非事务方式执行操作，如果当前存在事务，就把当前事务挂起。
     * 结果：张三（插入），李四（插入），王五（插入） </br>
     *
     * 外围方法未开启事务，插入“张三”方法在自己的事务中运行，插入“李四”方法不在任何事务中运行。外围方法抛出异常，但是外围方法没有事务，
     * 所以其他内部事务方法不会被回滚，非事务方法更不会被回滚。
     */
    @Test
    public void testNotransaction_exception_required_notSupported() {
        transactionPropagationDataSource1Example.notransaction_exception_required_notSupport();
    }

    /**
     * 结果：张三（插入），李四（插入），王五（插入） </br>
     *
     * 外围方法未开启事务，插入“张三”方法在自己的事务中运行，插入“李四”方法不在任何事务中运行。
     * 插入“李四”方法抛出异常，首先因为插入“李四”方法没有开启事务，所以“李四”方法不会回滚，外围方法感知异常。但是因为外围方法没有事务，所以外围方法也不会被回滚。
     */
    @Test
    public void testNotransaction_required_notSupported_exception() {
        transactionPropagationDataSource1Example.notransaction_required_notSupport_exception();
    }

    /**
     * 结果： 张三（未插入）， 李四（插入），王五（插入） </br>
     *
     * 外围方法开启事务，因为插入“张三”方法传播为required，所以和外围方法同一个事务。插入“李四”方法不在任何事务中运行。
     * 外围方法抛出异常，外围方法所在的事务将会被回滚。因为插入“张三”方法和外围方法同一个事务，所以将会回滚。
     */
    @Test
    public void testTransaction_exception_required_notSupported() {
        transactionPropagationDataSource1Example.transaction_exception_required_notSuppored();
    }

    /**
     * 结果：张三（未插入），李四（插入） </br>
     *
     * 外围方法开启事务，因为插入“张三”方法传播为required，所以和外围方法同一个事务。插入“李四”方法不在任何事务中运行。
     * 插入“李四”方法抛出异常，因为此方法不开启事务，所以此方法不会被回滚，外围方法接收到了异常，所以外围事务需要回滚，因插入“张三”方法和外围方法
     * 同一事务，故被回滚。
     */
    @Test
    public void testTransaction_required_notSupported_excepton() {
        transactionPropagationDataSource1Example.transaction_required_notSupported_exception();
    }
    //----------------------------------------------------------------------------
    // NOT_SUPPORTED明确表示不开启事务。
    //----------------------------------------------------------------------------

    /**
     * PROPAGATION_MANDATORY 	使用当前的事务，如果当前没有事务，就抛出异常。
     * 结果： 张三（未插入） </br>
     * 外围方法未开启事务。内部“张三”方法执行的时候因为外围没有事务而直接抛出异常，具体插入方法都没有机会执行。
     */
    @Test
    public void testNotransaction_mandatory() {
        transactionPropagationDataSource1Example.notransaction_mandatory();
    }

    /**
     * 结果：张三（未插入），李四（未插入）</br>
     *
     * 外围方法开启事务，插入“张三”方法和插入“李四”方法都加入外围方法事务，外围方法抛出异常，事务回滚。
     */
    @Test
    public void testTransaction_exception_mandatory_mandatory() {
        transactionPropagationDataSource1Example.transaction_exception_mandatory_mandatory();
    }

    /**
     * 结果： 张三（未插入），李四（未插入）</br>
     *
     * 外围方法开启事务，插入“张三”方法和插入“李四”方法都加入外围方法事务，内部方法抛出异常，整个事务回滚。
     */
    @Test
    public void testTransaction_mandatory_mandatory_exception() {
        transactionPropagationDataSource1Example.transaction_mandatory_mandatory_exception();
    }

    /**
     * PROPAGATION_NEVER 	以非事务方式执行，如果当前存在事务，则抛出异常。
     *
     * 结果：张三（未插入）</br>
     * 外围方法开启事务。内部插入“张三”方法执行的时候因为外围有事务而直接抛出异常，具体插入方法都没有机会执行。
     */
    @Test
    public void testTransaction_never() {
        transactionPropagationDataSource1Example.transaction_never();
    }

    /**
     * 结果： 张三（插入），李四（插入）</br>
     *
     * 外围方法没有开启事务，插入“张三”方法和插入“李四”方法也均无事务，任何异常都不会回滚。
     */
    @Test
    public void testNotransaction_exception_never_never() {
        transactionPropagationDataSource1Example.notransaction_exception_never_never();
    }

    /**
     * 结果：张三（插入），李四（插入）</br>
     *
     * 外围方法未开启事务，插入“张三”方法和插入“李四”方法也均无事务，任何异常都不会回滚。
     */
    @Test
    public void testNotransaction_never_never_exception() {
        transactionPropagationDataSource1Example.notransaction_never_never_exception();
    }

    /**
     * PROPAGATION_NESTED 	如果当前存在事务，则在嵌套事务内执行。如果当前没有事务，则执行与PROPAGATION_REQUIRED类似的操作。
     * 结果： 张三（插入）， 李四（插入）</br>
     *
     * 外围方法开启事务，插入“张三”方法和插入“李四”方法为外围方法的子事务，外围方法事务回滚，相应的子事务也会回滚。
     */
    @Test
    public void testTransaction_exception_nested_nested() {
        transactionPropagationDataSource1Example.transaction_exception_nested_nested();
    }

    /**
     * 结果：张三（未插入），李四（未插入）</br>
     *
     * 外围方法开启事务，插入“张三”方法和插入“李四”方法为外围方法的子事务，插入“李四”方法抛出异常，相应的子事务回滚，异常被外围方法感知，
     * 外围方法事务回滚，其他子事务即插入“张三”方法事务也回滚了。
     */
    @Test
    public void testTransaction_nested_nested_exception() {
        transactionPropagationDataSource1Example.transaction_nested_nested_exception();
    }

    /**
     * 结果：张三（插入），李四（未插入）</br>
     * 外围方法开启事务，插入“张三”方法和插入“李四”方法为外围方法的子事务，插入“李四”方法抛出异常，相应的子事务回滚，异常被捕获外围方法不可知，
     * 故外围方法事务无需回滚。
     */
    @Test
    public void testTransaction_nested_nested_exceptin_try() {
        transactionPropagationDataSource1Example.transaction_nested_nested_exception_try();
    }
}
