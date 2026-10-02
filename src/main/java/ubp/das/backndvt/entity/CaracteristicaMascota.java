package ubp.das.backndvt.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Nota: nro_caracteristica es un correlativo por (mascota, rasgo) que se
 * genera de forma atomica en la base con el stored procedure
 * sp_InsertarCaracteristicaMascota (ver contador_caracteristicas). No usar
 * @GeneratedValue aca: el numero se calcula del lado de la base de datos.
 */
@Entity
@Table(name = "caracteristicas_mascotas")
@IdClass(CaracteristicaMascotaId.class)
public class CaracteristicaMascota {

    @Id
    @Column(name = "nro_reg_municipal")
    private Integer nroRegMunicipal;

    @Id
    @Column(name = "cod_rasgo")
    private Integer codRasgo;

    @Id
    @Column(name = "nro_caracteristica")
    private Integer nroCaracteristica;

    @Column(name = "nro_valor_dominio", nullable = false)
    private Integer nroValorDominio;

    @Column(name = "valor_caracteristica", length = 200)
    private String valorCaracteristica;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "nro_reg_municipal", insertable = false, updatable = false)
    private Mascota mascota;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns({
            @JoinColumn(name = "cod_rasgo", referencedColumnName = "cod_rasgo", insertable = false, updatable = false),
            @JoinColumn(name = "nro_valor_dominio", referencedColumnName = "nro_valor_dominio", insertable = false, updatable = false)
    })
    private DominioRasgoMascota dominioRasgo;

    public Integer getNroRegMunicipal() {
        return nroRegMunicipal;
    }

    public void setNroRegMunicipal(Integer nroRegMunicipal) {
        this.nroRegMunicipal = nroRegMunicipal;
    }

    public Integer getCodRasgo() {
        return codRasgo;
    }

    public void setCodRasgo(Integer codRasgo) {
        this.codRasgo = codRasgo;
    }

    public Integer getNroCaracteristica() {
        return nroCaracteristica;
    }

    public void setNroCaracteristica(Integer nroCaracteristica) {
        this.nroCaracteristica = nroCaracteristica;
    }

    public Integer getNroValorDominio() {
        return nroValorDominio;
    }

    public void setNroValorDominio(Integer nroValorDominio) {
        this.nroValorDominio = nroValorDominio;
    }

    public String getValorCaracteristica() {
        return valorCaracteristica;
    }

    public void setValorCaracteristica(String valorCaracteristica) {
        this.valorCaracteristica = valorCaracteristica;
    }

    public Mascota getMascota() {
        return mascota;
    }

    public void setMascota(Mascota mascota) {
        this.mascota = mascota;
    }

    public DominioRasgoMascota getDominioRasgo() {
        return dominioRasgo;
    }

    public void setDominioRasgo(DominioRasgoMascota dominioRasgo) {
        this.dominioRasgo = dominioRasgo;
    }
}
