package com.example.sneakershop3.dbConnection;

import java.sql.Connection;
import com.mysql.jdbc.Driver;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class connectionClass {
    public  static Connection getConnection(){
        Connection connection = null;

        try {
            Driver driver = new Driver();
            DriverManager.registerDriver(driver);
            String connectionString = "jdbc:mysql://localhost:3306/sneakershop";
            String username = "root";
            String password = "123456";

            connection =DriverManager.getConnection(connectionString, username, password);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return connection;
    }

}
