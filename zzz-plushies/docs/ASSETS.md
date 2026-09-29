# Skins, vozes e recursos

## Fonte e geração das pelúcias

As skins selecionadas ficam fora deste projeto, em `../assets/zzz-plushie-skins/`; o manifesto de seleção é `SOURCES.md` nessa pasta. [SKIN_SOURCES.md](../SKIN_SOURCES.md) é a cópia de créditos no projeto. `generate_assets.py` lê a seleção, exige **58 IDs distintos** e PNGs de skin Minecraft **64×64**, e produz `PlushCatalog.java`, texturas, modelos de item, blockstates, loot tables, receitas e `en_us.json`/`pt_br.json`.

**Cuidado:** rodar o gerador sobrescreve os arquivos de idioma inteiros. As chaves da interface, fitas e gacha foram adicionadas depois; antes de gerar, preserve e recoloque essas chaves ou ajuste o gerador. Ele também sobrescreve o catálogo, receitas e texturas por personagem. Compare a saída com o estado anterior e não execute só para alterar uma pose ou chance.

Ao adicionar um personagem, conferir: fonte/autor da skin, ID estável, textura 64×64, entrada em `PlushCatalog`, bloco/item registrado, tradução, receitas/drops e voz se existir. O catálogo não é a lista de rank A; esta fica separada em `gacha/ARankPlushPool.java` e [A_RANK_SOURCES.md](../A_RANK_SOURCES.md).

## Vozes e música

`VoiceCatalog.java` lista as **48** vozes disponíveis dentre as 58 pelúcias; `ZzzPlushies.java` registra `voice_<id>` para cada uma. Os OGGs estão em `src/main/resources/assets/zzzplushies/sounds/voices/` e os eventos em `sounds.json`. [VOICE_SOURCES.md](../VOICE_SOURCES.md) registra a origem e as ausências. Não atribua fala de outro personagem a uma voz faltante sem pedido explícito.

Faixas e efeitos estão em `sounds/`; [SOUND_SOURCES.md](../SOUND_SOURCES.md) informa origem e uso. Arquivos de áudio fornecidos pelo usuário foram convertidos para OGG com streaming. Ao mudar som, confira registro em `sounds.json`, `ZzzPlushies.java` e chamada em `client/GachaMusic.java`.

## Recursos e localização

`src/main/resources/assets/zzzplushies/`: interface, modelos, texturas, sons e idiomas. `src/main/resources/data/zzzplushies/`: receitas, drops e modificador de loot. `src/main/resources/data/forge/`: inscrição global do modificador. Mantenha as traduções `en_us` e `pt_br` alinhadas para qualquer texto novo.
