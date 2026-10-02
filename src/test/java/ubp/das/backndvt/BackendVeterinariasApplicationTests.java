package ubp.das.backndvt;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Levanta el contexto completo de Spring, lo que incluye conectarse a
 * SQL Server para que Hibernate valide el mapeo (ddl-auto=validate) y
 * las variables de entorno de JWT. Se etiqueta como "integracion" y
 * se excluye de la corrida por defecto de "mvnw test" (ver pom.xml)
 * porque el CI no tiene SQL Server ni esos secretos disponibles.
 * Correrlo a mano con una base real conectada: mvnw test
 * -Dgroups=integracion
 */
@Tag("integracion")
@SpringBootTest
class BackendVeterinariasApplicationTests {

    @Test
    void contextLoads() {
    }

}
