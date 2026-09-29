package ubp.das.backndvt.dto;

import jakarta.validation.constraints.NotNull;

// RF06 - Registrar mascotas: una caracteristica puntual (especie, raza,
// pelaje, etc), segun el catalogo rasgos_mascotas/dominio_rasgos_mascotas.
public record CaracteristicaMascotaRequest(

        @NotNull(message = "el codRasgo es obligatorio")
        Integer codRasgo,

        @NotNull(message = "el nroValorDominio es obligatorio")
        Integer nroValorDominio,

        String valor) {
}
