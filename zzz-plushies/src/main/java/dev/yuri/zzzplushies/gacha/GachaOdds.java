package dev.yuri.zzzplushies.gacha;

/** Pity counts completed pulls since the last S. */
public final class GachaOdds {
    public static final int HARD_PITY = 80;
    public static final int SOFT_PITY = 60;
    private GachaOdds() {}

    public static double sChance(int completedPulls) {
        int next = Math.max(0, completedPulls) + 1;
        if (next >= HARD_PITY) return 1.0;
        if (next < SOFT_PITY) return 0.02;
        return 0.02 + 0.98 * (next - SOFT_PITY + 1) / (HARD_PITY - SOFT_PITY + 1);
    }

    public static GachaGrade grade(int completedPulls, double roll) {
        double s = sChance(completedPulls);
        return roll < s ? GachaGrade.S
                : roll < s + (1 - s) * (30.0 / 98.0) ? GachaGrade.A : GachaGrade.B;
    }
}
