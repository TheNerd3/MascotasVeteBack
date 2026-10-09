package ubp.das.backndvt.entity;

public class Mascota {

    private Integer nroRegMunicipal;
    private String nombre;
    private String sexo;
    private Short anioNacimiento;
    private String microchip;
    private Boolean vive = true;
    private Ciudadano responsable;
    private Refugio refugio;
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
