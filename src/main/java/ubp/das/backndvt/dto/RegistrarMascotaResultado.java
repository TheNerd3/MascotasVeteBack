package ubp.das.backndvt.dto;

// RF06 - Distingue si el service devolvio una mascota ya existente
// (el controller responde 200) o una mascota recien creada (201).
public record RegistrarMascotaResultado(MascotaResponse mascota, boolean creada) {
}
