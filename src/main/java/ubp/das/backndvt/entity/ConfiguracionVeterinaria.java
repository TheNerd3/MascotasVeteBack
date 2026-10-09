package ubp.das.backndvt.entity;

public class ConfiguracionVeterinaria {

    private Integer idVeterinaria;
    private Integer codAtributo;
    private String valor;
    private Veterinaria veterinaria;
    private AtributoSistema atributo;

    public Integer getIdVeterinaria() {
        return idVeterinaria;
    }

    public void setIdVeterinaria(Integer idVeterinaria) {
        this.idVeterinaria = idVeterinaria;
    }

    public Integer getCodAtributo() {
        return codAtributo;
    }

    public void setCodAtributo(Integer codAtributo) {
        this.codAtributo = codAtributo;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public Veterinaria getVeterinaria() {
        return veterinaria;
    }

    public void setVeterinaria(Veterinaria veterinaria) {
        this.veterinaria = veterinaria;
    }

    public AtributoSistema getAtributo() {
        return atributo;
    }

    public void setAtributo(AtributoSistema atributo) {
        this.atributo = atributo;
    }
}
