
package bindarkkk.litematicaassistant;

import net.minecraft.core.BlockPos;
import java.util.*;

public class MaterialTracker {

    public record MaterialInfo(
            String blockId,
            int required,
            int placed,
            int remaining
    ) {}

    private final Map<String, Integer> required = new TreeMap<>();
    private final Map<String, Integer> placed = new TreeMap<>();

    private final Map<BlockPos, String> completedPositions =
            new HashMap<>();

    public void load(LitematicaBridge.ScanResult result) {
        clear();

        if (result == null || !result.success()) return;

        for (var block : result.blocks()) {
            if (block == null || block.blockId() == null) continue;

            required.merge(block.blockId(), 1, Integer::sum);
        }
    }

    public boolean markPlaced(BlockPos position, String blockId) {
        if (position == null || blockId == null) return false;

        if (completedPositions.containsKey(position)) return false;

        int current = placed.getOrDefault(blockId, 0);
        int total = required.getOrDefault(blockId, 0);

        if (current >= total) return false;

        completedPositions.put(position, blockId);
        placed.put(blockId, current + 1);

        return true;
    }

    public void markPlaced(String blockId) {
        if (blockId == null) return;

        int current = placed.getOrDefault(blockId, 0);
        int total = required.getOrDefault(blockId, 0);

        if (current < total) {
            placed.put(blockId, current + 1);
        }
    }

    public boolean undoPlaced(BlockPos position) {
        String blockId = completedPositions.remove(position);

        if (blockId == null) return false;

        decrement(blockId);
        return true;
    }

    public void undoPlaced(String blockId) {
        if (blockId == null) return;
        decrement(blockId);
    }

    private void decrement(String blockId) {
        int current = placed.getOrDefault(blockId, 0);

        if (current <= 1) {
            placed.remove(blockId);
        } else {
            placed.put(blockId, current - 1);
        }
    }

    public int getRequired(String blockId) {
        return required.getOrDefault(blockId, 0);
    }

    public int getPlaced(String blockId) {
        return placed.getOrDefault(blockId, 0);
    }

    public int getRemaining(String blockId) {
        return Math.max(0, getRequired(blockId) - getPlaced(blockId));
    }

    public int getTotalRequired() {
        return required.values().stream()
                .mapToInt(Integer::intValue).sum();
    }

    public int getTotalPlaced() {
        return placed.values().stream()
                .mapToInt(Integer::intValue).sum();
    }

    public int getTotalRemaining() {
        return Math.max(0, getTotalRequired() - getTotalPlaced());
    }

    public float getProgress() {
        int total = getTotalRequired();
        return total == 0 ? 0 : (float) getTotalPlaced() / total * 100f;
    }

    public MaterialInfo getInfo(String blockId) {
        return new MaterialInfo(
                blockId,
                getRequired(blockId),
                getPlaced(blockId),
                getRemaining(blockId)
        );
    }

    public Map<String, Integer> getAllMaterials() {
        return Collections.unmodifiableMap(required);
    }

    public void reset() {
        placed.clear();
        completedPositions.clear();
    }

    public void clear() {
        required.clear();
        placed.clear();
        completedPositions.clear();
    }
}
