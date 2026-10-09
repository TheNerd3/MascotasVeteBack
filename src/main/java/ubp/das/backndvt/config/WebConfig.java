package ubp.das.backndvt.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

/**
 * Habilita manualmente el soporte de Spring Data para la web (resolver
 * de Pageable/Page en los @RequestParam de los controllers, ej.
 * GET /publicaciones). Normalmente esto lo auto-configura
 * spring-boot-starter-data-jpa, que el backend no usa (la catedra no
 * permite JPA/Hibernate): solo se depende de spring-data-commons, que
 * trae las clases Page/Pageable/PageImpl pero no activa su soporte
 * web por si solo.
 */
@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class WebConfig {
}
