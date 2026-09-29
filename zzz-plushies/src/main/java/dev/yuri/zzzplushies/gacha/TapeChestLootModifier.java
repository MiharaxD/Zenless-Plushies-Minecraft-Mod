package dev.yuri.zzzplushies.gacha;

import com.google.common.collect.ObjectArrays;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.yuri.zzzplushies.ZzzPlushies;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.LootModifier;

public final class TapeChestLootModifier extends LootModifier {
    public static final Codec<TapeChestLootModifier> CODEC = RecordCodecBuilder.create(instance ->
            codecStart(instance).and(Codec.FLOAT.fieldOf("chance").forGetter(modifier -> modifier.chance))
                    .apply(instance, TapeChestLootModifier::new));

    private final float chance;

    private TapeChestLootModifier(LootItemCondition[] conditions, float chance) {
        super(conditions);
        this.chance = chance;
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> loot, LootContext context) {
        if (context.getQueriedLootTableId().getPath().startsWith("chests/")
                && context.getRandom().nextFloat() < chance) {
            loot.add(new ItemStack(ZzzPlushies.MASTER_TAPE.get(), 1 + context.getRandom().nextInt(3)));
        }
        return loot;
    }

    @Override
    public Codec<? extends net.minecraftforge.common.loot.IGlobalLootModifier> codec() {
        return CODEC;
    }
}
