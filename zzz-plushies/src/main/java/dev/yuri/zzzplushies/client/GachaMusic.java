package dev.yuri.zzzplushies.client;

import dev.yuri.zzzplushies.PlushCatalog;
import dev.yuri.zzzplushies.ZzzPlushies;
import dev.yuri.zzzplushies.gacha.GachaGrade;
import dev.yuri.zzzplushies.gacha.GachaResult;
import dev.yuri.zzzplushies.gacha.GachaTiming;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = ZzzPlushies.MOD_ID, value = Dist.CLIENT)
public final class GachaMusic {
    public enum Phase { IDLE, ROLLING, WAITING, FOCUS, RESULTS }
    private static final GachaGrade[] CHANNELS = {GachaGrade.B, GachaGrade.A, GachaGrade.B,
            GachaGrade.A, GachaGrade.S, GachaGrade.B, GachaGrade.A};
    private static SoundInstance music, voice;
    private static List<GachaResult> results = List.of();
    private static List<Integer> focusOrder = List.of();
    private static Phase phase = Phase.IDLE;
    private static int ticks, phaseTicks, lastChannel, focusIndex, focusCursor;
    private static boolean voicePlayed;
    private GachaMusic() {}

    public static void play(List<GachaResult> batch) {
        reset();
        // A response arriving after the player closed the machine must not start orphan audio.
        if (!(Minecraft.getInstance().screen instanceof GachaScreen)) return;
        results = List.copyOf(batch);
        var characters = new ArrayList<Integer>();
        for (int i = 0; i < results.size(); i++) {
            if (results.get(i).grade() == GachaGrade.S) characters.add(i);
        }
        for (int i = 0; i < results.size(); i++) {
            GachaResult result = results.get(i);
            if (result.grade() == GachaGrade.A && result.plushIndex() >= 0) characters.add(i);
        }
        focusOrder = List.copyOf(characters);
        phase = Phase.ROLLING;
        lastChannel = -1;
        focusIndex = -1;
        focusCursor = -1;
        startMusic(hasS() ? ZzzPlushies.GACHA_SPIN_SOUND.get() : ZzzPlushies.GACHA_AB_SOUND.get());
    }

    private static void startMusic(SoundEvent event) {
        stopMusic();
        music = new LoopSound(event);
        Minecraft.getInstance().getSoundManager().play(music);
    }
    private static void stopMusic() {
        if (music != null) Minecraft.getInstance().getSoundManager().stop(music);
        music = null;
    }
    public static void reset() {
        stopMusic();
        if (voice != null) Minecraft.getInstance().getSoundManager().stop(voice);
        voice = null;
        phase = Phase.IDLE;
        results = List.of();
        focusOrder = List.of();
        ticks = phaseTicks = 0;
        focusIndex = focusCursor = -1;
    }
    public static boolean isRevealing() { return phase != Phase.IDLE; }
    public static Phase phase() { return phase; }
    public static int phaseTicks() { return phaseTicks; }
    public static int ticks() { return ticks; }
    public static List<GachaResult> results() { return results; }
    public static int focusIndex() { return focusIndex; }
    public static boolean hasS() { return results.stream().anyMatch(r -> r.grade() == GachaGrade.S); }
    public static GachaGrade visibleChannel(int index) {
        if (phase != Phase.ROLLING) return results.get(index).grade();
        if (ticks < 18) return null;
        return CHANNELS[Math.floorMod(channelIndex(ticks) + index * 3, CHANNELS.length)];
    }
    private static int channelIndex(int time) { return time < 118 ? (time - 18) / 8 : 13 + (time - 118) / 4; }
    private static void transition(Phase next) {
        phase = next;
        phaseTicks = 0;
        voicePlayed = false;
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(
                ZzzPlushies.REVEAL_SOUND.get(), next == Phase.FOCUS ? 0.85F : 1.0F, 0.7F));
    }
    public static void confirm() {
        if (phase == Phase.ROLLING || phase == Phase.IDLE) return;
        if (phase == Phase.FOCUS && phaseTicks < 16 || phase == Phase.RESULTS && phaseTicks < 30) return;
        if (phase == Phase.RESULTS) { reset(); return; }
        if (focusCursor + 1 < focusOrder.size()) {
            focusIndex = focusOrder.get(++focusCursor);
            if (voice != null) Minecraft.getInstance().getSoundManager().stop(voice);
            transition(Phase.FOCUS);
            return;
        }
        stopMusic();
        if (voice != null) Minecraft.getInstance().getSoundManager().stop(voice);
        transition(Phase.RESULTS);
        var player = Minecraft.getInstance().player;
        if (player != null) for (var r : results) {
            player.displayClientMessage(Component.translatable(r.grade() == GachaGrade.S
                    ? (r.won() ? "message.zzzplushies.won" : "message.zzzplushies.lost")
                    : "message.zzzplushies.reward", r.reward().getHoverName()), false);
            if (r.voucher()) player.displayClientMessage(Component.translatable("message.zzzplushies.token"), false);
        }
    }
    private static void playVoice(GachaResult result) {
        int index = result.plushIndex();
        if (index < 0 || index >= PlushCatalog.IDS.length) return;
        var sound = ZzzPlushies.AGENT_VOICES.get(PlushCatalog.IDS[index]);
        if (sound != null) {
            voice = SimpleSoundInstance.forUI(sound.get(), 1.0F);
            Minecraft.getInstance().getSoundManager().play(voice);
        }
    }
    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || phase == Phase.IDLE) return;
        var minecraft = Minecraft.getInstance();
        if (minecraft.player == null || !(minecraft.screen instanceof GachaScreen)) { reset(); return; }
        if (minecraft.isPaused()) return;
        ticks++;
        phaseTicks++;
        if (phase == Phase.ROLLING) {
            if (ticks >= GachaTiming.ROLL_TICKS) { phase = Phase.WAITING; phaseTicks = 0; return; }
            if (ticks >= 18 && channelIndex(ticks) != lastChannel) {
                lastChannel = channelIndex(ticks);
                minecraft.getSoundManager().play(SimpleSoundInstance.forUI(
                        ZzzPlushies.CHANNEL_STATIC_SOUND.get(), 0.93F + (lastChannel % 4) * 0.05F, 0.36F));
            }
        }
        if (!voicePlayed && phase == Phase.FOCUS && phaseTicks >= 12) {
            playVoice(results.get(focusIndex));
            voicePlayed = true;
        }
    }
    private static final class LoopSound extends AbstractTickableSoundInstance {
        LoopSound(SoundEvent event) {
            super(event, SoundSource.MASTER, SoundInstance.createUnseededRandom());
            looping = true;
            delay = 0;
            relative = true;
            attenuation = Attenuation.NONE;
            volume = 0.55F;
            pitch = 1.0F;
        }
        @Override public void tick() {}
    }
}
