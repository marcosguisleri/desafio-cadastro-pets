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
