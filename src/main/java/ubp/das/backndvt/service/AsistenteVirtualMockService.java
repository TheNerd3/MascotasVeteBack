package ubp.das.backndvt.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import ubp.das.backndvt.dto.ConsultaAsistenteRequest;
import ubp.das.backndvt.dto.ConsultaAsistenteResponse;

/**
 * RF21 - Implementacion mock de AsistenteVirtualService, activa por
 * defecto (todos los perfiles salvo "ia-real"). Devuelve una respuesta
 * fija para poder probar el endpoint de punta a punta sin depender de
 * un proveedor de IA todavia no elegido.
 */
@Service
@Profile("!ia-real")
public class AsistenteVirtualMockService implements AsistenteVirtualService {

    @Override
    public ConsultaAsistenteResponse responder(ConsultaAsistenteRequest request) {
        return new ConsultaAsistenteResponse(
                "Gracias por tu consulta. Todavia no tenemos un asistente virtual real conectado, "
                        + "pero un municipio pronto va a poder responderte por aca.");
    }
}
