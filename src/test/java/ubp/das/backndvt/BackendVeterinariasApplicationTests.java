package ubp.das.backndvt;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Necesita SQL Server real para que Hibernate valide el esquema al
 * levantar el contexto (ddl-auto). Se excluye del CI (sin base de
 * datos disponible) via maven-surefire-plugin/excludedGroups; se
 * puede correr a mano con: mvnw test -Dgroups=integracion
 */
@Tag("integracion")
@SpringBootTest
class BackendVeterinariasApplicationTests {

    @Test
    void contextLoads() {
    }

}
