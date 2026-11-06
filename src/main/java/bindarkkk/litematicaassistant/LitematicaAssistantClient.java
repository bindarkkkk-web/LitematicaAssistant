package bindarkkk.litematicaassistant;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;

public class LitematicaAssistantClient implements ClientModInitializer {

    private static boolean enabled = false;

    @Override
    public void onInitializeClient() {
        KeyMapping toggleKey = KeyBindingHelper.registerKeyBinding(
            new KeyMapping(
                "key.litematicaassistant.toggle",
                KeyMapping.Type.KEYSYM,
                72,
                KeyMapping.Category.MISC
            )
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.consumeClick()) {
                enabled = !enabled;

                if (client.player != null) {
                    client.player.displayClientMessage(
                        Component.literal("Assistant: " + (enabled ? "ON" : "OFF")),
                        true
                    );
                }
            }
        });
    }
}
