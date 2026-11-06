
package bindarkkk.litematicaassistant;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class LiquidGlassScreen extends Screen {

    private boolean assistantEnabled = false;

    public LiquidGlassScreen() {
        super(Component.literal("Litematica Assistant"));
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        addRenderableWidget(
            Button.builder(
                Component.literal(
                    assistantEnabled ? "Assistant: ON" : "Assistant: OFF"
                ),
                button -> {
                    assistantEnabled = !assistantEnabled;
                    button.setMessage(Component.literal(
                        assistantEnabled ? "Assistant: ON" : "Assistant: OFF"
                    ));
                }
            )
            .bounds(centerX - 85, centerY + 65, 170, 22)
            .build()
        );

        addRenderableWidget(
            Button.builder(
                Component.literal("Close"),
                button -> this.onClose()
            )
            .bounds(centerX - 85, centerY + 95, 170, 22)
            .build()
        );
    }

    @Override
    public void render(
        GuiGraphics graphics,
        int mouseX,
        int mouseY,
        float delta
    ) {
        this.renderBackground(graphics, mouseX, mouseY, delta);

        int cx = this.width / 2;
        int cy = this.height / 2;

        // Dark glass background
        graphics.fill(
            cx - 155, cy - 115,
            cx + 155, cy + 130,
            0xDD101522
        );

        // Glass border
        graphics.fill(
            cx - 155, cy - 115,
            cx + 155, cy - 113,
            0xFF55DDF5
        );

        graphics.fill(
            cx - 155, cy - 115,
            cx - 153, cy + 130,
            0xAA55DDF5
        );

        graphics.fill(
            cx + 153, cy - 115,
            cx + 155, cy + 130,
            0xAA55DDF5
        );

        // Header
        graphics.drawString(
            this.font,
            "LITEMATICA ASSISTANT",
            cx - 135,
            cy - 95,
            0xFF75E8FF
        );

        graphics.drawString(
            this.font,
            "LIQUID GLASS EDITION",
            cx - 135,
            cy - 78,
            0xFFAAAAAA
        );

        // Connection status
        boolean connected = LitematicaBridge.isAvailable();

        graphics.fill(
            cx - 135, cy - 48,
            cx + 135, cy - 10,
            0x88304055
        );

        graphics.drawString(
            this.font,
            "Litematica: " + (connected ? "CONNECTED" : "NOT FOUND"),
            cx - 125,
            cy - 35,
            connected ? 0xFF55FFAA : 0xFFFF7777
        );

        // Selected schematic
        String schematic = LitematicaBridge.getSelectedSchematicName();

        graphics.drawString(
            this.font,
            "ACTIVE SCHEMATIC",
            cx - 125,
            cy + 3,
            0xFF75E8FF
        );

        graphics.drawString(
            this.font,
            schematic == null ? "No placement selected" : schematic,
            cx - 125,
            cy + 20,
            0xFFFFFFFF
        );

        graphics.drawString(
            this.font,
            "Build Progress: Coming soon",
            cx - 125,
            cy + 43,
            0xFFBBBBBB
        );

        super.render(graphics, mouseX, mouseY, delta);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
