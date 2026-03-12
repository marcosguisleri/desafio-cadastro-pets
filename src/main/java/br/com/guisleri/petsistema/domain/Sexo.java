package br.com.guisleri.petsistema.domain;

public enum Sexo {
    MACHO, FEMEA;

    public static Sexo fromInput(String input) {
        if (input == null) throw new RuntimeException("Sexo é obrigatório.");
        String sexoString = input.trim().toUpperCase();

        return switch (sexoString) {
            case "MACHO" -> MACHO;
            case "FEMEA" -> FEMEA;
            default -> throw new RuntimeException("Sexo inválido - Digite Macho ou Femea.");
        };
    }
}
