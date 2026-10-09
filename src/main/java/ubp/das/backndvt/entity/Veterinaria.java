package ubp.das.backndvt.entity;

public class Veterinaria {

    private Integer idVeterinaria;
    private String razonSocial;
    private String correo;
    private String telefono;
    private String domicilio;
    private String habilitacionMunicipal;

    public Integer getIdVeterinaria() {
        return idVeterinaria;
    }

    public void setIdVeterinaria(Integer idVeterinaria) {
        this.idVeterinaria = idVeterinaria;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    public String getHabilitacionMunicipal() {
        return habilitacionMunicipal;
    }

    public void setHabilitacionMunicipal(String habilitacionMunicipal) {
        this.habilitacionMunicipal = habilitacionMunicipal;
    }
}
