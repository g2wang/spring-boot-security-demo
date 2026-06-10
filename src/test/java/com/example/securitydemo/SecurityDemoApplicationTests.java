package com.example.securitydemo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityDemoApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Test
    void protectedApiSecurityChainIncludesExceptionTranslationFilter() {
        assertThat(springSecurityFilterChain.getFilters("/api/reports/admin"))
                .anyMatch(ExceptionTranslationFilter.class::isInstance);
    }

    @Test
    void publicEndpointIsAccessibleWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/public/hello"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Hello from a public endpoint."));
    }

    @Test
    void unauthenticatedRequestUsesAuthenticationEntryPointThroughExceptionTranslationFilter() throws Exception {
        mockMvc.perform(get("/api/reports/user"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Authentication is required to access this resource."))
                .andExpect(jsonPath("$.path").value("/api/reports/user"));
    }

    @Test
    void authenticatedUserCanAccessUserReport() throws Exception {
        mockMvc.perform(get("/api/reports/user").with(httpBasic("user", "password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User report data."));
    }

    @Test
    void authenticatedUserWithoutRoleUsesAccessDeniedHandlerThroughExceptionTranslationFilter() throws Exception {
        mockMvc.perform(get("/api/reports/admin").with(httpBasic("user", "password")))
                .andExpect(status().isForbidden())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value(
                        "You are authenticated, but not authorized to access this resource."))
                .andExpect(jsonPath("$.path").value("/api/reports/admin"));
    }

    @Test
    void adminCanAccessAdminReport() throws Exception {
        mockMvc.perform(get("/api/reports/admin").with(httpBasic("admin", "password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", containsString("Admin report")));
    }
}
