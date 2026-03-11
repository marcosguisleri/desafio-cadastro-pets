package br.com.guisleri.petsistema.domain;

public enum Sexo {
    MASCULINO, FEMININO;

    public static Sexo fromInput(String input) {
        if (input == null) throw new RuntimeException("Sexo é obrigatório.");
        String sexoString = input.trim().toUpperCase();

        return switch (sexoString) {
            case "MASCULINO" -> MASCULINO;
            case "FEMININO" -> FEMININO;
            default -> throw new RuntimeException("Sexo inválido - Digite Masculino ou Feminino.");
        };
    }
}
