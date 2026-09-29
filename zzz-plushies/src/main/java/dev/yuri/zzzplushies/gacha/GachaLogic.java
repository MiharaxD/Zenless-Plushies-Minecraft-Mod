package dev.yuri.zzzplushies.gacha;

import dev.yuri.zzzplushies.PlushCatalog;
import dev.yuri.zzzplushies.ZzzPlushies;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = ZzzPlushies.MOD_ID)
public final class GachaLogic {
    private static final String KEY = "zzzplushies_gacha";
    private static final Item[] B_REWARDS = {Items.COAL, Items.RAW_COPPER, Items.COPPER_INGOT,
            Items.RAW_IRON, Items.IRON_INGOT, Items.REDSTONE, Items.LAPIS_LAZULI};
    private static final Item[] A_TOOLS = {Items.DIAMOND_PICKAXE, Items.DIAMOND_AXE,
            Items.DIAMOND_SWORD, Items.DIAMOND_SHOVEL, Items.BOW, Items.CROSSBOW};

    private GachaLogic() {}

    public static CompoundTag data(Player player) {
        CompoundTag persisted = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
        CompoundTag gacha = persisted.getCompound(KEY);
        persisted.put(KEY, gacha);
        player.getPersistentData().put(Player.PERSISTED_NBT_TAG, persisted);
        return gacha;
    }

    public static void select(Player player, int delta) {
        CompoundTag state = data(player);
        int length = PlushCatalog.IDS.length;
        state.putInt("selected", Math.floorMod(state.getInt("selected") + delta, length));
    }

    public static int tapeCount(Player player) {
        int count = 0;
        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(ZzzPlushies.MASTER_TAPE.get())) count += stack.getCount();
        }
        return count;
    }

    public static void spin(ServerPlayer player, int count) {
        if (count != 1 && count != 10) return;
        CompoundTag state = data(player);
        long now = player.level().getGameTime();
        long lastSpin = state.getLong("lastSpinGameTime");
        if (state.contains("lastSpinGameTime") && now >= lastSpin && now - lastSpin < GachaTiming.ROLL_TICKS) return;
        if (tapeCount(player) < count) {
            player.displayClientMessage(Component.translatable("message.zzzplushies.need_tapes", count), true);
            return;
        }
        int remaining = count;
        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(ZzzPlushies.MASTER_TAPE.get())) {
                int used = Math.min(remaining, stack.getCount());
                stack.shrink(used);
                remaining -= used;
                if (remaining == 0) break;
            }
        }

        state.putLong("lastSpinGameTime", now);
        var results = new java.util.ArrayList<GachaResult>(count);
        for (int i = 0; i < count; i++) results.add(rollOne(player, state));
        GachaNetwork.playFor(player, results);
        player.containerMenu.broadcastChanges();
    }

    private static GachaResult rollOne(ServerPlayer player, CompoundTag state) {
        int pity = Math.max(0, Math.min(GachaOdds.HARD_PITY - 1, state.getInt("pity")));
        GachaGrade grade = GachaOdds.grade(pity, player.getRandom().nextDouble());
        int awardedIndex = -1;
        boolean won = false;
        boolean voucher = false;
        ItemStack reward;
        if (grade == GachaGrade.S) {
            int chosen = Math.floorMod(state.getInt("selected"), PlushCatalog.IDS.length);
            won = player.getRandom().nextBoolean();
            int index = won ? chosen : Math.floorMod(chosen + 1 + player.getRandom().nextInt(PlushCatalog.IDS.length - 1), PlushCatalog.IDS.length);
            awardedIndex = index;
            reward = new ItemStack(ForgeRegistries.ITEMS.getValue(
                    new net.minecraft.resources.ResourceLocation(ZzzPlushies.MOD_ID, PlushCatalog.IDS[index])));
            state.putInt("pity", 0);
            if (!won) {
                int losses = state.getInt("losses") + 1;
                if (losses >= 2) {
                    give(player, new ItemStack(ZzzPlushies.CHOICE_TOKEN.get()));
                    voucher = true;
                    losses = 0;
                }
                state.putInt("losses", losses);
            }
        } else {
            state.putInt("pity", pity + 1);
            if (grade == GachaGrade.A && player.getRandom().nextInt(4) == 0) {
                awardedIndex = ARankPlushPool.randomIndex(player.getRandom());
                reward = new ItemStack(ForgeRegistries.ITEMS.getValue(
                        new net.minecraft.resources.ResourceLocation(ZzzPlushies.MOD_ID,
                                PlushCatalog.IDS[awardedIndex])));
            } else {
                reward = grade == GachaGrade.A ? rankAReward(player) : rankBReward(player);
            }
        }
        ItemStack displayedReward = reward.copy();
        give(player, reward);
        return new GachaResult(grade, awardedIndex, displayedReward, won, voucher);
    }

    private static ItemStack rankBReward(ServerPlayer player) {
        Item item = B_REWARDS[player.getRandom().nextInt(B_REWARDS.length)];
        return new ItemStack(item, 2 + player.getRandom().nextInt(7));
    }

    private static ItemStack rankAReward(ServerPlayer player) {
        int roll = player.getRandom().nextInt(1000);
        if (roll < 330) return new ItemStack(Items.GOLD_INGOT, 3 + player.getRandom().nextInt(6));
        if (roll < 580) return new ItemStack(Items.DIAMOND, 1 + player.getRandom().nextInt(3));
        if (roll < 740) return new ItemStack(Items.EMERALD, 2 + player.getRandom().nextInt(4));
        if (roll < 850) return EnchantmentHelper.enchantItem(player.getRandom(),
                new ItemStack(Items.BOOK), 18 + player.getRandom().nextInt(15), true);
        if (roll < 940) return EnchantmentHelper.enchantItem(player.getRandom(),
                new ItemStack(A_TOOLS[player.getRandom().nextInt(A_TOOLS.length)]), 18, false);
        if (roll < 996) return new ItemStack(Items.NETHERITE_SCRAP);
        return new ItemStack(Items.NETHERITE_INGOT);
    }

    private static void give(ServerPlayer player, ItemStack stack) {
        if (!player.getInventory().add(stack)) player.drop(stack, false);
    }

    @SubscribeEvent
    public static void clonePlayer(PlayerEvent.Clone event) {
        event.getOriginal().reviveCaps();
        CompoundTag oldData = event.getOriginal().getPersistentData();
        if (oldData.contains(Player.PERSISTED_NBT_TAG)) {
            event.getEntity().getPersistentData().put(Player.PERSISTED_NBT_TAG,
                    oldData.getCompound(Player.PERSISTED_NBT_TAG).copy());
        }
        event.getOriginal().invalidateCaps();
    }
}
