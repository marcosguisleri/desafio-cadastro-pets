package br.com.guisleri.petsistema.cli;

import br.com.guisleri.petsistema.domain.Endereco;
import br.com.guisleri.petsistema.domain.Pet;
import br.com.guisleri.petsistema.domain.Sexo;
import br.com.guisleri.petsistema.domain.TipoPet;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Main {

    void main() {

        while (true) {

            String caminhoPerguntas = "formulario.txt";

            IO.println("=== SISTEMA DE CADASTRO PET ===");
            IO.println("[1] Cadastrar  [2] Alterar  [3] Deletar");
            IO.println("[4] Listar     [5] Buscar   [6] Sair");
            IO.println("-------------------------------");
            String entrada = IO.readln("Digite a opção (1-6): ").trim();

            int opcao;
            try {
                opcao = Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                IO.println("Opção inválida: digite apenas números de 1 a 6.\n");
                continue;
            }

            if (opcao < 1 || opcao > 6) {
                IO.println("Opção inválida: escolha um número entre 1 e 6.\n");
                continue;
            }

            if (opcao == 6) {
                IO.println("Programa finalizado!");
                return;
            }

            if (opcao == 1) {
                List<String> respostas = new ArrayList<>();

                try (BufferedReader br = new BufferedReader(new FileReader(caminhoPerguntas))) {
                    String linha;
                    while ((linha = br.readLine()) != null) {
                        IO.println(linha);
                        respostas.add(IO.readln("R: "));
                    }
                } catch (IOException e) {
                    IO.println("Erro ao ler formulario.txt: " + e.getMessage());
                    continue;
                }

                try {

                    String nomeCompleto = respostas.get(0).trim();
                    TipoPet tipoPet = TipoPet.fromInput(respostas.get(1));
                    Sexo sexo = Sexo.fromInput(respostas.get(2));

                    Endereco endereco = capturarEndereco(respostas);

                    double idade = Double.parseDouble(respostas.get(4).replace(",", "."));
                    double pesoKg = Double.parseDouble(respostas.get(5).replace(",", "."));
                    String raca = respostas.get(6);

                    Pet pet = Pet.createPet(tipoPet, sexo, nomeCompleto, endereco, idade, pesoKg, raca);

                    File dir = new File("petsCadastrados");
                    dir.mkdirs();

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmm");
                    String dataHoraFormatada = formatter.format(pet.getDataCadastro());

                    File arquivo = criarArquivoPet(dataHoraFormatada, pet, dir);

                    IO.println("\nPet cadastrado com sucesso!");
                    IO.println("Arquivo salvo em: " + arquivo.getAbsolutePath() + "\n");

                } catch (RuntimeException e) {
                    IO.println("Erro no cadastro: " + e.getMessage() + "\n");
                } catch (IOException e) {
                    IO.println("Erro ao criar o arquivo: " + e.getMessage() + "\n");
                }
            }
        }
    }

    private static Endereco capturarEndereco(List<String> respostas) {
        String[] partesEndereco = respostas.get(3).split(",");
        if (partesEndereco.length != 4) {
            throw new RuntimeException("Endereço inválido. Use: Rua, Número, Bairro, Cidade.");
        }
        return new Endereco(
                partesEndereco[0].trim(),
                partesEndereco[1].trim(),
                partesEndereco[2].trim(),
                partesEndereco[3].trim()
        );
    }

    private static File criarArquivoPet(String dataHoraFormatada, Pet pet, File dir) throws IOException {

        String baseNomeArquivo = dataHoraFormatada + "-" +
                pet.getNomeCompleto().trim().toUpperCase().replaceAll("\\s+", "");
        String nomeArquivo =  baseNomeArquivo + ".txt";
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

}
