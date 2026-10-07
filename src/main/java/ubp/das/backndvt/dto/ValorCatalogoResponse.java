package ubp.das.backndvt.dto;

import ubp.das.backndvt.entity.DominioRasgoMascota;

// RF06/RF13 - Un valor de dominio para un rasgo de mascota (especie,
// raza, etc), tal como lo necesita el combo del frontend.
public record ValorCatalogoResponse(Integer codRasgo, Integer nroValorDominio, String nombre) {

    public static ValorCatalogoResponse from(DominioRasgoMascota dominioRasgo) {
        return new ValorCatalogoResponse(
                dominioRasgo.getCodRasgo(), dominioRasgo.getNroValorDominio(), dominioRasgo.getNomValorDominio());
    }
}
