package dev.yuri.zzzplushies.client;

import dev.yuri.zzzplushies.PlushCatalog;
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
    private Button single, ten;
    public GachaScreen(GachaMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 240;
        imageHeight = 166;
    }
    @Override protected void init() {
        super.init();
        addRenderableWidget(Button.builder(Component.literal("◀"), b -> press(1))
                .bounds(leftPos + 20, topPos + 46, 25, 20).build());
        addRenderableWidget(Button.builder(Component.literal("▶"), b -> press(2))
                .bounds(leftPos + 195, topPos + 46, 25, 20).build());
        single = addRenderableWidget(Button.builder(Component.translatable("button.zzzplushies.spin"), b -> press(0))
                .bounds(leftPos + 12, topPos + 126, 104, 24).build());
        ten = addRenderableWidget(Button.builder(Component.translatable("button.zzzplushies.spin_ten"), b -> press(3))
                .bounds(leftPos + 124, topPos + 126, 104, 24).build());
        updateButtons();
    }
    @Override protected void containerTick() { super.containerTick(); updateButtons(); }
    private void updateButtons() {
        single.active = !GachaMusic.isRevealing() && menu.tapes() >= 1;
        ten.active = !GachaMusic.isRevealing() && menu.tapes() >= 10;
    }
    private void press(int id) {
        if (!GachaMusic.isRevealing() && minecraft != null && minecraft.gameMode != null)
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, id);
    }
    @Override protected void renderBg(GuiGraphics g, float partialTick, int mouseX, int mouseY) {
        g.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFF111725);
        g.fill(leftPos + 3, topPos + 3, leftPos + imageWidth - 3, topPos + imageHeight - 3, 0xFF27334B);
        g.fill(leftPos + 50, topPos + 30, leftPos + 190, topPos + 84, 0xFF111725);
        String id = PlushCatalog.IDS[Math.floorMod(menu.selected(), PlushCatalog.IDS.length)];
        ItemStack plush = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("zzzplushies", id)));
        g.renderItem(plush, leftPos + 112, topPos + 37);
        g.drawCenteredString(font, plush.getHoverName(), leftPos + 120, topPos + 67, 0xFFF5E7CE);
    }
    @Override protected void renderLabels(GuiGraphics g, int x, int y) {
        g.drawCenteredString(font, title, imageWidth / 2, 12, 0xFFF5E7CE);
        g.drawString(font, Component.translatable("gui.zzzplushies.tapes", menu.tapes()), 12, 91, 0xFFFFFF, false);
        g.drawString(font, Component.translatable("gui.zzzplushies.pity", menu.pity(), GachaOdds.HARD_PITY), 123, 91, 0xFFFFFF, false);
        g.drawString(font, Component.translatable("gui.zzzplushies.losses", menu.losses(), 2), 12, 102, 0xF5C6C6, false);
        g.drawString(font, Component.translatable("gui.zzzplushies.chance",
                String.format(Locale.ROOT, "%.2f", 100 * GachaOdds.sChance(menu.pity()))), 12, 113, 0xFFD44F, false);
    }
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
