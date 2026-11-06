
package bindarkkk.litematicaassistant;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

public class LitematicaAssistantClient implements ClientModInitializer {

    private static final KeyMapping.Category CATEGORY =
        KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath(
                "litematicaassistant", "controls"
            )
        );

    private static final KeyMapping OPEN_GUI =
        KeyBindingHelper.registerKeyBinding(
            new KeyMapping(
                "key.litematicaassistant.open",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_H,
                CATEGORY
            )
        );

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (OPEN_GUI.consumeClick()) {
                if (client.screen == null) {
                    client.setScreen(new LiquidGlassScreen());
                }
            }
        });
    }
}
