package org.transaction.test.global_transaction.mybatis.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.transaction.test.global_transaction.mybatis.bean.datasource2.DataSource2User2;
import org.transaction.test.global_transaction.mybatis.service.datasource2.User2Service;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/applicationContext.xml"})
public class User2ServiceImplTest {

    @Autowired
    private User2Service user2Service;

    @Before
    public void setUpBeforeClass() throws Exception{
        user2Service.truncate();
    }

    @Test
    public void testAdd() {
        DataSource2User2 user2 = new DataSource2User2();
        user2.setName("李四");
        user2Service.add(user2);
    }
}
