# Asset da TV CRT

Criado em 29/09/2026; não altera o mod 1.8.1. Pasta canônica: [../../assets/retro-tv/](../../assets/retro-tv/). Comece pelo [README do modelo](../../assets/retro-tv/README.md).

`retro_tv.bbmodel` é o projeto nativo editável; `retro_tv.png` é o atlas pixel art 256×256, também incorporado no modelo. O móvel tem 52 cubos em cinco grupos, 26×32×16,6 unidades (2 blocos de altura) e chão em Y=0. A tela arredondada em degraus usa uma ilha própria com arte original de sinal eletrônico; frente em −Z.

Para mudar silhueta ou proporções, edite os grupos no Blockbench. Para trocar apenas a imagem da tela, use a ilha `screen_68x58` de `uv_layout.json`. Faces equivalentes compartilham UVs de propósito. O script `build_model.py` é uma reprodução procedural e sobrescreve edições manuais se executado.

Verificado no Blockbench 5.2.1: carregamento, aparência, aplicação da textura e organização. Validado por script: altura, cubos, grupos, UVs, atlas e igualdade dos pixels da textura incorporada com o PNG externo. Ainda não foi integrado nem testado como bloco no Minecraft. Uma futura integração deve definir colocação inferior/superior, colisão, quebra conjunta e renderização; a aparência brilhante da tela não configura emissão luminosa por si só.
