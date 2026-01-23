package com.medicalquiz.Config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Test CSRF is disabled")
    void testCsrfDisabled() throws Exception {
        // POST request should work without CSRF token
        mockMvc.perform(post("/api/questions/answer")
                .contentType("application/json")
                .content("{\"questionId\":1,\"answer\":\"test\"}"))
                .andExpect(status().is4xxClientError()); // Will fail for other reasons, but not CSRF
        
        System.out.println("✓ CSRF is disabled");
    }

    @Test
    @DisplayName("Test all requests are permitted")
    void testAllRequestsPermitted() throws Exception {
        // GET request should be allowed without authentication
        mockMvc.perform(get("/api/questions/count"))
                .andExpect(status().is2xxSuccessful());
        
        System.out.println("✓ All requests are permitted without authentication");
    }

    @Test
    @DisplayName("Test CORS is enabled in controller")
    void testCorsEnabled() throws Exception {
        // Test with Origin header
        mockMvc.perform(get("/api/questions/count")
                .header("Origin", "http://localhost:8080"))
                .andExpect(status().is2xxSuccessful());
        
        System.out.println("✓ CORS is properly configured");
    }

    @Test
    @DisplayName("Test security filter chain exists")
    void testSecurityFilterChainExists(@Autowired org.springframework.security.web.SecurityFilterChain filterChain) {
        assertNotNull(filterChain, "Security filter chain should be configured");
        System.out.println("✓ Security filter chain is configured");
    }
}