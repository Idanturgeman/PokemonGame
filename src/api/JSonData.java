package api;

import java.sql.*;
import java.util.Date;

public class JSonData {
    private static String STOP_HERE = "NICE - De-Compiling!! PLEASE DO NOT use this info for changing the server state!!!";
    private static String jdbcUrl = "jdbc:mysql://db-mysql-ams3-67328-do-user-4468260-0.db.ondigitalocean.com:25060/oop?useUnicode=yes&characterEncoding=UTF-8&useSSL=false";
    private static final String jdbcUser = "student";
    private static final String jdbcUserPassword = "OOP2020student";
    private static String jdbcAdmin = "doadmin";
    private static String jdbcAdminPassword = "guvvx0m0g955vix3";

    JSonData() {
    }

    static int login(long id) {
        int ans = -1;
        String allCustomersQuery = "SELECT * FROM Users where userID=" + id + ";";

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection(jdbcUrl, "student", "OOP2020student");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(allCustomersQuery);
            if (resultSet != null && resultSet.next()) {
                ans = resultSet.getInt("levelNum");
            }
        } catch (SQLException var6) {
            System.out.println("SQLException: " + var6.getMessage());
            System.out.println("Vendor Error: " + var6.getErrorCode());
        } catch (ClassNotFoundException var7) {
            var7.printStackTrace();
        }

        return ans;
    }

    static boolean writeRes(long id, int level, int moves, int grade) {
        boolean ans = false;
        long now = (new Date()).getTime();
        String query = " insert into Logs (UserID, LevelID, time, moves, score) values (" + id + ", " + level + ", CURRENT_TIMESTAMP," + moves + " , " + grade + ")";

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection(jdbcUrl, jdbcAdmin, jdbcAdminPassword);
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
        } catch (SQLException var10) {
            System.out.println("SQLException: " + var10.getMessage());
            System.out.println("Vendor Error: " + var10.getErrorCode());
        } catch (ClassNotFoundException var11) {
            var11.printStackTrace();
        }

        return ans;
    }

    static boolean writeKML(long id, int level, String KML) {
        boolean ans = false;
        String sql = "UPDATE Users SET kml_" + level + " = '" + KML + "' WHERE userID =" + id;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection(jdbcUrl, jdbcAdmin, jdbcAdminPassword);
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException var7) {
            System.out.println("SQLException: " + var7.getMessage());
            System.out.println("Vendor Error: " + var7.getErrorCode());
        } catch (ClassNotFoundException var8) {
            var8.printStackTrace();
        }

        return ans;
    }

    static boolean updateMaxLevel(long id, int max_level) {
        boolean ans = false;
        String sql = "UPDATE Users SET levelNum = " + max_level + " WHERE userID =" + id;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection(jdbcUrl, jdbcAdmin, jdbcAdminPassword);
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException var6) {
            System.out.println("SQLException: " + var6.getMessage());
            System.out.println("Vendor Error: " + var6.getErrorCode());
        } catch (ClassNotFoundException var7) {
            var7.printStackTrace();
        }

        return ans;
    }
}
