package ubp.das.backndvt.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "mascotas")
public class Mascota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nro_reg_municipal")
    private Integer nroRegMunicipal;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "sexo", nullable = false, length = 1, columnDefinition = "char(1)")
    private String sexo;

    @Column(name = "año_nacimiento")
    private Short anioNacimiento;

    @Column(name = "microchip", unique = true, length = 50)
    private String microchip;

    @Column(name = "vive", nullable = false)
    private Boolean vive = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_responsable", nullable = false)
    private Ciudadano responsable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_refugio")
    private Refugio refugio;

    @Column(name = "ultimo_nro_atencion", nullable = false)
    private Integer ultimoNroAtencion = 0;

    public Integer getNroRegMunicipal() {
        return nroRegMunicipal;
    }

    public void setNroRegMunicipal(Integer nroRegMunicipal) {
        this.nroRegMunicipal = nroRegMunicipal;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public Short getAnioNacimiento() {
        return anioNacimiento;
    }

    public void setAnioNacimiento(Short anioNacimiento) {
        this.anioNacimiento = anioNacimiento;
    }

    public String getMicrochip() {
        return microchip;
    }

    public void setMicrochip(String microchip) {
        this.microchip = microchip;
    }

    public Boolean getVive() {
        return vive;
    }

    public void setVive(Boolean vive) {
        this.vive = vive;
    }

    public Ciudadano getResponsable() {
        return responsable;
    }

    public void setResponsable(Ciudadano responsable) {
        this.responsable = responsable;
    }

    public Refugio getRefugio() {
        return refugio;
    }

    public void setRefugio(Refugio refugio) {
        this.refugio = refugio;
    }

    public Integer getUltimoNroAtencion() {
        return ultimoNroAtencion;
    }

    public void setUltimoNroAtencion(Integer ultimoNroAtencion) {
        this.ultimoNroAtencion = ultimoNroAtencion;
    }
}
