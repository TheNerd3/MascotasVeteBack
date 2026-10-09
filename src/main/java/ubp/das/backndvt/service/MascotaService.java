package ubp.das.backndvt.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ubp.das.backndvt.dto.CaracteristicaMascotaRequest;
import ubp.das.backndvt.dto.DatosPropietarioRequest;
import ubp.das.backndvt.dto.MascotaResponse;
import ubp.das.backndvt.dto.RegistrarMascotaRequest;
import ubp.das.backndvt.dto.RegistrarMascotaResultado;
import ubp.das.backndvt.entity.Ciudadano;
import ubp.das.backndvt.entity.DominioRasgoMascota;
import ubp.das.backndvt.entity.Mascota;
import ubp.das.backndvt.entity.Refugio;
import ubp.das.backndvt.exception.RecursoNoEncontradoException;
import ubp.das.backndvt.repository.CaracteristicaMascotaProcedureRepository;
import ubp.das.backndvt.repository.CiudadanoRepository;
import ubp.das.backndvt.repository.DominioRasgoMascotaRepository;
import ubp.das.backndvt.repository.MascotaExistenteProcedureRepository;
import ubp.das.backndvt.repository.MascotaRepository;
import ubp.das.backndvt.repository.RefugioRepository;

/**
 * RF06 - Registrar mascotas.
 * Busca duplicados con sp_BuscarMascotaExistente (microchip prioritario,
 * si no nombre+anio_nacimiento+responsable). Si el propietario no
 * existe todavia, lo crea en ciudadanos. Inserta la mascota y cada
 * caracteristica dentro de una unica transaccion, usando
 * sp_InsertarCaracteristicaMascota para las caracteristicas (nunca
 * INSERT directo en caracteristicas_mascotas).
 */
@Service
public class MascotaService {

    private final MascotaRepository mascotaRepository;
    private final MascotaExistenteProcedureRepository mascotaExistenteProcedureRepository;
    private final CaracteristicaMascotaProcedureRepository caracteristicaMascotaProcedureRepository;
    private final CiudadanoRepository ciudadanoRepository;
    private final RefugioRepository refugioRepository;
    private final DominioRasgoMascotaRepository dominioRasgoMascotaRepository;
    private final PasswordEncoder passwordEncoder;

    public MascotaService(
            MascotaRepository mascotaRepository,
            MascotaExistenteProcedureRepository mascotaExistenteProcedureRepository,
            CaracteristicaMascotaProcedureRepository caracteristicaMascotaProcedureRepository,
            CiudadanoRepository ciudadanoRepository,
            RefugioRepository refugioRepository,
            DominioRasgoMascotaRepository dominioRasgoMascotaRepository,
            PasswordEncoder passwordEncoder) {
        this.mascotaRepository = mascotaRepository;
        this.mascotaExistenteProcedureRepository = mascotaExistenteProcedureRepository;
        this.caracteristicaMascotaProcedureRepository = caracteristicaMascotaProcedureRepository;
        this.ciudadanoRepository = ciudadanoRepository;
        this.refugioRepository = refugioRepository;
        this.dominioRasgoMascotaRepository = dominioRasgoMascotaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public RegistrarMascotaResultado registrar(RegistrarMascotaRequest request) {
        Ciudadano responsable = resolverResponsable(request);

        Mascota existente = mascotaExistenteProcedureRepository
                .buscarExistente(request.nombre(), request.anioNacimiento(), responsable.getIdCiudadano(), request.microchip())
                .orElse(null);

        if (existente != null) {
            return new RegistrarMascotaResultado(MascotaResponse.from(existente), false);
        }

        validarRasgos(request.caracteristicas());

        Mascota mascota = new Mascota();
        mascota.setNombre(request.nombre());
        mascota.setSexo(request.sexo());
        mascota.setAnioNacimiento(request.anioNacimiento());
        mascota.setMicrochip(request.microchip());
        mascota.setVive(true);
        mascota.setResponsable(responsable);
        mascota.setRefugio(resolverRefugio(request.idRefugio()));

        Mascota guardada = mascotaRepository.save(mascota);

        for (CaracteristicaMascotaRequest caracteristica : request.caracteristicas()) {
            caracteristicaMascotaProcedureRepository.insertarCaracteristicaMascota(
                    guardada.getNroRegMunicipal(),
                    caracteristica.codRasgo(),
                    caracteristica.nroValorDominio(),
                    caracteristica.valor());
        }

        return new RegistrarMascotaResultado(MascotaResponse.from(guardada), true);
    }

    private Ciudadano resolverResponsable(RegistrarMascotaRequest request) {
        if (request.idResponsable() != null) {
            return ciudadanoRepository.findById(request.idResponsable())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "No existe un ciudadano con id " + request.idResponsable()));
        }

        DatosPropietarioRequest propietario = request.propietario();
        if (propietario == null) {
            throw new IllegalArgumentException("Debe indicar idResponsable o los datos del propietario");
        }

        return ciudadanoRepository.findByCuil(propietario.cuil())
                .orElseGet(() -> crearPropietario(propietario));
    }

    private Ciudadano crearPropietario(DatosPropietarioRequest propietario) {
        Ciudadano nuevo = new Ciudadano();
        nuevo.setApellido(propietario.apellido());
        nuevo.setNombre(propietario.nombre());
        nuevo.setCuil(propietario.cuil());
        nuevo.setClave(passwordEncoder.encode(propietario.clave()));
        nuevo.setCorreo(propietario.correo());
        nuevo.setTelefono(propietario.telefono());
        nuevo.setDomicilio(propietario.domicilio());
        nuevo.setHabilitado(true);
        return ciudadanoRepository.save(nuevo);
    }

    private Refugio resolverRefugio(Integer idRefugio) {
        if (idRefugio == null) {
            return null;
        }
        return refugioRepository.findById(idRefugio)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un refugio con id " + idRefugio));
    }

    private void validarRasgos(List<CaracteristicaMascotaRequest> caracteristicas) {
        for (CaracteristicaMascotaRequest caracteristica : caracteristicas) {
            DominioRasgoMascota valorDominio = dominioRasgoMascotaRepository
                    .findById(caracteristica.codRasgo(), caracteristica.nroValorDominio())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "El valor de dominio " + caracteristica.nroValorDominio()
                                    + " no existe para el rasgo " + caracteristica.codRasgo()));
            if (!valorDominio.getRasgo().getCodRasgo().equals(caracteristica.codRasgo())) {
                throw new RecursoNoEncontradoException("El rasgo " + caracteristica.codRasgo() + " no existe");
            }
        }
    }
}
