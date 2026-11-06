package bindarkkk.litematicaassistant;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class LiquidGlassScreen extends Screen {

    private static final int BG = 0xE90B101A;
    private static final int PANEL = 0xE9161E2B;
    private static final int CARD = 0xE91D2736;
    private static final int ACCENT = 0xFF65D6C4;
    private static final int WHITE = 0xFFF0F4FA;
    private static final int MUTED = 0xFF8795A8;
    private static final int RED = 0xFFFF7777;

    private static final String[] PAGES = {
        "Overview", "Modules", "Schematic", "Settings"
    };

    private static final String[] MODULE_NAMES = {
        "Build Assistant",
        "Material Tracker",
        "Placement Guide",
        "Crafter Assistant",
        "Recipe Detection",
        "Auto Ingredient"
    };

    private static final String[] MODULE_DESC = {
        "Building workspace",
        "Track required materials",
        "Block placement information",
        "Crafter layout assistance",
        "Detect crafter recipes",
        "Ingredient preparation"
    };

    private static final boolean[] modules = {
        true, true, true, false, false, false
    };

    private static boolean assistantEnabled = true;

    private int page = 0;
    private final float[] switchAnimation = new float[6];

    private boolean compactMode = false;
    private boolean notifications = true;

    private LitematicaBridge.ScanResult scanResult;
    private String scanMessage = "Ready to scan";
    private boolean scanning = false;

    public LiquidGlassScreen() {
        super(Component.literal("Litematica Assistant"));

        for (int i = 0; i < switchAnimation.length; i++) {
            switchAnimation[i] = modules[i] ? 1.0f : 0.0f;
        }
    }

    public static void open() {
        Minecraft.getInstance().setScreen(new LiquidGlassScreen());
    }

    public static boolean isAssistantEnabled() {
        return assistantEnabled;
    }

    public static boolean isModuleEnabled(int index) {
        return index >= 0
            && index < modules.length
            && modules[index]
            && assistantEnabled;
    }

    private int countModules() {
        int count = 0;

        for (boolean enabled : modules) {
            if (enabled) count++;
        }

        return count;
    }

    private String getSchematicName() {
        try {
            String name = LitematicaBridge.getSelectedSchematicName();

            if (name != null && !name.isBlank()) {
                return name;
            }
        } catch (Exception ignored) {
        }

        return "No schematic";
    }

    private void scanSchematic() {
        if (scanning) return;

        scanning = true;
        scanMessage = "Scanning...";

        try {
            scanResult = LitematicaBridge.scanSelectedSchematic();

            if (scanResult.success()) {
                scanMessage = "Scan completed successfully";
            } else {
                scanMessage = scanResult.message();
            }
        } catch (Exception e) {
            scanResult = null;
            scanMessage = "Scan error: " + e.getMessage();
        } finally {
            scanning = false;
        }
    }

    @Override
    public void render(
        GuiGraphics g,
        int mouseX,
        int mouseY,
        float partialTick
    ) {
        int panelWidth = Math.min(570, width - 24);
        int panelHeight = Math.min(350, height - 24);

        int x = (width - panelWidth) / 2;
        int y = (height - panelHeight) / 2;

        g.fill(0, 0, width, height, 0x99000000);

        drawRound(g, x, y, panelWidth, panelHeight, BG);
        drawRound(g, x + 8, y + 8, 132, panelHeight - 16, PANEL);

        // Branding
        g.drawString(font, "LITEMATICA", x + 20, y + 20, ACCENT, false);
        g.drawString(font, "ASSISTANT", x + 20, y + 32, WHITE, false);

        g.fill(x + 20, y + 49, x + 126, y + 50, 0x554C657A);

        // Navigation
        for (int i = 0; i < PAGES.length; i++) {
            int itemY = y + 67 + i * 34;
            boolean selected = page == i;

            boolean hover =
                mouseX >= x + 14 &&
                mouseX <= x + 132 &&
                mouseY >= itemY - 5 &&
                mouseY <= itemY + 22;

            if (selected) {
                drawRound(g, x + 14, itemY - 5, 118, 27, 0xFF263D4A);
                g.fill(x + 14, itemY, x + 17, itemY + 17, ACCENT);
            } else if (hover) {
                drawRound(g, x + 14, itemY - 5, 118, 27, 0x553A4A5C);
            }

            g.drawString(
                font,
                PAGES[i],
                x + 25,
                itemY + 3,
                selected ? ACCENT : WHITE,
                false
            );
        }

        g.drawString(
            font,
            "v2.0.0",
            x + 20,
            y + panelHeight - 26,
            MUTED,
            false
        );

        int cx = x + 154;
        int cy = y + 20;
        int cw = panelWidth - 174;

        switch (page) {
            case 0 -> renderOverview(g, cx, cy, cw);
            case 1 -> renderModules(g, cx, cy, cw);
            case 2 -> renderSchematic(g, cx, cy, cw);
            case 3 -> renderSettings(g, cx, cy, cw);
        }

        g.drawString(
            font,
            "LIQUID GLASS / FABRIC",
            cx,
            y + panelHeight - 19,
            MUTED,
            false
        );

        super.render(g, mouseX, mouseY, partialTick);
    }

    private void renderOverview(GuiGraphics g, int x, int y, int w) {
        drawTitle(g, "Overview", "Your building workspace");

        drawRound(g, x, y + 40, w, 78, CARD);

        g.drawString(font, "Assistant Status", x + 13, y + 52, WHITE, false);
        g.drawString(
            font,
            assistantEnabled ? "ENABLED" : "DISABLED",
            x + 13,
            y + 72,
            assistantEnabled ? ACCENT : RED,
            false
        );

        drawSwitch(g, x + w - 48, y + 65, assistantEnabled ? 1 : 0);

        int half = (w - 8) / 2;

        drawRound(g, x, y + 128, half, 66, CARD);
        drawRound(g, x + half + 8, y + 128, half, 66, CARD);

        g.drawString(font, "Active Modules", x + 11, y + 140, MUTED, false);
        g.drawString(font, countModules() + " / 6", x + 11, y + 162, WHITE, false);

        g.drawString(font, "Schematic", x + half + 19, y + 140, MUTED, false);
        g.drawString(
            font,
            getSchematicName(),
            x + half + 19,
            y + 162,
            ACCENT,
            false
        );

        drawRound(g, x, y + 204, w, 58, PANEL);

        g.drawString(font, "QUICK INFORMATION", x + 12, y + 215, ACCENT, false);
        g.drawString(
            font,
            "Open Schematic to scan your build.",
            x + 12,
            y + 234,
            MUTED,
            false
        );
    }

    private void renderModules(GuiGraphics g, int x, int y, int w) {
        drawTitle(g, "Modules", "Manage assistant features");

        for (int i = 0; i < MODULE_NAMES.length; i++) {
            int cardY = y + 40 + i * 39;

            drawRound(g, x, cardY, w, 34, CARD);

            g.drawString(font, MODULE_NAMES[i], x + 10, cardY + 5, WHITE, false);
            g.drawString(font, MODULE_DESC[i], x + 10, cardY + 19, MUTED, false);

            drawSwitch(g, x + w - 39, cardY + 9, switchAnimation[i]);
        }
    }

    private void renderSchematic(GuiGraphics g, int x, int y, int w) {
        drawTitle(g, "Schematic", "Schematic scanner and information");

        drawRound(g, x, y + 40, w, 49, CARD);

        g.drawString(font, "SELECTED FILE", x + 12, y + 49, MUTED, false);
        g.drawString(font, getSchematicName(), x + 12, y + 68, WHITE, false);

        drawRound(g, x, y + 96, w, 58, CARD);

        int total = scanResult != null && scanResult.success()
            ? scanResult.totalBlocks() : 0;

        int crafters = scanResult != null && scanResult.success()
            ? scanResult.crafterCount() : 0;

        g.drawString(font, "SCANNED BLOCKS", x + 12, y + 105, MUTED, false);
        g.drawString(font, String.valueOf(total), x + 12, y + 128, ACCENT, false);

        g.drawString(font, "CRAFTERS", x + w / 2 + 10, y + 105, MUTED, false);
        g.drawString(font, String.valueOf(crafters), x + w / 2 + 10, y + 128, ACCENT, false);

        drawRound(g, x, y + 161, w, 48, PANEL);

        g.drawString(font, "SCAN STATUS", x + 12, y + 169, ACCENT, false);
        g.drawString(
            font,
            scanMessage,
            x + 12,
            y + 188,
            scanResult != null && !scanResult.success() ? RED : WHITE,
            false
        );

        drawRound(g, x, y + 216, w, 34, 0xFF245A58);

        String buttonText = scanning ? "SCANNING..." : "SCAN SCHEMATIC";

        int textWidth = font.width(buttonText);

        g.drawString(
            font,
            buttonText,
            x + (w - textWidth) / 2,
            y + 228,
            WHITE,
            false
        );

        g.drawString(
            font,
            "Read-only scan. Block placement is not active yet.",
            x,
            y + 261,
            MUTED,
            false
        );
    }

    private void renderSettings(GuiGraphics g, int x, int y, int w) {
        drawTitle(g, "Settings", "Customize your interface");

        settingCard(g, x, y + 44, w, "Compact Mode",
            "Reduce interface spacing", compactMode);

        settingCard(g, x, y + 88, w, "Notifications",
            "Show assistant notifications", notifications);
    }

    private void settingCard(
        GuiGraphics g,
        int x,
        int y,
        int w,
        String title,
        String description,
        boolean enabled
    ) {
        drawRound(g, x, y, w, 38, CARD);

        g.drawString(font, title, x + 10, y + 5, WHITE, false);
        g.drawString(font, description, x + 10, y + 19, MUTED, false);

        drawSwitch(g, x + w - 39, y + 10, enabled ? 1 : 0);
    }

    private void drawTitle(GuiGraphics g, String title, String subtitle) {
        int panelWidth = Math.min(570, width - 24);
        int x = (width - panelWidth) / 2 + 154;
        int y = (height - Math.min(350, height - 24)) / 2 + 20;

        g.drawString(font, title, x, y, WHITE, false);
        g.drawString(font, subtitle, x, y + 14, MUTED, false);
    }

    private void drawSwitch(GuiGraphics g, int x, int y, float progress) {
        progress = Math.max(0.0f, Math.min(1.0f, progress));

        drawRound(
            g, x, y, 30, 14,
            progress > 0.5f ? 0xFF286B65 : 0xFF394555
        );

        int knobX = x + 2 + Math.round(progress * 14);
        drawRound(g, knobX, y + 2, 10, 10, WHITE);
    }

    private void drawRound(
        GuiGraphics g,
        int x,
        int y,
        int w,
        int h,
        int color
    ) {
        if (w <= 0 || h <= 0) return;

        if (w <= 6 || h <= 6) {
            g.fill(x, y, x + w, y + h, color);
            return;
        }

        g.fill(x + 3, y, x + w - 3, y + h, color);
        g.fill(x, y + 3, x + w, y + h - 3, color);
    }

    @Override
    public void tick() {
        super.tick();

        for (int i = 0; i < modules.length; i++) {
            float target = modules[i] ? 1.0f : 0.0f;

            switchAnimation[i] +=
                (target - switchAnimation[i]) * 0.28f;
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() != 0) {
            return super.mouseClicked(event, doubleClick);
        }

        double mx = event.x();
        double my = event.y();

        int panelWidth = Math.min(570, width - 24);
        int panelHeight = Math.min(350, height - 24);

        int x = (width - panelWidth) / 2;
        int y = (height - panelHeight) / 2;

        for (int i = 0; i < PAGES.length; i++) {
            int itemY = y + 67 + i * 34;

            if (inside(mx, my, x + 14, itemY - 5, 118, 27)) {
                page = i;
                return true;
            }
        }

        int cx = x + 154;
        int cy = y + 20;
        int cw = panelWidth - 174;

        if (page == 0) {
            if (inside(mx, my, cx, cy + 40, cw, 78)) {
                assistantEnabled = !assistantEnabled;
                return true;
            }
        }

        if (page == 1) {
            for (int i = 0; i < MODULE_NAMES.length; i++) {
                int cardY = cy + 40 + i * 39;

                if (inside(mx, my, cx, cardY, cw, 34)) {
                    modules[i] = !modules[i];
                    return true;
                }
            }
        }

        if (page == 2) {
            if (inside(mx, my, cx, cy + 216, cw, 34)) {
                scanSchematic();
                return true;
            }
        }

        if (page == 3) {
            if (inside(mx, my, cx, cy + 44, cw, 38)) {
                compactMode = !compactMode;
                return true;
            }

            if (inside(mx, my, cx, cy + 88, cw, 38)) {
                notifications = !notifications;
                return true;
            }
        }

        return super.mouseClicked(event, doubleClick);
    }

    private boolean inside(
        double mx, double my,
        int x, int y, int w, int h
    ) {
        return mx >= x && mx <= x + w
            && my >= y && my <= y + h;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
                  }
