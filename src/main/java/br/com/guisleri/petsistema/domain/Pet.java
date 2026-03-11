package br.com.guisleri.petsistema.domain;

import java.time.LocalDateTime;

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

        if (nomeCompleto == null || nomeCompleto.isEmpty()) {
            throw new RuntimeException("É necessário informar um nome e sobrenome para o PET");
        }

        if (!nomeCompleto.matches("^[a-zA-Z ]+$")) {
            throw new RuntimeException("O nome do PET deve conter apenas letras");
        }

        if (pesoKg < 0.5 || pesoKg > 60) {
            throw new RuntimeException("O peso deve ser entre 0.5kg e 60kg");
        }

        if (idadeAnos > 20) {
            throw new RuntimeException("A idade deve ser entre 20 anos");
        }

        if (raca == null || raca.isEmpty()) {
            throw new RuntimeException("É necessário informar uma raça para o PET");
        }

        if (!raca.matches("^[a-zA-Z ]+$")) {
            throw new RuntimeException("A raça do PET deve conter apenas letras");
        }

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

    public TipoPet getTipoPet() {
        return tipoPet;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

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
}
