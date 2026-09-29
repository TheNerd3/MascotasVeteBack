package ubp.das.backndvt.dto;

import jakarta.validation.Valid;

// RF13 - Crear publicacion de adopcion. Si nroRegMunicipal viene nulo,
// se registra la mascota en el mismo flujo con los datos de mascota
// (reusa MascotaService.registrar); si viene, se publica una mascota
// ya existente en el registro.
public record CrearPublicacionAdopcionRequest(

        Integer nroRegMunicipal,

        @Valid
        RegistrarMascotaRequest mascota,

        String caracteristicasMascota,

        String condicionAdopcion,

        byte[] foto) {

    public boolean requiereRegistrarMascota() {
        return nroRegMunicipal == null;
    }
}
