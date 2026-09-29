package dev.yuri.zzzplushies.gacha;

import net.minecraft.world.item.ItemStack;

public record GachaResult(GachaGrade grade, int plushIndex, ItemStack reward,
                          boolean won, boolean voucher) {}
