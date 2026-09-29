package dev.yuri.zzzplushies.gacha;

public enum GachaGrade {
    B, A, S;

    public static GachaGrade fromNetwork(int value) {
        return values()[Math.max(0, Math.min(value, values().length - 1))];
    }
}
