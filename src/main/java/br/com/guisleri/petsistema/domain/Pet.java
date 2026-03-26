package br.com.guisleri.petsistema.domain;

import java.time.LocalDateTime;
import java.util.List;

import static br.com.guisleri.petsistema.domain.Endereco.NAO_INFORMADO;

public class Pet {

    private final TipoPet tipoPet;
    private final Sexo sexo;
    private String nomeCompleto;
    private Endereco endereco;
    private double idadeAnos;
    private double pesoKg;
    private String raca;
    private List<String> respostasExtra;
    private final LocalDateTime dataCadastro;

    private Pet(TipoPet tipoPet,
                Sexo sexo,
                String nomeCompleto,
                Endereco endereco,
                double idadeAnos,
                double pesoKg,
                String raca,
                List<String> respostasExtra,
                LocalDateTime dataCadastro) {
        this.tipoPet        = tipoPet;
        this.sexo           = sexo;
        this.nomeCompleto   = nomeCompleto;
        this.endereco       = endereco;
        this.idadeAnos      = idadeAnos;
        this.pesoKg         = pesoKg;
        this.raca           = raca;
        this.respostasExtra = respostasExtra;
        this.dataCadastro   = dataCadastro;
    }

    public static Pet createPet(TipoPet tipoPet,
                                Sexo sexo,
                                String nomeCompleto,
                                Endereco endereco,
                                double idadeAnos,
                                double pesoKg,
                                String raca,
                                List<String> respostasExtra) {

        if (tipoPet == null)  throw new RuntimeException("Tipo de pet é obrigatório.");
        if (sexo == null)     throw new RuntimeException("Sexo é obrigatório.");
        if (endereco == null) throw new RuntimeException("Endereço é obrigatório.");

        if (nomeCompleto == null || nomeCompleto.trim().isEmpty())
            throw new RuntimeException("Nome e sobrenome são obrigatórios.");

        nomeCompleto = nomeCompleto.trim();

        if (!nomeCompleto.matches("^[A-Za-z]+(?: [A-Za-z]+)*$"))
            throw new RuntimeException("Nome deve conter apenas letras e espaços.");
        if (nomeCompleto.split("\\s+").length < 2)
            throw new RuntimeException("Informe nome e sobrenome.");

        raca = (raca == null || raca.trim().isEmpty()) ? NAO_INFORMADO : raca.trim();

        if (!raca.equals(NAO_INFORMADO) && !raca.matches("^[A-Za-z]+(?: [A-Za-z]+)*$"))
            throw new RuntimeException("Raça deve conter apenas letras e espaços.");

        if (idadeAnos < 0 || idadeAnos > 20)
            throw new RuntimeException("Idade deve estar entre 0 e 20 anos.");
        if (pesoKg < 0.5 || pesoKg > 60.0)
            throw new RuntimeException("Peso deve estar entre 0,5 kg e 60 kg.");

        return new Pet(tipoPet, sexo, nomeCompleto, endereco, idadeAnos, pesoKg, raca,
                respostasExtra == null ? List.of() : respostasExtra,
                LocalDateTime.now());
    }

    @Override
    public String toString() {
        return "Pet{" +
                "tipoPet=" + tipoPet +
                ", sexo=" + sexo +
                ", nomeCompleto='" + nomeCompleto + '\'' +
                ", endereco=" + endereco +
                ", idadeAnos=" + idadeAnos +
                ", pesoKg=" + pesoKg +
                ", raca='" + raca + '\'' +
                ", dataCadastro=" + dataCadastro +
                '}';
    }

    // Getters
    public TipoPet getTipoPet()              { return tipoPet; }
    public Sexo getSexo()                    { return sexo; }
    public String getNomeCompleto()          { return nomeCompleto; }
    public Endereco getEndereco()            { return endereco; }
    public double getIdadeAnos()             { return idadeAnos; }
    public double getPesoKg()                { return pesoKg; }
    public String getRaca()                  { return raca; }
    public List<String> getRespostasExtra()  { return respostasExtra; }
    public LocalDateTime getDataCadastro()   { return dataCadastro; }

    // Setters (Tipo e Sexo não são alteráveis)
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }
    public void setEndereco(Endereco endereco)        { this.endereco = endereco; }
    public void setIdadeAnos(double idadeAnos)        { this.idadeAnos = idadeAnos; }
    public void setPesoKg(double pesoKg)              { this.pesoKg = pesoKg; }
    public void setRaca(String raca)                  { this.raca = raca; }
}