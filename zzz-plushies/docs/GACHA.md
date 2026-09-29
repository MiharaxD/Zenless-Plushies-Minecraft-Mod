# Gacha: regras e fluxo atual (1.8.1)

## Autoridade e dados

O **servidor** decide e entrega cada prêmio em `gacha/GachaLogic.java`. `GachaMachineBlock.java` abre `GachaMenu.java`; botões 0/3 fazem 1/10 giros, 1/2 mudam o alvo S. Um giro custa **1 Master Tape**; dez custam dez. O servidor valida a quantidade antes de consumir, processa giros em sequência, entrega itens imediatamente e envia os resultados ao cliente por `GachaNetwork.java` (canal versão `2`). Fechar a animação não remove prêmios.

`GachaLogic.data()` guarda `selected`, `pity`, `losses` e `lastSpinGameTime` em `Player.PERSISTED_NBT_TAG/zzzplushies_gacha`. `PlayerEvent.Clone` copia esses dados ao morrer. `GachaMenu` sincroniza alvo, pity, perdas e número de fitas; não há inventário interno na máquina. O bloqueio entre giros usa `GachaTiming.ROLL_TICKS` (**160 ticks**). Alterar a duração da animação exige conferir também esse limite.

## Chances e recompensas

- `GachaOdds.java`: chance S base **2%** nos giros 1–59 após o último S. Soft pity começa no giro **60**, cresce a cada giro e chega a **100% no 80**. A chance A é `30/98` do restante após S; o resto é B. `pity` conta giros concluídos desde o último S, avança com A/B e zera só com S.
- `GachaLogic.rollOne()`: se sair **A**, `nextInt(4)==0` dá **25% condicionais** de uma pelúcia A aleatória dentre 13 IDs de `ARankPlushPool.java`; os outros 75% dão itens A. Pelúcia A **não** aciona o 50/50 nem zera pity. A seleção do alvo na máquina vale para S.
- **S**: 50% de ganhar a pelúcia selecionada; a outra metade sorteia outra pelúcia do catálogo. A cada segunda perda é entregue um `choice_token` e `losses` volta a zero. O código atual **não zera** esse contador numa vitória intermediária.
- **B**: 2–8 unidades de carvão, cobre, ferro, redstone ou lápis-lazúli. **A item**: ouro, diamante, esmeralda, livro/ferramenta encantada, sucata ou lingote de netherite; pesos em `rankAReward()`.

`GachaResult` carrega rank, índice da pelúcia (`-1` para item), cópia do prêmio, resultado do 50/50 e voucher. Em lotes de dez, cada giro altera pity antes do seguinte.

## Telas e áudio

`client/GachaScreen.java` desenha a máquina, a grade de até dez TVs, o foco de personagem e os cartões. `client/GachaMusic.java` controla as fases `IDLE → ROLLING → WAITING → FOCUS* → RESULTS`. As TVs piscam B azul, A roxo e S dourado; o rank final trava aos **8 segundos**. `WAITING` dura até o jogador clicar, Enter ou Espaço. Depois vêm todos os S em ordem e, em seguida, todas as pelúcias A; itens não recebem foco. Cada foco tem zoom próprio, na cor do rank. O último clique mostra o conjunto de prêmios com animação de slide.

No começo do giro, se o lote contém S, toca `gacha_spin` (Golden Sign); caso contrário, toca `gacha_ab` (Marion instrumental). **Entrar em foco não reinicia nem corta a faixa**; ela continua da posição atual, inclusive entre focos, e para ao avançar para `RESULTS` ou fechar a tela. O chiado de canal é `channel_static`, e a transição usa `gacha_reveal`. A fala do agente toca **só durante `FOCUS`**, a partir de 12 ticks de foco, se houver áudio no `VoiceCatalog`; é interrompida ao sair do foco. `gacha_vocal` permanece no pacote por histórico, mas não é chamado pelo fluxo atual.

## Fitas em baús

`TapeChestLootModifier.java`, `data/forge/loot_modifiers/global_loot_modifiers.json` e `data/zzzplushies/loot_modifiers/tape_chests.json` aplicam um Forge Global Loot Modifier em loot tables cujo caminho começa com `chests/`: **50%** de chance de adicionar **1–3 fitas**. A integração é por tabela de loot, sem dependência direta de Lootr ou LootJS; confira a tabela e as alterações do pack ao investigar incompatibilidades.
