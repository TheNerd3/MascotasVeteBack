package ubp.das.backndvt.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;

/**
 * Documentacion OpenAPI/Swagger del backend. La UI interactiva queda
 * en /swagger-ui.html, la especificacion cruda en /v3/api-docs.
 * Define el esquema de seguridad Bearer para que el boton "Authorize"
 * de Swagger UI permita probar los endpoints protegidos pegando el
 * JWT obtenido en POST /auth/login.
 *
 * Cada nombre de tag arranca con un numero (acá y en el @Tag de cada
 * controller) porque springdoc ignora el orden de declaracion de
 * tags() y ordena por el orden de escaneo interno de los beans; la
 * unica forma confiable de fijar el orden es ordenar alfabeticamente
 * (springdoc.swagger-ui.tags-sorter=alpha) sobre un nombre que ya
 * tenga el orden deseado. Login (1.) queda primero para que sea lo
 * primero que ve quien entra a probar la API.
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
                                        + "salvo los marcados explicitamente como publicos. "
                                        + "Empeza por RF15 - Login: hace POST /auth/login, copia el "
                                        + "token de la respuesta y pegalo en el boton Authorize de "
                                        + "arriba (sin el prefijo \"Bearer \") antes de probar el resto.")
                        .version("v1"))
                .tags(List.of(
                        new Tag().name("1. RF15 - Login").description("Autenticacion local contra ciudadanos.cuil/clave"),
                        new Tag().name("2. RF06/RF09/RF10/RF11 - Mascotas")
                                .description("Alta, atenciones sanitarias y carnet sanitario digital"),
                        new Tag().name("3. RF13/RF18 - Publicaciones de adopcion")
                                .description("Alta y gestion de publicaciones, listado publico"),
                        new Tag().name("4. RF17 - Veterinarias").description("Listado publico de veterinarias habilitadas"),
                        new Tag().name("5. RF18 - Refugios")
                                .description("Listado publico de refugios habilitados y sus publicaciones activas"),
                        new Tag().name("6. RF20 - Ciudadanos").description("Datos propios del ciudadano autenticado y sus mascotas"),
                        new Tag().name("7. RF11 - Validacion de carnet")
                                .description("Validacion publica del carnet sanitario para terceros"),
                        new Tag().name("8. RF21 - Asistente virtual").description("Consulta de texto libre a un asistente virtual"),
                        new Tag().name("9. Diagnostico")
                                .description("Endpoint tecnico de verificacion de conexion a la base, no implementa ningun RF")))
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
