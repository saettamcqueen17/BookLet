package Entities;

import java.util.HashMap;

import java.util.Map;


/**
 * Genere viene implementato come  enum gerarchico
 * in cui abbiamo 7 macroGeneri che si dividono in sottoGeneri
 */

public enum Genere {

    FANTASTICO(null),
    HORROR(null),
    STORICO(null),
    FANTASCIENZA(null),
    FANTASY(null),
    ROMANZO(null),
    SAGGISTICA(null),


    FANTASTICO_EPICO(FANTASTICO),
    FANTASTICO_MODERNO(FANTASTICO),
    FANTASTICO_URBANO(FANTASTICO),


    HORROR_GOTICO(HORROR),
    HORROR_PSICOLOGICO(HORROR),
    HORROR_SUPERNATURALE(HORROR),


    STORICO_ANTICO(STORICO),
    STORICO_MEDIOEVALE(STORICO),
    STORICO_MODERNO(STORICO),


    FANTASCIENZA_DISTOPIA(FANTASCIENZA),
    FANTASCIENZA_SPACE_OPERA(FANTASCIENZA),
    FANTASCIENZA_CYBERPUNK(FANTASCIENZA),


    FANTASY_EPICO(FANTASY),
    FANTASY_URBANO(FANTASY),
    FANTASY_HEROIC(FANTASY),

    ROMANZO_ROMANTICO(ROMANZO),
    ROMANZO_POLITICO(ROMANZO),
    ROMANZO_WESTERN(ROMANZO),


    SAGGISTICA_STORICA(SAGGISTICA),
    SAGGISTICA_SCIENZA(SAGGISTICA),
    SAGGISTICA_FILOSOFIA(SAGGISTICA);

    private final Genere parent;

    Genere(Genere parent) {
        this.parent = parent;
    }


    public Genere getParent() {
        return parent;
    }


    private static final Map<String, Genere> lookup = new HashMap<>();

    static {
        for (Genere g : Genere.values()) {
            String key = normalizeToKey(g.name());
            lookup.put(key, g);
        }
    }


    private static String normalizeToKey(String raw) {
        String s = raw.trim().toLowerCase().replaceAll("\\s+", "_");
        return s.toUpperCase();
    }





    @Override
    public String toString() {
        String raw = this.name().toLowerCase();
        String[] parts = raw.split("_");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            String p = parts[i];
            if (p.isEmpty()) continue;
            sb.append(Character.toUpperCase(p.charAt(0)))
                    .append(p.substring(1));
            if (i < parts.length - 1) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }
}
