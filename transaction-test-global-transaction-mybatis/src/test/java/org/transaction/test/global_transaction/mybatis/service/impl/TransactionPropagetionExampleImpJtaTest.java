package org.transaction.test.global_transaction.mybatis.service.impl;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 测试各种多数据源下，使用JTA事务的情况下，Spring事务的传播性
 *
 * @author JerryTse
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/applicationContext-atomikos.xml"})
public class TransactionPropagetionExampleImpJtaTest {
}
