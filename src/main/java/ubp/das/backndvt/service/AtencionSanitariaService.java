package ubp.das.backndvt.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ubp.das.backndvt.dto.AtencionSanitariaResponse;
import ubp.das.backndvt.dto.RegistrarAtencionSanitariaRequest;
import ubp.das.backndvt.entity.Mascota;
import ubp.das.backndvt.entity.ProfesionalVeterinaria;
import ubp.das.backndvt.entity.Veterinaria;
import ubp.das.backndvt.exception.RecursoNoEncontradoException;
import ubp.das.backndvt.repository.InformacionSanitariaProcedureRepository;
import ubp.das.backndvt.repository.MascotaRepository;
import ubp.das.backndvt.repository.ProfesionalVeterinariaRepository;
import ubp.das.backndvt.repository.TipoAtencionSanitariaRepository;
import ubp.das.backndvt.repository.VeterinariaRepository;

/**
 * RF09 - Registrar información sanitaria. Valida que la mascota exista
 * (precondición del RF), que el tipo de atención sea válido, que la
 * veterinaria esté habilitada y que el profesional esté vinculado a
 * esa veterinaria y sin baja, antes de llamar a
 * sp_InsertarInformacionSanitaria (nunca INSERT directo, ver
 * InformacionSanitariaProcedureRepository).
 */
@Service
public class AtencionSanitariaService {

    private final MascotaRepository mascotaRepository;
    private final TipoAtencionSanitariaRepository tipoAtencionSanitariaRepository;
    private final VeterinariaRepository veterinariaRepository;
    private final ProfesionalVeterinariaRepository profesionalVeterinariaRepository;
    private final InformacionSanitariaProcedureRepository informacionSanitariaProcedureRepository;

    public AtencionSanitariaService(
            MascotaRepository mascotaRepository,
            TipoAtencionSanitariaRepository tipoAtencionSanitariaRepository,
            VeterinariaRepository veterinariaRepository,
            ProfesionalVeterinariaRepository profesionalVeterinariaRepository,
            InformacionSanitariaProcedureRepository informacionSanitariaProcedureRepository) {
        this.mascotaRepository = mascotaRepository;
        this.tipoAtencionSanitariaRepository = tipoAtencionSanitariaRepository;
        this.veterinariaRepository = veterinariaRepository;
        this.profesionalVeterinariaRepository = profesionalVeterinariaRepository;
        this.informacionSanitariaProcedureRepository = informacionSanitariaProcedureRepository;
    }

    @Transactional
    public AtencionSanitariaResponse registrar(Integer nroRegMunicipal, RegistrarAtencionSanitariaRequest request) {
        Mascota mascota = mascotaRepository.findById(nroRegMunicipal)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe una mascota con nro_reg_municipal " + nroRegMunicipal));

        tipoAtencionSanitariaRepository.findById(request.codTipoAtencion())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el tipo de atencion " + request.codTipoAtencion()));

        Veterinaria veterinaria = veterinariaRepository.findById(request.idVeterinaria())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe una veterinaria con id " + request.idVeterinaria()));

        if (veterinaria.getHabilitacionMunicipal() == null || veterinaria.getHabilitacionMunicipal().isBlank()) {
            throw new RecursoNoEncontradoException(
                    "La veterinaria " + request.idVeterinaria() + " no está habilitada");
        }

        ProfesionalVeterinaria profesional = profesionalVeterinariaRepository
                .findById(request.idVeterinaria(), request.idProfesional())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "El profesional " + request.idProfesional()
                                + " no está vinculado a la veterinaria " + request.idVeterinaria()));

        if (Boolean.TRUE.equals(profesional.getBaja())) {
            throw new RecursoNoEncontradoException(
                    "El profesional " + request.idProfesional() + " está dado de baja en esa veterinaria");
        }

        Integer nroRegistroGenerado = informacionSanitariaProcedureRepository.insertarInformacionSanitaria(
                mascota.getNroRegMunicipal(),
                request.fechaAtencion(),
                request.codTipoAtencion(),
                request.detalleAtencion(),
                request.fechaVencimiento(),
                request.idVeterinaria(),
                request.idProfesional());

        return new AtencionSanitariaResponse(mascota.getNroRegMunicipal(), nroRegistroGenerado);
    }
}
