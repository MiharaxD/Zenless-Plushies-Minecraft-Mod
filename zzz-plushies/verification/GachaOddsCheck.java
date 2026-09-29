import dev.yuri.zzzplushies.gacha.GachaGrade;
import dev.yuri.zzzplushies.gacha.GachaOdds;
import dev.yuri.zzzplushies.gacha.GachaTiming;

public final class GachaOddsCheck {
    private static void require(boolean ok, String why) {
        if (!ok) throw new AssertionError(why);
    }
    public static void main(String[] args) {
        require(GachaOdds.sChance(0) == .02, "First pull base chance");
        require(GachaOdds.sChance(58) == .02, "59th pull still base chance");
        require(Math.abs(GachaOdds.sChance(59) - 1.0 / 15) < 1e-9, "60th pull starts soft pity");
        require(GachaOdds.sChance(78) < 1, "79th pull is not guaranteed");
        require(GachaOdds.sChance(79) == 1, "80th pull is guaranteed");
        for (int pity = 59; pity < 80; pity++) {
            require(GachaOdds.sChance(pity) > GachaOdds.sChance(pity - 1), "Soft pity increases every pull");
        }
        require(GachaOdds.grade(0, .019999) == GachaGrade.S, "2% S interval");
        require(GachaOdds.grade(0, .02) == GachaGrade.A, "S boundary");
        require(GachaOdds.grade(0, .31999) == GachaGrade.A, "30% A interval");
        require(GachaOdds.grade(0, .32) == GachaGrade.B, "A boundary");
        require(GachaOdds.grade(79, Math.nextDown(1.0)) == GachaGrade.S, "Hard pity ignores bad luck");
        require(GachaOdds.sChance(-10) == .02, "Invalid negative save value is safe");
        require(GachaTiming.ROLL_TICKS == 8 * 20, "Gacha result locks at eight seconds");
        System.out.println("PASS: base odds, soft pity boundaries, monotonicity, hard pity");
    }
}
