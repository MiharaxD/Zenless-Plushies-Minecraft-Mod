package dev.yuri.zzzplushies.client;

import dev.yuri.zzzplushies.PlushCatalog;
import dev.yuri.zzzplushies.ZzzPlushies;
import dev.yuri.zzzplushies.gacha.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import java.util.Locale;

public final class GachaScreen extends AbstractContainerScreen<GachaMenu> {
    private Button single, ten, prev, next;

    public GachaScreen(GachaMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    // ---- layout (tudo calculado a partir do tamanho da tela: a UI ocupa a tela toda, estilo banner) ----
    private static final int TOP_BAR = 26, BOTTOM_BAR = 48;
    private int stageTop() { return TOP_BAR; }
    private int stageBottom() { return height - BOTTOM_BAR; }
    private float mainScale() {
        float byH = (stageBottom() - stageTop()) * 0.62F / 16F;
        float byW = width * 0.26F / 16F;
        return Math.max(3F, Math.min(byH, byW));
    }
    private int charCx() { return Math.round(width * 0.66F); }

    @Override protected void init() {
        imageWidth = width;
        imageHeight = height;
        super.init();
        int cx = charCx();
        int arrowY = (stageTop() + stageBottom()) / 2 - 10;
        int reach = Math.round(mainScale() * 16 * 1.75F);
        prev = addRenderableWidget(Button.builder(Component.literal("\u25C0"), b -> press(1))
                .bounds(Math.max(width * 4 / 10, cx - reach - 12), arrowY, 24, 20).build());
        next = addRenderableWidget(Button.builder(Component.literal("\u25B6"), b -> press(2))
                .bounds(Math.min(width - 36, cx + reach - 12), arrowY, 24, 20).build());
        int by = height - BOTTOM_BAR + 12;
        int bw = Math.min(120, Math.max(80, (width - 60) / 5));
        int xTen = width - 16 - bw;
        int xOne = xTen - 40 - bw;
        single = addRenderableWidget(Button.builder(Component.translatable("button.zzzplushies.spin_short"), b -> press(0))
                .bounds(xOne, by, bw, 24).build());
        ten = addRenderableWidget(Button.builder(Component.translatable("button.zzzplushies.spin_ten_short"), b -> press(3))
                .bounds(xTen, by, bw, 24).build());
        updateButtons();
    }
    @Override protected void containerTick() { super.containerTick(); updateButtons(); }
    private void updateButtons() {
        boolean idle = !GachaMusic.isRevealing();
        single.active = idle && menu.tapes() >= 1;
        ten.active = idle && menu.tapes() >= 10;
        prev.active = idle;
        next.active = idle;
    }
    private void press(int id) {
        if (!GachaMusic.isRevealing() && minecraft != null && minecraft.gameMode != null)
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, id);
    }

    // ---- helpers de visual "minecraft": painel chanfrado ----
    private static void bevel(GuiGraphics g, int x, int y, int w, int h, int base, int light, int dark) {
        g.fill(x, y, x + w, y + h, 0xFF000000);
        g.fill(x + 1, y + 1, x + w - 1, y + h - 1, base);
        g.fill(x + 1, y + 1, x + w - 1, y + 2, light);
        g.fill(x + 1, y + 1, x + 2, y + h - 1, light);
        g.fill(x + 1, y + h - 2, x + w - 1, y + h - 1, dark);
        g.fill(x + w - 2, y + 1, x + w - 1, y + h - 1, dark);
    }
    private static void panel(GuiGraphics g, int x, int y, int w, int h) {
        bevel(g, x, y, w, h, 0xFF2B2B2B, 0xFF4A4A4A, 0xFF161616);
    }
    private ItemStack plushAt(int offset) {
        int len = PlushCatalog.IDS.length;
        String id = PlushCatalog.IDS[Math.floorMod(menu.selected() + offset, len)];
        return new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("zzzplushies", id)));
    }

    @Override protected void renderBg(GuiGraphics g, float partialTick, int mouseX, int mouseY) {
        // fundo estilo banner (laranja escuro) com listras verticais
        g.fillGradient(0, 0, width, height, 0xFF5A3410, 0xFF160D05);
        for (int x = -(int) (System.currentTimeMillis() / 60 % 48); x < width; x += 48)
            g.fill(x, 0, x + 18, height, 0x14000000);

        int top = stageTop(), bottom = stageBottom();
        float s = mainScale();
        int cx = charCx();
        int baseY = bottom - 34;

        // plush vizinhos (menores, atras) + plush selecionado (grande, na frente)
        drawItem(g, plushAt(-1), cx - Math.round(s * 16 * 1.3F), baseY - Math.round(s * 16 * 0.32F), s * 0.62F);
        drawItem(g, plushAt(1), cx + Math.round(s * 16 * 1.3F), baseY - Math.round(s * 16 * 0.32F), s * 0.62F);
        ItemStack plush = plushAt(0);
        float bob = (float) Math.sin((GachaMusic.ticks() + partialTick) / 14F) * 2F;
        drawItem(g, plush, cx, baseY - Math.round(s * 16 * 0.5F) + Math.round(bob), s);

        // etiqueta do personagem: [S] Nome
        String name = plush.getHoverName().getString();
        int tw = font.width(name);
        int pw = Math.max(96, tw + 46), ph = 24;
        int px = cx - pw / 2, py = bottom - 30;
        panel(g, px, py, pw, ph);
        bevel(g, px + 4, py + 4, 16, 16, 0xFFE0A800, 0xFFFFE27A, 0xFF8A6800);
        g.drawCenteredString(font, "S", px + 12, py + 8, 0xFF1A1200);
        g.drawString(font, name, px + 26, py + 8, 0xFFFFFFFF, true);

        // barra superior
        g.fill(0, 0, width, TOP_BAR, 0xFF0C0C0C);
        g.fill(0, TOP_BAR, width, TOP_BAR + 1, 0xFF6B4A18);
        g.drawString(font, title, 12, 9, 0xFFFFAA00, true);
        // contador de fitas (canto superior direito)
        String tapes = String.format(Locale.ROOT, "%08d", menu.tapes());
        int boxW = font.width(tapes) + 34;
        int bx = width - boxW - 10;
        bevel(g, bx, 4, boxW, 18, 0xFF1C1C1C, 0xFF303030, 0xFF0A0A0A);
        g.renderItem(new ItemStack(ZzzPlushies.MASTER_TAPE.get()), bx + 3, 5);
        g.drawString(font, tapes, bx + 24, 9, 0xFFFFFFFF, false);

        // coluna esquerda: titulo do banner + descricao + status
        int colX = 16, colW = Math.min(width * 4 / 10 - 24, 260);
        float ts = 1.7F;
        var lines = font.split(plush.getHoverName(), Math.max(40, (int) (colW / ts)));
        int y = top + 20;
        g.pose().pushPose();
        g.pose().translate(colX, y, 0);
        g.pose().scale(ts, ts, 1);
        for (int i = 0; i < Math.min(2, lines.size()); i++) g.drawString(font, lines.get(i), 0, i * 11, 0xFFFFD44F, true);
        g.pose().popPose();
        y += Math.min(2, lines.size()) * 19 + 8;
        g.drawString(font, Component.translatable("gui.zzzplushies.banner_limited"), colX, y, 0xFFFFFFFF, true);
        y += 14;
        for (var l : font.split(Component.translatable("gui.zzzplushies.banner_desc"), colW)) {
            g.drawString(font, l, colX, y, 0xFFE8D9BF, true);
            y += 10;
        }

        // status embaixo a esquerda (estilo "Times: 90 S-Rank guaranteed")
        int hard = GachaOdds.HARD_PITY;
        int sy = bottom - 62;
        drawChip(g, colX, sy, Component.translatable("gui.zzzplushies.pity", menu.pity(), hard), 0xFFFFD44F, colW);
        drawChip(g, colX, sy + 16, Component.translatable("gui.zzzplushies.losses", menu.losses(), 2), 0xFFF5C6C6, colW);
        drawChip(g, colX, sy + 32, Component.translatable("gui.zzzplushies.chance",
                String.format(Locale.ROOT, "%.2f", 100 * GachaOdds.sChance(menu.pity()))), 0xFFFFFFFF, colW);

        // barra inferior com fitas por botao (x1 / x10)
        g.fill(0, bottom, width, height, 0xFF0C0C0C);
        g.fill(0, bottom, width, bottom + 1, 0xFF6B4A18);
        drawCost(g, single, 1);
        drawCost(g, ten, 10);
    }
    private void drawChip(GuiGraphics g, int x, int y, Component text, int color, int maxW) {
        int w = Math.min(maxW, font.width(text) + 14);
        bevel(g, x, y, w, 14, 0xFF1C1C1C, 0xFF303030, 0xFF0A0A0A);
        g.drawString(font, text, x + 7, y + 3, color, false);
    }
    private void drawCost(GuiGraphics g, Button b, int n) {
        int x = b.getX() - 34, y = b.getY() + 4;
        g.renderItem(new ItemStack(ZzzPlushies.MASTER_TAPE.get()), x, y);
        g.drawString(font, "\u00D7" + n, x + 17, y + 5, b.active ? 0xFFFFFFFF : 0xFF777777, true);
    }
    @Override protected void renderLabels(GuiGraphics g, int x, int y) {}
    @Override public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        renderBackground(g);
        super.render(g, mouseX, mouseY, partialTick);
        if (!GachaMusic.isRevealing()) { renderTooltip(g, mouseX, mouseY); return; }
        g.flush();
        g.pose().pushPose();
        g.pose().translate(0, 0, 500);
        renderReveal(g, partialTick, mouseX, mouseY);
        g.flush();
        g.pose().popPose();
    }
    @Override public boolean mouseClicked(double x, double y, int button) {
        if (GachaMusic.isRevealing()) { if (button == 0) GachaMusic.confirm(); return true; }
        return super.mouseClicked(x, y, button);
    }
    @Override public boolean keyPressed(int key, int scan, int modifiers) {
        if (GachaMusic.isRevealing() && (key == 257 || key == 335 || key == 32)) {
            GachaMusic.confirm(); return true;
        }
        return super.keyPressed(key, scan, modifiers);
    }
    @Override public void removed() { GachaMusic.reset(); super.removed(); }

    private record Rect(int x, int y, int w, int h) {}
    private Rect bounds(int index, boolean grid) {
        if (!grid) {
            int w = Math.min(width - 32, 350), h = Math.min(height - 60, 230);
            return new Rect((width - w) / 2, (height - h) / 2, w, h);
        }
        int cols = width >= 330 ? 5 : 3;
        int rows = (10 + cols - 1) / cols;
        int gap = 7, w = Math.min(100, (width - 32 - (cols - 1) * gap) / cols);
        int h = Math.min(100, (height - 66 - (rows - 1) * gap) / rows);
        int left = (width - (cols * w + (cols - 1) * gap)) / 2;
        int top = (height - (rows * h + (rows - 1) * gap)) / 2;
        return new Rect(left + index % cols * (w + gap), top + index / cols * (h + gap), w, h);
    }
    private static float ease(float t) { t = Math.max(0, Math.min(1, t)); return 1 - (float)Math.pow(1-t, 4); }
    private static int mix(int a, int b, float t) { return Math.round(a + (b-a)*t); }
    private static int color(GachaGrade grade) {
        return grade == GachaGrade.S ? 0xFFFFD44F : grade == GachaGrade.A ? 0xFFCF61F2 : 0xFF53C8FA;
    }
    private void renderReveal(GuiGraphics g, float partial, int mouseX, int mouseY) {
        var phase = GachaMusic.phase();
        var results = GachaMusic.results();
        boolean grid = results.size() == 10;
        float time = GachaMusic.phaseTicks() + partial;
        g.fill(0, 0, width, height, 0xF5080D17);
        if (phase == GachaMusic.Phase.ROLLING || phase == GachaMusic.Phase.WAITING) {
            g.drawCenteredString(font, Component.translatable(phase == GachaMusic.Phase.ROLLING
                    ? "gui.zzzplushies.signal_search" : "gui.zzzplushies.signal_locked"), width/2, 9, 0xFFF2F0E8);
            for (int i = 0; i < results.size(); i++) drawTV(g, bounds(i, grid), GachaMusic.visibleChannel(i),
                    phase == GachaMusic.Phase.ROLLING, i, ItemStack.EMPTY, grid);
        } else if (phase == GachaMusic.Phase.FOCUS) {
            float t = ease(time/16);
            int idx = GachaMusic.focusIndex();
            GachaResult character = results.get(idx);
            Rect a = results.size() == 1
                    ? new Rect(width / 2 - 55, height / 2 - 40, 110, 80)
                    : bounds(idx, true);
            Rect b = bounds(0, false);
            Rect r = new Rect(mix(a.x,b.x,t),mix(a.y,b.y,t),mix(a.w,b.w,t),mix(a.h,b.h,t));
            drawTV(g, r, character.grade(), false, idx, time > 6 ? character.reward() : ItemStack.EMPTY, false);
            g.drawCenteredString(font, Component.translatable("gui.zzzplushies.agent_obtained"), width/2, 9, color(character.grade()));
        } else {
            g.drawCenteredString(font, Component.translatable("gui.zzzplushies.rewards"), width/2, 9, 0xFFF2F0E8);
            for (int i = 0; i < results.size(); i++) {
                Rect r = bounds(i, grid);
                float t = ease((time - i * 1.1F)/16);
                r = new Rect(r.x + Math.round((1-t)*(width+30)),r.y,r.w,r.h);
                drawReward(g, r, results.get(i), grid);
                if (time > 30 && mouseX >= r.x && mouseX < r.x+r.w && mouseY >= r.y && mouseY < r.y+r.h)
                    g.renderTooltip(font, results.get(i).reward(), mouseX, mouseY);
            }
        }
        if (phase != GachaMusic.Phase.ROLLING) {
            boolean ready = phase == GachaMusic.Phase.WAITING || time >= (phase == GachaMusic.Phase.RESULTS ? 30 : 16);
            if (ready) g.drawCenteredString(font, Component.translatable(phase == GachaMusic.Phase.RESULTS
                    ? "gui.zzzplushies.click_close" : "gui.zzzplushies.click_continue"), width/2, height-16, 0xFFE5EAF3);
        }
    }
    private void drawTV(GuiGraphics g, Rect r, GachaGrade grade, boolean noise, int index, ItemStack item, boolean small) {
        int accent = grade == null ? 0xFF8796A4 : color(grade);
        int inset = small ? 5 : 12, controls = small ? 12 : 36;
        int sx = r.x+inset, sy=r.y+inset, sw=r.w-inset*2-controls, sh=r.h-inset*2;
        g.fill(r.x+3,r.y+4,r.x+r.w+3,r.y+r.h+4,0xAA000000);
        g.fill(r.x,r.y,r.x+r.w,r.y+r.h,0xFF394351);
        g.fill(r.x+3,r.y+3,r.x+r.w-3,r.y+r.h-3,0xFF151D29);
        g.fill(sx-2,sy-2,sx+sw+2,sy+sh+2,0xFF03060B);
        g.fill(sx,sy,sx+sw,sy+sh,grade==GachaGrade.S?0xFF362810:grade==GachaGrade.A?0xFF251333:0xFF102536);
        g.fill(sx,sy,sx+sw,sy+2,accent);
        g.fill(sx,sy+sh-2,sx+sw,sy+sh,accent);
        for(int y=sy+4;y<sy+sh-2;y+=4) g.fill(sx,y,sx+sw,y+1,0x40000000);
        if(noise) for(int n=0;n<4;n++) {
            int y=sy+3+Math.floorMod(GachaMusic.ticks()*(n+3)*7+index*17+n*29,Math.max(1,sh-8));
            int x=sx+2+Math.floorMod(GachaMusic.ticks()*3+n*13,Math.max(1,sw/3));
            g.fill(x,y,Math.min(sx+sw-2,x+sw/2),y+1,0x66BCE1EF);
        }
        if(grade!=null) {
            float scale = Math.min(small?3.2F:8F, Math.min(sw/12F,sh/12F));
            if(!item.isEmpty()) scale = Math.min(3F, scale);
            g.pose().pushPose();
            g.pose().translate(sx+sw/2F, item.isEmpty()?sy+sh/2F-4*scale:sy+6,0);
            g.pose().scale(scale,scale,1);
            g.drawCenteredString(font,grade.name(),0,0,accent);
            g.pose().popPose();
        }
        if(!item.isEmpty()) {
            float scale=Math.max(1F,Math.min(5F,Math.min(sw/24F,sh/30F)));
            drawItem(g,item,sx+sw/2,sy+sh/2+3,scale);
            g.drawCenteredString(font,font.plainSubstrByWidth(item.getHoverName().getString(),sw-8),sx+sw/2,sy+sh-15,0xFFFFFFFF);
        }
        int cx=sx+sw+5, cw=Math.max(4,controls-10);
        g.fill(cx,sy+5,cx+cw,sy+5+cw,accent);
        g.fill(cx+2,sy+7,cx+cw-2,sy+3+cw,0xFF111722);
        for(int n=0;n<4;n++) g.fill(cx,sy+sh/2+n*5,cx+cw,sy+sh/2+n*5+2,0xFF03070C);
    }
    private void drawItem(GuiGraphics g, ItemStack item, int x, int y, float scale) {
        g.pose().pushPose();
        g.pose().translate(x-8*scale,y-8*scale,10);
        g.pose().scale(scale,scale,1);
        g.renderItem(item,0,0);
        g.pose().popPose();
    }
    private void drawReward(GuiGraphics g, Rect r, GachaResult result, boolean small) {
        int accent=color(result.grade());
        g.fill(r.x,r.y,r.x+r.w,r.y+r.h,accent);
        g.fill(r.x+2,r.y+2,r.x+r.w-2,r.y+r.h-2,0xFF151D2B);
        g.fill(r.x+3,r.y+3,r.x+r.w-3,r.y+smallHeader(small),0x443D5168);
        g.drawString(font,result.grade().name(),r.x+7,r.y+6,accent,false);
        float scale=small?Math.min(2F,r.h/35F):Math.min(6F,r.h/25F);
        drawItem(g,result.reward(),r.x+r.w/2,r.y+r.h/2-3,scale);
        if(result.reward().getCount()>1) g.drawString(font,"×"+result.reward().getCount(),r.x+r.w-22,r.y+6,0xFFFFFFFF,false);
        var lines=font.split(result.reward().getHoverName(),r.w-10);
        int n=Math.min(2,lines.size());
        for(int i=0;i<n;i++) g.drawCenteredString(font,lines.get(i),r.x+r.w/2,r.y+r.h-7-(n-i)*10,0xFFFFFFFF);
        if(result.voucher()&&!small) g.drawCenteredString(font,Component.translatable("gui.zzzplushies.voucher"),r.x+r.w/2,r.y+20,0xFFFFD44F);
    }
    private int smallHeader(boolean small) { return small?18:22; }
}
