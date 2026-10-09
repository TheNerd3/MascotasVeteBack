package ubp.das.backndvt.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import ubp.das.backndvt.entity.Ciudadano;
import ubp.das.backndvt.entity.Mascota;
import ubp.das.backndvt.entity.Refugio;

// RF06 - Registrar mascotas: busca duplicados usando sp_BuscarMascotaExistente
// (microchip prioritario; si no viene, cae a nombre + año_nacimiento + responsable).
@Repository
public class MascotaExistenteProcedureRepository {

    private static final RowMapper<Mascota> MAPEADOR = (fila, numeroFila) -> {
        Ciudadano responsable = new Ciudadano();
        responsable.setIdCiudadano(fila.getInt("id_responsable"));

        Mascota mascota = new Mascota();
        mascota.setNroRegMunicipal(fila.getInt("nro_reg_municipal"));
        mascota.setNombre(fila.getString("nombre"));
        mascota.setSexo(fila.getString("sexo"));
        mascota.setAnioNacimiento(fila.getObject("año_nacimiento") != null ? fila.getShort("año_nacimiento") : null);
        mascota.setMicrochip(fila.getString("microchip"));
        mascota.setVive(fila.getBoolean("vive"));
        mascota.setResponsable(responsable);
        mascota.setUltimoNroAtencion(fila.getInt("ultimo_nro_atencion"));

        Integer idRefugio = fila.getObject("id_refugio") != null ? fila.getInt("id_refugio") : null;
        if (idRefugio != null) {
            Refugio refugio = new Refugio();
            refugio.setIdRefugio(idRefugio);
            mascota.setRefugio(refugio);
        }
        return mascota;
    };

    private final JdbcTemplate jdbcTemplate;

    public MascotaExistenteProcedureRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Mascota> buscarExistente(
            String nombre,
            Short anioNacimiento,
            Integer idResponsable,
            String microchip) {

        List<Mascota> resultado = jdbcTemplate.query(
                "{call sp_BuscarMascotaExistente(?, ?, ?, ?)}",
                MAPEADOR,
                nombre, anioNacimiento, idResponsable, microchip);

        return resultado.stream().findFirst();
    }
}
