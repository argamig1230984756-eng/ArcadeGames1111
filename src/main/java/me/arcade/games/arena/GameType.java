package me.arcade.games.arena;

public enum GameType {
    PROP_HUNT("prophunt", "Prop Hunt", 2, 16),
    BLOCK_PARTY("blockparty", "BlockParty", 2, 16),
    TNT_RUN("tntrun", "TNTRun", 2, 24),
    PARTY_GAMES("partygames", "Party Games", 2, 16),
    MINI_WALLS("miniwalls", "Mini Walls", 2, 16);

    /** Short id used in commands and for the settings file name (games/&lt;key&gt;.yml). */
    public final String key;
    public final String display;
    public final int min, max;

    GameType(String key, String display, int min, int max) {
        this.key = key;
        this.display = display;
        this.min = min;
        this.max = max;
    }

    public static GameType parse(String s) {
        if (s == null) return null;
        String k = s.replace("_", "").replace("-", "").replace(" ", "").toLowerCase();
        for (GameType t : values()) {
            if (t.name().replace("_", "").toLowerCase().equals(k)) return t;
        }
        return null;
    }
}
