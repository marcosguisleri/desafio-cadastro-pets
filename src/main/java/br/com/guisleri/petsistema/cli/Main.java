package br.com.guisleri.petsistema.cli;

import br.com.guisleri.petsistema.domain.Endereco;
import br.com.guisleri.petsistema.domain.Pet;
import br.com.guisleri.petsistema.domain.Sexo;
import br.com.guisleri.petsistema.domain.TipoPet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Main {

    void main() {

        while (true) {

            String caminho = "formulario.txt";

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

                try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
                    String linha;
                    while ((linha = br.readLine()) != null) {
                        IO.println(linha);
                        respostas.add(IO.readln("R: "));
                    }
                } catch (Exception e) {
                    IO.println("Erro ao ler formulario.txt: " + e.getMessage());
                    continue;
                }

                try {

                    String nomeCompleto = respostas.get(0);

                    TipoPet tipoPet = TipoPet.fromInput(respostas.get(1));

                    Sexo sexo = Sexo.fromInput(respostas.get(2));

                    String[] partesEndereco = respostas.get(3).split(",");
                    if (partesEndereco.length != 4) throw new RuntimeException("Endereço inválido. Use: Rua, Número, Bairro, Cidade.");
                    String rua = partesEndereco[0].trim();
                    String numero = partesEndereco[1].trim();
                    String bairro = partesEndereco[2].trim();
                    String cidade = partesEndereco[3].trim();
                    Endereco endereco = new Endereco(rua, numero, bairro, cidade);

                    String idadeString = respostas.get(4).replaceAll(",", ".");
                    double idade = Double.parseDouble(idadeString);

                    String peso = respostas.get(5).replaceAll(",", ".");
                    double pesoKg = Double.parseDouble(peso);

                    String raca = respostas.get(6);

                    Pet pet = Pet.createPet(tipoPet, sexo, nomeCompleto, endereco, idade, pesoKg, raca);

                    IO.println("\nPet cadastrado com sucesso!\n");

                } catch (RuntimeException e) {
                    IO.println("Erro no cadastro: " + e.getMessage() + "\n");
                    continue;
                }

            }

        }

    }

}
