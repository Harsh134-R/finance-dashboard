package com.zorvyn.financedashboard.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Finance Dashboard API",
                version = "1.0.0",
                description = """
                        ## Finance Dashboard Backend - Zorvyn Internship Assessment

                        ### How to authenticate
                        1. Call **POST /api/auth/login** with one of the test credentials below
                        2. Copy the token value from the response
                        3. Click the Authorize button at the top right
                        4. Paste the token and click Authorize
                        5. All requests will now carry your token automatically

                        ### Test credentials
                        | Role    | Email                  | Password    | Access |
                        |---------|------------------------|-------------|--------|
                        | Admin   | admin@zorvyn.com       | admin123    | Full   |
                        | Analyst | analyst@zorvyn.com     | analyst123  | Read + Dashboard |
                        | Viewer  | viewer@zorvyn.com      | viewer123   | Read only |

                        ### Role permissions
                        - **Viewer** - can only read transactions
                        - **Analyst** - read transactions + access dashboard analytics
                        - **Admin** - full access including create, update, delete, user management
                        """,
                contact = @Contact(
                        name = "Finance Dashboard API"
                )
        ),
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        description = "Paste your JWT token here after logging in",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}