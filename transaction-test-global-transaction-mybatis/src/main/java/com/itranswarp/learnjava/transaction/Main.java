package com.itranswarp.learnjava.transaction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

/**
 * https://www.liaoxuefeng.com/wiki/1252599548343744/1321748500840481 《JDBC事务》廖雪峰
 *
 * 数据库事务（Transaction）是由若干个SQL语句构成的一个操作序列，有点类似于Java的synchronized同步。数据库系统保证在一个事务中的所有SQL要么全部执行成功，要么全部不执行，即数据库事务具有ACID特性：
 * Atomicity：原子性
 * Consistency：一致性
 * Isolation：隔离性
 * Durability：持久性
 * 数据库事务可以并发执行，而数据库系统从效率考虑，对事务定义了不同的隔离级别。SQL标准定义了4种隔离级别，分别对应可能出现的数据不一致的情况：
 * Isolation Level	            脏读（Dirty Read）	不可重复读（Non Repeatable Read）	幻读（Phantom Read）
 * Read Uncommitted	                Yes	                Yes	                            Yes
 * Read Committed	                -	                Yes	                            Yes
 * Repeatable Read	                -	                -	                            Yes
 * Serializable	                    -	                -	                             -
 * 这里我们不讨论详细的SQL事务，如果对SQL事务不熟悉，请参考SQL事务。 https://www.liaoxuefeng.com/wiki/1177760294764384/1179611198786848
 * 要在JDBC中执行事务，本质上就是如何把多条SQL包裹在一个数据库事务中执行。我们来看JDBC的事务代码：
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
 * 其中，开启事务的关键代码是conn.setAutoCommit(false)，表示关闭自动提交。提交事务的代码在执行完指定的若干条SQL语句后，调用conn.commit()。
 * 要注意事务不是总能成功，如果事务提交失败，会抛出SQL异常（也可能在执行SQL语句的时候就抛出了），此时我们必须捕获并调用conn.rollback()回滚事务。最后，在finally中通过conn.setAutoCommit(true)把Connection对象的状态恢复到初始值。
 * 实际上，默认情况下，我们获取到Connection连接后，总是处于“自动提交”模式，也就是每执行一条SQL都是作为事务自动执行的，这也是为什么前面几节我们的更新操作总能成功的原因：因为默认有这种“隐式事务”。
 * 只要关闭了Connection的autoCommit，那么就可以在一个事务中执行多条语句，事务以commit()方法结束。
 * 如果要设定事务的隔离级别，可以使用如下代码：
 * // 设定隔离级别为READ COMMITTED:
 * conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
 * 如果没有调用上述方法，那么会使用数据库的默认隔离级别。MySQL的默认隔离级别是REPEATABLE READ。
 *
 * https://www.liaoxuefeng.com/wiki/1252599548343744/1282383642886177 《使用声明式事务》 廖雪峰
 */
public class Main {

    /*
     * URL是由数据库厂商指定的格式，例如，MySQL的URL是：
     * jdbc:mysql://<hostname>:<port>/<db>?key1=value1&key2=value2
     * 假设数据库运行在本机localhost，端口使用标准的3306，数据库名称是learnjdbc，那么URL如下：
     * jdbc:mysql://localhost:3306/learnjdbc?useSSL=false&characterEncoding=utf8
     * 后面的两个参数表示不使用SSL加密，使用UTF-8作为字符编码（注意MySQL的UTF-8是utf8）。
     *
     * public key is not allowed 解决办法：static final String jdbcUrl = "jdbc:mysql://localhost/learnjdbc?allowPublicKeyRetrieval=true&useSSL=false&characterEncoding=utf8";
     */
    static final String mySQL5jdbcUrl = "jdbc:mysql://localhost/learnjdbc?useSSL=false&characterEncoding=utf8";
    static final String mySQL8jdbcUrl = "jdbc:mysql://localhost/learnjdbc?useSSL=false&characterEncoding=utf8&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    static final String jdbcUsername = "learn";
    static final String jdbcPassword = "learnpassword";

    public static void main(String[] args) throws SQLException {
        insertStudents("大黄", true, 3, 100);
        insertStudents("大头", false, 3, 101);
        try{
            testAutoCommitTrue();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        testAutoCommitFalse();
        testAutoCommitFalse02();
        testAutoCommitFalse03();
        try {
            TimeUnit.SECONDS.sleep(6);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static void insertStudents(String name, boolean gender, int grade, int score) throws SQLException {
        try (Connection conn = DriverManager.getConnection(mySQL8jdbcUrl, jdbcUsername, jdbcPassword)) {
            boolean isAutoCommit = conn.getAutoCommit();
            // 关闭自动提交事务（隐式事务）
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO students (name, gender, grade, score) values (?, ?, ?, ?)")) {
                ps.setString(1, name);
                ps.setBoolean(2, gender);
                ps.setInt(3, grade);
                ps.setInt(4, score);
                int n = ps.executeUpdate();
                System.out.println("插入多少行数据" + n);
            }
            if (score > 100) {
                conn.rollback();
                System.out.println("事务回滚");
            } else {
                conn.commit();
                System.out.println("事务提交");
            }
            // 恢复AutoCommit设置
            conn.setAutoCommit(isAutoCommit);

        }
    }

    static void testAutoCommitTrue() throws SQLException {
        try (Connection conn = DriverManager.getConnection(mySQL8jdbcUrl, jdbcUsername, jdbcPassword)){
            System.out.println("默认的开关是什么:" + conn.getAutoCommit());
            try (PreparedStatement ps = conn.prepareStatement("insert into students (name, gender, grade, score) values (?, ?, ?, ?)")){
                ps.setString(1, "AutoCommit成功true");
                /*
                 * https://blog.csdn.net/inrgihc/article/details/118713282
                 * https://dev.mysql.com/doc/connector-j/5.1/en/connector-j-reference-type-conversions.html
                 * 如果tinyInt1isBit =true(默认)，且tinyInt存储长度为1 ，则转为java.lang.Boolean;否则转为java.lang.Integer。
                 * 注: 是ResultSet.getObject() 方法
                 * tinyInt(1) 只用来代表Boolean含义的字段，且0代表False，1代表True。如果要存储多个数值，则定义为tinyInt(N), N>1。例如 tinyInt(2)。
                 * JDBC的URL增加 tinyInt1isBit=false参数，注意参数名区分大小写，否则不生效:
                 * jdbc:mysql://localhost:3306/test?tinyInt1isBit=false
                 */
                ps.setBoolean(2, true);
                ps.setInt(3, 99);
                ps.setInt(4, 100);
                // 这行执行完数据库里面就有数据了，你可以在这里打个断点试试
                int n = ps.executeUpdate();
                System.out.println("插入多少行" + n);
                System.out.println("AutoCommit这里模式是true，你用手工commit提交，上面这行代码执行完，数据库里面就有数据了");

                // 下面这个SQL报错，不影响上面的SQL插入成功
                PreparedStatement ps1 = conn.prepareStatement("insert into students (name, gender, grade, score) values (?, ?, ?, ?)");
                ps1.setString(1 , "失败失败失败失败失败失败失败失败失败失败失败失败失败失败失败失败失败失败失败失败失败失败失败失败失败失败失败失败失败失败失败失败失败失败失败失败失败失败失败失败失败失败失败失败失败失败失败失败失败失败失败失败失败失败失败失败失败失败失败失败");
                ps1.setBoolean(2, false);
                ps1.setInt(3, 100);
                ps1.setInt(4, 100);
                n = ps1.executeUpdate();
                System.out.println("插入多少行 " + n);

            }
        }
    }


    static void testAutoCommitFalse() throws SQLException {
        try (Connection conn = DriverManager.getConnection(mySQL8jdbcUrl, jdbcUsername, jdbcPassword)){
            System.out.println("默认的开关是什么:" + conn.getAutoCommit());
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement("insert into students (name, gender, grade, score) values (?, ?, ?, ?)")){
                ps.setString(1, "AutoCommit-false");
                ps.setBoolean(2, true);
                ps.setInt(3, 99);
                ps.setInt(4, 100);
                int n = ps.executeUpdate();
                /*
                 * AutoCommit为false关闭自动提交事务，关闭隐式事务，那这个时候事务时从什么时候开始创建呢？
                 * 就是在int n = ps.executeUpdate();这行代码执行之后事务就自动创建了。
                 * 你可以在这里打个断点，等int n = ps.executeUpdate();执行之后，去数据库执行这个SQL语句：
                 * select * from information_schema.innodb_trx;
                 * 就可以看到了，这个SQL是查看当前数据库有哪些事务正在执行的语句。
                 * 等Connection关闭之后，再执行select * from information_schema.innodb_trx;就看不到了，我们这里的conn是利用try自动关闭的
                 * https://juejin.cn/post/6999856083208503333 《当Transactional碰到锁，有个大坑，要小心。 》   why技术
                 */
                System.out.println("插入多少行" + n);
                System.out.println("AutoCommit这里模式是false，我这个方法不手工调用commit，即使方法执行完了，没报错，数据库里面也没有数据。");

                PreparedStatement ps1 = conn.prepareStatement("insert into students (name, gender, grade, score) values (?, ?, ?, ?)");
                ps1.setString(1 , "AutoCommit-失败失false");
                ps1.setBoolean(2, false);
                ps1.setInt(3, 100);
                ps1.setInt(4, 100);
                n = ps1.executeUpdate();
                System.out.println("插入多少行 " + n);
                System.out.println("AutoCommit这里模式是false，我这个方法不手工调用commit，即使方法执行完了，没报错，数据库里面也没有数据。");
                System.out.println("我没有提交事务----------------------");
            }
        }
    }


    static void testAutoCommitFalse02() throws SQLException {
        try (Connection conn = DriverManager.getConnection(mySQL8jdbcUrl, jdbcUsername, jdbcPassword)){
            System.out.println("默认的开关是什么:" + conn.getAutoCommit());
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement("insert into students (name, gender, grade, score) values (?, ?, ?, ?)")){
                ps.setString(1, "我手工提交了事务AutoCommit-false");
                ps.setBoolean(2, true);
                ps.setInt(3, 99);
                ps.setInt(4, 100);
                int n = ps.executeUpdate();
                /*
                 * AutoCommit为false关闭自动提交事务，关闭隐式事务，那这个时候事务时从什么时候开始创建呢？
                 * 就是在int n = ps.executeUpdate();这行代码执行之后事务就自动创建了。
                 * 你可以在这里打个断点，等int n = ps.executeUpdate();执行之后，去数据库执行这个SQL语句：
                 * select * from information_schema.innodb_trx;
                 * 就可以看到了，这个SQL是查看当前数据库有哪些事务正在执行的语句。
                 * https://juejin.cn/post/6999856083208503333 《当Transactional碰到锁，有个大坑，要小心。 》   why技术
                 */
                System.out.println("插入多少行" + n);
                System.out.println("AutoCommit这里模式是false，我这个方法不手工调用commit，即使方法执行完了，没报错，数据库里面也没有数据。");

                PreparedStatement ps1 = conn.prepareStatement("insert into students (name, gender, grade, score) values (?, ?, ?, ?)");
                ps1.setString(1 , "我手工提交了事务AutoCommit-失败失false");
                ps1.setBoolean(2, false);
                ps1.setInt(3, 100);
                ps1.setInt(4, 100);
                n = ps1.executeUpdate();
                System.out.println("插入多少行 " + n);
                System.out.println("AutoCommit这里模式是false，我这个方法不手工调用commit，即使方法执行完了，没报错，数据库里面也没有数据。");
                conn.commit();
                System.out.println("我手工提交了事务");
            }
        }
    }

    /**
     * 如果conn不关闭，这个没有手工提交的事务也没有回滚的事务，将会一直存在于mysql中
     * select * from information_schema.innodb_trx;
     * 这个SQL语句将会一直能查到这个还没有提交的事务
     * @throws SQLException
     */
    static void testAutoCommitFalse03() throws SQLException {
        Connection conn = DriverManager.getConnection(mySQL8jdbcUrl, jdbcUsername, jdbcPassword);
        try {
            System.out.println("默认的开关是什么:" + conn.getAutoCommit());
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement("insert into students (name, gender, grade, score) values (?, ?, ?, ?)")){
                ps.setString(1, "AutoCommit-false");
                ps.setBoolean(2, true);
                ps.setInt(3, 99);
                ps.setInt(4, 100);
                int n = ps.executeUpdate();
                /*
                 * AutoCommit为false关闭自动提交事务，关闭隐式事务，那这个时候事务时从什么时候开始创建呢？
                 * 就是在int n = ps.executeUpdate();这行代码执行之后事务就自动创建了。
                 * 你可以在这里打个断点，等int n = ps.executeUpdate();执行之后，去数据库执行这个SQL语句：
                 * select * from information_schema.innodb_trx;
                 * 就可以看到了，这个SQL是查看当前数据库有哪些事务正在执行的语句。
                 * 等Connection关闭之后，再执行select * from information_schema.innodb_trx;就看不到了，我们这里的conn是利用try自动关闭的
                 * https://juejin.cn/post/6999856083208503333 《当Transactional碰到锁，有个大坑，要小心。 》   why技术
                 */
                System.out.println("插入多少行" + n);
                System.out.println("AutoCommit这里模式是false，我这个方法不手工调用commit，即使方法执行完了，没报错，数据库里面也没有数据。");

                PreparedStatement ps1 = conn.prepareStatement("insert into students (name, gender, grade, score) values (?, ?, ?, ?)");
                ps1.setString(1 , "AutoCommit-失败失false");
                ps1.setBoolean(2, false);
                ps1.setInt(3, 100);
                ps1.setInt(4, 100);
                n = ps1.executeUpdate();
                System.out.println("插入多少行 " + n);
                System.out.println("AutoCommit这里模式是false，我这个方法不手工调用commit，即使方法执行完了，没报错，数据库里面也没有数据。");
                System.out.println("我没有提交事务----------------------");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
