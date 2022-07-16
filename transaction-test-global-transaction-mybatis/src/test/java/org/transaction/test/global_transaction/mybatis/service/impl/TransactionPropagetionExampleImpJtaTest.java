package org.transaction.test.global_transaction.mybatis.service.impl;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.transaction.test.global_transaction.mybatis.service.datasource1.TransactionPropagationExample;

/**
 * 测试各种多数据源下，使用JTA事务的情况下，Spring事务的传播性
 *
 * @author JerryTse
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/applicationContext-atomikos.xml"})
public class TransactionPropagetionExampleImpJtaTest {

    @Autowired
    private TransactionPropagationExample transactionPropagationExample;

    @Before
    public void setUpBeforeClass() throws Exception {
        transactionPropagationExample.truncated();
    }
}
