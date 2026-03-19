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

    private final PetRepository petRepository = new PetRepository();

    void main() {

        while (true) {
            int opcao = lerOpcaoMenu();

            switch (opcao) {
                case 1 -> cadastrarPet();
                case 2 -> alterarPet();
                case 3 -> deletarPet();
                case 4 -> listarPets();
                case 5 -> buscarPet();
                case 6 -> {
                    IO.println("Programa finalizado!");
                    return;
                }
            }
        }

    }

    private int lerOpcaoMenu() {
        int opcao = 0;

        while (true) {
            IO.println("\n=== SISTEMA DE CADASTRO PET ===");
            IO.println("[1] Cadastrar  [2] Alterar  [3] Deletar");
            IO.println("[4] Listar     [5] Buscar   [6] Sair");
            IO.println("-------------------------------");
            String entrada = IO.readln("Digite a opção (1-6): ").trim();

            try {
                opcao = Integer.parseInt(entrada);
                if (opcao >= 1 && opcao <= 6) {
                    return opcao;
                } else {
                    IO.println("Opção inválida: digite apenas números de 1 a 6.\n");
                }
            } catch (NumberFormatException e) {
                IO.println("Opção inválida: digite apenas números de 1 a 6.\n");
            }
        }
    }

    private void cadastrarPet() {
        String caminhoPerguntas = "formulario.txt";
        List<String> respostas = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(caminhoPerguntas))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                IO.println("\n" + linha);
                respostas.add(IO.readln("R: "));
            }
        } catch (IOException e) {
            IO.println("\nErro ao ler formulario.txt: " + e.getMessage());
            return;
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
    }

    private void alterarPet() {
    }

    private void deletarPet() {
    }

    private void listarPets() {
        List<Pet> pets = carregarPetsOuVazio();
        exibirListaPets(pets);
    }

    private void buscarPet() {
        List<Pet> resultado = buscarPetsPorCriterios();
        exibirListaPets(resultado);
    }

    private List<Pet> buscarPetsPorCriterios() {
        List<Pet> pets = carregarPetsOuVazio();

        if (pets.isEmpty()) {
            return List.of();
        }

        TipoPet tipoBuscado = lerTipoPetBusca();

        List<Pet> petsFiltradosPorTipo = filtrarPorTipo(pets, tipoBuscado);
        if (petsFiltradosPorTipo.isEmpty()) {
            IO.println("Nenhum pet encontrado para esse tipo!");
            return List.of();
        }

        int criterio1 = lerCriterioBusca1();
        Predicate<Pet> filtro = lerFiltro(criterio1);

        int criterio2 = lerCriterioBusca2();
        if (criterio2 != 7) {
            filtro = filtro.and(lerFiltro(criterio2));
        }

        return petsFiltradosPorTipo.stream()
                .filter(filtro)
                .toList();
    }

    private List<Pet> carregarPetsOuVazio() {
        try {
            List<Pet> pets = petRepository.carregarPets();

            if (pets.isEmpty()) {
                IO.println("\nNenhum pet cadastrado ainda!");
                return List.of();
            }

            return pets;
        } catch (Exception e) {
            IO.println("Erro ao carregar pets: " + e.getMessage());
            return List.of();
        }
    }

    private TipoPet lerTipoPetBusca() {
        while (true) {
            IO.println("\n=== BUSCAR PET ===");
            IO.println("[1] Cachorro  [2] Gato");
            IO.println("-------------------------------");

            try {
                int opcaoTipo = Integer.parseInt(IO.readln("Tipo do animal: ").trim());

                if (opcaoTipo == 1) return TipoPet.CACHORRO;
                if (opcaoTipo == 2) return TipoPet.GATO;

                IO.println("Opção inválida: escolha 1 ou 2.\n");
            } catch (NumberFormatException e) {
                IO.println("Opção inválida: digite 1 para Cachorro ou 2 para Gato.\n");
            }
        }
    }

    private List<Pet> filtrarPorTipo(List<Pet> pets, TipoPet tipoBuscado) {
        return pets.stream()
                .filter(pet -> pet.getTipoPet() == tipoBuscado)
                .toList();
    }

    private int lerCriterioBusca1() {
        while (true) {
            IO.println("\n=== BUSCAR PET ===");
            IO.println("Por qual critério deseja buscar?");
            IO.println("[1] Nome ou Sobrenome  [2] Sexo  [3] Idade");
            IO.println("[4] Peso  [5] Raça  [6] Endereço");
            IO.println("-------------------------------");

            try {
                int entrada = Integer.parseInt(IO.readln("Critério 1: ").trim());

                if (entrada >= 1 && entrada <= 6) {
                    return entrada;
                }

                IO.println("Opção inválida: escolha de 1 a 6.\n");
            } catch (NumberFormatException e) {
                IO.println("Opção inválida: digite o número correspondente.\n");
            }
        }
    }

    private int lerCriterioBusca2() {
        while (true) {
            IO.println("\n=== BUSCAR PET ===");
            IO.println("Deseja adicionar mais um critério?");
            IO.println("[1] Nome ou Sobrenome  [2] Sexo  [3] Idade");
            IO.println("[4] Peso  [5] Raça  [6] Endereço  [7] Não");
            IO.println("-------------------------------");

            try {
                int entrada = Integer.parseInt(IO.readln("Critério 2: ").trim());

                if (entrada >= 1 && entrada <= 7) {
                    return entrada;
                }

                IO.println("Opção inválida: escolha de 1 a 7.\n");
            } catch (NumberFormatException e) {
                IO.println("Opção inválida: digite o número correspondente.\n");
            }
        }
    }

    private void exibirListaPets(List<Pet> pets) {
        int cont = 1;
        for (Pet pet : pets) {
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
                double idade = lerDouble("Idade (anos): ");
                yield pet -> Double.compare(pet.getIdadeAnos(), idade) == 0;
            }
            case 4 -> {
                double peso = lerDouble("Peso (kg): ");
                yield pet -> Double.compare(pet.getPesoKg(), peso) == 0;
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