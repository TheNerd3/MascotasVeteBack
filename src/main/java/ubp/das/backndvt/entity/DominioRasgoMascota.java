package ubp.das.backndvt.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "dominio_rasgos_mascotas")
@IdClass(DominioRasgoMascotaId.class)
public class DominioRasgoMascota {

    @Id
    @Column(name = "cod_rasgo")
    private Integer codRasgo;

    @Id
    @Column(name = "nro_valor_dominio")
    private Integer nroValorDominio;

    @Column(name = "nom_valor_dominio", nullable = false, length = 100)
    private String nomValorDominio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("codRasgo")
    @JoinColumn(name = "cod_rasgo", insertable = false, updatable = false)
    private RasgoMascota rasgo;

    public Integer getCodRasgo() {
        return codRasgo;
    }

    public void setCodRasgo(Integer codRasgo) {
        this.codRasgo = codRasgo;
    }

    public Integer getNroValorDominio() {
        return nroValorDominio;
    }

    public void setNroValorDominio(Integer nroValorDominio) {
        this.nroValorDominio = nroValorDominio;
    }

    public String getNomValorDominio() {
        return nomValorDominio;
    }

    public void setNomValorDominio(String nomValorDominio) {
        this.nomValorDominio = nomValorDominio;
    }

    public RasgoMascota getRasgo() {
        return rasgo;
    }

    public void setRasgo(RasgoMascota rasgo) {
        this.rasgo = rasgo;
    }
}
