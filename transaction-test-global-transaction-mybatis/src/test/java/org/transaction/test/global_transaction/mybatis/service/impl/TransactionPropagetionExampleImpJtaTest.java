package org.transaction.test.global_transaction.mybatis.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.transaction.test.global_transaction.mybatis.service.common.TransactionPropagationCommonExample;
import org.transaction.test.global_transaction.mybatis.service.datasource1.TransactionPropagationExample;

/**
 * 测试各种多数据源下，使用JTA分布式事务的情况下，Spring事务的传播性
 *
 * 这个类要跟TransactionPropagationCommonExampleImplTest这个类对比观察。
 * @author JerryTse
 *
 * 什么是JTA(Java Transaction API),分布式事务JTA
 * https://www.liaoxuefeng.com/wiki/1252599548343744/1282383642886177 《使用声明式事务》廖雪峰
 * Spring为啥要抽象出PlatformTransactionManager和TransactionStatus？原因是JavaEE除了提供JDBC事务外，它还支持分布式事务JTA（Java Transaction API）。
 * 分布式事务是指多个数据源（比如多个数据库，多个消息系统）要在分布式环境下实现事务的时候，应该怎么实现。
 * 分布式事务实现起来非常复杂，简单地说就是通过一个分布式事务管理器实现两阶段提交，但本身数据库事务就不快，基于数据库事务实现的分布式事务就慢得难以忍受，所以使用率不高。
 * Spring为了同时支持JDBC和JTA两种事务模型，就抽象出PlatformTransactionManager。因为我们的代码只需要JDBC事务，因此，在AppConfig中，需要再定义一个PlatformTransactionManager对应的Bean，它的实际类型是DataSourceTransactionManager：
 * 使用编程的方式使用Spring事务仍然比较繁琐，更好的方式是通过声明式事务来实现。
 * 使用声明式事务非常简单，除了在AppConfig中追加一个上述定义的PlatformTransactionManager外，再加一个@EnableTransactionManagement就可以启用声明式事务：
 * 然后，对需要事务支持的方法，加一个@Transactional注解：
 *      // 此public方法自动具有事务支持:
 *     @Transactional
 *     public User register(String email, String password, String name) {
 *        ...
 *     }
 * 或者更简单一点，直接在Bean的class处加上@Transactional，表示所有public方法都具有事务支持：
 * @Component
 * @Transactional
 * public class UserService {
 *     ...
 * }
 * Spring对一个声明式事务的方法，如何开启事务支持？原理仍然是AOP代理，即通过自动创建Bean的Proxy实现：
 * 注意：声明了@EnableTransactionManagement后，不必额外添加@EnableAspectJAutoProxy。
 * 回滚事务
 * 默认情况下，如果发生了RuntimeException，Spring的声明式事务将自动回滚。在一个事务方法中，如果程序判断需要回滚事务，只需抛出RuntimeException，例如：
 * 如果要针对Checked Exception回滚事务，需要在@Transactional注解中写出来：
 * @Transactional(rollbackFor = {RuntimeException.class, IOException.class})
 * 上述代码表示在抛出RuntimeException或IOException时，事务将回滚。
 * 为了简化代码，我们强烈建议业务异常体系从RuntimeException派生，这样就不必声明任何特殊异常即可让Spring的声明式事务正常工作：
 * 事务传播
 * 因此，Spring的声明式事务为事务传播定义了几个级别，默认传播级别就是REQUIRED，它的意思是，如果当前没有事务，就创建一个新事务，如果当前有事务，就加入到当前事务中执行。
 * 默认的事务传播级别是REQUIRED，它满足绝大部分的需求。还有一些其他的传播级别：
 * SUPPORTS：表示如果有事务，就加入到当前事务，如果没有，那也不开启事务执行。这种传播级别可用于查询方法，因为SELECT语句既可以在事务内执行，也可以不需要事务；
 * MANDATORY：表示必须要存在当前事务并加入执行，否则将抛出异常。这种传播级别可用于核心更新逻辑，比如用户余额变更，它总是被其他事务方法调用，不能直接由非事务方法调用；
 * REQUIRES_NEW：表示不管当前有没有事务，都必须开启一个新的事务执行。如果当前已经有事务，那么当前事务会挂起，等新事务完成后，再恢复执行；
 *      廖老师，REQUIRES_NEW这种事务传播方式需要数据库支持吧？我查了下MySQL5.6官方文档，里面提到MySQL不支持事务嵌套，当在一个事务内再次用start transaction开启一个新事务时，会先隐式的执行commit命令提交之前的事务。那这个REQUIRES_NEW是如何实现的呢？是需要特定数据库的支持吧？或者新打开了一个db连接吗
 *      应该是新开一个连接，这样实现起来最简单
 * NOT_SUPPORTED：表示不支持事务，如果当前有事务，那么当前事务会挂起，等这个方法执行完成后，再恢复执行；
 * NEVER：和NOT_SUPPORTED相比，它不但不支持事务，而且在监测到当前有事务时，会抛出异常拒绝执行；
 * NESTED：表示如果当前有事务，则开启一个嵌套级别事务，如果当前没有事务，则开启一个新事务。
 * 上面这么多种事务的传播级别，其实默认的REQUIRED已经满足绝大部分需求，SUPPORTS和REQUIRES_NEW在少数情况下会用到，其他基本不会用到，因为把事务搞得越复杂，不仅逻辑跟着复杂，而且速度也会越慢。
 * 现在只剩最后一个问题了：Spring是如何传播事务的？
 * 我们在JDBC中使用事务(https://www.liaoxuefeng.com/wiki/1252599548343744/1321748500840481)的时候，是这么个写法：
 * Connection conn = openConnection();
 * try {
 *     // 关闭自动提交:
 *     conn.setAutoCommit(false);
 *     // 执行多条SQL语句:
 *     insert(); update(); delete();
 *     // 提交事务:
 *     conn.commit();
 * } catch (SQLException e) {
 *     // 回滚事务:
 *     conn.rollback();
 * } finally {
 *     conn.setAutoCommit(true);
 *     conn.close();
 * }
 * Spring使用声明式事务，最终也是通过执行JDBC事务来实现功能的，那么，一个事务方法，如何获知当前是否存在事务？
 * 答案是使用ThreadLocal。Spring总是把JDBC相关的Connection和TransactionStatus实例绑定到ThreadLocal。
 * 如果一个事务方法从ThreadLocal未取到事务，那么它会打开一个新的JDBC连接，同时开启一个新的事务，否则，它就直接使用从ThreadLocal获取的JDBC连接以及TransactionStatus。
 * 因此，事务能正确传播的前提是，方法调用是在一个线程内才行。如果像下面这样写：
 * 换句话说，事务只能在当前线程传播，无法跨线程传播。
 * 那如果我们想实现跨线程传播事务呢？原理很简单，就是要想办法把当前线程绑定到ThreadLocal的Connection和TransactionStatus实例传递给新线程，但实现起来非常复杂，根据异常回滚更加复杂，不推荐自己去实现。
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/applicationContext-atomikos.xml"})
public class TransactionPropagetionExampleImpJtaTest {

    @Autowired
    private TransactionPropagationCommonExample transactionPropagationCommonExampleExample;

    @Before
    public void setUpBeforeClass() throws Exception {
        transactionPropagationCommonExampleExample.truncated();
    }

    /**
     * 结果：张三（插入），李四（插入）</br>
     *
     */
    @Test
    public void testNotransaction_exception_noTransaction_noTransaction() {
        transactionPropagationCommonExampleExample.notransaction_exception_notransaction_notransaction();
    }

    /**
     * 结果：张三（插入），李四（插入）</br>
     *
     *
     */
    @Test
    public void testNotransaction_notransaction_notransaction_exception() {
        transactionPropagationCommonExampleExample.notransaction_notransaction_notransaction_exception();
    }

    /**
     * 结果： 张三（未插入），李四（未插入）</br>
     *
     * JTA多数据源，外围方法开启事务，外围方法内的方法就应该在同一个事务中，外围方法抛出异常，整个事务回滚。
     */
    @Test
    public void testTransaction_exception_notransaction_notransaction() {
        transactionPropagationCommonExampleExample.transaction_exception_notransaction_notransaction();
    }

    /**
     * 结果： 张三（未插入），李四（未插入） </br>
     *
     */
    @Test
    public void testTransaction_notransaction_notransaction_exception() {
        transactionPropagationCommonExampleExample.transaction_notransaction_notransaction_exception();
    }


    /**
     * 结果：张三（插入），李四（插入）</br>
     *
     */
    @Test
    public void testTransaction_notransaction_notransaction_exception_try() {
        transactionPropagationCommonExampleExample.transaction_noTransaction_noTransaction_exception_try();
    }

    /**
     * 结果：张三（插入），李四（插入）</br>
     *
     */
    @Test
    public void testNoTransaction() {
        transactionPropagationCommonExampleExample.notransaction_required_required();
    }

    /**
     * 结果：张三（插入），李四（插入）</br>
     * 外围方法没有事务：插入“张三”，“李四”方法在自己的事务中独立运行，外围方法异常不影响内部插入“张三”，“李四”方法独立事务
     */
    @Test
    public void testNotransaction_exception_required_required() {
        transactionPropagationCommonExampleExample.notransaction_exception_required_required();
    }

    /**
     * 结果：张三（插入），李四（未插入） </br>
     * 外围方法没有事务，插入“张三”，“李四”方法都在自己的事务中独立运行，所以插入“李四”方法抛出异常只会回滚插入“李四方法”，插入“张三”方法
     * 不受影响
     */
    @Test
    public void testNotransaction_required_required_exception() {
        transactionPropagationCommonExampleExample.notransaction_required_required_exception();
    }

    /**
     * 结果：张三（未插入），李四（未插入）</br>
     *
     * 外围方法开启事务，插入“张三”，“李四”方法都在外围方法的事务中运行，加入外围方法事务，所以三个方法同一个事务，
     * 外围方法或内部方法抛出异常，整个事务全部回滚。
     * 但是这个类TransactionPropagationCommonExampleImplTest就不行。
     */
    @Test
    public void testTransaction_exception_required_required() {
        transactionPropagationCommonExampleExample.transaction_exception_required_required();
    }

    /**
     * 结果：张三（未插入），李四（未插入）</br>
     *
     * 外围方法开启事务，插入“张三”，“李四”方法都在外围方法的事务中运行，加入外围方法事务，所以三个方法同一个事务。
     * 外围方法或内部方法抛出异常，整个事务全部回滚。
     */
    @Test
    public void testTransaction_required_required_exception() {
        transactionPropagationCommonExampleExample.transaction_required_required_exception();
    }

    /**
     * 结果： 张三（未插入），李四（未插入）</br>
     *
     * 外围方法开启事务，插入“张三”，“李四”方法都在外围方法的事务中运行，加入外围方法事务，所以三个方法同一个事务。外围方法或内部方法抛出异常，
     * 整个事务全部回滚。虽然我们catch了插入“李四”方法的异常，使异常不会被外围方法感知，但是插入“李四”方法事务被回滚，内部方法和外围方法属于同一个事务，
     * 所以整体事务被回滚了。
     *
     * 该方法最终会抛出这个异常：org.springframework.transaction.UnexpectedRollbackException:
     * JTA transaction unexpectedly rolled back (maybe due to a timeout);
     * nested exception is javax.transaction.RollbackException: Transaction set to rollback only
     */
    @Test
    public void testTransaction_required_required_exception_try() {
        transactionPropagationCommonExampleExample.transaction_required_required_exception_try();
    }

    /**
     * PROPAGATION_SUPPORTS 	支持当前事务，如果当前没有事务，就以非事务方式执行。 常用于查询方法。
     *
     * 结果：张三（插入），李四（插入）</br>
     * 外围方法开启事务，插入“张三”，“李四方法”也均未开启事务，因为不存在事务所以无论外围方法或者内部方法抛出异常都不会回滚。
     */
    @Test
    public void testNotransaction_supports_supports_exception() {
        transactionPropagationCommonExampleExample.notransaction_supports_supports_exception();
    }

    /**
     * 结果：张三（插入），李四（插入）</br>
     *
     * 外围方法未开启事务，插入“张三”，“李四”方法也均未开启事务，因为不存在事务所以无论外围方法或者内部方法抛出异常都不会回滚。
     */
    @Test
    public void testNotransaction_exception_supports_supports() {
        transactionPropagationCommonExampleExample.notransaction_exception_supports_supports();
    }

    /**
     * 结果：张三（未插入），李四（未插入）</br>
     * 外围方法开启事务，插入“张三”，”李四“方法都在外围方法的事务中运行，加入外围方法事务，所以三个方法同一个事务。外围方法或内部方法抛出异常，整个事务全部回滚。
     */
    @Test
    public void testTransaction_supports_supports_exception() {
        transactionPropagationCommonExampleExample.transaction_supports_supports_exception();
    }

    /**
     * 结果：张三（未插入），李四（未插入）</br>
     *
     * 外围方法开启事务，插入“张三”，“李四”方法都在外围方法的事务中运行，加入外围方法事务，所以三个方法同一个事务。
     * 外围方法或内部方法抛出异常，整个事务全部回滚。
     */
    @Test
    public void testTransaction_exception_supports_supports() {
        transactionPropagationCommonExampleExample.transaction_exception_supports_supports();
    }
    //-----------------------------------------------------------------------------------------
    // REQUIRED和SUPPORTS在外围方法支持事务的时候没有区别，均加入外围方法的事务中。
    // 当外围方法不支持事务，REQUIRED开启新的事务而SUPPORTS不开启事务
    // REQUIRED的事务一定和外围方法事务统一。如果外围方法没有事务，每一个内部REQUIRED方法都会开启一个新的事务，互不干扰。
    // ------------------------------------------------------------------------------------------

    /**
     * PROPAGATION_REQUIRES_NEW 	新建事务，如果当前存在事务，把当前事务挂起。
     * 结果：张三（插入），李四（插入）</br>
     *
     * 外围方法未开启事务，插入“张三”，“李四”方法都在自己的事务中独立运行。外围方法抛出异常，插入“张三”，“李四”方法事务均不回滚。
     */
    @Test
    public void testNotransaction_exception_requiresNew_requiresNew() {
        transactionPropagationCommonExampleExample.notransaction_exception_requiresNew_requiresNew();
    }

    /**
     * 结果：张三（插入），李四（未插入）</br>
     *
     * 外围方法未开启事务，插入“张三”，“李四”方法都在自己的事务中独立运行。插入“李四”方法抛出异常只会导致插入“李四”方法中的事务被回滚，
     * 不会影响插入“张三”方法的事务。
     */
    @Test
    public void testNotransaction_requiresNew_requiresNew_exception() {
        transactionPropagationCommonExampleExample.notransaction_requiresNew_requiresNew_exception();
    }

    /**
     * 结果：张三（未插入），李四（插入），王五（插入）</br>
     *
     * 外围方法开启事务，插入“张三”方法和外围方法一个事务，插入“李四”方法、插入“王五”方法分别在独立的新建事务中，外围方法抛出异常只回滚和外围方法
     * 同一事务的方法，故插入“张三”的方法回滚。
     *
     * 这个方法报错了，org.springframework.transaction.TransactionSuspensionNotSupportedException:
     * JtaTransactionManager needs a JTA TransactionManager for suspending a transaction: specify the 'transactionManager' or 'transactionManagerName' property
     *
     * 上面这个报错没解决，
     * https://github.com/liuchenwei2000/Spring/blob/d4f78b381e2afd46164966d12fb9950b76b207af/Transaction/src/main/java/transaction/manager/jpa.xml
     *         使用 JTA 作为持久化手段时，JtaTransactionManager 可用于事务管理。
     * 	    JtaTransactionManager 把事务管理委托给一个 JTA 的实现，JTA 指定一个标准的 API 来协调应用程序和一个或多个数据源之间的事务。
     * 	    这里的 transactionManagerName 属性指定了一个 JTA 事务管理器，这个事务管理器应该能够通过 JNDI 找到。
     *                 <property name="transactionManagerName" value="java:/TransactionManager"></property>
     *
     */
    @Test
    public void testTransaction_exception_required_requiresNew_requiresNew() {
        transactionPropagationCommonExampleExample.transaction_exception_required_requiresNew_requiresNew();
    }




}
