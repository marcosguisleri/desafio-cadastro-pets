package br.com.guisleri.petsistema.domain;

import java.time.LocalDateTime;

import static br.com.guisleri.petsistema.domain.Endereco.NAO_INFORMADO;

public class Pet {

    private final TipoPet tipoPet;
    private final Sexo sexo;
    private String nomeCompleto;
    private Endereco endereco;
    private double idadeAnos;
    private double pesoKg;
    private String raca;
    private final LocalDateTime dataCadastro;

    private Pet(TipoPet tipoPet,
                Sexo sexo,
                String nomeCompleto,
                Endereco endereco,
                double idadeAnos,
                double pesoKg,
                String raca,
                LocalDateTime dataCadastro) {

        this.tipoPet = tipoPet;
        this.sexo = sexo;
        this.nomeCompleto = nomeCompleto;
        this.endereco = endereco;
        this.idadeAnos = idadeAnos;
        this.pesoKg = pesoKg;
        this.raca = raca;
        this.dataCadastro = dataCadastro;
    }

    public static Pet createPet(TipoPet tipoPet,
                             Sexo sexo,
                             String nomeCompleto,
                             Endereco endereco,
                             double idadeAnos,
                             double pesoKg,
                             String raca) {

        if (tipoPet == null) throw new RuntimeException("Tipo de pet é obrigatório.");
        if (sexo == null) throw new RuntimeException("Sexo é obrigatório.");
        if (endereco == null) throw new RuntimeException("Endereço é obrigatório");


        if (nomeCompleto == null || nomeCompleto.trim().isEmpty()) throw new RuntimeException("Nome e sobrenome é obrigatório.");
        nomeCompleto = nomeCompleto.trim();
        if (!nomeCompleto.matches("^[A-Za-z]+(?: [A-Za-z]+)*$")) throw new RuntimeException("Nome e sobrenome deve conter apenas letras e espaços.");
        if (nomeCompleto.split("\\s+").length < 2) throw new RuntimeException("Nome completo deve conter pelo menos 2 nomes.");

        if (raca == null || raca.trim().isEmpty()) {
            raca = NAO_INFORMADO;
        } else {
            raca = raca.trim();
        }

        if (!raca.equals(NAO_INFORMADO)) {
            if (!raca.matches("^[A-Za-z]+(?: [A-Za-z]+)*$")) throw new RuntimeException("Raça deve conter apenas letras e espaços.");
        }

        if (idadeAnos < 0 || idadeAnos > 20) throw new RuntimeException("Idade deve estar entre 0 e 20 anos.");
        if (pesoKg < 0.5 || pesoKg > 60.0) throw new RuntimeException("Peso precisa estar entre 0.5kg e 60kg.");

        return new Pet(tipoPet, sexo, nomeCompleto, endereco, idadeAnos, pesoKg, raca, LocalDateTime.now());
    }

    @Override
    public String toString() {
        return "Pet{" +
                "tipoPet=" + tipoPet +
                ", sexo=" + sexo +
                ", nomeCompleto='" + nomeCompleto + '\'' +
                ", endereco=" + endereco.toString() +
                ", idadeAnos=" + idadeAnos +
                ", pesoKg=" + pesoKg +
                ", raca='" + raca + '\'' +
                ", dataCadastro=" + dataCadastro +
                '}';
    }

    public TipoPet getTipoPet() { return tipoPet; }

    public Sexo getSexo() { return sexo; }

    public String getNomeCompleto() { return nomeCompleto; }

    public Endereco getEndereco() {
        return endereco;
    }

    public double getIdadeAnos() {
        return idadeAnos;
    }

    public double getPesoKg() {
        return pesoKg;
    }

    public String getRaca() {
        return raca;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }

    public void setEndereco(Endereco endereco) { this.endereco = endereco; }

    public void setIdadeAnos(double idadeAnos) { this.idadeAnos = idadeAnos; }

    public void setPesoKg(double pesoKg) { this.pesoKg = pesoKg; }

    public void setRaca(String raca) { this.raca = raca; }
}
