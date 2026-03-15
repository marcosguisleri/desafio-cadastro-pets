package br.com.guisleri.petsistema.cli;

import br.com.guisleri.petsistema.domain.Endereco;
import br.com.guisleri.petsistema.domain.Pet;
import br.com.guisleri.petsistema.domain.Sexo;
import br.com.guisleri.petsistema.domain.TipoPet;
import br.com.guisleri.petsistema.repository.PetRepository;

import java.io.*;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Main {

    void main() {

        PetRepository petRepository = new PetRepository();

        while (true) {

            String caminhoPerguntas = "formulario.txt";

            IO.println("\n=== SISTEMA DE CADASTRO PET ===");
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

                    File arquivo = petRepository.salvarPet(pet);

                    IO.println("\nPet cadastrado com sucesso!");
                    IO.println("Arquivo salvo em: " + arquivo.getAbsolutePath());

                } catch (RuntimeException e) {
                    IO.println("Erro no cadastro: " + e.getMessage() + "\n");
                } catch (IOException e) {
                    IO.println("Erro ao criar o arquivo: " + e.getMessage() + "\n");
                }

            } else if (opcao == 5) {
                buscarPet();
            }
        }
    }

    private void buscarPet() {
        PetRepository petRepository = new PetRepository();
        List<Pet> pets;

        try {
            pets = petRepository.carregarPets();
            if (pets.isEmpty()) {
                IO.println("Nenhum pet encontrado!");
                return;
            }
        } catch (Exception e) {
            IO.println("Erro ao carregar pets: " + e.getMessage());
            return;
        }

        TipoPet tipoBuscado = null;
        while (tipoBuscado == null) {
            IO.println("\n=== BUSCAR PET ===");
            IO.println("[1] Cachorro  [2] Gato");
            IO.println("-------------------------------");

            try {
                int opcaoTipo = Integer.parseInt(IO.readln("Tipo do animal: ").trim());
                if (opcaoTipo == 1)      tipoBuscado = TipoPet.CACHORRO;
                else if (opcaoTipo == 2) tipoBuscado = TipoPet.GATO;
                else IO.println("Opção inválida: escolha 1 ou 2.\n");
            } catch (NumberFormatException e) {
                IO.println("Opção inválida: digite 1 para Cachorro ou 2 para Gato.\n");
            }
        }

        TipoPet finalTipoBuscado = tipoBuscado;
        List<Pet> petsFiltrados = pets.stream()
                .filter(pet -> pet.getTipoPet() == finalTipoBuscado)
                .toList();

        if (petsFiltrados.isEmpty()) {
            IO.println("Nenhum pet encontrado para esse tipo!");
            return;
        }

        int opcaoCriterio1 = 0;
        while (opcaoCriterio1 == 0) {
            IO.println("\n=== BUSCAR PET ===");
            IO.println("Por qual critério deseja buscar?");
            IO.println("[1] Nome ou Sobrenome  [2] Sexo  [3] Idade");
            IO.println("[4] Peso  [5] Raça  [6] Endereço");
            IO.println("-------------------------------");

            try {
                int entrada1 = Integer.parseInt(IO.readln("Critério 1: ").trim());
                if (entrada1 >= 1 && entrada1 <= 6) opcaoCriterio1 = entrada1;
                else IO.println("Opção inválida: escolha de 1 a 6.\n");
            } catch (NumberFormatException e) {
                IO.println("Opção inválida: digite o número correspondente.\n");
            }
        }

        Predicate<Pet> filtro = lerFiltro(opcaoCriterio1);

        int opcaoCriterio2 = 0;
        while (opcaoCriterio2 == 0) {
            IO.println("\n=== BUSCAR PET ===");
            IO.println("Deseja adicionar mais um critério?");
            IO.println("[1] Nome ou Sobrenome  [2] Sexo  [3] Idade");
            IO.println("[4] Peso  [5] Raça  [6] Endereço  [7] Não");
            IO.println("-------------------------------");

            try {
                int entrada2 = Integer.parseInt(IO.readln("Critério 2: ").trim());
                if (entrada2 >= 1 && entrada2 <= 7) opcaoCriterio2 = entrada2;
                else IO.println("Opção inválida: escolha de 1 a 7.\n");
            } catch (NumberFormatException e) {
                IO.println("Opção inválida: digite o número correspondente.\n");
            }
        }

        if (opcaoCriterio2 != 7) {
            filtro = filtro.and(lerFiltro(opcaoCriterio2));
        }

        List<Pet> resultado = petsFiltrados.stream()
                .filter(filtro)
                .toList();

        if (resultado.isEmpty()) {
            IO.println("\nNenhum pet encontrado com os critérios informados.");
            return;
        }

        int cont = 1;
        for (Pet pet : resultado) {
            IO.println(formatarLinhaResultado(cont, pet));
            cont++;
        }
    }

    private Predicate<Pet> lerFiltro(int criterio) {
        return switch (criterio) {
            case 1 -> {
                String valor = normalizar(IO.readln("Nome ou sobrenome: "));
                yield pet -> normalizar(pet.getNomeCompleto()).contains(valor);
            }
            case 2 -> {
                String valor = normalizar(IO.readln("Sexo (Macho/Fêmea): "));
                yield pet -> normalizar(pet.getSexo().toString()).contains(valor);
            }
            case 3 -> {
                double idade = lerDouble("Idade (anos): "); // ✅ Bug 2 corrigido
                yield pet -> Double.compare(pet.getIdadeAnos(), idade) == 0; // ✅ Bug 3 corrigido
            }
            case 4 -> {
                double peso = lerDouble("Peso (kg): "); // ✅ Bug 2 corrigido
                yield pet -> Double.compare(pet.getPesoKg(), peso) == 0; // ✅ Bug 3 corrigido
            }
            case 5 -> {
                String valor = normalizar(IO.readln("Raça: "));
                yield pet -> normalizar(pet.getRaca()).contains(valor);
            }
            case 6 -> {
                String valor = normalizar(IO.readln("Endereço: "));
                yield pet -> normalizar(pet.getEndereco().toSearchString()).contains(valor);
            }
            default -> pet -> true;
        };
    }

    private double lerDouble(String prompt) {
        while (true) {
            try {
                return Double.parseDouble(IO.readln(prompt).trim().replace(",", "."));
            } catch (NumberFormatException e) {
                IO.println("Valor inválido: digite um número.\n");
            }
        }
    }

    private static String formatarLinhaResultado(int id, Pet pet) {
        String rua = pet.getEndereco().getRua();
        String numero = pet.getEndereco().getNumero();
        String bairro = pet.getEndereco().getBairro();
        String cidade = pet.getEndereco().getCidade();

        String enderecoResumo = String.format("%s, %s - %s - %s", rua, numero, bairro, cidade);

        return String.format(
                "\n%d. %s - %s - %s - %s - %.1f anos - %.1fkg - %s",
                id,
                pet.getNomeCompleto(),
                pet.getTipoPet(),
                pet.getSexo(),
                enderecoResumo,
                pet.getIdadeAnos(),
                pet.getPesoKg(),
                pet.getRaca()
        );
    }

    private static String normalizar(String texto) {
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}", "")
                .toLowerCase();
    }
}