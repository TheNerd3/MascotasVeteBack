package ubp.das.backndvt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * Documentacion OpenAPI/Swagger del backend. La UI interactiva queda
 * en /swagger-ui.html, la especificacion cruda en /v3/api-docs.
 * Define el esquema de seguridad Bearer para que el boton "Authorize"
 * de Swagger UI permita probar los endpoints protegidos pegando el
 * JWT obtenido en POST /auth/login.
 */
@Configuration
public class OpenApiConfig {

    private static final String ESQUEMA_BEARER = "bearerAuth";

    @Bean
    public OpenAPI mascotasCordobaOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Mascotas Cordoba - API")
                        .description(
                                "Registro Unico Municipal de Mascotas de Cordoba. "
                                        + "Consolida el alta de mascotas, su informacion sanitaria "
                                        + "(cargada por veterinarias externas), el carnet sanitario "
                                        + "digital y las publicaciones de adopcion de los refugios. "
                                        + "Todos los endpoints requieren autenticacion (Bearer JWT) "
                                        + "salvo los marcados explicitamente como publicos.")
                        .version("v1"))
                .components(new Components()
                        .addSecuritySchemes(ESQUEMA_BEARER, new SecurityScheme()
                                .name(ESQUEMA_BEARER)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Token obtenido en POST /auth/login. Pegar solo el valor, sin el prefijo \"Bearer \".")))
                .addSecurityItem(new SecurityRequirement().addList(ESQUEMA_BEARER));
    }
}
