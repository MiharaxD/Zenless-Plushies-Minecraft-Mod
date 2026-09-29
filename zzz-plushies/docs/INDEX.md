# ZZZ Plushies: índice técnico

Estado documentado: **1.8.1**, em 29/09/2026. A raiz do projeto é a pasta `zzz-plushies` que contém este documento em `docs/`; Forge **1.20.1**, Java **17**, namespace `zzzplushies`. Verifique `build.gradle` e `mods.toml` ao iniciar uma nova versão. A pasta irmã `../assets/zzz-plushie-skins/` contém as skins originais necessárias ao gerador.

## Leia só o assunto do pedido

| Pedido | Documento | Código principal |
| --- | --- | --- |
| Corpo, pose, tamanho, textura ou ícone 3D das pelúcias | [PLUSHES.md](PLUSHES.md) | `block/`, `client/Plush*`, `PlushCatalog.java` |
| Modelo e textura da TV CRT decorativa | [RETRO_TV.md](RETRO_TV.md) | `../assets/retro-tv/` |
| Chances, pity, fitas, recompensa, interface, animação ou áudio do gacha | [GACHA.md](GACHA.md) | `gacha/`, `client/GachaScreen.java`, `client/GachaMusic.java` |
| Adicionar personagem, skin, fala, tradução ou outros recursos | [ASSETS.md](ASSETS.md) | `generate_assets.py`, `VoiceCatalog.java`, `src/main/resources/` |
| Compilar, testar, restaurar mundo ou entregar JAR | [BUILD_AND_RELEASE.md](BUILD_AND_RELEASE.md) | `build.gradle`, `verification/`, `versions/`, `output/` |

`README.md` conta a evolução das versões, incluindo regras antigas; para o comportamento **atual**, consulte o código e as páginas acima. Catálogos e créditos: [SKIN_SOURCES.md](../SKIN_SOURCES.md), [A_RANK_SOURCES.md](../A_RANK_SOURCES.md), [VOICE_SOURCES.md](../VOICE_SOURCES.md), [SOUND_SOURCES.md](../SOUND_SOURCES.md).

## Fluxo mínimo para qualquer alteração

1. Abra a página pertinente e os arquivos nela indicados; confira o estado atual antes de concluir que a documentação ainda corresponde ao código.
2. Faça a menor mudança que resolva o pedido. Não altere saves de teste com o Minecraft aberto.
3. Verifique a regra afetada; compile se houver código ou recurso do mod alterado.
4. Atualize a página pertinente e, ao lançar nova versão, `README.md` e o registro de teste em `verification/`.

As versões em `versions/` são snapshots preservados. `output/` guarda JARs entregues. `build/`, `run/` e `run-server/` são áreas de trabalho, não fontes canônicas.
