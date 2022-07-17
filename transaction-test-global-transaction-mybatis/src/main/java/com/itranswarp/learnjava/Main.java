package com.itranswarp.learnjava;

import java.net.CookieHandler;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * https://www.liaoxuefeng.com/wiki/1252599548343744/1305152088703009 廖雪峰
 * Java为关系数据库定义了一套标准的访问接口：JDBC（Java Database Connectivity），本章我们介绍如何在Java程序中使用JDBC。
 *
 * 数据库类别
 * 既然我们要使用关系数据库，就必须选择一个关系数据库。目前广泛使用的关系数据库也就这么几种：
 * 付费的商用数据库：
 *     Oracle，典型的高富帅；
 *     SQL Server，微软自家产品，Windows定制专款；
 *     DB2，IBM的产品，听起来挺高端；
 *     Sybase，曾经跟微软是好基友，后来关系破裂，现在家境惨淡。
 * 这些数据库都是不开源而且付费的，最大的好处是花了钱出了问题可以找厂家解决，不过在Web的世界里，常常需要部署成千上万的数据库服务器，当然不能把大把大把的银子扔给厂家，所以，无论是Google、Facebook，还是国内的BAT，无一例外都选择了免费的开源数据库：
 *     MySQL，大家都在用，一般错不了；
 *     PostgreSQL，学术气息有点重，其实挺不错，但知名度没有MySQL高；
 *     sqlite，嵌入式数据库，适合桌面和移动应用。
 * 作为一个Java工程师，选择哪个免费数据库呢？当然是MySQL。因为MySQL普及率最高，出了错，可以很容易找到解决方法。而且，围绕MySQL有一大堆监控和运维的工具，安装和使用很方便。
 * 安装MySQL
 * 为了能继续后面的学习，你需要从MySQL官方网站下载并安装MySQL Community Server 5.6，这个版本是免费的，其他高级版本是要收钱的（请放心，收钱的功能我们用不上）。MySQL是跨平台的，选择对应的平台下载安装文件，安装即可。
 * 安装时，MySQL会提示输入root用户的口令，请务必记清楚。如果怕记不住，就把口令设置为password。
 * 在Windows上，安装时请选择UTF-8编码，以便正确地处理中文。
 * 在Mac或Linux上，需要编辑MySQL的配置文件，把数据库默认的编码全部改为UTF-8。MySQL的配置文件默认存放在/etc/my.cnf或者/etc/mysql/my.cnf：
 * [client]
 * default-character-set = utf8
 *
 * [mysqld]
 * default-storage-engine = INNODB
 * character-set-server = utf8
 * collation-server = utf8_general_ci
 * 大家记得把collation-server里的utf8_general_ci也改成utf8mb4_general_ci，不然会启动失败
 * [client]
 * default-character-set = utf8mb4
 *
 * [mysqld]
 * default-storage-engine = INNODB
 * character-set-server = utf8mb4
 * collation-server = utf8mb4_general_ci
 * 重启MySQL后，可以通过MySQL的客户端命令行检查编码：
 * show variables like '%char%';
 * 看到utf8字样就表示编码设置正确。
 * 注：如果MySQL的版本≥5.5.3，可以把编码设置为utf8mb4，utf8mb4和utf8完全兼容，但它支持最新的Unicode标准，可以显示emoji字符。
 *
 * JDBC
 * 什么是JDBC？JDBC是Java DataBase Connectivity的缩写，它是Java程序访问数据库的标准接口。
 * 使用Java程序访问数据库时，Java代码并不是直接通过TCP连接去访问数据库，而是通过JDBC接口来访问，而JDBC接口则通过JDBC驱动来实现真正对数据库的访问。
 * 例如，我们在Java代码中如果要访问MySQL，那么必须编写代码操作JDBC接口。注意到JDBC接口是Java标准库自带的，所以可以直接编译。而具体的JDBC驱动是由数据库厂商提供的，
 * 例如，MySQL的JDBC驱动由Oracle提供。因此，访问某个具体的数据库，我们只需要引入该厂商提供的JDBC驱动，就可以通过JDBC接口来访问，这样保证了Java程序编写的是一套数据库访问代码，却可以访问各种不同的数据库，因为他们都提供了标准的JDBC驱动：
 * ┌ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┐
 *
 * │  ┌───────────────┐  │
 *    │   Java App    │
 * │  └───────────────┘  │
 *            │
 * │          ▼          │
 *    ┌───────────────┐
 * │  │JDBC Interface │<─┼─── JDK
 *    └───────────────┘
 * │          │          │
 *            ▼
 * │  ┌───────────────┐  │
 *    │  JDBC Driver  │<───── Vendor
 * │  └───────────────┘  │
 *            │
 * └ ─ ─ ─ ─ ─│─ ─ ─ ─ ─ ┘
 *            ▼
 *    ┌───────────────┐
 *    │   Database    │
 *    └───────────────┘
 *
 * 从代码来看，Java标准库自带的JDBC接口其实就是定义了一组接口，而某个具体的JDBC驱动其实就是实现了这些接口的类：
 *
 * ┌ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┐
 *
 * │  ┌───────────────┐  │
 *    │   Java App    │
 * │  └───────────────┘  │
 *            │
 * │          ▼          │
 *    ┌───────────────┐
 * │  │JDBC Interface │<─┼─── JDK
 *    └───────────────┘
 * │          │          │
 *            ▼
 * │  ┌───────────────┐  │
 *    │ MySQL Driver  │<───── Oracle
 * │  └───────────────┘  │
 *            │
 * └ ─ ─ ─ ─ ─│─ ─ ─ ─ ─ ┘
 *            ▼
 *    ┌───────────────┐
 *    │     MySQL     │
 *    └───────────────┘
 *
 * 实际上，一个MySQL的JDBC的驱动就是一个jar包，它本身也是纯Java编写的。我们自己编写的代码只需要引用Java标准库提供的java.sql包下面的相关接口，由此再间接地通过MySQL驱动的jar包通过网络访问MySQL服务器，所有复杂的网络通讯都被封装到JDBC驱动中，因此，Java程序本身只需要引入一个MySQL驱动的jar包就可以正常访问MySQL服务器：
 *
 * ┌ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┐
 *    ┌───────────────┐
 * │  │   App.class   │  │
 *    └───────────────┘
 * │          │          │
 *            ▼
 * │  ┌───────────────┐  │
 *    │  java.sql.*   │
 * │  └───────────────┘  │
 *            │
 * │          ▼          │
 *    ┌───────────────┐     TCP    ┌───────────────┐
 * │  │ mysql-xxx.jar │──┼────────>│     MySQL     │
 *    └───────────────┘            └───────────────┘
 * └ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┘
 *           JVM
 *
 * JDBC查询
 * 前面我们讲了Java程序要通过JDBC接口来查询数据库。JDBC是一套接口规范，它在哪呢？就在Java的标准库java.sql里放着，不过这里面大部分都是接口。接口并不能直接实例化，而是必须实例化对应的实现类，然后通过接口引用这个实例。那么问题来了：JDBC接口的实现类在哪？
 * 因为JDBC接口并不知道我们要使用哪个数据库，所以，用哪个数据库，我们就去使用哪个数据库的“实现类”，我们把某个数据库实现了JDBC接口的jar包称为JDBC驱动。
 * 因为我们选择了MySQL 5.x作为数据库，所以我们首先得找一个MySQL的JDBC驱动。所谓JDBC驱动，其实就是一个第三方jar包，我们直接添加一个Maven依赖就可以了：
 *<dependency>
 *     <groupId>mysql</groupId>
 *     <artifactId>mysql-connector-java</artifactId>
 *     <version>5.1.47</version>
 *     <scope>runtime</scope>
 * </dependency>
 * 注意到这里添加依赖的scope是runtime，因为编译Java程序并不需要MySQL的这个jar包，只有在运行期才需要使用。如果把runtime改成compile，虽然也能正常编译，但是在IDE里写程序的时候，会多出来一大堆类似com.mysql.jdbc.Connection这样的类，非常容易与Java标准库的JDBC接口混淆，所以坚决不要设置为compile。
 *
 * JDBC连接
 * 使用JDBC时，我们先了解什么是Connection。Connection代表一个JDBC连接，它相当于Java程序到数据库的连接（通常是TCP连接）。打开一个Connection时，需要准备URL、用户名和口令，才能成功连接到数据库。
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

    public static void main(String[] args) throws Exception{
        try (Connection conn = DriverManager.getConnection(mySQL8jdbcUrl, jdbcUsername, jdbcPassword)){
            try (Statement stmt = conn.createStatement()){
                try (ResultSet rs = stmt.executeQuery("select id, grade, name, gender from students where gender=1")){
                    // rs.next()用于判断是否有下一行记录，如果有，将自动把当前行移动到下一行（一开始获得ResultSet时当前行不是第一行）；
                    while (rs.next()) {
                        // 注意：索引从1开始,ResultSet获取列时，索引从1开始而不是0；
                        long id = rs.getLong(1);
                        long grade = rs.getLong(2);
                        String name = rs.getString(3);
                        int gender = rs.getInt(4);
                        System.out.println("JDBC获取数据,id" + id);
                    }
                }
            }
        }

        List<Student> students = queryStudents();
        students.forEach(System.out::println);

        /*
         * 数据库操作总结起来就四个字：增删改查，行话叫CRUD：Create，Retrieve，Update和Delete。
         * 插入
         * 插入操作是INSERT，即插入一条新记录。通过JDBC进行插入，本质上也是用PreparedStatement执行一条SQL语句，不过最后执行的不是executeQuery()，而是executeUpdate()。示例代码如下：
         * 虽然Statement也可以执行插入操作，但我们仍然要严格遵循绝不能手动拼SQL字符串的原则，以避免安全漏洞。
         *
         */
        System.out.println("insert new student:");
        try(Connection conn = DriverManager.getConnection(mySQL8jdbcUrl, jdbcUsername, jdbcPassword)) {
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO students (name, gender, grade, score) VALUES (?, ?, ?, ?)")){
                ps.setString(1, "大白");
                /*
                 * https://blog.csdn.net/inrgihc/article/details/118713282
                 * https://dev.mysql.com/doc/connector-j/5.1/en/connector-j-reference-type-conversions.html
                 * 如果tinyInt1isBit =true(默认)，且tinyInt存储长度为1 ，则转为java.lang.Boolean;否则转为java.lang.Integer。
                 * 注: 是ResultSet.getObject() 方法
                 * tinyInt(1) 只用来代表Boolean含义的字段，且0代表False，1代表True。如果要存储多个数值，则定义为tinyInt(N), N>1。例如 tinyInt(2)。
                 * JDBC的URL增加 tinyInt1isBit=false参数，注意参数名区分大小写，否则不生效:
                 * jdbc:mysql://localhost:3306/test?tinyInt1isBit=false
                 */
                ps.setBoolean(2 ,true);
                ps.setInt(3, 3);
                ps.setInt(4, 97);
                int n = ps.executeUpdate();
                System.out.println("插入多少行？" + n);
            }
        }

        /**
         * 插入并获取主键
         * 如果数据库的表设置了自增主键，那么在执行INSERT语句时，并不需要指定主键，数据库会自动分配主键。对于使用自增主键的程序，有个额外的步骤，就是如何获取插入后的自增主键的值。
         * 要获取自增主键，不能先插入，再查询。因为两条SQL执行期间可能有别的程序也插入了同一个表。
         * 获取自增主键的正确写法是在创建PreparedStatement的时候，指定一个RETURN_GENERATED_KEYS标志位，表示JDBC驱动必须返回插入的自增主键。示例代码如下：
         * 观察下面代码，有两点注意事项：
         * 一是调用prepareStatement()时，第二个参数必须传入常量Statement.RETURN_GENERATED_KEYS，否则JDBC驱动不会返回自增主键；
         * 二是执行executeUpdate()方法后，必须调用getGeneratedKeys()获取一个ResultSet对象，这个对象包含了数据库自动生成的主键的值，读取该对象的每一行来获取自增主键的值。
         * 如果一次插入多条记录，那么这个ResultSet对象就会有多行返回值。如果插入时有多列自增，那么ResultSet对象的每一行都会对应多个自增值（自增列不一定必须是主键）。
         */
        System.out.println("插入一行，并获得插入后的自增ID");
        try(Connection conn = DriverManager.getConnection(mySQL8jdbcUrl, jdbcUsername, jdbcPassword)){
            try(PreparedStatement ps = conn.prepareStatement("INSERT INTO students (name, gender, grade, score) values (?, ?, ?, ?)",
                    // 返回数据库自动生成的key
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, "老王");
                ps.setBoolean(2, true);
                ps.setInt(3, 3);
                ps.setInt(4, 99);
                int n = ps.executeUpdate();
                long id = 0;
                try (ResultSet rs = ps.getGeneratedKeys()){
                    if (rs.next()) {
                        // 注意：索引从1开始
                        id = rs.getLong(1);
                    }
                }
                System.out.println("插入" + n + "行，插入后自增的ID为：" + id);
            }
        }

        System.out.println("更新数据");
        try(Connection conn = DriverManager.getConnection(mySQL8jdbcUrl, jdbcUsername, jdbcPassword)){
            try (PreparedStatement ps = conn.prepareStatement("UPDATE students set score = ? where name = ? ")){
                ps.setInt(1, 99);
                ps.setString(2, "小贝");
                int n = ps.executeUpdate();
                System.out.println(n + "updated.");
            }
        }

        System.out.println("删除");
        try (Connection conn = DriverManager.getConnection(mySQL8jdbcUrl, jdbcUsername, jdbcPassword)){
            try (PreparedStatement ps = conn.prepareStatement("delete from students where score < ?")){
                ps.setInt(1, 80);
                int n = ps.executeUpdate();
                System.out.println(n + "deleted.");
            }
        }
    }

    static List<Student> queryStudents() throws SQLException {
        List<Student> students = new ArrayList<>();
        /*
         * 使用JDBC时，我们先了解什么是Connection。Connection代表一个JDBC连接，它相当于Java程序到数据库的连接（通常是TCP连接）。打开一个Connection时，需要准备URL、用户名和口令，才能成功连接到数据库。
         * 核心代码是DriverManager提供的静态方法getConnection()。DriverManager会自动扫描classpath，找到所有的JDBC驱动，然后根据我们传入的URL自动挑选一个合适的驱动。
         * 因为JDBC连接是一种昂贵的资源，所以使用后要及时释放。使用try (resource)来自动释放JDBC连接是一个好方法：
         */
        try (Connection conn = DriverManager.getConnection(mySQL8jdbcUrl, jdbcUsername, jdbcPassword)){
            // Statment和ResultSet都是需要关闭的资源，因此嵌套使用try (resource)确保及时关闭；
            try(PreparedStatement ps = conn.prepareStatement("select * from students where grade = ? and score >= ? ")){
                // 第一个参数grade = ?
                ps.setInt(1, 3);
                // 第二个参数score = ?
                ps.setInt(2, 90);
                try(ResultSet rs = ps.executeQuery()){
                    // rs.next()用于判断是否有下一行记录，如果有，将自动把当前行移动到下一行（一开始获得ResultSet时当前行不是第一行）；
                    while (rs.next()) {
                        // ResultSet获取列时，索引从1开始而不是0；
                        students.add(extractRow(rs));
                    }
                }
            }
        }
        return students;
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

    /**
     * SQL注入
     * 使用Statement拼字符串非常容易引发SQL注入的问题，这是因为SQL参数往往是从方法参数传入的。
     * 我们来看一个例子：假设用户登录的验证方法如下：
     *     stmt.executeQuery("SELECT * FROM user WHERE login='" + name + "' AND pass='" + pass + "'");
     * 其中，参数name和pass通常都是Web页面输入后由程序接收到的。
     * 如果用户的输入是程序期待的值，就可以拼出正确的SQL。例如：name = "bob"，pass = "1234"：
     * SELECT * FROM user WHERE login='bob' AND pass='1234'
     * 但是，如果用户的输入是一个精心构造的字符串，就可以拼出意想不到的SQL，这个SQL也是正确的，但它查询的条件不是程序设计的意图。例如：name = "bob' OR pass=", pass = " OR pass='"：
     * SELECT * FROM user WHERE login='bob' OR pass=' AND pass=' OR pass=''
     * 这个SQL语句执行的时候，根本不用判断口令是否正确，这样一来，登录就形同虚设。
     * 要避免SQL注入攻击，一个办法是针对所有字符串参数进行转义，但是转义很麻烦，而且需要在任何使用SQL的地方增加转义代码。
     * 还有一个办法就是使用PreparedStatement。使用PreparedStatement可以完全避免SQL注入的问题，因为PreparedStatement始终使用?作为占位符，并且把数据连同SQL本身传给数据库，这样可以保证每次传给数据库的SQL语句是相同的，只是占位符的数据不同，还能高效利用数据库本身对查询的缓存。上述登录SQL如果用PreparedStatement可以改写如下：
     *  String sql = "SELECT * FROM user WHERE login=? AND pass=?";
     *     PreparedStatement ps = conn.prepareStatement(sql);
     *     ps.setObject(1, name);
     *     ps.setObject(2, pass);
     * 所以，PreparedStatement比Statement更安全，而且更快。
     * 使用Java对数据库进行操作时，必须使用PreparedStatement，严禁任何通过参数拼字符串的代码！
     * 我们把上面使用Statement的代码改为使用PreparedStatement：
     * try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
     *     try (PreparedStatement ps = conn.prepareStatement("SELECT id, grade, name, gender FROM students WHERE gender=? AND grade=?")) {
     *         ps.setObject(1, "M"); // 注意：索引从1开始
     *         ps.setObject(2, 3);
     *         try (ResultSet rs = ps.executeQuery()) {
     *             while (rs.next()) {
     *                 long id = rs.getLong("id");
     *                 long grade = rs.getLong("grade");
     *                 String name = rs.getString("name");
     *                 String gender = rs.getString("gender");
     *             }
     *         }
     *     }
     * }
     * 使用PreparedStatement和Statement稍有不同，必须首先调用setObject()设置每个占位符?的值，最后获取的仍然是ResultSet对象。
     * 另外注意到从结果集读取列时，使用String类型的列名比索引要易读，而且不易出错。
     * 注意到JDBC查询的返回值总是ResultSet，即使我们写这样的聚合查询SELECT SUM(score) FROM ...，也需要按结果集读取：
     * ResultSet rs = ...
     * if (rs.next()) {
     *     double sum = rs.getDouble(1);
     * }
     * 数据类型
     * 有的童鞋可能注意到了，使用JDBC的时候，我们需要在Java数据类型和SQL数据类型之间进行转换。JDBC在java.sql.Types定义了一组常量来表示如何映射SQL数据类型，但是平时我们使用的类型通常也就以下几种：
     * SQL数据类型	        Java数据类型
     * BIT, BOOL	            boolean
     * INTEGER	                int
     * BIGINT	                long
     * REAL	                    float
     * FLOAT,               DOUBLE	double
     * CHAR,                VARCHAR	String
     * DECIMAL              BigDecimal
     * DATE	                java.sql.Date, LocalDate
     * TIME	            java.sql.Time, LocalTime
     * 注意：只有最新的JDBC驱动才支持LocalDate和LocalTime。
     *
     * 这个占位符很有意思，就是因为如此才能完美避免sql注入。
     * 其实换一种说法，就是使用PreparedStatement的方式，它会自动帮我们进行“转义”功能。
     * 假如 ? 的位置，你赋值参数是java的String字符串，那么接下来会帮你转成sql的varchar，如同 'xxxxx' 的形式，同时里面包含特殊的字符 ' ，也会帮你自动转义\'。
     * com.mysql.jdbc.JDBC42PreparedStatement@61dc03ce: SELECT * FROM user WHERE login='bob\' OR pass=' AND pass=' OR pass=\''
     * 加载驱动的问题。
     * jdbc4之后自动会加载驱动，看源码好像时找META-INF/services目录下面的配置。
     * mysql驱动有这个目录下面配置了驱动类，会自动加；
     * 但是oracle的驱动没有看到这个目录，测试发现没有用Class.forName()加载确实找不到驱动，写了就找到了。
     * 是不是意味着自动mysql才会自动加载而oracle由于没有配置META-INF/services所以不会自动加载？
     * 就像你说的在“META-INF”目录里自动找，只要实现了这样的SPI机制的都可以自动加载，没实现就得手动Class.forName()了
     */
}
