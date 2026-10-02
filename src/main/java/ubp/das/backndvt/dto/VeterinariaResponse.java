package ubp.das.backndvt.dto;

import ubp.das.backndvt.entity.Veterinaria;

// RF17 - Datos publicos de una veterinaria habilitada
public record VeterinariaResponse(
        Integer idVeterinaria,
        String razonSocial,
        String correo,
        String telefono,
        String domicilio) {

    public static VeterinariaResponse from(Veterinaria veterinaria) {
        return new VeterinariaResponse(
                veterinaria.getIdVeterinaria(),
                veterinaria.getRazonSocial(),
                veterinaria.getCorreo(),
                veterinaria.getTelefono(),
                veterinaria.getDomicilio());
    }
}
