package ubp.das.backndvt.dto;

import ubp.das.backndvt.entity.Mascota;

// RF06/RF20 - Datos publicos de una mascota (sin exponer la entidad JPA)
public record MascotaResponse(
        Integer nroRegMunicipal,
        String nombre,
        String sexo,
        Short anioNacimiento,
        String microchip,
        Boolean vive,
        Integer idResponsable,
        Integer idRefugio) {

    public static MascotaResponse from(Mascota mascota) {
        return new MascotaResponse(
                mascota.getNroRegMunicipal(),
                mascota.getNombre(),
                mascota.getSexo(),
                mascota.getAnioNacimiento(),
                mascota.getMicrochip(),
                mascota.getVive(),
                mascota.getResponsable() != null ? mascota.getResponsable().getIdCiudadano() : null,
                mascota.getRefugio() != null ? mascota.getRefugio().getIdRefugio() : null);
    }
}
