package br.com.guisleri.petsistema.repository;

import br.com.guisleri.petsistema.domain.Endereco;
import br.com.guisleri.petsistema.domain.Pet;
import br.com.guisleri.petsistema.domain.Sexo;
import br.com.guisleri.petsistema.domain.TipoPet;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PetRepository {

    private static final String PASTA = "petsCadastrados";

    public File salvarPet(Pet pet) throws IOException {

        File dir = new File(PASTA);
        dir.mkdirs();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmm");
        String dataHoraFormatada = formatter.format(pet.getDataCadastro());

        String baseNomeArquivo = dataHoraFormatada + "-" +
                pet.getNomeCompleto().trim().toUpperCase().replaceAll("\\s+", "");
        String nomeArquivo = baseNomeArquivo + ".txt";
        File arquivo = new File(dir, nomeArquivo);

        int cont = 1;
        while (arquivo.exists()) {
            nomeArquivo = baseNomeArquivo + "-" + cont + ".txt";
            arquivo = new File(dir, nomeArquivo);
            cont++;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(arquivo))) {
            bw.write("1 - " + pet.getNomeCompleto() + "\n");
            bw.write("2 - " + pet.getTipoPet() + "\n");
            bw.write("3 - " + pet.getSexo() + "\n");
            bw.write("4 - " + pet.getEndereco().getRua() + ", " + pet.getEndereco().getNumero() + ", " + pet.getEndereco().getBairro() + ", " + pet.getEndereco().getCidade() + "\n");
            bw.write("5 - " + pet.getIdadeAnos() + " ano(s)" + "\n");
            bw.write("6 - " + pet.getPesoKg() + "kg" + "\n");
            bw.write("7 - " + pet.getRaca());
        }

        return arquivo;
    }

    public void deletarPet(File arquivo) {
        if (!arquivo.delete()) {
            throw new RuntimeException("Não foi possível deletar o arquivo: " + arquivo.getName());
        }
    }

    public File atualizarPet(PetArquivo petArquivo, Pet petAtualizado) throws IOException {
        deletarPet(petArquivo.getArquivo());
        return salvarPet(petAtualizado);
    }

    private List<File> listarArquivosValidos() {
        File dir = new File(PASTA);

        if (!dir.exists() || !dir.isDirectory()) {
            return new ArrayList<>();
        }

        File[] arquivos = dir.listFiles();
        if (arquivos == null) {
            return new ArrayList<>();
        }

        return Arrays.stream(arquivos)
                .filter(file -> file.isFile() && file.getName().endsWith(".txt"))
                .collect(Collectors.toList());
    }

    public List<Pet> carregarPets() throws Exception {
        List<File> arquivos = listarArquivosValidos();
        List<Pet> pets = new ArrayList<>();
        for (File arquivo : arquivos) {
            pets.add(parserArquivo(arquivo));
        }
        return pets;
    }

    public List<PetArquivo> carregarPetsComArquivo() throws Exception {
        List<File> arquivos = listarArquivosValidos();
        List<PetArquivo> petsComArquivo = new ArrayList<>();
        for (File arquivo : arquivos) {
            Pet pet = parserArquivo(arquivo);
            petsComArquivo.add(new PetArquivo(pet, arquivo));
        }
        return petsComArquivo;
    }

    private Pet parserArquivo(File arquivo) {

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String nomeCompleto = br.readLine().substring(4).trim();
            TipoPet tipo = TipoPet.fromInput(br.readLine().substring(4).trim());
            Sexo sexo = Sexo.fromInput(br.readLine().substring(4).trim());
            Endereco endereco = Endereco.fromInput(br.readLine().substring(4).trim());
            double idade = Double.parseDouble(br.readLine().substring(4).trim().replace(" ano(s)", ""));
            double peso = Double.parseDouble(br.readLine().substring(4).trim().replace("kg", ""));
            String raca = br.readLine().substring(4).trim();

            return Pet.createPet(tipo, sexo, nomeCompleto, endereco, idade, peso, raca);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao ler arquivo: " + arquivo.getName());
        }
    }

}
