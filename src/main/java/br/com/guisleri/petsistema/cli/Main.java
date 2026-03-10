package br.com.guisleri.petsistema.cli;

public class Main {

    void main() {

        while (true) {

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

            if  (opcao < 1 || opcao > 6) {
                IO.println("Opção inválida: escolha um número entre 1 e 6.\n");
                continue;
            }

            if (opcao == 6) {
                IO.println("Programa finalizado!");
                return;
            }

            IO.println("Opção " + opcao + " ainda em construção.\n");

        }

    }

}
