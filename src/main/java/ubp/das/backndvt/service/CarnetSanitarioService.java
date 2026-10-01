package ubp.das.backndvt.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ubp.das.backndvt.dto.AtencionCarnetResponse;
import ubp.das.backndvt.dto.CarnetSanitarioResponse;
import ubp.das.backndvt.dto.ValidarCarnetResponse;
import ubp.das.backndvt.entity.Mascota;
import ubp.das.backndvt.exception.RecursoNoEncontradoException;
import ubp.das.backndvt.repository.CarnetSanitarioFila;
import ubp.das.backndvt.repository.CarnetSanitarioRepository;
import ubp.das.backndvt.repository.MascotaRepository;
import ubp.das.backndvt.security.CarnetTokenService;
import ubp.das.backndvt.security.CarnetTokenService.ResultadoValidacion;

/**
 * RF10/RF11 - Genera y valida el carnet sanitario digital. Lee
 * vw_carnet_sanitario tal cual (sin reconstruir el join a mano) y
 * firma un token HMAC-SHA256 del NRM + fecha de emision para que se
 * pueda verificar la autenticidad del carnet sin exponer datos.
 */
@Service
public class CarnetSanitarioService {

    private final MascotaRepository mascotaRepository;
    private final CarnetSanitarioRepository carnetSanitarioRepository;
    private final CarnetTokenService carnetTokenService;

    public CarnetSanitarioService(
            MascotaRepository mascotaRepository,
            CarnetSanitarioRepository carnetSanitarioRepository,
            CarnetTokenService carnetTokenService) {
        this.mascotaRepository = mascotaRepository;
        this.carnetSanitarioRepository = carnetSanitarioRepository;
        this.carnetTokenService = carnetTokenService;
    }

    @Transactional(readOnly = true)
    public CarnetSanitarioResponse obtenerCarnet(Integer nroRegMunicipal, Integer idCiudadanoAutenticado) {
        Mascota mascota = mascotaRepository.findById(nroRegMunicipal)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe una mascota con nro_reg_municipal " + nroRegMunicipal));

        if (!mascota.getResponsable().getIdCiudadano().equals(idCiudadanoAutenticado)) {
            throw new AccessDeniedException("El carnet sanitario solo lo puede ver el responsable de la mascota");
        }

        List<CarnetSanitarioFila> filas = carnetSanitarioRepository.buscarPorMascota(nroRegMunicipal);
        CarnetSanitarioFila primeraFila = filas.get(0);

        List<AtencionCarnetResponse> atenciones = filas.stream()
                .filter(f -> f.nroRegistro() != null)
                .map(f -> new AtencionCarnetResponse(
                        f.nroRegistro(),
                        f.fechaAtencion(),
                        f.descTipoAtencion(),
                        f.detalleAtencion(),
                        f.fechaVencimiento(),
                        f.veterinaria()))
                .toList();

        String token = carnetTokenService.generarToken(nroRegMunicipal, LocalDate.now());

        return new CarnetSanitarioResponse(
                nroRegMunicipal,
                primeraFila.nombreMascota(),
                primeraFila.nombreResponsable(),
                primeraFila.apellidoResponsable(),
                primeraFila.cuil(),
                atenciones,
                token);
    }

    public ValidarCarnetResponse validarToken(String token) {
        ResultadoValidacion resultado = carnetTokenService.validar(token);

        if (!resultado.esValido()) {
            return new ValidarCarnetResponse(false, null, null, null);
        }

        Mascota mascota = mascotaRepository.findById(resultado.nroRegMunicipal()).orElse(null);
        if (mascota == null) {
            return new ValidarCarnetResponse(false, null, null, null);
        }

        return new ValidarCarnetResponse(true, mascota.getNroRegMunicipal(), mascota.getNombre(), resultado.fechaEmision());
    }
}
