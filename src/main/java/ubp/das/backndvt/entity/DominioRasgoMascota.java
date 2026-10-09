package ubp.das.backndvt.entity;

public class DominioRasgoMascota {

    private Integer codRasgo;
    private Integer nroValorDominio;
    private String nomValorDominio;
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
