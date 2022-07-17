package org.transaction.test.local_transaction.mybatis.service.impl;

import org.springframework.stereotype.Service;
import org.transaction.test.local_transaction.mybatis.service.User1Service;

/**
 * https://www.liaoxuefeng.com/wiki/1252599548343744/1282383642886177 《使用声明式事务》 廖雪峰
 *
 * 使用Spring操作JDBC虽然方便，但是我们在前面讨论JDBC的时候，讲到过JDBC事务，如果要在Spring中操作事务，没必要手写JDBC事务，可以使用Spring提供的高级接口来操作事务。
 * Spring提供了一个PlatformTransactionManager来表示事务管理器，所有的事务都由它负责管理。而事务由TransactionStatus表示。如果手写事务代码，使用try...catch如下：
 * TransactionStatus tx = null;
 * try {
 *     // 开启事务:
 *     tx = txManager.getTransaction(new DefaultTransactionDefinition());
 *     // 相关JDBC操作:
 *     jdbcTemplate.update("...");
 *     jdbcTemplate.update("...");
 *     // 提交事务:
 *     txManager.commit(tx);
 * } catch (RuntimeException e) {
 *     // 回滚事务:
 *     txManager.rollback(tx);
 *     throw e;
 * }
 * Spring为啥要抽象出PlatformTransactionManager和TransactionStatus？原因是JavaEE除了提供JDBC事务外，它还支持分布式事务JTA（Java Transaction API）。
 * 分布式事务是指多个数据源（比如多个数据库，多个消息系统）要在分布式环境下实现事务的时候，应该怎么实现。
 * 分布式事务实现起来非常复杂，简单地说就是通过一个分布式事务管理器实现两阶段提交，但本身数据库事务就不快，基于数据库事务实现的分布式事务就慢得难以忍受，所以使用率不高。
 *
 * Spring为了同时支持JDBC和JTA两种事务模型，就抽象出PlatformTransactionManager。
 * 因为我们的代码只需要JDBC事务，因此，在AppConfig中，需要再定义一个PlatformTransactionManager对应的Bean，它的实际类型是DataSourceTransactionManager：
 * @Configuration
 * @ComponentScan
 * @PropertySource("jdbc.properties")
 * public class AppConfig {
 *     ...
 *     @Bean
 *     PlatformTransactionManager createTxManager(@Autowired DataSource dataSource) {
 *         return new DataSourceTransactionManager(dataSource);
 *     }
 * }
 * 使用编程的方式使用Spring事务仍然比较繁琐，更好的方式是通过声明式事务来实现。
 * 使用声明式事务非常简单，除了在AppConfig中追加一个上述定义的PlatformTransactionManager外，再加一个@EnableTransactionManagement就可以启用声明式事务：
 * @Configuration
 * @ComponentScan
 * @EnableTransactionManagement // 启用声明式
 * @PropertySource("jdbc.properties")
 * public class AppConfig {
 *     ...
 * }
 * 然后，对需要事务支持的方法，加一个@Transactional注解：
 * @Component
 * public class UserService {
 *     // 此public方法自动具有事务支持:
 *     @Transactional
 *     public User register(String email, String password, String name) {
 *        ...
 *     }
 * }
 * 或者更简单一点，直接在Bean的class处加上，表示所有public方法都具有事务支持：
 * @Component
 * @Transactional
 * public class UserService {
 *     ...
 * }
 * Spring对一个声明式事务的方法，如何开启事务支持？原理仍然是AOP代理，即通过自动创建Bean的Proxy实现：
 * public class UserService$$EnhancerBySpringCGLIB extends UserService {
 *     UserService target = ...
 *     PlatformTransactionManager txManager = ...
 *
 *     public User register(String email, String password, String name) {
 *         TransactionStatus tx = null;
 *         try {
 *             tx = txManager.getTransaction(new DefaultTransactionDefinition());
 *             target.register(email, password, name);
 *             txManager.commit(tx);
 *         } catch (RuntimeException e) {
 *             txManager.rollback(tx);
 *             throw e;
 *         }
 *     }
 *     ...
 * }
 * 注意：声明了@EnableTransactionManagement后，不必额外添加@EnableAspectJAutoProxy。
 * 回滚事务
 * 默认情况下，如果发生了RuntimeException，Spring的声明式事务将自动回滚。在一个事务方法中，如果程序判断需要回滚事务，只需抛出RuntimeException，例如：
 * @Transactional
 * public buyProducts(long productId, int num) {
 *     ...
 *     if (store < num) {
 *         // 库存不够，购买失败:
 *         throw new IllegalArgumentException("No enough products");
 *     }
 *     ...
 * }
 * 如果要针对Checked Exception回滚事务，需要在@Transactional注解中写出来：
 * @Transactional(rollbackFor = {RuntimeException.class, IOException.class})
 * public buyProducts(long productId, int num) throws IOException {
 *     ...
 * }
 * 上述代码表示在抛出RuntimeException或IOException时，事务将回滚。
 * 为了简化代码，我们强烈建议业务异常体系从RuntimeException派生，这样就不必声明任何特殊异常即可让Spring的声明式事务正常工作：
 * 事务边界
 * 在使用事务的时候，明确事务边界非常重要。对于声明式事务，例如，下面的register()方法：
 * @Component
 * public class UserService {
 *     @Transactional
 *     public User register(String email, String password, String name) { // 事务开始
 *        ...
 *     } // 事务结束
 * }
 * 它的事务边界就是register()方法开始和结束。
 * 类似的，一个负责给用户增加积分的addBonus()方法：
 * @Component
 * public class BonusService {
 *     @Transactional
 *     public void addBonus(long userId, int bonus) { // 事务开始
 *        ...
 *     } // 事务结束
 * }
 * 它的事务边界就是addBonus()方法开始和结束。
 * 在现实世界中，问题总是要复杂一点点。用户注册后，能自动获得100积分，因此，实际代码如下：
 * @Component
 * public class UserService {
 *     @Autowired
 *     BonusService bonusService;
 *
 *     @Transactional
 *     public User register(String email, String password, String name) {
 *         // 插入用户记录:
 *         User user = jdbcTemplate.insert("...");
 *         // 增加100积分:
 *         bonusService.addBonus(user.id, 100);
 *     }
 * }
 * 现在问题来了：调用方（比如RegisterController）调用UserService.register()这个事务方法，它在内部又调用了BonusService.addBonus()这个事务方法，一共有几个事务？如果addBonus()抛出了异常需要回滚事务，register()方法的事务是否也要回滚？
 * 问题的复杂度是不是一下子提高了10倍？
 * 事务传播
 * 要解决上面的问题，我们首先要定义事务的传播模型。
 * 假设用户注册的入口是RegisterController，它本身没有事务，仅仅是调用UserService.register()这个事务方法：
 * @Controller
 * public class RegisterController {
 *     @Autowired
 *     UserService userService;
 *
 *     @PostMapping("/register")
 *     public ModelAndView doRegister(HttpServletRequest req) {
 *         String email = req.getParameter("email");
 *         String password = req.getParameter("password");
 *         String name = req.getParameter("name");
 *         User user = userService.register(email, password, name);
 *         return ...
 *     }
 * }
 * 因此，UserService.register()这个事务方法的起始和结束，就是事务的范围。
 * 我们需要关心的问题是，在UserService.register()这个事务方法内，调用BonusService.addBonus()，我们期待的事务行为是什么：
 * @Transactional
 * public User register(String email, String password, String name) {
 *     // 事务已开启:
 *     User user = jdbcTemplate.insert("...");
 *     // ???:
 *     bonusService.addBonus(user.id, 100);
 * } // 事务结束
 * 对于大多数业务来说，我们期待BonusService.addBonus()的调用，和UserService.register()应当融合在一起，它的行为应该如下：
 * UserService.register()已经开启了一个事务，那么在内部调用BonusService.addBonus()时，BonusService.addBonus()方法就没必要再开启一个新事务，直接加入到BonusService.register()的事务里就好了。
 * 其实就相当于：
 *     UserService.register()先执行了一条INSERT语句：INSERT INTO users ...
 *     BonusService.addBonus()再执行一条INSERT语句：INSERT INTO bonus ...
 * 因此，Spring的声明式事务为事务传播定义了几个级别，默认传播级别就是REQUIRED，它的意思是，如果当前没有事务，就创建一个新事务，如果当前有事务，就加入到当前事务中执行。
 * 我们观察UserService.register()方法，它在RegisterController中执行，因为RegisterController没有事务，因此，UserService.register()方法会自动创建一个新事务。
 * 在UserService.register()方法内部，调用BonusService.addBonus()方法时，因为BonusService.addBonus()检测到当前已经有事务了，因此，它会加入到当前事务中执行。
 * 因此，整个业务流程的事务边界就清晰了：它只有一个事务，并且范围就是UserService.register()方法。
 * 有的童鞋会问：把BonusService.addBonus()方法的@Transactional去掉，变成一个普通方法，那不就规避了复杂的传播模型吗？
 * 去掉BonusService.addBonus()方法的@Transactional，会引来另一个问题，即其他地方如果调用BonusService.addBonus()方法，那就没法保证事务了。例如，规定用户登录时积分+5：
 * @Controller
 * public class LoginController {
 *     @Autowired
 *     BonusService bonusService;
 *
 *     @PostMapping("/login")
 *     public ModelAndView doLogin(HttpServletRequest req) {
 *         User user = ...
 *         bonusService.addBonus(user.id, 5);
 *     }
 * }
 * 可见，BonusService.addBonus()方法必须要有@Transactional，否则，登录后积分就无法添加了。
 * 默认的事务传播级别是REQUIRED，它满足绝大部分的需求。还有一些其他的传播级别：
 * SUPPORTS：表示如果有事务，就加入到当前事务，如果没有，那也不开启事务执行。这种传播级别可用于查询方法，因为SELECT语句既可以在事务内执行，也可以不需要事务；
 * MANDATORY：表示必须要存在当前事务并加入执行，否则将抛出异常。这种传播级别可用于核心更新逻辑，比如用户余额变更，它总是被其他事务方法调用，不能直接由非事务方法调用；
 * REQUIRES_NEW：表示不管当前有没有事务，都必须开启一个新的事务执行。如果当前已经有事务，那么当前事务会挂起，等新事务完成后，再恢复执行；
 * NOT_SUPPORTED：表示不支持事务，如果当前有事务，那么当前事务会挂起，等这个方法执行完成后，再恢复执行；
 * NEVER：和NOT_SUPPORTED相比，它不但不支持事务，而且在监测到当前有事务时，会抛出异常拒绝执行；
 * NESTED：表示如果当前有事务，则开启一个嵌套级别事务，如果当前没有事务，则开启一个新事务。
 * 上面这么多种事务的传播级别，其实默认的REQUIRED已经满足绝大部分需求，SUPPORTS和REQUIRES_NEW在少数情况下会用到，其他基本不会用到，因为把事务搞得越复杂，不仅逻辑跟着复杂，而且速度也会越慢。
 * 定义事务的传播级别也是写在@Transactional注解里的：
 * 定义事务的传播级别也是写在@Transactional注解里的：
 * @Transactional(propagation = Propagation.REQUIRES_NEW)
 * public Product createProduct() {
 *     ...
 * }
 * 现在只剩最后一个问题了：Spring是如何传播事务的？
 * 我们在JDBC中使用事务的时候，是这么个写法：
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
 * 答案是使用ThreadLocal。Spring总是把JDBC相关的Connection和TransactionStatus实例绑定到ThreadLocal。如果一个事务方法从ThreadLocal未取到事务，那么它会打开一个新的JDBC连接，同时开启一个新的事务，否则，它就直接使用从ThreadLocal获取的JDBC连接以及TransactionStatus。
 * 因此，事务能正确传播的前提是，方法调用是在一个线程内才行。如果像下面这样写：
 * @Transactional
 * public User register(String email, String password, String name) { // BEGIN TX-A
 *     User user = jdbcTemplate.insert("...");
 *     new Thread(() -> {
 *         // BEGIN TX-B:
 *         bonusService.addBonus(user.id, 100);
 *         // END TX-B
 *     }).start();
 * } // END TX-A
 * 在另一个线程中调用BonusService.addBonus()，它根本获取不到当前事务，因此，UserService.register()和BonusService.addBonus()两个方法，将分别开启两个完全独立的事务。
 * 换句话说，事务只能在当前线程传播，无法跨线程传播。
 * 那如果我们想实现跨线程传播事务呢？原理很简单，就是要想办法把当前线程绑定到ThreadLocal的Connection和TransactionStatus实例传递给新线程，但实现起来非常复杂，根据异常回滚更加复杂，不推荐自己去实现。
 *
 * 廖老师，REQUIRES_NEW这种事务传播方式需要数据库支持吧？
 * 廖老师，REQUIRES_NEW这种事务传播方式需要数据库支持吧？我查了下MySQL5.6官方文档，里面提到MySQL不支持事务嵌套，当在一个事务内再次用start transaction开启一个新事务时，会先隐式的执行commit命令提交之前的事务。那这个REQUIRES_NEW是如何实现的呢？是需要特定数据库的支持吧？或者新打开了一个db连接吗
 */
@Service
public class User1ServiceImpl implements User1Service {
}
