package com.medicalquiz.Util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ConnectionUtilTest {

    @Autowired
    private ConnectionUtil connectionUtil;

    @Test
    @DisplayName("Test if ConnectionUtil bean is created")
    public void testConnectionUtilBeanExists() {
        assertNotNull(connectionUtil, "ConnectionUtil should be autowired and not null");
    }

    @Test
    @DisplayName("Test getting a database connection")
    public void testGetConnection() {
        try (Connection conn = connectionUtil.getConnection()) {
            assertNotNull(conn, "Connection should not be null");
            assertFalse(conn.isClosed(), "Connection should be open");
            System.out.println("✓ Successfully obtained database connection");
        } catch (SQLException e) {
            fail("Failed to get connection: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Test connection is valid and can execute queries")
    public void testConnectionCanExecuteQuery() {
        try (Connection conn = connectionUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Try a simple query
            ResultSet rs = stmt.executeQuery("SELECT 1");
            assertTrue(rs.next(), "Query should return at least one row");
            assertEquals(1, rs.getInt(1), "Query should return value 1");
            
            System.out.println("✓ Connection can execute queries successfully");
        } catch (SQLException e) {
            fail("Failed to execute query: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Test DISEASE_WITH_SYMPTOMS table exists and is accessible")
    public void testDiseaseWithSymptomsTableExists() {
        String query = "SELECT COUNT(*) as total FROM DISEASE_WITH_SYMPTOMS";
        
        try (Connection conn = connectionUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            assertTrue(rs.next(), "Query should return a result");
            long count = rs.getLong("total");
            System.out.println("✓ DISEASE_WITH_SYMPTOMS table exists with " + count + " records");
            
        } catch (SQLException e) {
            fail("DISEASE_WITH_SYMPTOMS table may not exist or is not accessible: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Test DISEASE_WITH_SYMPTOMS table has required columns")
    public void testDiseaseWithSymptomsTableStructure() {
        String query = "SELECT DISEASE, PRIMARY_SYMPTOM_1, PRIMARY_SYMPTOM_2, PRIMARY_SYMPTOM_3, PRIMARY_SYMPTOM_4, " +
                      "ADDITIONAL_SYMPTOM_1, ADDITIONAL_SYMPTOM_2, ADDITIONAL_SYMPTOM_3, ADDITIONAL_SYMPTOM_4, CATEGORY " +
                      "FROM DISEASE_WITH_SYMPTOMS LIMIT 1";
        
        try (Connection conn = connectionUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            // Check if columns exist by trying to access them
            if (rs.next()) {
                assertDoesNotThrow(() -> rs.getString("DISEASE"));
                assertDoesNotThrow(() -> rs.getString("PRIMARY_SYMPTOM_1"));
                assertDoesNotThrow(() -> rs.getString("PRIMARY_SYMPTOM_2"));
                assertDoesNotThrow(() -> rs.getString("PRIMARY_SYMPTOM_3"));
                assertDoesNotThrow(() -> rs.getString("PRIMARY_SYMPTOM_4"));
                assertDoesNotThrow(() -> rs.getString("ADDITIONAL_SYMPTOM_1"));
                assertDoesNotThrow(() -> rs.getString("ADDITIONAL_SYMPTOM_2"));
                assertDoesNotThrow(() -> rs.getString("ADDITIONAL_SYMPTOM_3"));
                assertDoesNotThrow(() -> rs.getString("ADDITIONAL_SYMPTOM_4"));
                assertDoesNotThrow(() -> rs.getString("CATEGORY"));
                
                System.out.println("✓ DISEASE_WITH_SYMPTOMS table has all required columns");
            } else {
                System.out.println("⚠ DISEASE_WITH_SYMPTOMS table is empty, but structure is correct");
            }
            
        } catch (SQLException e) {
            fail("DISEASE_WITH_SYMPTOMS table is missing required columns: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Test multiple connections can be obtained")
    public void testMultipleConnections() {
        try (Connection conn1 = connectionUtil.getConnection();
             Connection conn2 = connectionUtil.getConnection();
             Connection conn3 = connectionUtil.getConnection()) {
            
            assertNotNull(conn1, "First connection should not be null");
            assertNotNull(conn2, "Second connection should not be null");
            assertNotNull(conn3, "Third connection should not be null");
            
            assertFalse(conn1.isClosed(), "First connection should be open");
            assertFalse(conn2.isClosed(), "Second connection should be open");
            assertFalse(conn3.isClosed(), "Third connection should be open");
            
            System.out.println("✓ Multiple connections can be obtained simultaneously");
            
        } catch (SQLException e) {
            fail("Failed to obtain multiple connections: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Test connection properties")
    public void testConnectionProperties() {
        try (Connection conn = connectionUtil.getConnection()) {
            assertFalse(conn.isClosed(), "Connection should be open");
            assertFalse(conn.isReadOnly(), "Connection should not be read-only");
            
            // Check database metadata
            String dbProduct = conn.getMetaData().getDatabaseProductName();
            System.out.println("✓ Connected to: " + dbProduct);
            
        } catch (SQLException e) {
            fail("Failed to check connection properties: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Test sample query from DISEASE_WITH_SYMPTOMS table")
    public void testSampleDataQuery() {
        String query = "SELECT DISEASE, PRIMARY_SYMPTOM_1, PRIMARY_SYMPTOM_2, PRIMARY_SYMPTOM_3, PRIMARY_SYMPTOM_4, " +
                      "ADDITIONAL_SYMPTOM_1, ADDITIONAL_SYMPTOM_2, CATEGORY FROM DISEASE_WITH_SYMPTOMS LIMIT 1";
        
        try (Connection conn = connectionUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            if (rs.next()) {
                String disease = rs.getString("DISEASE");
                String primarySymptom1 = rs.getString("PRIMARY_SYMPTOM_1");
                String primarySymptom2 = rs.getString("PRIMARY_SYMPTOM_2");
                String primarySymptom3 = rs.getString("PRIMARY_SYMPTOM_3");
                String primarySymptom4 = rs.getString("PRIMARY_SYMPTOM_4");
                String additionalSymptom1 = rs.getString("ADDITIONAL_SYMPTOM_1");
                String additionalSymptom2 = rs.getString("ADDITIONAL_SYMPTOM_2");
                String category = rs.getString("CATEGORY");
                
                assertNotNull(disease, "Disease should not be null");
                assertNotNull(category, "Category should not be null");
                
                System.out.println("✓ Sample disease: " + disease);
                System.out.println("  Category: " + category);
                System.out.println("  Primary Symptoms:");
                if (primarySymptom1 != null) System.out.println("    - " + primarySymptom1);
                if (primarySymptom2 != null) System.out.println("    - " + primarySymptom2);
                if (primarySymptom3 != null) System.out.println("    - " + primarySymptom3);
                if (primarySymptom4 != null) System.out.println("    - " + primarySymptom4);
                
                if (additionalSymptom1 != null || additionalSymptom2 != null) {
                    System.out.println("  Additional Symptoms:");
                    if (additionalSymptom1 != null) System.out.println("    - " + additionalSymptom1);
                    if (additionalSymptom2 != null) System.out.println("    - " + additionalSymptom2);
                }
            } else {
                System.out.println("⚠ No data in DISEASE_WITH_SYMPTOMS table yet");
            }
            
        } catch (SQLException e) {
            fail("Failed to query sample data: " + e.getMessage());
        }
    }
}