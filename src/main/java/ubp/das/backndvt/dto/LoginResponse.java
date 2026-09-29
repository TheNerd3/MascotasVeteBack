package ubp.das.backndvt.dto;

// RF15 - Autenticar usuarios
public record LoginResponse(
        String token,
        Integer idCiudadano,
        String nombre,
        String apellido,
        String perfil,
        Integer idRefugio,
        Integer idVeterinaria) {
}
