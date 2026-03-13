package br.com.guisleri.petsistema.cli;

import br.com.guisleri.petsistema.domain.Endereco;
import br.com.guisleri.petsistema.domain.Pet;
import br.com.guisleri.petsistema.domain.Sexo;
import br.com.guisleri.petsistema.domain.TipoPet;
import br.com.guisleri.petsistema.repository.PetRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {

    void main() {

        PetRepository petRepository = new PetRepository();

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

                    Endereco endereco = Endereco.fromInput(respostas.get(3));

                    double idade = Double.parseDouble(respostas.get(4).replace(",", "."));
                    double pesoKg = Double.parseDouble(respostas.get(5).replace(",", "."));
                    String raca = respostas.get(6);

                    Pet pet = Pet.createPet(tipoPet, sexo, nomeCompleto, endereco, idade, pesoKg, raca);

                    File dir = new File("petsCadastrados");
                    File arquivo = petRepository.salvarPet(pet);

                    IO.println("\nPet cadastrado com sucesso!");
                    IO.println("Arquivo salvo em: " + arquivo.getAbsolutePath() + "\n");

                } catch (RuntimeException e) {
                    IO.println("Erro no cadastro: " + e.getMessage() + "\n");
                } catch (IOException e) {
                    IO.println("Erro ao criar o arquivo: " + e.getMessage() + "\n");
                }
            } else if (opcao == 5) {

                List<Pet> pets;

                try {
                    pets = petRepository.carregarPets();
                    if (pets.isEmpty()) {
                        IO.println("Nenhum pet encontrado!");
                        continue;
                    }
                } catch (Exception e) {
                    IO.println("Erro ao carregar pets: " + e.getMessage());
                    continue;
                }

                IO.println("=== BUSCAR PET ===");
                IO.println("[1] Cachorro  [2] Gato");
                IO.println("-------------------------------");

                int opcaoTipo;
                try {
                    opcaoTipo = Integer.parseInt(IO.readln("Tipo do animal: ").trim());
                } catch (NumberFormatException e) {
                    IO.println("Opção inválida: digite 1 para Cachorro ou 2 para Gato.\n");
                    continue;
                }

                if (opcaoTipo < 1 || opcaoTipo > 2) {
                    IO.println("Opção inválida: escolha 1 ou 2.\n");
                    continue;
                }

                TipoPet tipoBuscado = opcaoTipo == 1 ? TipoPet.CACHORRO : TipoPet.GATO;

                IO.println("\nDeseja buscar por algum critério?");
                IO.println("[1] Nome  [2] Sexo  [3] Idade");
                IO.println("[4] Peso  [5] Raça  [6] Endereço  [7] Nenhum");
                int criterio1 = Integer.parseInt(IO.readln("Critério: ").trim());

                if (criterio1 < 1 || criterio1 > 7) {
                    IO.println("Opção inválida: escolha de 1 a 7.\n");
                    continue;
                }

            }
        }
    }

}
