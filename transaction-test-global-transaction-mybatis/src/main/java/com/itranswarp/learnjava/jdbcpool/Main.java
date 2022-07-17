package com.itranswarp.learnjava.jdbcpool;

import com.itranswarp.learnjava.Student;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * https://www.liaoxuefeng.com/wiki/1252599548343744/1321748528103458 《JDBC连接池》 廖雪峰
 *
 * 我们在讲多线程的时候说过，创建线程是一个昂贵的操作，如果有大量的小任务需要执行，并且频繁地创建和销毁线程，实际上会消耗大量的系统资源，往往创建和消耗线程所耗费的时间比执行任务的时间还长，所以，为了提高效率，可以用线程池。
 * 类似的，在执行JDBC的增删改查的操作时，如果每一次操作都来一次打开连接，操作，关闭连接，那么创建和销毁JDBC连接的开销就太大了。为了避免频繁地创建和销毁JDBC连接，我们可以通过连接池（Connection Pool）复用已经创建好的连接。
 * JDBC连接池有一个标准的接口javax.sql.DataSource，注意这个类位于Java标准库中，但仅仅是接口。要使用JDBC连接池，我们必须选择一个JDBC连接池的实现。常用的JDBC连接池有：
 *     HikariCP
 *     C3P0
 *     BoneCP
 *     Druid
 * 目前使用最广泛的是HikariCP。我们以HikariCP为例，要使用JDBC连接池，先添加HikariCP的依赖如下：
 * <dependency>
 *     <groupId>com.zaxxer</groupId>
 *     <artifactId>HikariCP</artifactId>
 *     <version>2.7.1</version>
 * </dependency>
 * 紧接着，我们需要创建一个DataSource实例，这个实例就是连接池：
 * HikariConfig config = new HikariConfig();
 * config.setJdbcUrl("jdbc:mysql://localhost:3306/test");
 * config.setUsername("root");
 * config.setPassword("password");
 * config.addDataSourceProperty("connectionTimeout", "1000"); // 连接超时：1秒
 * config.addDataSourceProperty("idleTimeout", "60000"); // 空闲超时：60秒
 * config.addDataSourceProperty("maximumPoolSize", "10"); // 最大连接数：10
 * DataSource ds = new HikariDataSource(config);
 * 注意创建DataSource也是一个非常昂贵的操作，所以通常DataSource实例总是作为一个全局变量存储，并贯穿整个应用程序的生命周期。
 * 有了连接池以后，我们如何使用它呢？和前面的代码类似，只是获取Connection时，把DriverManage.getConnection()改为ds.getConnection()：
 * try (Connection conn = ds.getConnection()) { // 在此获取连接
 *     ...
 * } // 在此“关闭”连接
 * 通过连接池获取连接时，并不需要指定JDBC的相关URL、用户名、口令等信息，因为这些信息已经存储在连接池内部了（创建HikariDataSource时传入的HikariConfig持有这些信息）。一开始，连接池内部并没有连接，所以，第一次调用ds.getConnection()，会迫使连接池内部先创建一个Connection，再返回给客户端使用。当我们调用conn.close()方法时（在try(resource){...}结束处），不是真正“关闭”连接，而是释放到连接池中，以便下次获取连接时能直接返回。
 * 因此，连接池内部维护了若干个Connection实例，如果调用ds.getConnection()，就选择一个空闲连接，并标记它为“正在使用”然后返回，如果对Connection调用close()，那么就把连接再次标记为“空闲”从而等待下次调用。这样一来，我们就通过连接池维护了少量连接，但可以频繁地执行大量的SQL语句。
 *
 * 通常连接池提供了大量的参数可以配置，例如，维护的最小、最大活动连接数，指定一个连接在空闲一段时间后自动关闭等，需要根据应用程序的负载合理地配置这些参数。此外，大多数连接池都提供了详细的实时状态以便进行监控。
 *
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
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(mySQL8jdbcUrl);
        config.setUsername(jdbcUsername);
        config.setPassword(jdbcPassword);
        config.addDataSourceProperty("cachePrepStms", "true");
        config.addDataSourceProperty("prepStmtCaheSize", "100");
        config.addDataSourceProperty("maximumPoolSize", "10");
        DataSource ds = new HikariDataSource(config);
        List<Student> studentList = queryStudents(ds);
        studentList.forEach(System.out::println);
    }

    static List<Student> queryStudents(DataSource ds) throws SQLException {
        List<Student> studentList = new ArrayList<>();
        // 从数据源中获取连接,try会自动释放连接
        try (Connection conn = ds.getConnection()){
            // conn的真正实现类未:class com.zaxxer.hikari.pool.HikariProxyConnection
            System.out.println("conn的真正实现类未:" + conn.getClass());
            System.out.println("conn的真正实现类未:" + conn.getClass().getSimpleName());
            try (PreparedStatement ps = conn.prepareStatement("select  * from students where grade = ? and score >= ? ")) {
                // 第一个参数grade = ?
                ps.setInt(1, 3);
                // 第二个参数score = ?
                ps.setInt(2, 90);
                try(ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        studentList.add(extractRow(rs));
                    }
                }
            }
        }
        return studentList;
    }

    static Student extractRow(ResultSet rs) throws SQLException {
        Student std = new Student();
        std.setId(rs.getLong("id"));
        std.setName(rs.getString("name"));
        std.setGender(rs.getBoolean("gender"));
        std.setGrade(rs.getInt("grade"));
        std.setScore(rs.getInt("score"));
        return std;
    }
}
