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

    private static final String PASTA      = "petsCadastrados";
    private static final String FORMULARIO = "formulario.txt";
    private static final int TOTAL_PERGUNTAS_ORIGINAIS = 7;

    public File salvarPet(Pet pet) throws IOException {
        File dir = new File(PASTA);
        dir.mkdirs();

        File arquivo = resolverNomeArquivo(dir, pet);
        List<String> perguntasExtras = lerPerguntasExtras();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(arquivo))) {
            bw.write("1 - " + pet.getNomeCompleto() + "\n");
            bw.write("2 - " + pet.getTipoPet() + "\n");
            bw.write("3 - " + pet.getSexo() + "\n");
            bw.write("4 - " + pet.getEndereco().getRua() + ", " + pet.getEndereco().getNumero()
                    + ", " + pet.getEndereco().getBairro() + ", " + pet.getEndereco().getCidade() + "\n");
            bw.write("5 - " + pet.getIdadeAnos() + " ano(s)\n");
            bw.write("6 - " + pet.getPesoKg() + "kg\n");
            bw.write("7 - " + pet.getRaca());

            List<String> respostasExtra = pet.getRespostasExtra();
            for (int i = 0; i < respostasExtra.size(); i++) {
                int numero = TOTAL_PERGUNTAS_ORIGINAIS + i + 1;
                String textoPergunta = perguntasExtras.get(i).split(" - ")[1];
                bw.write("\n" + numero + " - [EXTRA - " + textoPergunta + "] - " + respostasExtra.get(i));
            }
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

    public List<Pet> carregarPets() throws Exception {
        List<Pet> pets = new ArrayList<>();
        for (File arquivo : listarArquivosValidos()) {
            pets.add(parserArquivo(arquivo));
        }
        return pets;
    }

    public List<PetArquivo> carregarPetsComArquivo() throws Exception {
        List<PetArquivo> petsComArquivo = new ArrayList<>();
        for (File arquivo : listarArquivosValidos()) {
            petsComArquivo.add(new PetArquivo(parserArquivo(arquivo), arquivo));
        }
        return petsComArquivo;
    }

    private Pet parserArquivo(File arquivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String nomeCompleto = br.readLine().substring(4).trim();
            TipoPet tipo        = TipoPet.fromInput(br.readLine().substring(4).trim());
            Sexo sexo           = Sexo.fromInput(br.readLine().substring(4).trim());
            Endereco endereco   = Endereco.fromInput(br.readLine().substring(4).trim());
            double idade        = Double.parseDouble(br.readLine().substring(4).trim().replace(" ano(s)", ""));
            double peso         = Double.parseDouble(br.readLine().substring(4).trim().replace("kg", ""));
            String raca         = br.readLine().substring(4).trim();

            return Pet.createPet(tipo, sexo, nomeCompleto, endereco, idade, peso, raca, new ArrayList<>());

        } catch (Exception e) {
            throw new RuntimeException("Erro ao ler arquivo: " + arquivo.getName());
        }
    }

    private File resolverNomeArquivo(File dir, Pet pet) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmm");
        String base = formatter.format(pet.getDataCadastro()) + "-"
                + pet.getNomeCompleto().trim().toUpperCase().replaceAll("\\s+", "");

        File arquivo = new File(dir, base + ".txt");
        int cont = 1;
        while (arquivo.exists()) {
            arquivo = new File(dir, base + "-" + cont++ + ".txt");
        }
        return arquivo;
    }

    private List<String> lerPerguntasExtras() {
        List<String> extras = new ArrayList<>();
        int cont = 1;
        try (BufferedReader br = new BufferedReader(new FileReader(FORMULARIO))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                if (cont > TOTAL_PERGUNTAS_ORIGINAIS) extras.add(linha);
                cont++;
            }
        } catch (IOException e) {
            IO.println("Erro ao ler formulário: " + e.getMessage());
        }
        return extras;
    }

    private List<File> listarArquivosValidos() {
        File dir = new File(PASTA);
        if (!dir.exists() || !dir.isDirectory()) return new ArrayList<>();

        File[] arquivos = dir.listFiles();
        if (arquivos == null) return new ArrayList<>();

        return Arrays.stream(arquivos)
                .filter(file -> file.isFile() && file.getName().endsWith(".txt"))
                .collect(Collectors.toList());
    }
}