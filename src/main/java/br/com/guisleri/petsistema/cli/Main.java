package br.com.guisleri.petsistema.cli;

import br.com.guisleri.petsistema.domain.Endereco;
import br.com.guisleri.petsistema.domain.Pet;
import br.com.guisleri.petsistema.domain.Sexo;
import br.com.guisleri.petsistema.domain.TipoPet;
import br.com.guisleri.petsistema.repository.PetArquivo;
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
                    IO.println("Opção inválida: digite apenas números de 1 a 6.");
                }
            } catch (NumberFormatException e) {
                IO.println("Opção inválida: digite apenas números de 1 a 6.");
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
        List<PetArquivo> resultado = buscarPetsComArquivoPorCriterios();
        if (resultado.isEmpty()) return;

        exibirListaPetsComArquivo(resultado);
        int escolha = lerEscolhaDaLista(resultado.size());
        PetArquivo petArquivo = resultado.get(escolha - 1);
        Pet pet = petArquivo.getPet();

        IO.println("\n=== ALTERAR PET: " + pet.getNomeCompleto() + " ===");
        IO.println("(Deixe em branco para manter o valor atual)\n");

        String novoNome = IO.readln("Nome completo [" + pet.getNomeCompleto() + "]: ").trim();
        if (!novoNome.isEmpty()) {
            if (!novoNome.matches("^[A-Za-zÀ-ú]+(?: [A-Za-zÀ-ú]+)*$") || novoNome.split("\\s+").length < 2) {
                IO.println("Nome inválido (somente letras, mínimo 2 nomes). Mantendo o valor atual.");
            } else {
                pet.setNomeCompleto(novoNome);
            }
        }

        Endereco endAtual = pet.getEndereco();
        String endAtualFormatado = endAtual.getRua() + ", " + endAtual.getNumero() + ", "
                + endAtual.getBairro() + ", " + endAtual.getCidade();
        String novoEndereco = IO.readln("Endereço [" + endAtualFormatado + "]: ").trim();
        if (!novoEndereco.isEmpty()) {
            try {
                pet.setEndereco(Endereco.fromInput(novoEndereco));
            } catch (RuntimeException e) {
                IO.println("Endereço inválido (use: Rua, Número, Bairro, Cidade). Mantendo o valor atual.");
            }
        }

        String novaIdade = IO.readln("Idade em anos [" + pet.getIdadeAnos() + "]: ").trim();
        if (!novaIdade.isEmpty()) {
            try {
                double idade = Double.parseDouble(novaIdade.replace(",", "."));
                if (idade < 0 || idade > 20) {
                    IO.println("Idade inválida (0 a 20 anos). Mantendo o valor atual.");
                } else {
                    pet.setIdadeAnos(idade);
                }
            } catch (NumberFormatException e) {
                IO.println("Valor inválido. Mantendo o valor atual.");
            }
        }

        String novoPeso = IO.readln("Peso em kg [" + pet.getPesoKg() + "]: ").trim();
        if (!novoPeso.isEmpty()) {
            try {
                double peso = Double.parseDouble(novoPeso.replace(",", "."));
                if (peso < 0.5 || peso > 60) {
                    IO.println("Peso inválido (0.5kg a 60kg). Mantendo o valor atual.");
                } else {
                    pet.setPesoKg(peso);
                }
            } catch (NumberFormatException e) {
                IO.println("Valor inválido. Mantendo o valor atual.");
            }
        }

        String novaRaca = IO.readln("Raça [" + pet.getRaca() + "]: ").trim();
        if (!novaRaca.isEmpty()) {
            if (!novaRaca.matches("^[A-Za-zÀ-ú]+(?: [A-Za-zÀ-ú]+)*$")) {
                IO.println("Raça inválida (somente letras). Mantendo o valor atual.");
            } else {
                pet.setRaca(novaRaca);
            }
        }

        try {
            File novoArquivo = petRepository.atualizarPet(petArquivo, pet);
            IO.println("\nPet atualizado com sucesso!");
            IO.println("Arquivo salvo em: " + novoArquivo.getAbsolutePath());
        } catch (IOException e) {
            IO.println("Erro ao salvar alterações: " + e.getMessage());
        }

    }

    private void deletarPet() {
        List<PetArquivo> resultado = buscarPetsComArquivoPorCriterios();
        if (resultado.isEmpty()) return;

        exibirListaPetsComArquivo(resultado);
        int escolha = lerEscolhaDaLista(resultado.size());
        PetArquivo petArquivo = resultado.get(escolha - 1);

        IO.println("\nVocê está prestes a deletar: \"" + petArquivo.getPet().getNomeCompleto() + "\"");
        String confirmacao = IO.readln("Tem certeza? Digite SIM para confirmar: ").trim();

        if (confirmacao.equalsIgnoreCase("SIM")) {
            petRepository.deletarPet(petArquivo.getArquivo());
            IO.println("\nPet deletado com sucesso!");
        } else {
            IO.println("\nOperação cancelada. Nenhum pet foi deletado.");
        }

    }

    private void listarPets() {
        List<Pet> pets = carregarPetsOuVazio();

        if (pets.isEmpty()) {
            return;
        }

        exibirListaPets(pets);
    }

    private void buscarPet() {
        List<Pet> resultado = buscarPetsPorCriterios();

        if (resultado.isEmpty()) {
            return;
        }

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

        List<Pet> resultado = petsFiltradosPorTipo.stream()
                .filter(filtro)
                .toList();

        if (resultado.isEmpty()) {
            IO.println("\nNenhum pet encontrado com os critérios informados.");
            return List.of();
        }

        return resultado;
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

    private List<PetArquivo> buscarPetsComArquivoPorCriterios() {
        List<PetArquivo> petsComArquivo = carregarPetsComArquivoOuVazio();

        if (petsComArquivo.isEmpty()) {
            return List.of();
        }

        TipoPet tipoBuscado = lerTipoPetBusca();

        List<PetArquivo> petsFiltradosPorTipo = petsComArquivo.stream()
                .filter(item -> item.getPet().getTipoPet() == tipoBuscado)
                .toList();

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

        Predicate<Pet> finalFiltro = filtro;
        List<PetArquivo> resultado = petsFiltradosPorTipo.stream()
                .filter(item -> finalFiltro.test(item.getPet()))
                .toList();

        if (resultado.isEmpty()) {
            IO.println("\nNenhum pet encontrado com os critérios informados.");
            return List.of();
        }

        return resultado;

    }

    private List<PetArquivo> carregarPetsComArquivoOuVazio() {
        try {
            List<PetArquivo> petsComArquivo = petRepository.carregarPetsComArquivo();

            if (petsComArquivo.isEmpty()) {
                IO.println("\nNenhum pet cadastrado ainda!");
                return List.of();
            }

            return petsComArquivo;
        } catch (Exception e) {
            IO.println("Erro ao carregar pets: " + e.getMessage());
            return List.of();
        }
    }

    private void exibirListaPetsComArquivo(List<PetArquivo> petsComArquivo) {
        int cont = 1;
        for (PetArquivo item : petsComArquivo) {
            IO.println(formatarLinhaResultado(cont, item.getPet()));
            cont++;
        }
    }

    private int lerEscolhaDaLista(int total) {
        while (true) {
            try {
                int escolha = Integer.parseInt(IO.readln("\nDigite o número do pet desejado: ").trim());
                if (escolha >= 1 && escolha <= total) {
                    return escolha;
                }
                IO.println("Número inválido: escolha entre 1 e " + total + ".");
            } catch (NumberFormatException e) {
                IO.println("Entrada inválida: digite apenas números.");
            }
        }
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