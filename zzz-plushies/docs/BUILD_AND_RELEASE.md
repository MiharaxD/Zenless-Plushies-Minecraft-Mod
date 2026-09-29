# Compilação, testes e versões

## Ambiente

Forge **47.3.0** para Minecraft **1.20.1**, Gradle wrapper incluído e Java **17**. De `zzz-plushies/`, execute `./gradlew.bat build --no-daemon` no PowerShell; o JAR resultante fica em `build/libs/zzz-plushies-forge-1.20.1-<versão>.jar`. Se o sistema escolher Java incompatível, configure `JAVA_HOME` para um JDK 17 instalado no novo local. O wrapper pode precisar baixar dependências quando o projeto for aberto em outro computador.

`./gradlew.bat runClient --no-daemon` usa `run/`; `runServer` usa `run-server/`. Não trate esses diretórios como código-fonte. Em particular, salve/feche o Minecraft antes de modificar `run/saves/`. Se precisar forçar estado de pity ou fitas, faça backup dos arquivos afetados e restaure após o teste. O usuário acompanha os testes: evite capturas de tela e logs extensos sem necessidade.

## Verificação proporcional à mudança

- Regras de chance/tempo: confira `verification/GachaOddsCheck.java` e execute a checagem apropriada; veja relatos `verification/TESTING-*.md`. A ausência de teste Gradle (`test NO-SOURCE`) não equivale a teste de jogo.
- Mudança em Java ou recursos: compile e confirme que o JAR contém as classes/arquivos esperados.
- Interface, animação ou áudio: teste no cliente quando o comportamento visual/sonoro é o motivo da mudança. Inspeção de código e compilação não comprovam qualidade do áudio ouvido.
- Conteúdo gerado: valide IDs, contagem, caminhos, JSON, traduções e referência aos recursos antes de empacotar.

## Entrega

`build.gradle` e `src/main/resources/META-INF/mods.toml` precisam da mesma versão. Copie o JAR final para `output/` sem remover os anteriores. Em `versions/<versão>/`, preserve JAR, ZIP de fontes e `manifest.json` com tamanho e SHA-256; `versions/1.8.1/` mostra o formato. Inclua no ZIP as fontes, recursos, wrapper, arquivos de configuração, scripts necessários, créditos e a documentação pertinente. Não inclua `build/`, `run/`, `run-server/`, `output/`, outros `versions/` nem mundos de teste. Confira integridade do JAR/ZIP e o número de versão antes de entregar.

O projeto **não é um repositório Git** neste diretório no estado documentado; não presuma histórico de commits. Preserve versões anteriores como arquivos. A pasta `versions/1.8.1/` é um snapshot do momento do lançamento e não inclui esta documentação criada depois; novas versões devem incluí-la.

## Mudança de lugar

Para levar o projeto, mantenha `zzz-plushies/` e `assets/zzz-plushie-skins/` dentro de uma mesma pasta principal. O `generate_assets.py` usa esse caminho relativo. A cópia de transporte `ZZZ-Plushies-Para-Mover/` também contém `references/` com os materiais originais fornecidos pelo usuário; esses materiais ajudam em futuras edições, mas não são exigidos para compilar o JAR atual. Abra a pasta interna `zzz-plushies/` na IDE e configure Java 17 no novo local. Os caminhos de saves em `run/` podem depender do ambiente antigo; use-os apenas como dados de teste preservados.
