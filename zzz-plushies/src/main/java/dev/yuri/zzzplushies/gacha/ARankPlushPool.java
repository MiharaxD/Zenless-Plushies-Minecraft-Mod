package dev.yuri.zzzplushies.gacha;

import dev.yuri.zzzplushies.PlushCatalog;
import net.minecraft.util.RandomSource;
import java.util.Arrays;

/** Agent ranks checked against the game's A-rank roster on 2026-09-29. */
public final class ARankPlushPool {
    public static final String[] IDS = {
            "anby_demara", "anton_ivanov", "ben_bigger", "billy_kid",
            "corin_wickes", "komano_manato", "lucy", "nicole_demara",
            "pan_yinhu", "piper_wheel", "pulchra_fellini", "seth_lowell", "soukaku"
    };
    private static final int[] CATALOG_INDICES = Arrays.stream(IDS).mapToInt(id -> {
        for (int i = 0; i < PlushCatalog.IDS.length; i++) {
            if (PlushCatalog.IDS[i].equals(id)) return i;
        }
        throw new IllegalStateException("Missing A-rank plush: " + id);
    }).toArray();

    private ARankPlushPool() {}

    public static int randomIndex(RandomSource random) {
        return CATALOG_INDICES[random.nextInt(CATALOG_INDICES.length)];
    }
}
