package br.com.guisleri.petsistema.repository;

import br.com.guisleri.petsistema.domain.Pet;

import java.io.File;

public class PetArquivo {

    private final Pet pet;
    private final File arquivo;

    public PetArquivo(Pet pet, File arquivo) {
        this.pet = pet;
        this.arquivo = arquivo;
    }

    public Pet getPet() {
        return pet;
    }

    public File getArquivo() {
        return arquivo;
    }

}
