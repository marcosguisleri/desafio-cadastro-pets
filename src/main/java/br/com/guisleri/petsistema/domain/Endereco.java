package br.com.guisleri.petsistema.domain;

public class Endereco {

    public static final String NAO_INFORMADO = "NÃO INFORMADO";

    private String rua;
    private String numero;
    private String bairro;
    private String cidade;

    public Endereco(String rua, String numero, String bairro, String cidade) {
        this.rua = emptyIfBlank(rua);
        this.numero = naoInformadoIfBlank(numero);
        this.bairro = emptyIfBlank(bairro);
        this.cidade = emptyIfBlank(cidade);
    }

    private static String emptyIfBlank(String s) {
        if (s == null) {
            return "";
        }
        String t =  s.trim();
        return (t.isEmpty()) ? "" : t;
    }

    private static String naoInformadoIfBlank(String s) {
        if (s == null) {
            return NAO_INFORMADO;
        }
        String t =  s.trim();
        return (t.isEmpty()) ? NAO_INFORMADO : t;
    }

    public static Endereco fromInput(String input) {
        String[] partes = input.split(",");
        if (partes.length != 4) {
            throw new RuntimeException("Endereço inválido. Use: Rua, Número, Bairro, Cidade.");
        }
        return new Endereco(
                partes[0].trim(),
                partes[1].trim(),
                partes[2].trim(),
                partes[3].trim()
        );
    }

    @Override
    public String toString() {
        return "Endereco {" +
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

    public String toSearchString() {
        return rua + " " + numero + " " + bairro + " " + cidade;
    }
}
