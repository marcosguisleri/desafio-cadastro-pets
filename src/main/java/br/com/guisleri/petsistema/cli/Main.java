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

    private static final String FORMULARIO = "formulario.txt";
    private static final int TOTAL_PERGUNTAS_ORIGINAIS = 7;

    private final PetRepository petRepository = new PetRepository();

    void main() {
        while (true) {
            int opcaoInicial = lerOpcaoMenuInicial();

            if (opcaoInicial == 1) {
                while (true) {
                    int opcao = lerOpcaoMenuCadastro();
                    if (opcao == 1)      cadastrarPet();
                    else if (opcao == 2) alterarPet();
                    else if (opcao == 3) deletarPet();
                    else if (opcao == 4) listarPets();
                    else if (opcao == 5) buscarPet();
                    else if (opcao == 6) { IO.println("\nVoltando ao menu inicial..."); break; }
                }
            } else if (opcaoInicial == 2) {
                while (true) {
                    int opcao = lerOpcaoMenuFormulario();
                    if (opcao == 1)      criarPergunta();
                    else if (opcao == 2) alterarPergunta();
                    else if (opcao == 3) deletarPergunta();
                    else if (opcao == 4) { IO.println("\nVoltando ao menu inicial..."); break; }
                }
            } else if (opcaoInicial == 3) {
                IO.println("\nAté logo!");
                break;
            }
        }
    }

    private int lerOpcaoMenuInicial() {
        while (true) {
            IO.println("\n=== ACOLHE PET ===");
            IO.println("[1] Cadastro de pets");
            IO.println("[2] Gerenciar formulário");
            IO.println("[3] Sair");
            IO.println("-------------------------------");
            String entrada = IO.readln("Opção (1-3): ").trim();

            try {
                int opcao = Integer.parseInt(entrada);
                if (opcao >= 1 && opcao <= 3) return opcao;
                IO.println("Opção inválida: escolha entre 1 e 3.");
            } catch (NumberFormatException e) {
                IO.println("Opção inválida: escolha entre 1 e 3.");
            }
        }
    }

    private int lerOpcaoMenuCadastro() {
        while (true) {
            IO.println("\n=== CADASTRO DE PETS ===");
            IO.println("[1] Cadastrar  [2] Alterar   [3] Deletar");
            IO.println("[4] Listar     [5] Buscar    [6] Voltar");
            IO.println("-------------------------------");
            String entrada = IO.readln("Opção (1-6): ").trim();

            try {
                int opcao = Integer.parseInt(entrada);
                if (opcao >= 1 && opcao <= 6) return opcao;
                IO.println("Opção inválida: escolha entre 1 e 6.");
            } catch (NumberFormatException e) {
                IO.println("Opção inválida: escolha entre 1 e 6.");
            }
        }
    }

    private int lerOpcaoMenuFormulario() {
        while (true) {
            IO.println("\n=== GERENCIAR FORMULÁRIO ===");
            IO.println("[1] Nova pergunta    [2] Alterar pergunta");
            IO.println("[3] Deletar pergunta [4] Voltar");
            IO.println("-------------------------------");
            String entrada = IO.readln("Opção (1-4): ").trim();

            try {
                int opcao = Integer.parseInt(entrada);
                if (opcao >= 1 && opcao <= 4) return opcao;
                IO.println("Opção inválida: escolha entre 1 e 4.");
            } catch (NumberFormatException e) {
                IO.println("Opção inválida: escolha entre 1 e 4.");
            }
        }
    }

    private void cadastrarPet() {
        List<String> respostas = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(FORMULARIO))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                IO.println("\n" + linha);
                respostas.add(IO.readln("R: "));
            }
        } catch (IOException e) {
            IO.println("\nErro ao ler formulário: " + e.getMessage());
            return;
        }

        try {
            String nomeCompleto = respostas.get(0).trim();
            TipoPet tipoPet    = TipoPet.fromInput(respostas.get(1));
            Sexo sexo          = Sexo.fromInput(respostas.get(2));
            Endereco endereco  = Endereco.fromInput(respostas.get(3));
            double idade       = Double.parseDouble(respostas.get(4).replace(",", "."));
            double pesoKg      = Double.parseDouble(respostas.get(5).replace(",", "."));
            String raca        = respostas.get(6);

            List<String> respostasExtra = new ArrayList<>();
            for (int i = TOTAL_PERGUNTAS_ORIGINAIS; i < respostas.size(); i++) {
                String resposta = respostas.get(i).trim();
                respostasExtra.add(resposta.isEmpty() ? Endereco.NAO_INFORMADO : resposta);
            }

            Pet pet = Pet.createPet(tipoPet, sexo, nomeCompleto, endereco, idade, pesoKg, raca, respostasExtra);
            File arquivo = petRepository.salvarPet(pet);

            IO.println("\nPet cadastrado com sucesso!");
            IO.println("Arquivo: " + arquivo.getAbsolutePath());

        } catch (RuntimeException e) {
            IO.println("\nErro no cadastro: " + e.getMessage());
        } catch (IOException e) {
            IO.println("\nErro ao salvar arquivo: " + e.getMessage());
        }
    }

    private void alterarPet() {
        List<PetArquivo> resultado = buscarPetsComArquivoPorCriterios();
        if (resultado.isEmpty()) return;

        exibirListaPetsComArquivo(resultado);
        PetArquivo petArquivo = resultado.get(lerEscolhaDaListaPet(resultado.size()) - 1);
        Pet pet = petArquivo.getPet();

        IO.println("\n=== ALTERAR: " + pet.getNomeCompleto() + " ===");
        IO.println("Deixe em branco para manter o valor atual.\n");

        String novoNome = IO.readln("Nome completo [" + pet.getNomeCompleto() + "]: ").trim();
        if (!novoNome.isEmpty()) {
            if (!novoNome.matches("^[A-Za-zÀ-ú]+(?: [A-Za-zÀ-ú]+)*$") || novoNome.split("\\s+").length < 2) {
                IO.println("Nome inválido — mantendo o valor atual.");
            } else {
                pet.setNomeCompleto(novoNome);
            }
        }

        Endereco endAtual = pet.getEndereco();
        String endFormatado = endAtual.getRua() + ", " + endAtual.getNumero() + ", "
                + endAtual.getBairro() + ", " + endAtual.getCidade();
        String novoEndereco = IO.readln("Endereço [" + endFormatado + "]: ").trim();
        if (!novoEndereco.isEmpty()) {
            try {
                pet.setEndereco(Endereco.fromInput(novoEndereco));
            } catch (RuntimeException e) {
                IO.println("Endereço inválido — mantendo o valor atual.");
            }
        }

        String novaIdade = IO.readln("Idade em anos [" + pet.getIdadeAnos() + "]: ").trim();
        if (!novaIdade.isEmpty()) {
            try {
                double idade = Double.parseDouble(novaIdade.replace(",", "."));
                if (idade < 0 || idade > 20) IO.println("Idade inválida (0–20 anos) — mantendo o valor atual.");
                else pet.setIdadeAnos(idade);
            } catch (NumberFormatException e) {
                IO.println("Valor inválido — mantendo o valor atual.");
            }
        }

        String novoPeso = IO.readln("Peso em kg [" + pet.getPesoKg() + "]: ").trim();
        if (!novoPeso.isEmpty()) {
            try {
                double peso = Double.parseDouble(novoPeso.replace(",", "."));
                if (peso < 0.5 || peso > 60) IO.println("Peso inválido (0,5–60 kg) — mantendo o valor atual.");
                else pet.setPesoKg(peso);
            } catch (NumberFormatException e) {
                IO.println("Valor inválido — mantendo o valor atual.");
            }
        }

        String novaRaca = IO.readln("Raça [" + pet.getRaca() + "]: ").trim();
        if (!novaRaca.isEmpty()) {
            if (!novaRaca.matches("^[A-Za-zÀ-ú]+(?: [A-Za-zÀ-ú]+)*$")) {
                IO.println("Raça inválida — mantendo o valor atual.");
            } else {
                pet.setRaca(novaRaca);
            }
        }

        try {
            File novoArquivo = petRepository.atualizarPet(petArquivo, pet);
            IO.println("\nPet atualizado com sucesso!");
            IO.println("Arquivo: " + novoArquivo.getAbsolutePath());
        } catch (IOException e) {
            IO.println("\nErro ao salvar alterações: " + e.getMessage());
        }
    }

    private void deletarPet() {
        List<PetArquivo> resultado = buscarPetsComArquivoPorCriterios();
        if (resultado.isEmpty()) return;

        exibirListaPetsComArquivo(resultado);
        PetArquivo petArquivo = resultado.get(lerEscolhaDaListaPet(resultado.size()) - 1);

        IO.println("\nPet selecionado: \"" + petArquivo.getPet().getNomeCompleto() + "\"");
        String confirmacao = IO.readln("Confirmar exclusão? (SIM para confirmar): ").trim();

        if (confirmacao.equalsIgnoreCase("SIM")) {
            petRepository.deletarPet(petArquivo.getArquivo());
            IO.println("\nPet deletado com sucesso!");
        } else {
            IO.println("\nOperação cancelada.");
        }
    }

    private void listarPets() {
        List<Pet> pets = carregarPetsOuVazio();
        if (!pets.isEmpty()) exibirListaPets(pets);
    }

    private void buscarPet() {
        List<Pet> resultado = buscarPetsPorCriterios();
        if (!resultado.isEmpty()) exibirListaPets(resultado);
    }

    private void criarPergunta() {
        int totalLinhas = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(FORMULARIO))) {
            while (br.readLine() != null) totalLinhas++;
        } catch (IOException e) {
            IO.println("\nErro ao ler formulário: " + e.getMessage());
            return;
        }

        int proximoNumero = totalLinhas + 1;
        String pergunta = IO.readln("Nova pergunta: ");

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FORMULARIO, true))) {
            bw.newLine();
            bw.write(proximoNumero + " - " + pergunta);
            IO.println("\nPergunta " + proximoNumero + " adicionada com sucesso!");
        } catch (IOException e) {
            IO.println("\nErro ao salvar pergunta: " + e.getMessage());
        }
    }

    private void alterarPergunta() {
        List<String> originais   = new ArrayList<>();
        List<String> adicionadas = new ArrayList<>();
        if (!lerPerguntasFormulario(originais, adicionadas)) return;

        if (adicionadas.isEmpty()) {
            IO.println("\nNenhuma pergunta extra cadastrada ainda.");
            return;
        }

        exibirListaPerguntas(adicionadas);
        int escolha = lerEscolhaDaListaPerguntas(adicionadas.size());

        IO.println("\nPergunta atual: \"" + adicionadas.get(escolha - 1).split(" - ")[1] + "\"");
        String novaPergunta = IO.readln("Nova pergunta: ").trim();

        adicionadas.set(escolha - 1, novaPergunta);
        reescreverFormulario(originais, adicionadas);
        IO.println("\nPergunta alterada com sucesso!");
    }

    private void deletarPergunta() {
        List<String> originais   = new ArrayList<>();
        List<String> adicionadas = new ArrayList<>();
        if (!lerPerguntasFormulario(originais, adicionadas)) return;

        if (adicionadas.isEmpty()) {
            IO.println("\nNenhuma pergunta extra cadastrada ainda.");
            return;
        }

        exibirListaPerguntas(adicionadas);
        int escolha = lerEscolhaDaListaPerguntas(adicionadas.size());

        IO.println("\nPergunta selecionada: \"" + adicionadas.get(escolha - 1) + "\"");
        String confirmacao = IO.readln("Confirmar exclusão? (SIM para confirmar): ").trim();

        if (confirmacao.equalsIgnoreCase("SIM")) {
            adicionadas.remove(escolha - 1);
            reescreverFormulario(originais, adicionadas);
            IO.println("\nPergunta deletada com sucesso!");
        } else {
            IO.println("\nOperação cancelada.");
        }
    }

    private boolean lerPerguntasFormulario(List<String> originais, List<String> adicionadas) {
        int cont = 1;
        try (BufferedReader br = new BufferedReader(new FileReader(FORMULARIO))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                if (cont <= TOTAL_PERGUNTAS_ORIGINAIS) originais.add(linha);
                else adicionadas.add(linha);
                cont++;
            }
            return true;
        } catch (IOException e) {
            IO.println("\nErro ao ler formulário: " + e.getMessage());
            return false;
        }
    }

    private void reescreverFormulario(List<String> originais, List<String> adicionadas) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FORMULARIO))) {
            for (String linha : originais) {
                bw.write(linha);
                bw.newLine();
            }
            for (int i = 0; i < adicionadas.size(); i++) {
                int numero = TOTAL_PERGUNTAS_ORIGINAIS + i + 1;
                bw.write(numero + " - " + adicionadas.get(i));
                if (i < adicionadas.size() - 1) bw.newLine();
            }
        } catch (IOException e) {
            IO.println("\nErro ao salvar formulário: " + e.getMessage());
        }
    }

    private List<Pet> buscarPetsPorCriterios() {
        List<Pet> pets = carregarPetsOuVazio();
        if (pets.isEmpty()) return List.of();

        List<Pet> filtradosPorTipo = filtrarPorTipo(pets, lerTipoPetBusca());
        if (filtradosPorTipo.isEmpty()) {
            IO.println("\nNenhum pet encontrado para esse tipo.");
            return List.of();
        }

        List<Pet> resultado = filtradosPorTipo.stream().filter(montarFiltro()).toList();
        if (resultado.isEmpty()) IO.println("\nNenhum pet encontrado com os critérios informados.");
        return resultado;
    }

    private List<PetArquivo> buscarPetsComArquivoPorCriterios() {
        List<PetArquivo> petsComArquivo = carregarPetsComArquivoOuVazio();
        if (petsComArquivo.isEmpty()) return List.of();

        TipoPet tipoBuscado = lerTipoPetBusca();
        List<PetArquivo> filtradosPorTipo = petsComArquivo.stream()
                .filter(item -> item.getPet().getTipoPet() == tipoBuscado)
                .toList();

        if (filtradosPorTipo.isEmpty()) {
            IO.println("\nNenhum pet encontrado para esse tipo.");
            return List.of();
        }

        Predicate<Pet> filtro = montarFiltro();
        List<PetArquivo> resultado = filtradosPorTipo.stream()
                .filter(item -> filtro.test(item.getPet()))
                .toList();

        if (resultado.isEmpty()) IO.println("\nNenhum pet encontrado com os critérios informados.");
        return resultado;
    }

    private Predicate<Pet> montarFiltro() {
        Predicate<Pet> filtro = lerFiltro(lerCriterioBusca1());
        int criterio2 = lerCriterioBusca2();
        if (criterio2 != 7) filtro = filtro.and(lerFiltro(criterio2));
        return filtro;
    }

    private List<Pet> carregarPetsOuVazio() {
        try {
            List<Pet> pets = petRepository.carregarPets();
            if (pets.isEmpty()) IO.println("\nNenhum pet cadastrado ainda.");
            return pets.isEmpty() ? List.of() : pets;
        } catch (Exception e) {
            IO.println("\nErro ao carregar pets: " + e.getMessage());
            return List.of();
        }
    }

    private List<PetArquivo> carregarPetsComArquivoOuVazio() {
        try {
            List<PetArquivo> pets = petRepository.carregarPetsComArquivo();
            if (pets.isEmpty()) IO.println("\nNenhum pet cadastrado ainda.");
            return pets.isEmpty() ? List.of() : pets;
        } catch (Exception e) {
            IO.println("\nErro ao carregar pets: " + e.getMessage());
            return List.of();
        }
    }

    private void exibirListaPets(List<Pet> pets) {
        for (int i = 0; i < pets.size(); i++)
            IO.println(formatarLinhaResultadoPet(i + 1, pets.get(i)));
    }

    private void exibirListaPetsComArquivo(List<PetArquivo> petsComArquivo) {
        for (int i = 0; i < petsComArquivo.size(); i++)
            IO.println(formatarLinhaResultadoPet(i + 1, petsComArquivo.get(i).getPet()));
    }

    private void exibirListaPerguntas(List<String> perguntas) {
        for (int i = 0; i < perguntas.size(); i++)
            IO.println(formatarLinhaResultadoPergunta(i + 1, perguntas.get(i)));
    }

    private TipoPet lerTipoPetBusca() {
        while (true) {
            IO.println("\n=== TIPO DO ANIMAL ===");
            IO.println("[1] Cachorro  [2] Gato");
            IO.println("-------------------------------");
            try {
                int opcao = Integer.parseInt(IO.readln("Tipo: ").trim());
                if (opcao == 1) return TipoPet.CACHORRO;
                if (opcao == 2) return TipoPet.GATO;
                IO.println("Opção inválida: escolha 1 ou 2.");
            } catch (NumberFormatException e) {
                IO.println("Opção inválida: escolha 1 ou 2.");
            }
        }
    }

    private int lerCriterioBusca1() {
        while (true) {
            IO.println("\n=== CRITÉRIO DE BUSCA ===");
            IO.println("[1] Nome/Sobrenome  [2] Sexo   [3] Idade");
            IO.println("[4] Peso            [5] Raça   [6] Endereço");
            IO.println("-------------------------------");
            try {
                int entrada = Integer.parseInt(IO.readln("Critério 1: ").trim());
                if (entrada >= 1 && entrada <= 6) return entrada;
                IO.println("Opção inválida: escolha entre 1 e 6.");
            } catch (NumberFormatException e) {
                IO.println("Opção inválida: escolha entre 1 e 6.");
            }
        }
    }

    private int lerCriterioBusca2() {
        while (true) {
            IO.println("\n=== SEGUNDO CRITÉRIO (OPCIONAL) ===");
            IO.println("[1] Nome/Sobrenome  [2] Sexo   [3] Idade");
            IO.println("[4] Peso            [5] Raça   [6] Endereço  [7] Sem segundo critério");
            IO.println("-------------------------------");
            try {
                int entrada = Integer.parseInt(IO.readln("Critério 2: ").trim());
                if (entrada >= 1 && entrada <= 7) return entrada;
                IO.println("Opção inválida: escolha entre 1 e 7.");
            } catch (NumberFormatException e) {
                IO.println("Opção inválida: escolha entre 1 e 7.");
            }
        }
    }

    private Predicate<Pet> lerFiltro(int criterio) {
        return switch (criterio) {
            case 1 -> { String v = normalizar(IO.readln("Nome ou sobrenome: "));
                yield pet -> normalizar(pet.getNomeCompleto()).contains(v); }
            case 2 -> { String v = normalizar(IO.readln("Sexo (Macho/Fêmea): "));
                yield pet -> normalizar(pet.getSexo().toString()).contains(v); }
            case 3 -> { double idade = lerDouble("Idade (anos): ");
                yield pet -> Double.compare(pet.getIdadeAnos(), idade) == 0; }
            case 4 -> { double peso = lerDouble("Peso (kg): ");
                yield pet -> Double.compare(pet.getPesoKg(), peso) == 0; }
            case 5 -> { String v = normalizar(IO.readln("Raça: "));
                yield pet -> normalizar(pet.getRaca()).contains(v); }
            case 6 -> { String v = normalizar(IO.readln("Endereço: "));
                yield pet -> normalizar(pet.getEndereco().toSearchString()).contains(v); }
            default -> pet -> true;
        };
    }

    private List<Pet> filtrarPorTipo(List<Pet> pets, TipoPet tipo) {
        return pets.stream().filter(pet -> pet.getTipoPet() == tipo).toList();
    }

    private int lerEscolhaDaListaPet(int total) {
        while (true) {
            try {
                int escolha = Integer.parseInt(IO.readln("\nNúmero do pet: ").trim());
                if (escolha >= 1 && escolha <= total) return escolha;
                IO.println("Inválido: escolha entre 1 e " + total + ".");
            } catch (NumberFormatException e) {
                IO.println("Inválido: digite apenas números.");
            }
        }
    }

    private int lerEscolhaDaListaPerguntas(int total) {
        while (true) {
            try {
                int escolha = Integer.parseInt(IO.readln("\nNúmero da pergunta: ").trim());
                if (escolha >= 1 && escolha <= total) return escolha;
                IO.println("Inválido: escolha entre 1 e " + total + ".");
            } catch (NumberFormatException e) {
                IO.println("Inválido: digite apenas números.");
            }
        }
    }

    private double lerDouble(String prompt) {
        while (true) {
            try {
                return Double.parseDouble(IO.readln(prompt).trim().replace(",", "."));
            } catch (NumberFormatException e) {
                IO.println("Valor inválido: digite um número.");
            }
        }
    }

    private static String formatarLinhaResultadoPet(int id, Pet pet) {
        String enderecoResumo = String.format("%s, %s - %s - %s",
                pet.getEndereco().getRua(),
                pet.getEndereco().getNumero(),
                pet.getEndereco().getBairro(),
                pet.getEndereco().getCidade());

        return String.format("\n%d. %s - %s - %s - %s - %.1f anos - %.1fkg - %s",
                id,
                pet.getNomeCompleto(),
                pet.getTipoPet(),
                pet.getSexo(),
                enderecoResumo,
                pet.getIdadeAnos(),
                pet.getPesoKg(),
                pet.getRaca());
    }

    private static String formatarLinhaResultadoPergunta(int cont, String pergunta) {
        return String.format("\n%d. %s", cont, pergunta);
    }

    private static String normalizar(String texto) {
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}", "")
                .toLowerCase();
    }
}