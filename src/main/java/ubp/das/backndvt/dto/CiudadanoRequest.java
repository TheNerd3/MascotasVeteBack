package ubp.das.backndvt.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO de alta/actualizacion de ciudadano. "clave" llega en texto plano
 * (via HTTPS) y el service la hashea con BCrypt antes de persistir;
 * nunca se devuelve en las respuestas.
 */
public record CiudadanoRequest(

        @NotBlank(message = "el apellido es obligatorio")
        @Size(max = 100)
        String apellido,

        @NotBlank(message = "el nombre es obligatorio")
        @Size(max = 100)
        String nombre,

        @NotBlank(message = "el cuil es obligatorio")
        @Pattern(regexp = "\\d{11}", message = "el cuil debe tener 11 digitos numericos, sin guiones")
        String cuil,

        @NotBlank(message = "la clave es obligatoria")
        @Size(min = 6, message = "la clave debe tener al menos 6 caracteres")
        String clave,

        @Email(message = "el correo no tiene un formato valido")
        String correo,

        String telefono,

        String domicilio) {
}
