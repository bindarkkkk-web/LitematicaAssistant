
package bindarkkk.litematicaassistant;

import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BuildRenderer {

    public enum MarkerType {
        NEXT,
        COMPLETED,
        REMAINING,
        CRAFTER
    }

    public record RenderMarker(
            BlockPos position,
            MarkerType type,
            String blockId
    ) {}

    private boolean enabled = true;
    private boolean showCompleted = false;
    private boolean showRemaining = true;

    public List<RenderMarker> prepare(
            BuildAssistant assistant
    ) {
        if (!enabled || assistant == null) {
            return List.of();
        }

        List<RenderMarker> markers = new ArrayList<>();

        var guide = assistant.getGuide();
        var next = guide.getNext();

        if (next != null) {
            markers.add(new RenderMarker(
                    next.position(),
                    MarkerType.NEXT,
                    next.blockId()
            ));
        }

        for (var entry : guide.getEntries()) {
            if (entry.completed()) {
                if (showCompleted) {
                    markers.add(new RenderMarker(
                            entry.position(),
                            MarkerType.COMPLETED,
                            entry.blockId()
                    ));
                }
            } else if (showRemaining) {
                markers.add(new RenderMarker(
                        entry.position(),
                        entry.crafter()
                                ? MarkerType.CRAFTER
                                : MarkerType.REMAINING,
                        entry.blockId()
                ));
            }
        }

        return Collections.unmodifiableList(markers);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setShowCompleted(boolean show) {
        showCompleted = show;
    }

    public void setShowRemaining(boolean show) {
        showRemaining = show;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isShowingCompleted() {
        return showCompleted;
    }

    public boolean isShowingRemaining() {
        return showRemaining;
    }
}
