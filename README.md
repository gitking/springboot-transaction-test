# springboot-transaction-test
2022-07-09 00:18 
学习资料：
https://segmentfault.com/a/1190000013341344
JerryTse 大佬的github https://github.com/TmTse/transaction-test
Spring官方文档： https://docs.spring.io/spring-framework/docs/5.0.9.RELEASE/spring-framework-reference/data-access.html#tx-propagation-required
Spring官方文档: https://docs.spring.io/spring-framework/docs/5.0.9.RELEASE/spring-framework-reference/data-access.html#tx-propagation-required
Spring官方文档: https://docs.spring.io/spring-framework/docs/current/reference/html/data-access.html#tx-propagation-required

马行空：

使用HibernateTransactionManager事务管理器 测试nested场景二方法三时，两个对象都会保存，官方文档表示不支持nested

.vtpkw：

老师，请问一下，关于2.2的验证方法3，按您文档中写的话，异常被catch不会被外围方法感知，外围方法事务不回滚，故插入“张三”方法插入成功。但是我实际中测试发现“张三”并没有被插入，我用的是jpa测试。

@Test
@Transactional(propagation = Propagation.REQUIRED)
public void transaction_required_requiresNew_requiresNew_exception_try() {
User1 user1 = new User1();
user1.setName("张三");
user1Service.addRequired(user1);

    User2 user2 = new User2();
    user2.setName("李四");
    user2Service.addRequiresNew(user2);
    User2 user3 = new User2();
    user3.setName("王五");
    try {
        user2Service.addRequiresNewException(user3);
    } catch (Exception e) {
        System.out.println("回滚");
    }
}

测试代码是这样的。
Hibernate: select next_val as id_val from generator for update
Hibernate: update generator set next_val= ? where next_val=?
Hibernate: select next_val as id_val from generator for update
Hibernate: update generator set next_val= ? where next_val=?
Hibernate: insert into user2 (name, id) values (?, ?)
Hibernate: select next_val as id_val from generator for update
Hibernate: update generator set next_val= ? where next_val=?
回滚
控制台也显示只有一次插入动作。

请问这是为什么呢？

https://segmentfault.com/a/1190000013341344