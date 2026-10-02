package ubp.das.backndvt;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Necesita SQL Server real y las variables de entorno de JWT para
 * levantar el contexto completo. Se excluye del CI (sin base de datos
 * ni secretos disponibles) via maven-surefire-plugin/excludedGroups;
 * se puede correr a mano con: mvnw test -Dgroups=integracion
 */
@Tag("integracion")
@SpringBootTest
class BackendVeterinariasApplicationTests {

    @Test
    void contextLoads() {
    }

}
