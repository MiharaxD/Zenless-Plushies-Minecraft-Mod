package dev.yuri.zzzplushies.gacha;

import dev.yuri.zzzplushies.ZzzPlushies;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public final class GachaMenu extends AbstractContainerMenu {
    private final BlockPos pos;
    private final ContainerData data;

    public GachaMenu(int id, Inventory inventory, FriendlyByteBuf buffer) {
        this(id, inventory, buffer.readBlockPos(), new SimpleContainerData(4));
    }

    public GachaMenu(int id, Inventory inventory, BlockPos pos) {
        this(id, inventory, pos, new ContainerData() {
            @Override public int get(int index) {
                var state = GachaLogic.data(inventory.player);
                return switch (index) {
                    case 0 -> state.getInt("selected");
                    case 1 -> state.getInt("pity");
                    case 2 -> state.getInt("losses");
                    case 3 -> GachaLogic.tapeCount(inventory.player);
                    default -> 0;
                };
            }
            @Override public void set(int index, int value) {}
            @Override public int getCount() { return 4; }
        });
    }

    private GachaMenu(int id, Inventory inventory, BlockPos pos, ContainerData data) {
        super(ZzzPlushies.GACHA_MENU.get(), id);
        this.pos = pos;
        this.data = data;
        addDataSlots(data);
    }

    public int selected() { return data.get(0); }
    public int pity() { return data.get(1); }
    public int losses() { return data.get(2); }
    public int tapes() { return data.get(3); }

    @Override
    public boolean clickMenuButton(Player player, int buttonId) {
        if (!stillValid(player)) return false;
        if (!player.level().isClientSide) {
            if ((buttonId == 0 || buttonId == 3) && player instanceof ServerPlayer serverPlayer)
                GachaLogic.spin(serverPlayer, buttonId == 3 ? 10 : 1);
            else if (buttonId == 1) GachaLogic.select(player, -1);
            else if (buttonId == 2) GachaLogic.select(player, 1);
            else return false;
            broadcastChanges();
        }
        return true;
    }

    @Override
    public boolean stillValid(Player player) {
        BlockState state = player.level().getBlockState(pos);
        return state.is(ZzzPlushies.GACHA_MACHINE.get())
                && player.distanceToSqr(pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5) <= 64;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}
