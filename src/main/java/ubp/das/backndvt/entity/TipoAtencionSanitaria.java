package ubp.das.backndvt.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tipos_atencion_sanitaria")
public class TipoAtencionSanitaria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_tipo_atencion")
    private Integer codTipoAtencion;

    @Column(name = "desc_tipo_atencion", nullable = false, length = 100)
    private String descTipoAtencion;

    public Integer getCodTipoAtencion() {
        return codTipoAtencion;
    }

    public void setCodTipoAtencion(Integer codTipoAtencion) {
        this.codTipoAtencion = codTipoAtencion;
    }

    public String getDescTipoAtencion() {
        return descTipoAtencion;
    }

    public void setDescTipoAtencion(String descTipoAtencion) {
        this.descTipoAtencion = descTipoAtencion;
    }
}
