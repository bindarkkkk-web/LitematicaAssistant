
package bindarkkk.litematicaassistant;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class LiquidGlassScreen extends Screen {

    private static final int BG = 0xE90B101A;
    private static final int PANEL = 0xE9161E2B;
    private static final int CARD = 0xE91D2736;
    private static final int CARD_HOVER = 0xF0263547;
    private static final int ACCENT = 0xFF65D6C4;
    private static final int WHITE = 0xFFF0F4FA;
    private static final int MUTED = 0xFF8795A8;

    private static final String[] PAGES = {
        "Overview",
        "Modules",
        "Schematic",
        "Settings"
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
        "Main building assistant interface",
        "Track required schematic materials",
        "Show block placement information",
        "Assist with crafter schematic layouts",
        "Detect crafter recipe patterns",
        "Assist with ingredient preparation"
    };

    private final boolean[] modules = {
        true, true, true, false, false, false
    };

    private int page = 0;
    private int hovered = -1;

    private float animation = 1.0f;
    private float[] switchAnimation = new float[6];

    private boolean assistantEnabled = true;
    private boolean compactMode = false;
    private boolean notifications = true;

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
        return true;
    }

    public static boolean isModuleEnabled(int index) {
        return false;
    }

    @Override
    public void render(
        GuiGraphics g,
        int mouseX,
        int mouseY,
        float partialTick
    ) {
        int width = this.width;
        int height = this.height;

        g.fill(0, 0, width, height, 0x99000000);

        int panelWidth = Math.min(570, width - 24);
        int panelHeight = Math.min(350, height - 24);

        int x = (width - panelWidth) / 2;
        int y = (height - panelHeight) / 2;

        drawRound(g, x, y, panelWidth, panelHeight, BG);

        // Sidebar
        drawRound(g, x + 8, y + 8, 132, panelHeight - 16, PANEL);

        // Header
        g.drawString(
            this.font,
            "LITEMATICA",
            x + 20,
            y + 20,
            ACCENT,
            false
        );

        g.drawString(
            this.font,
            "ASSISTANT",
            x + 20,
            y + 32,
            WHITE,
            false
        );

        g.fill(x + 20, y + 49, x + 126, y + 50, 0x554C657A);

        for (int i = 0; i < PAGES.length; i++) {
            int itemY = y + 67 + i * 34;
            boolean selected = page == i;
            boolean hover = mouseX >= x + 14
                && mouseX <= x + 134
                && mouseY >= itemY - 5
                && mouseY <= itemY + 22;

            if (selected) {
                drawRound(
                    g,
                    x + 14,
                    itemY - 5,
                    118,
                    27,
                    0xFF263D4A
                );

                g.fill(
                    x + 14,
                    itemY,
                    x + 17,
                    itemY + 17,
                    ACCENT
                );
            } else if (hover) {
                drawRound(
                    g,
                    x + 14,
                    itemY - 5,
                    118,
                    27,
                    0x553A4A5C
                );
            }

            g.drawString(
                this.font,
                PAGES[i],
                x + 25,
                itemY + 3,
                selected ? ACCENT : WHITE,
                false
            );
        }

        g.drawString(
            this.font,
            "v1.0.0",
            x + 20,
            y + panelHeight - 26,
            MUTED,
            false
        );

        // Main content
        int contentX = x + 154;
        int contentY = y + 20;
        int contentWidth = panelWidth - 174;

        if (page == 0) {
            renderOverview(g, contentX, contentY, contentWidth);
        } else if (page == 1) {
            renderModules(g, contentX, contentY, contentWidth);
        } else if (page == 2) {
            renderSchematic(g, contentX, contentY, contentWidth);
        } else {
            renderSettings(g, contentX, contentY, contentWidth);
        }

        // Footer
        g.drawString(
            this.font,
            "LIQUID GLASS  /  FABRIC",
            contentX,
            y + panelHeight - 19,
            MUTED,
            false
        );

        super.render(g, mouseX, mouseY, partialTick);
    }

    private void renderOverview(
        GuiGraphics g,
        int x,
        int y,
        int w
    ) {
        drawTitle(g, "Overview", "Your building workspace");

        drawRound(g, x, y + 40, w, 78, CARD);

        g.drawString(
            this.font,
            "Assistant Status",
            x + 13,
            y + 52,
            WHITE,
            false
        );

        g.drawString(
            this.font,
            assistantEnabled ? "ENABLED" : "DISABLED",
            x + 13,
            y + 72,
            assistantEnabled ? ACCENT : MUTED,
            false
        );

        drawSwitch(
            g,
            x + w - 48,
            y + 65,
            assistantEnabled ? 1 : 0
        );

        int half = (w - 8) / 2;

        drawRound(g, x, y + 128, half, 66, CARD);
        drawRound(g, x + half + 8, y + 128, half, 66, CARD);

        g.drawString(
            this.font,
            "Active Modules",
            x + 11,
            y + 140,
            MUTED,
            false
        );

        g.drawString(
            this.font,
            countModules() + " / 6",
            x + 11,
            y + 162,
            WHITE,
            false
        );

        g.drawString(
            this.font,
            "Schematic",
            x + half + 19,
            y + 140,
            MUTED,
            false
        );

        g.drawString(
            this.font,
            getSchematicName(),
            x + half + 19,
            y + 162,
            ACCENT,
            false
        );

        drawRound(g, x, y + 204, w, 58, PANEL);

        g.drawString(
            this.font,
            "QUICK INFORMATION",
            x + 12,
            y + 215,
            ACCENT,
            false
        );

        g.drawString(
            this.font,
            "Configure modules from the sidebar.",
            x + 12,
            y + 234,
            MUTED,
            false
        );
    }

    private void renderModules(
        GuiGraphics g,
        int x,
        int y,
        int w
    ) {
        drawTitle(g, "Modules", "Manage assistant features");

        for (int i = 0; i < MODULE_NAMES.length; i++) {
            int cardY = y + 40 + i * 39;

            drawRound(g, x, cardY, w, 34, CARD);

            g.drawString(
                this.font,
                MODULE_NAMES[i],
                x + 10,
                cardY + 5,
                WHITE,
                false
            );

            g.drawString(
                this.font,
                MODULE_DESC[i],
                x + 10,
                cardY + 19,
                MUTED,
                false
            );

            drawSwitch(
                g,
                x + w - 39,
                cardY + 9,
                switchAnimation[i]
            );
        }
    }

    private void renderSchematic(
        GuiGraphics g,
        int x,
        int y,
        int w
    ) {
        drawTitle(g, "Schematic", "Connected schematic information");

        drawRound(g, x, y + 42, w, 72, CARD);

        g.drawString(
            this.font,
            "SELECTED FILE",
            x + 12,
            y + 53,
            MUTED,
            false
        );

        g.drawString(
            this.font,
            getSchematicName(),
            x + 12,
            y + 73,
            WHITE,
            false
        );

        drawRound(g, x, y + 122, w, 91, CARD);

        g.drawString(
            this.font,
            "CRAFTER ASSISTANT",
            x + 12,
            y + 134,
            ACCENT,
            false
        );

        g.drawString(
            this.font,
            "Recipe Detection: " + status(4),
            x + 12,
            y + 155,
            WHITE,
            false
        );

        g.drawString(
            this.font,
            "Auto Ingredient: " + status(5),
            x + 12,
            y + 173,
            WHITE,
            false
        );

        g.drawString(
            this.font,
            "Requires schematic data support.",
            x + 12,
            y + 194,
            MUTED,
            false
        );

        drawRound(g, x, y + 221, w, 39, PANEL);

        g.drawString(
            this.font,
            "Schematic bridge connected when available.",
            x + 10,
            y + 235,
            MUTED,
            false
        );
    }

    private void renderSettings(
        GuiGraphics g,
        int x,
        int y,
        int w
    ) {
        drawTitle(g, "Settings", "Customize your interface");

        settingCard(
            g,
            x,
            y + 44,
            w,
            "Compact Mode",
            "Reduce interface spacing",
            compactMode
        );

        settingCard(
            g,
            x,
            y + 88,
            w,
            "Notifications",
            "Show assistant notifications",
            notifications
        );
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

        g.drawString(
            this.font,
            title,
            x + 10,
            y + 5,
            WHITE,
            false
        );

        g.drawString(
            this.font,
            description,
            x + 10,
            y + 19,
            MUTED,
            false
        );

        drawSwitch(
            g,
            x + w - 39,
            y + 10,
            enabled ? 1 : 0
        );
    }

    private void drawTitle(
        GuiGraphics g,
        String title,
        String subtitle
    ) {
        g.drawString(
            this.font,
            title,
            this.width / 2 - 110,
            this.height / 2 - 140,
            WHITE,
            false
        );

        g.drawString(
            this.font,
            subtitle,
            this.width / 2 - 110,
            this.height / 2 - 126,
            MUTED,
            false
        );
    }

    private void drawSwitch(
        GuiGraphics g,
        int x,
        int y,
        float progress
    ) {
        int background = progress > 0.5f
            ? 0xFF286B65
            : 0xFF394555;

        drawRound(g, x, y, 30, 14, background);

        int knobX = x + 2 + Math.round(progress * 14);

        drawRound(
            g,
            knobX,
            y + 2,
            10,
            10,
            WHITE
        );
    }

    private void drawRound(
        GuiGraphics g,
        int x,
        int y,
        int w,
        int h,
        int color
    ) {
        g.fill(x + 3, y, x + w - 3, y + h, color);
        g.fill(x, y + 3, x + w, y + h - 3, color);

        g.fill(x + 1, y + 2, x + 2, y + h - 2, color);
        g.fill(x + w - 2, y + 2, x + w - 1, y + h - 2, color);
    }

    private String getSchematicName() {
        try {
            if (LitematicaBridge.isAvailable()) {
                String name =
                    LitematicaBridge.getSelectedSchematicName();

                if (name != null && !name.isBlank()) {
                    return name;
                }
            }
        } catch (Exception ignored) {
        }

        return "No schematic";
    }

    private String status(int index) {
        return modules[index] ? "Enabled" : "Disabled";
    }

    private int countModules() {
        int count = 0;

        for (boolean module : modules) {
            if (module) count++;
        }

        return count;
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
    public boolean mouseClicked(
        double mouseX,
        double mouseY,
        int button
    ) {
        if (button != 0) return false;

        int panelWidth = Math.min(570, this.width - 24);
        int panelHeight = Math.min(350, this.height - 24);

        int x = (this.width - panelWidth) / 2;
        int y = (this.height - panelHeight) / 2;

        for (int i = 0; i < PAGES.length; i++) {
            int itemY = y + 67 + i * 34;

            if (inside(
                mouseX, mouseY,
                x + 14, itemY - 5, 118, 27
            )) {
                page = i;
                return true;
            }
        }

        int contentX = x + 154;
        int contentY = y + 20;
        int contentWidth = panelWidth - 174;

        if (page == 0) {
            if (inside(
                mouseX, mouseY,
                contentX, contentY + 40,
                contentWidth, 78
            )) {
                assistantEnabled = !assistantEnabled;
                return true;
            }
        }

        if (page == 1) {
            for (int i = 0; i < MODULE_NAMES.length; i++) {
                int cardY = contentY + 40 + i * 39;

                if (inside(
                    mouseX, mouseY,
                    contentX, cardY, contentWidth, 34
                )) {
                    modules[i] = !modules[i];
                    return true;
                }
            }
        }

        if (page == 3) {
            if (inside(
                mouseX, mouseY,
                contentX, contentY + 44,
                contentWidth, 38
            )) {
                compactMode = !compactMode;
                return true;
            }

            if (inside(
                mouseX, mouseY,
                contentX, contentY + 88,
                contentWidth, 38
            )) {
                notifications = !notifications;
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean inside(
        double mx,
        double my,
        int x,
        int y,
        int w,
        int h
    ) {
        return mx >= x && mx <= x + w
            && my >= y && my <= y + h;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
    }
