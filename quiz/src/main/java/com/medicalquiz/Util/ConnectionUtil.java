package com.medicalquiz.Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConnectionUtil {
    
    @Value("${snowflake.url}")
    private String snowflakeUrl;
    
    @Value("${snowflake.user}")
    private String user;
    
    @Value("${snowflake.password}")
    private String password;
    
    @Value("${snowflake.warehouse:COMPUTE_WH}")
    private String warehouse;
    
    @Value("${snowflake.database}")
    private String database;
    
    @Value("${snowflake.schema:PUBLIC}")
    private String schema;
    
    @Value("${snowflake.role:}")
    private String role;
    
    /**
     * Get a connection to Snowflake
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public Connection getConnection() throws SQLException {
        try {
            // Load Snowflake JDBC driver
            Class.forName("net.snowflake.client.jdbc.SnowflakeDriver");
            
            // Set connection properties
            Properties properties = new Properties();
            properties.put("user", user);
            properties.put("password", password);
            properties.put("warehouse", warehouse);
            properties.put("db", database);
            properties.put("schema", schema);
            
            // Set role if specified
            if (role != null && !role.isEmpty()) {
                properties.put("role", role);
            }
            
            // Establish connection
            Connection connection = DriverManager.getConnection(snowflakeUrl, properties);
            System.out.println("✓ Connected to Snowflake successfully!");
            
            return connection;
            
        } catch (ClassNotFoundException e) {
            throw new SQLException("Snowflake JDBC driver not found. Add snowflake-jdbc dependency to pom.xml", e);
        }
    }
    
    /**
     * Get a connection with custom properties
     * @param additionalProps Additional connection properties
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public Connection getConnection(Properties additionalProps) throws SQLException {
        try {
            Class.forName("net.snowflake.client.jdbc.SnowflakeDriver");
            
            Properties properties = new Properties();
            properties.put("user", user);
            properties.put("password", password);
            properties.put("warehouse", warehouse);
            properties.put("db", database);
            properties.put("schema", schema);
            
            if (role != null && !role.isEmpty()) {
                properties.put("role", role);
            }
            
            // Add additional properties
            if (additionalProps != null) {
                properties.putAll(additionalProps);
            }
            
            return DriverManager.getConnection(snowflakeUrl, properties);
            
        } catch (ClassNotFoundException e) {
            throw new SQLException("Snowflake JDBC driver not found. Add snowflake-jdbc dependency to pom.xml", e);
        }
    }
    
    /**
     * Test the connection
     * @return true if connection successful
     */
    public boolean testConnection() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT CURRENT_VERSION()")) {
            
            if (rs.next()) {
                System.out.println("✓ Snowflake Version: " + rs.getString(1));
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("✗ Connection test failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Execute a query and return ResultSet
     * Note: Caller is responsible for closing the connection and statement
     * @param query SQL query to execute
     * @return ResultSet
     * @throws SQLException if query fails
     */
    public ResultSet executeQuery(String query) throws SQLException {
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(query);
    }
    
    /**
     * Execute an update/insert/delete query
     * @param query SQL query to execute
     * @return Number of rows affected
     * @throws SQLException if query fails
     */
    public int executeUpdate(String query) throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            return stmt.executeUpdate(query);
        }
    }
    
    /**
     * Close connection safely
     * @param conn Connection to close
     */
    public void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("✓ Connection closed");
            } catch (SQLException e) {
                System.err.println("✗ Error closing connection: " + e.getMessage());
            }
        }
    }
    
    /**
     * Get connection details for logging (without password)
     * @return Connection info string
     */
    public String getConnectionInfo() {
        return String.format(
            "Snowflake Connection: URL=%s, User=%s, DB=%s, Schema=%s, Warehouse=%s",
            snowflakeUrl, user, database, schema, warehouse
        );
    }
}