package br.com.guisleri.petsistema.domain;

public class Endereco {

    private String rua;
    private String numero;
    private String bairro;
    private String cidade;

    private static final String NAO_INFORMADO = "NÃO INFORMADO";

    public Endereco(String rua, String numero, String bairro, String cidade) {

        this.rua = rua.trim();

        if (numero == null) {
            this.numero = NAO_INFORMADO;
        } else {
            this.numero = numero.trim();
        }

        this.bairro = bairro.trim();
        this.cidade = cidade.trim();
    }

    @Override
    public String toString() {
        return "Endereco{" +
                "rua='" + rua + '\'' +
                ", numero='" + numero + '\'' +
                ", bairro='" + bairro + '\'' +
                ", cidade='" + cidade + '\'' +
                '}';
    }

    public String getRua() {
        return rua;
    }

    public String getNumero() {
        return numero;
    }

    public String getBairro() {
        return bairro;
    }

    public String getCidade() {
        return cidade;
    }
}
