package ubp.das.backndvt.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// RF13 - Crear publicacion de adopcion. Si nroRegMunicipal viene nulo,
// se registra la mascota en el mismo flujo con los datos de mascota
// (reusa MascotaService.registrar); si viene, se publica una mascota
// ya existente en el registro. mascota solo es obligatoria cuando no
// viene nroRegMunicipal (ver requiereRegistrarMascota); como esa regla
// no se puede expresar con una anotacion simple, se valida a mano en
// el service, no aca.
public record CrearPublicacionAdopcionRequest(

        Integer nroRegMunicipal,

        @Valid
        RegistrarMascotaRequest mascota,

        @NotBlank(message = "las caracteristicas de la mascota son obligatorias")
        String caracteristicasMascota,

        @NotBlank(message = "la condicion de adopcion es obligatoria")
        String condicionAdopcion,

        @NotNull(message = "la foto es obligatoria")
        byte[] foto) {

    public boolean requiereRegistrarMascota() {
        return nroRegMunicipal == null;
    }
}
