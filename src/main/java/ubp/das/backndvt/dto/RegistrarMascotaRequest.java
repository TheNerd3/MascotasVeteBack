package ubp.das.backndvt.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

// RF06 - Registrar mascotas. Trae idResponsable si el propietario ya
// esta en ciudadanos, o propietario con sus datos para crearlo. idRefugio
// se usa cuando el alta viene del flujo de una publicacion de adopcion (RF13).
public record RegistrarMascotaRequest(

        @NotBlank(message = "el nombre de la mascota es obligatorio")
        String nombre,

        @NotBlank(message = "el sexo es obligatorio")
        @Pattern(regexp = "[MH]", message = "el sexo debe ser M o H")
        String sexo,

        Short anioNacimiento,

        String microchip,

        Integer idResponsable,

        @Valid
        DatosPropietarioRequest propietario,

        Integer idRefugio,

        @NotNull(message = "las caracteristicas son obligatorias (puede ser una lista vacia)")
        @Valid
        List<CaracteristicaMascotaRequest> caracteristicas) {
}
