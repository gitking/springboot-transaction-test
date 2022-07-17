package com.itranswarp.learnjava.batch;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * https://www.liaoxuefeng.com/wiki/1252599548343744/1322290857902113 《JDBC Batch》 廖雪峰
 *
 * 使用JDBC操作数据库的时候，经常会执行一些批量操作。
 * 例如，一次性给会员增加可用优惠券若干，我们可以执行以下SQL代码：
 * INSERT INTO coupons (user_id, type, expires) VALUES (123, 'DISCOUNT', '2030-12-31');
 * INSERT INTO coupons (user_id, type, expires) VALUES (234, 'DISCOUNT', '2030-12-31');
 * INSERT INTO coupons (user_id, type, expires) VALUES (345, 'DISCOUNT', '2030-12-31');
 * INSERT INTO coupons (user_id, type, expires) VALUES (456, 'DISCOUNT', '2030-12-31');
 * 实际上执行JDBC时，因为只有占位符参数不同，所以SQL实际上是一样的：
 * for (var params : paramsList) {
 *     PreparedStatement ps = conn.preparedStatement("INSERT INTO coupons (user_id, type, expires) VALUES (?,?,?)");
 *     ps.setLong(params.get(0));
 *     ps.setString(params.get(1));
 *     ps.setString(params.get(2));
 *     ps.executeUpdate();
 * }
 * 类似的还有，给每个员工薪水增加10%～30%：
 * UPDATE employees SET salary = salary * ? WHERE id = ?
 * 通过一个循环来执行每个PreparedStatement虽然可行，但是性能很低。
 * SQL数据库对SQL语句相同，但只有参数不同的若干语句可以作为batch执行，即批量执行，这种操作有特别优化，速度远远快于循环执行每个SQL。
 * 在JDBC代码中，我们可以利用SQL数据库的这一特性，把同一个SQL但参数不同的若干次操作合并为一个batch执行。我们以批量插入为例，示例代码如下：
 * try (PreparedStatement ps = conn.prepareStatement("INSERT INTO students (name, gender, grade, score) VALUES (?, ?, ?, ?)")) {
 *     // 对同一个PreparedStatement反复设置参数并调用addBatch():
 *     for (Student s : students) {
 *         ps.setString(1, s.name);
 *         ps.setBoolean(2, s.gender);
 *         ps.setInt(3, s.grade);
 *         ps.setInt(4, s.score);
 *         ps.addBatch(); // 添加到batch
 *     }
 *     // 执行batch:
 *     int[] ns = ps.executeBatch();
 *     for (int n : ns) {
 *         System.out.println(n + " inserted."); // batch中每个SQL执行的结果数量
 *     }
 * }
 * 执行batch和执行一个SQL不同点在于，需要对同一个PreparedStatement反复设置参数并调用addBatch()，这样就相当于给一个SQL加上了多组参数，相当于变成了“多行”SQL。
 * 第二个不同点是调用的不是executeUpdate()，而是executeBatch()，因为我们设置了多组参数，相应地，返回结果也是多个int值，因此返回类型是int[]，循环int[]数组即可获取每组参数执行后影响的结果数量。
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
        batchInsertStudents(Arrays.asList("大黄黄", "小黑黑", "大偷偷"), false, 3, 99);
    }

    static void batchInsertStudents(List<String> names , boolean gender, int grade, int score) throws SQLException {
        try(Connection conn = DriverManager.getConnection(mySQL8jdbcUrl, jdbcUsername, jdbcPassword)) {
            boolean isAutoCommit = conn.getAutoCommit();
            // 关闭自动提交事务
            conn.setAutoCommit(false);
            try(PreparedStatement ps = conn.prepareStatement("INSERT INTO students (name, gender, grade, score) values (?, ?, ?, ?)")) {
                for (String name : names) {
                    ps.setString(1, name);
                    /*
                     * https://blog.csdn.net/inrgihc/article/details/118713282
                     * https://dev.mysql.com/doc/connector-j/5.1/en/connector-j-reference-type-conversions.html
                     * 如果tinyInt1isBit =true(默认)，且tinyInt存储长度为1 ，则转为java.lang.Boolean;否则转为java.lang.Integer。
                     * 注: 是ResultSet.getObject() 方法
                     * tinyInt(1) 只用来代表Boolean含义的字段，且0代表False，1代表True。如果要存储多个数值，则定义为tinyInt(N), N>1。例如 tinyInt(2)。
                     * JDBC的URL增加 tinyInt1isBit=false参数，注意参数名区分大小写，否则不生效:
                     * jdbc:mysql://localhost:3306/test?tinyInt1isBit=false
                     */
                    ps.setBoolean(2, gender);
                    ps.setInt(3, grade);
                    ps.setInt(4, score);
                    // 添加到batch
                    ps.addBatch();
                }
                // 批量执行batch,jdbc批量执行
                int[] ns = ps.executeBatch();
                for (int n : ns) {
                    System.out.println(n + " batch中每个SQL执行的结果数量，影响行数");
                }
            }
            conn.commit();
            // 恢复autoCommit设置
            conn.setAutoCommit(isAutoCommit);
        }
    }
}
