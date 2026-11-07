
package bindarkkk.litematicaassistant;

import net.minecraft.core.BlockPos;
import java.util.*;

public class PlacementGuide {

    public enum Order {
        BOTTOM_UP,
        TOP_DOWN,
        NEAREST_FIRST
    }

    public record GuideEntry(
            int index,
            BlockPos position,
            String blockId,
            boolean crafter,
            boolean completed
    ) {}

    private final List<LitematicaBridge.SchematicBlock> blocks =
            new ArrayList<>();

    private final Set<BlockPos> completed = new HashSet<>();

    private Order order = Order.BOTTOM_UP;
    private BlockPos playerPosition = BlockPos.ZERO;
    private PlacementTransformer transformer;
    private int currentIndex;

    public void load(LitematicaBridge.ScanResult result) {
        blocks.clear();
        completed.clear();
        currentIndex = 0;

        if (result == null || !result.success()) return;

        blocks.addAll(result.blocks());
        sortBlocks();
    }

    public void setTransformer(PlacementTransformer transformer) {
        this.transformer = transformer;
        sortBlocks();
    }

    public void setOrder(Order order) {
        if (order == null) return;
        this.order = order;
        sortBlocks();
    }

    public void setPlayerPosition(BlockPos position) {
        if (position != null) {
            playerPosition = position;
            if (order == Order.NEAREST_FIRST) sortBlocks();
        }
    }

    private BlockPos worldPosition(BlockPos position) {
        return transformer == null
                ? position
                : transformer.transform(position);
    }

    private void sortBlocks() {
        switch (order) {
            case BOTTOM_UP -> blocks.sort(
                    Comparator.comparingInt(
                            (LitematicaBridge.SchematicBlock b) ->
                                    b.pos().getY()
                    ).thenComparingInt(b -> b.pos().getZ())
                     .thenComparingInt(b -> b.pos().getX())
            );

            case TOP_DOWN -> blocks.sort(
                    Comparator.comparingInt(
                            (LitematicaBridge.SchematicBlock b) ->
                                    b.pos().getY()
                    ).reversed()
                     .thenComparingInt(b -> b.pos().getZ())
                     .thenComparingInt(b -> b.pos().getX())
            );

            case NEAREST_FIRST -> blocks.sort(
                    Comparator.comparingDouble(
                            b -> worldPosition(b.pos())
                                    .distSqr(playerPosition)
                    )
            );
        }
    }

    public GuideEntry getNext() {
        for (int i = 0; i < blocks.size(); i++) {
            var block = blocks.get(i);

            if (!completed.contains(block.pos())) {
                currentIndex = i;

                return new GuideEntry(
                        i + 1,
                        block.pos(),
                        block.blockId(),
                        block.crafter(),
                        false
                );
            }
        }

        return null;
    }

    public void markCompleted(BlockPos position) {
        if (position == null) return;

        for (var block : blocks) {
            if (block.pos().equals(position)) {
                completed.add(position);
                return;
            }
        }
    }

    public void undoCompleted(BlockPos position) {
        if (position != null) completed.remove(position);
    }

    public List<GuideEntry> getEntries() {
        List<GuideEntry> entries = new ArrayList<>();

        for (int i = 0; i < blocks.size(); i++) {
            var block = blocks.get(i);

            entries.add(new GuideEntry(
                    i + 1,
                    block.pos(),
                    block.blockId(),
                    block.crafter(),
                    completed.contains(block.pos())
            ));
        }

        return Collections.unmodifiableList(entries);
    }

    public int getTotal() {
        return blocks.size();
    }

    public int getCompletedCount() {
        return completed.size();
    }

    public int getRemainingCount() {
        return Math.max(0, getTotal() - getCompletedCount());
    }

    public float getProgress() {
        return blocks.isEmpty() ? 0
                : (float) getCompletedCount() / blocks.size() * 100f;
    }

    public boolean isComplete() {
        return !blocks.isEmpty()
                && completed.size() >= blocks.size();
    }

    public void reset() {
        completed.clear();
        currentIndex = 0;
    }

    public void clear() {
        blocks.clear();
        completed.clear();
        currentIndex = 0;
    }
}
