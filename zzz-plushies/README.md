# ZZZ Plushies — Forge 1.20.1

For the current architecture and a short path to the relevant files for each kind of change, start with [docs/INDEX.md](docs/INDEX.md).

Small, placeable, seated decorative plushies made from the selected Zenless Zone Zero Minecraft skins. Each agent has its own block and item. The plushes face the player when placed, sit flush on the ground, can be broken by hand and drop themselves. They appear in the **ZZZ Plushies** creative tab.

In survival, craft four **Plush Bases** with one white wool surrounded by four string, then use a stonecutter to choose a character plush. This recipe keeps the character selection in one familiar screen.

The 58 currently selected agents are listed with original skin artists and links in [SKIN_SOURCES.md](SKIN_SOURCES.md). Pyrois and Claret Flint need identifiable source skins before they can be added. Roxy is included in advance of her September 30, 2026 release. NameMC does not identify the Starlight Billy skin by name; verify that visual before distributing the mod.


## Current version: 1.9.2

Fixed pink missing-texture particles when breaking plushes; each plush now uses its character icon for fragments.

### Version 1.9.1

Tela da TV ampliada até a borda interna da moldura (21×14 unidades). Vídeo preenche a superfície sem faixas laterais adicionadas.

## Version 1.9.0

Gacha machine: revised user CRT model, two-block placement, looping Bangboo video and supplied banner UI. See docs/RETRO_TV.md.

### Version 1.8.1

Every character plush now gets its own animated focus screen when revealed. S plushes appear first, followed by A plushes, each in its rank color; only character rewards play their agent voice, and only while their focus is on screen. The music keeps playing from its current position as each plush enters focus, without restarting or cutting off. It stops when the player advances to the final reward cards. Item rewards still go straight to the reward cards. The 1.8.0 release remains preserved in `versions/1.8.0/`.

### Version 1.8.0


In version 1.8.0, A/B-only pulls use the supplied Marion instrumental from the beginning. The channel animation settles on its final rank at **8 seconds**, when the music reaches its drop; the rank and music then wait for confirmation. Pulls containing S continue to use Golden Sign. Each A-rank pull has a **25% chance to award a random A-rank agent plush** from the 13 agents listed in [A_RANK_SOURCES.md](A_RANK_SOURCES.md); the other 75% keep the A-tier item rewards. A-rank plushes do not reset the S pity or affect the S 50/50 and voucher. The earlier 1.7.0 release is preserved in `versions/1.7.0/`.

### Version 1.7.0 mechanics

The machine offers one pull for one Master Tape or ten pulls for ten tapes. Each pull in a batch is resolved sequentially on the server; every S resets pity immediately, including in the middle of a batch. Insufficient tapes reject the whole batch without consuming anything. The base S chance is now **2%**, with a guaranteed S on pull **80**. Pulls 1–59 use 2%; starting on pull 60 the chance increases linearly by 98/21 percentage points per pull (60: 6.6667%, 70: 53.3333%, 79: 95.3333%, 80: 100%). Below soft pity, A is 30% and B is 68%; as S rises, the remaining A/B probability keeps the same ratio. The machine displays the next pull's actual S chance. Existing saved pity carries forward.

The TVs hold their ranks indefinitely until a left click, Enter, or Space. A batch containing S uses Golden Sign. Confirming an A/B batch stops its music and slides in all rewards with a custom swoosh/impact sound. A ten-pull batch containing S first zooms into each S in order, playing the vocal section of Golden Sign and the agent voice when available; the next confirmation advances to the next S, then to all ten rewards. The final reward screen also waits for a click. A single S reveals its reward and voice on confirmation. Escape closes the machine and stops its audio. Rewards are already safely delivered by the server, so closing the screen never loses them.

The target 50/50 and choice voucher after two losses continue to apply separately to every S. Item tooltips on the reward grid show complete names and enchantments. The original audio sources and generated effects are documented in [SOUND_SOURCES.md](SOUND_SOURCES.md). Prior release JARs and sources remain in `versions/`.

## Build

Requires Java 17. Run `gradlew.bat build` from this directory. The mod JAR will be in `build/libs/` and goes in the `mods` folder of a Forge 1.20.1 client and server. No other mods are required.

The resources can be regenerated from `../assets/zzz-plushie-skins/` by running `python generate_assets.py` with Pillow installed.

Version 1.3.0 uses the supplied `llary-plush` Blockbench model as the geometry reference for the six shared body parts: a 10 x 9 x 10 head, 6 x 6 x 6 torso, short sideways arms, and seated legs. It keeps each agent's original Minecraft skin and clothing layers. The four extra head decorations in that character-specific model are not applied to every ZZZ character. Earlier JARs and source snapshots are preserved in `versions/1.0.0/`, `versions/1.1.0/`, and `versions/1.2.0/`.

Version 1.4.0 renders the placed plushes at 60% of that geometry (nine pixels tall) and gives each inventory item a live 3D preview of its own plush model. The prior 1.3.0 build and source are preserved in `versions/1.3.0/`.

Version 1.4.1 increases the plush scale to 65% of the reference geometry (9.75 pixels tall). The prior 1.4.0 build and source are preserved in `versions/1.4.0/`.

Version 1.5.0 adds a craftable gacha machine. One Master Tape gives one spin. Tapes appear in generated chest loot (50% chance, 1–3 tapes), including chest loot rolled by Lootr; the Forge loot modifier is data driven at `data/zzzplushies/loot_modifiers/tape_chests.json`, so loot packs and LootJS setups can adjust distribution. A plush has a 0.60% base chance and is guaranteed on the 75th spin since the last plush. Choose a target in the machine: a plush roll has a 50% chance to give that target and 50% to give a different random plush. Two lost 50/50 rolls award a Plush Choice Voucher; put it in a stonecutter to select any of the 58 plushes. Other rolls give ores, ingots, tools or enchanted books. Pity and losses are stored per player across machines and deaths.

Each spin plays the supplied Golden Sign music. Ordinary prizes stop it at 7.5 seconds; a plush keeps the song playing and triggers that agent's first Obtain Agent voice at 8.25 seconds. The 48 available voice files and their original wiki links are listed in [VOICE_SOURCES.md](VOICE_SOURCES.md). The remaining 10 plushes have no substitute voice where the wiki does not provide that file. A Starlight Billy plush uses Billy Kid's voice.

Version 1.6.0 adds a full-screen retro TV animation inspired by the supplied ZZZ gacha video. Its channels flash B in blue, A in purple, and S in yellow, with a short synthetic channel-static sound on each change. At 7.5 seconds the TV reveals the prize: B gives 2–8 coal, copper, iron, redstone, or lapis items; A gives better rewards such as gold, diamonds, emeralds, enchanted books or gear, and rare netherite; S gives a plush. Base chances are B 69.4%, A 30%, and S 0.6% until the 75th-spin plush guarantee applies. The TV clears after the reveal so the machine can be used again. Version 1.5.0 remains preserved in `versions/1.5.0/`.

This is an unofficial fan project. Do not redistribute the third-party skins without checking their creators' terms.
