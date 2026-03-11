package br.com.guisleri.petsistema.domain;

public enum TipoPet {
    CACHORRO, GATO;

    public static TipoPet fromInput(String input) {
        if (input == null) throw new RuntimeException("Tipo de pet é obrigatório.");
        String v = input.trim().toUpperCase();

        return switch (v) {
            case "CACHORRO" -> CACHORRO;
            case "GATO" -> GATO;
            default -> throw new RuntimeException("Tipo inválido - Digite Cachorro ou Gato.");
        };
    }
}
