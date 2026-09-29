# TV CRT integrada — 1.9.1

O modelo atual é a revisão de 27 cubos fornecida pelo usuário em ../assets/retro-tv/tv_retro_zzz.bbmodel, com exportação tv_retro_zzz_mod_export.json e textura tv_retro_zzz.png (caminhos relativos à raiz Gradle). Não regenerar com o antigo script de 52 cubos. O corpo tem 32 unidades de largura e aproximadamente 32 de altura, incluindo antenas rotacionadas; frente em −Z.

Regeneração: tools/import_tv.py (Python, Pillow, imageio-ffmpeg) importa o JSON recebido, remapeia texturas e aplica o vídeo somente à face norte de tela. A fonte é ../references/zenless-zone-zero-bangboo.mp4. bangboo_screen.png + .png.mcmeta usam 126 quadros 96×64 a 20 fps: ciclo de 6,3 segundos. Desde 1.9.1, a face da tela ocupa [1,13,1]–[22,27,3] (21×14 unidades), preenchendo a região interna da moldura. O vídeo é redimensionado para 96×64, sem faixas laterais adicionadas; isso alarga a imagem quadrada para a proporção 3:2. O MP4 original e o modelo Blockbench permanecem preservados. Sem áudio na fonte. TVs compartilham o atlas animado nativo; não há decodificação MP4 no jogo. A tela usa iluminação normal, sem shader emissivo.

GachaMachineBlock usa half=lower/upper: inferior desenha o modelo completo; superior tem colisão e interação, com modelo vazio. Colocação exige espaço acima e respeita altura máxima; clique superior abre o menu da posição inferior. A remoção de uma metade remove a outra; loot é só da inferior. Quebra criativa da superior remove a inferior sem drop. Máquinas antigas completam a parte superior no primeiro uso quando há espaço; para instalações com obstáculos, libere a área e recoloque a máquina. O corpo largo ultrapassa lateralmente a coluna de dois blocos; deixe espaço lateral.

O importador também gera modelos, blockstates, condição de loot e quatro traduções da UI recebida. O arquivo Blockbench do usuário é preservado. A UI recebida foi integrada em client/GachaScreen.java; regras do gacha e controlador de áudio não foram reescritos.

Validação: build Forge, JSONs, limites de modelo, animação com 124 quadros distintos e chaves de idioma. Ainda sem teste interativo de aparência, UI ou colocação/quebra nesta versão.
