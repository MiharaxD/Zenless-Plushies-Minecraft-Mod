package dev.yuri.zzzplushies.gacha;

import dev.yuri.zzzplushies.ZzzPlushies;
import dev.yuri.zzzplushies.client.GachaMusic;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

public final class GachaNetwork {
    private static final String VERSION = "2";
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(ZzzPlushies.MOD_ID, "gacha"), () -> VERSION,
            VERSION::equals, VERSION::equals);
    private GachaNetwork() {}

    public static void register() {
        CHANNEL.registerMessage(0, SpinResults.class, (message, buffer) -> {
            buffer.writeVarInt(message.results.size());
            for (GachaResult result : message.results) {
                buffer.writeVarInt(result.grade().ordinal());
                buffer.writeVarInt(result.plushIndex());
                buffer.writeItem(result.reward());
                buffer.writeBoolean(result.won());
                buffer.writeBoolean(result.voucher());
            }
        }, buffer -> {
            int count = buffer.readVarInt();
            if (count != 1 && count != 10) throw new IllegalArgumentException("Invalid pull count");
            var results = new ArrayList<GachaResult>(count);
            for (int i = 0; i < count; i++) results.add(new GachaResult(
                    GachaGrade.fromNetwork(buffer.readVarInt()), buffer.readVarInt(),
                    buffer.readItem(), buffer.readBoolean(), buffer.readBoolean()));
            return new SpinResults(List.copyOf(results));
        }, (message, contextSupplier) -> {
            var context = contextSupplier.get();
            context.enqueueWork(() -> GachaMusic.play(message.results));
            context.setPacketHandled(true);
        }, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    public static void playFor(ServerPlayer player, List<GachaResult> results) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new SpinResults(List.copyOf(results)));
    }
    private record SpinResults(List<GachaResult> results) {}
}
