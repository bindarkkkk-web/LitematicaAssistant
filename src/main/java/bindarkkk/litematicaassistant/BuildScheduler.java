
package bindarkkk.litematicaassistant;

import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BuildScheduler {

    public enum Priority {
        BOTTOM_UP,
        TOP_DOWN,
        NEAREST,
        CRAFTER_FIRST
    }

    private Priority priority = Priority.BOTTOM_UP;

    public void setPriority(Priority priority) {
        if (priority != null) {
            this.priority = priority;
        }
    }

    public Priority getPriority() {
        return priority;
    }

    public List<LitematicaBridge.SchematicBlock> schedule(
            List<LitematicaBridge.SchematicBlock> blocks,
            BlockPos reference
    ) {
        if (blocks == null) return List.of();

        List<LitematicaBridge.SchematicBlock> result =
                new ArrayList<>(blocks);

        BlockPos origin = reference == null
                ? BlockPos.ZERO : reference;

        Comparator<LitematicaBridge.SchematicBlock> comparator;

        switch (priority) {
            case TOP_DOWN -> comparator =
                    Comparator.comparingInt(
                            (LitematicaBridge.SchematicBlock b) ->
                                    b.pos().getY()
                    ).reversed();

            case NEAREST -> comparator =
                    Comparator.comparingDouble(
                            b -> b.pos().distSqr(origin)
                    );

            case CRAFTER_FIRST -> comparator =
                    Comparator.comparing(
                            LitematicaBridge.SchematicBlock::crafter
                    ).reversed();

            default -> comparator =
                    Comparator.comparingInt(
                            (LitematicaBridge.SchematicBlock b) ->
                                    b.pos().getY()
                    );
        }

        result.removeIf(b -> b == null || b.pos() == null);
        result.sort(comparator);

        return List.copyOf(result);
    }
}
