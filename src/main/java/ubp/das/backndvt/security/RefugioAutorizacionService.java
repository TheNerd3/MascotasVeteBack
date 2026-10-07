package ubp.das.backndvt.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import ubp.das.backndvt.entity.Mascota;
import ubp.das.backndvt.entity.Refugio;
import ubp.das.backndvt.exception.RecursoNoEncontradoException;
import ubp.das.backndvt.repository.RefugioRepository;

/**
 * RF13 - Unico lugar que resuelve "a que refugio pertenece el usuario
 * autenticado" y valida que pueda operar sobre un recurso dado. El
 * refugio sale siempre del JWT (AuthenticatedUser.idRefugio), nunca
 * del body ni del path de la request: antes esta regla estaba
 * repetida de 3 formas distintas (comparando id_responsable a mano en
 * cada service), lo que permitia que un refugio pasara el idRefugio
 * de otro en el path y el chequeo de turno no lo detectara.
 */
@Service
public class RefugioAutorizacionService {

    private static final String MENSAJE_SIN_REFUGIO = "El usuario autenticado no es responsable de ningun refugio";
    private static final String MENSAJE_MASCOTA_AJENA = "La mascota no pertenece al refugio autenticado";

    private final RefugioRepository refugioRepository;

    public RefugioAutorizacionService(RefugioRepository refugioRepository) {
        this.refugioRepository = refugioRepository;
    }

    /**
     * Devuelve el refugio del usuario autenticado, o 403 si no es
     * responsable de ninguno (perfil distinto de REFUGIO, o el token
     * quedo sin ese claim).
     */
    public Refugio refugioAutenticado(AuthenticatedUser usuario) {
        if (usuario.idRefugio() == null) {
            throw new AccessDeniedException(MENSAJE_SIN_REFUGIO);
        }
        return refugioRepository.findById(usuario.idRefugio())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un refugio con id " + usuario.idRefugio()));
    }

    /**
     * Valida que una mascota pertenezca al refugio autenticado (RF13:
     * un refugio solo puede publicar o gestionar mascotas que tiene a
     * su cargo, nunca las de otro refugio ni las de un ciudadano
     * particular sin vincular).
     */
    public void validarMascotaDelRefugio(Mascota mascota, Refugio refugio) {
        Refugio refugioDeLaMascota = mascota.getRefugio();
        if (refugioDeLaMascota == null || !refugioDeLaMascota.getIdRefugio().equals(refugio.getIdRefugio())) {
            throw new AccessDeniedException(MENSAJE_MASCOTA_AJENA);
        }
    }
}
