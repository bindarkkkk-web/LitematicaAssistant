
package bindarkkk.litematicaassistant;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class LiquidGlassScreen extends Screen {

    private boolean assistantEnabled = false;

    private long startTime;
    private long lastTime;

    private float entrance = 0f;
    private float toggleProgress = 0f;

    private static final int CYAN = 0xFF65E5FF;
    private static final int WHITE = 0xFFFFFFFF;
    private static final int MUTED = 0xFF9BAABD;

    public LiquidGlassScreen() {
        super(Component.literal("Litematica Assistant"));
    }

    @Override
    protected void init() {
        startTime = System.currentTimeMillis();
        lastTime = startTime;

        int cx = width / 2;
        int cy = height / 2;

        addRenderableWidget(
            Button.builder(
                Component.literal("Assistant: OFF"),
                button -> {
                    assistantEnabled = !assistantEnabled;
                    button.setMessage(Component.literal(
                        assistantEnabled
                            ? "Assistant: ON"
                            : "Assistant: OFF"
                    ));
                }
            )
            .bounds(cx - 85, cy + 76, 170, 24)
            .build()
        );

        addRenderableWidget(
            Button.builder(
                Component.literal("CLOSE"),
                button -> onClose()
            )
            .bounds(cx - 85, cy + 108, 170, 22)
            .build()
        );
    }

    private float smooth(float x) {
        x = Math.max(0f, Math.min(1f, x));
        return x * x * (3f - 2f * x);
    }

    private int alpha(int color, float opacity) {
        int a = Math.max(0, Math.min(255,
            (int)(opacity * 255)));

        return (a << 24) | (color & 0xFFFFFF);
    }

    @Override
    public void render(
        GuiGraphics g,
        int mouseX,
        int mouseY,
        float delta
    ) {
        long now = System.currentTimeMillis();

        float dt = Math.min(
            (now - lastTime) / 1000f,
            0.05f
        );

        lastTime = now;

        entrance += (1f - entrance)
            * Math.min(1f, dt * 9f);

        toggleProgress += (
            (assistantEnabled ? 1f : 0f)
            - toggleProgress
        ) * Math.min(1f, dt * 10f);

        float appear = smooth(entrance);

        // Background: no extra Minecraft blur call
        g.fill(
            0, 0, width, height,
            0x99060B16
        );

        int cx = width / 2;
        int cy = height / 2;

        int panelW = 310;
        int panelH = 260;

        int left = cx - panelW / 2;
        int top = cy - panelH / 2;

        // Glass shadow
        g.fill(
            left - 3, top + 4,
            left + panelW + 3, top + panelH + 5,
            alpha(0xFF000000, appear * 0.55f)
        );

        // Glass body
        g.fill(
            left, top,
            left + panelW, top + panelH,
            alpha(0xFF111C30, appear * 0.94f)
        );

        // Glass top reflection
        g.fill(
            left + 1, top + 1,
            left + panelW - 1, top + 3,
            alpha(CYAN, appear * 0.8f)
        );

        // Border
        g.fill(
            left, top,
            left + panelW, top + 1,
            alpha(CYAN, appear * 0.65f)
        );

        g.fill(
            left, top,
            left + 1, top + panelH,
            alpha(CYAN, appear * 0.35f)
        );

        g.fill(
            left + panelW - 1, top,
            left + panelW, top + panelH,
            alpha(CYAN, appear * 0.35f)
        );

        // Header
        g.drawString(
            font,
            "LITEMATICA",
            left + 20,
            top + 18,
            alpha(CYAN, appear)
        );

        g.drawString(
            font,
            "ASSISTANT",
            left + 20,
            top + 31,
            alpha(WHITE, appear)
        );

        g.drawString(
            font,
            "LIQUID GLASS EDITION",
            left + 20,
            top + 47,
            alpha(MUTED, appear)
        );

        // Divider
        g.fill(
            left + 20, top + 66,
            left + panelW - 20, top + 67,
            alpha(0xFF536780, appear * 0.65f)
        );

        // Connection card
        boolean connected = LitematicaBridge.isAvailable();

        g.fill(
            left + 18, top + 78,
            left + panelW - 18, top + 115,
            alpha(0xFF26374D, appear * 0.85f)
        );

        g.drawString(
            font,
            "LITEMATICA STATUS",
            left + 28,
            top + 84,
            alpha(MUTED, appear)
        );

        g.drawString(
            font,
            connected ? "CONNECTED" : "NOT FOUND",
            left + 28,
            top + 98,
            alpha(
                connected ? 0xFF55FFAA : 0xFFFF7777,
                appear
            )
        );

        // Schematic card
        String schematic =
            LitematicaBridge.getSelectedSchematicName();

        g.drawString(
            font,
            "ACTIVE SCHEMATIC",
            left + 20,
            top + 130,
            alpha(CYAN, appear)
        );

        g.drawString(
            font,
            schematic == null
                ? "No placement selected"
                : schematic,
            left + 20,
            top + 145,
            alpha(WHITE, appear)
        );

        // Assistant status
        g.drawString(
            font,
            "ASSISTANT STATUS",
            left + 20,
            top + 166,
            alpha(MUTED, appear)
        );

        int barX = left + 20;
        int barY = top + 181;
        int barW = panelW - 40;

        g.fill(
            barX, barY,
            barX + barW, barY + 5,
            alpha(0xFF34465D, appear)
        );

        int fillW = (int)(barW * toggleProgress);

        if (fillW > 0) {
            g.fill(
                barX, barY,
                barX + fillW, barY + 5,
                alpha(CYAN, appear)
            );
        }

        g.drawString(
            font,
            assistantEnabled ? "SYSTEM ENABLED" : "SYSTEM DISABLED",
            left + 20,
            top + 194,
            alpha(
                assistantEnabled ? 0xFF55FFAA : MUTED,
                appear
            )
        );

        g.drawString(
            font,
            "Smooth interface | Fabric",
            left + 20,
            top + 228,
            alpha(MUTED, appear * 0.8f)
        );

        // Buttons
        super.render(g, mouseX, mouseY, delta);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
