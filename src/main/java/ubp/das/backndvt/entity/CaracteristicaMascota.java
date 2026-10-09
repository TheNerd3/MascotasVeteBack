package ubp.das.backndvt.entity;

public class CaracteristicaMascota {

    private Integer nroRegMunicipal;
    private Integer codRasgo;
    private Integer nroCaracteristica;
    private Integer nroValorDominio;
    private String valorCaracteristica;
    private Mascota mascota;
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
