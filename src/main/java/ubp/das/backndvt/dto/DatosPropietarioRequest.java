package ubp.das.backndvt.dto;

import jakarta.validation.constraints.NotBlank;

// RF06 - Registrar mascotas: datos del propietario para crearlo en
// ciudadanos si todavia no existe (se usa cuando no viene idResponsable).
public record DatosPropietarioRequest(

        @NotBlank(message = "el apellido del propietario es obligatorio")
        String apellido,

        @NotBlank(message = "el nombre del propietario es obligatorio")
        String nombre,

        @NotBlank(message = "el cuil del propietario es obligatorio")
        String cuil,

        @NotBlank(message = "la clave del propietario es obligatoria")
        String clave,

        String correo,

        String telefono,

        String domicilio) {
}
