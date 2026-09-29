package ubp.das.backndvt.service;

import ubp.das.backndvt.dto.ConsultaAsistenteRequest;
import ubp.das.backndvt.dto.ConsultaAsistenteResponse;

/**
 * RF21 - Asistente virtual de texto libre. Se define como interfaz a
 * proposito (RNF13): la implementacion actual (AsistenteVirtualMockService)
 * devuelve una respuesta fija, para poder reemplazarla despues por una
 * integracion real con un proveedor de IA sin tocar el controller.
 */
public interface AsistenteVirtualService {

    ConsultaAsistenteResponse responder(ConsultaAsistenteRequest request);
}
