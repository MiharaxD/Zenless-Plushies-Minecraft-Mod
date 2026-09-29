package dev.yuri.zzzplushies;

import dev.yuri.zzzplushies.block.PlushBlock;
import dev.yuri.zzzplushies.block.PlushBlockEntity;
import dev.yuri.zzzplushies.block.PlushBlockItem;
import dev.yuri.zzzplushies.gacha.GachaMachineBlock;
import dev.yuri.zzzplushies.gacha.GachaMenu;
import dev.yuri.zzzplushies.gacha.TapeChestLootModifier;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import dev.yuri.zzzplushies.gacha.GachaNetwork;

import java.util.LinkedHashMap;
import java.util.Map;

@Mod(ZzzPlushies.MOD_ID)
public final class ZzzPlushies {
    public static final String MOD_ID = "zzzplushies";
    private static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    private static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MOD_ID);
    private static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);
    private static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, MOD_ID);
    private static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MOD_ID);
    private static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MOD_ID);

    public static final Map<String, RegistryObject<PlushBlock>> PLUSHES = new LinkedHashMap<>();
    public static final RegistryObject<Item> PLUSH_BASE = ITEMS.register("plush_base",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> MASTER_TAPE = ITEMS.register("master_tape",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CHOICE_TOKEN = ITEMS.register("choice_token",
            () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<GachaMachineBlock> GACHA_MACHINE = BLOCKS.register("gacha_machine",
            GachaMachineBlock::new);
    public static final RegistryObject<Item> GACHA_MACHINE_ITEM = ITEMS.register("gacha_machine",
            () -> new net.minecraft.world.item.BlockItem(GACHA_MACHINE.get(), new Item.Properties()));
    public static final RegistryObject<MenuType<GachaMenu>> GACHA_MENU = MENUS.register("gacha_machine",
            () -> IForgeMenuType.create(GachaMenu::new));
    public static final RegistryObject<SoundEvent> GACHA_SPIN_SOUND = SOUNDS.register("gacha_spin",
            () -> SoundEvent.createVariableRangeEvent(new net.minecraft.resources.ResourceLocation(MOD_ID, "gacha_spin")));
    public static final RegistryObject<SoundEvent> CHANNEL_STATIC_SOUND = SOUNDS.register("channel_static",
            () -> SoundEvent.createVariableRangeEvent(new net.minecraft.resources.ResourceLocation(MOD_ID, "channel_static")));
    public static final RegistryObject<SoundEvent> GACHA_AB_SOUND = SOUNDS.register("gacha_ab",
            () -> SoundEvent.createVariableRangeEvent(new net.minecraft.resources.ResourceLocation(MOD_ID, "gacha_ab")));
    public static final RegistryObject<SoundEvent> GACHA_VOCAL_SOUND = SOUNDS.register("gacha_vocal",
            () -> SoundEvent.createVariableRangeEvent(new net.minecraft.resources.ResourceLocation(MOD_ID, "gacha_vocal")));
    public static final RegistryObject<SoundEvent> REVEAL_SOUND = SOUNDS.register("gacha_reveal",
            () -> SoundEvent.createVariableRangeEvent(new net.minecraft.resources.ResourceLocation(MOD_ID, "gacha_reveal")));
    public static final Map<String, RegistryObject<SoundEvent>> AGENT_VOICES = new LinkedHashMap<>();
    static {
        for (String id : VoiceCatalog.IDS) {
            AGENT_VOICES.put(id, SOUNDS.register("voice_" + id,
                    () -> SoundEvent.createVariableRangeEvent(new net.minecraft.resources.ResourceLocation(MOD_ID, "voice_" + id))));
        }
    }
    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> TAPE_LOOT =
            LOOT_MODIFIERS.register("tape_chests", () -> TapeChestLootModifier.CODEC);

    static {
        for (String id : PlushCatalog.IDS) {
            RegistryObject<PlushBlock> block = BLOCKS.register(id,
                    () -> new PlushBlock());
            PLUSHES.put(id, block);
            ITEMS.register(id, () -> new PlushBlockItem(block.get(), new Item.Properties()));
        }
    }

    public static final RegistryObject<BlockEntityType<PlushBlockEntity>> PLUSH_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("plush", () -> BlockEntityType.Builder.of(
                    PlushBlockEntity::new,
                    PLUSHES.values().stream().map(RegistryObject::get).toArray(PlushBlock[]::new)
            ).build(null));

    public static final RegistryObject<CreativeModeTab> PLUSH_TAB = TABS.register("plushies",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.zzzplushies.plushies"))
                    .icon(() -> new ItemStack(PLUSHES.get("anby_demara").get()))
                    .displayItems((parameters, output) -> {
                        output.accept(PLUSH_BASE.get());
                        output.accept(MASTER_TAPE.get());
                        output.accept(CHOICE_TOKEN.get());
                        output.accept(GACHA_MACHINE_ITEM.get());
                        for (RegistryObject<PlushBlock> block : PLUSHES.values()) {
                            output.accept(block.get());
                        }
                    })
                    .build());

    public ZzzPlushies() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(bus);
        ITEMS.register(bus);
        BLOCK_ENTITIES.register(bus);
        TABS.register(bus);
        MENUS.register(bus);
        SOUNDS.register(bus);
        LOOT_MODIFIERS.register(bus);
        GachaNetwork.register();
    }
}
