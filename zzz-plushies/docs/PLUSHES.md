# Pelúcias e renderização

## Onde mexer

- `src/main/java/dev/yuri/zzzplushies/PlushCatalog.java`: IDs e ordem dos **58** personagens registrados; a ordem também define índices enviados pelo gacha.
- `ZzzPlushies.java`: registra um bloco e um item por ID, a entidade de bloco comum e a aba criativa.
- `block/PlushBlock.java`: orientação para o jogador, colisão e seleção; formato `ENTITYBLOCK_ANIMATED`.
- `block/PlushBlockEntity.java`: entidade necessária ao renderizador; não guarda dados específicos do personagem.
- `client/PlushBlockEntityRenderer.java`: modelo sentado no mundo. Usa as UVs da skin do jogador e a textura `textures/entity/<id>.png`. A escala externa atual é **0,65**; pés em `y=0`, altura aproximada de **9,75 pixels** de bloco.
- `block/PlushBlockItem.java`, `client/PlushItemClient.java` e `client/PlushItemRenderer.java`: renderização 3D do próprio modelo nos itens, inclusive inventário.

## Geometria

A referência foi o modelo Blockbench fornecido em `llary-plush`: cabeça 10×9×10, torso 6×6×6, braços curtos laterais e pernas sentadas. O renderizador monta seis partes de `PlayerModel` com transformações individuais, aplicando também as camadas de roupa e cabeça da skin. Ajustes de proporção devem considerar o corpo **no mundo e no ícone 3D**, porque ambos usam o mesmo renderizador. O bloco tem quatro formas de colisão conforme a direção; se o modelo crescer, confira também `PlushBlock.java`.

## Recursos por personagem

Cada ID possui `blockstates/<id>.json`, `models/item/<id>.json`, `textures/entity/<id>.png`, textura de item, `loot_tables/blocks/<id>.json` e receitas de stonecutter. O `Plush Base` permite escolher qualquer pelúcia no stonecutter; o voucher também possui receita por personagem. A origem das skins e observações de autoria estão em [SKIN_SOURCES.md](../SKIN_SOURCES.md).

Para adicionar ou trocar skins, leia [ASSETS.md](ASSETS.md) antes de rodar o gerador: ele sobrescreve arquivos gerados e as traduções.
