
package bindarkkk.litematicaassistant;

import net.minecraft.core.BlockPos;
import java.util.*;

public class ProgressTracker {

    private final Set<BlockPos> completed = new HashSet<>();
    private final Map<String, Integer> completedMaterials =
            new HashMap<>();

    private final Map<BlockPos, String> expectedBlocks =
            new HashMap<>();

    private int totalBlocks;

    public void load(LitematicaBridge.ScanResult result) {
        reset();

        if (result == null || !result.success()) return;

        for (var block : result.blocks()) {
            if (block == null || block.pos() == null) continue;
            expectedBlocks.put(block.pos(), block.blockId());
        }

        totalBlocks = expectedBlocks.size();
    }

    public boolean markCompleted(BlockPos position, String blockId) {
        if (position == null || blockId == null) return false;

        String expected = expectedBlocks.get(position);

        if (expected == null || !expected.equals(blockId)) {
            return false;
        }

        if (!completed.add(position)) return false;

        completedMaterials.merge(blockId, 1, Integer::sum);
        return true;
    }

    public boolean undo(BlockPos position, String blockId) {
        if (position == null || blockId == null) return false;

        if (!completed.remove(position)) return false;

        completedMaterials.computeIfPresent(
                blockId,
                (key, value) -> value <= 1 ? null : value - 1
        );

        return true;
    }

    public int getCompleted() {
        return completed.size();
    }

    public int getRemaining() {
        return Math.max(0, totalBlocks - getCompleted());
    }

    public int getTotal() {
        return totalBlocks;
    }

    public float getProgress() {
        if (totalBlocks == 0) return 0;

        return Math.min(
                100f,
                (float) getCompleted() / totalBlocks * 100f
        );
    }

    public int getCompletedMaterial(String blockId) {
        return completedMaterials.getOrDefault(blockId, 0);
    }

    public boolean isCompleted(BlockPos position) {
        return completed.contains(position);
    }

    public boolean isFinished() {
        return totalBlocks > 0 && getCompleted() >= totalBlocks;
    }

    public Set<BlockPos> getCompletedPositions() {
        return Collections.unmodifiableSet(completed);
    }

    public void reset() {
        completed.clear();
        completedMaterials.clear();
        expectedBlocks.clear();
        totalBlocks = 0;
    }
}
