
package bindarkkk.litematicaassistant;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class MaterialTracker {

    public record MaterialInfo(
            String blockId,
            int required,
            int placed,
            int remaining
    ) {}

    private final Map<String, Integer> required = new TreeMap<>();
    private final Map<String, Integer> placed = new TreeMap<>();

    // Memuat kebutuhan material dari hasil scan schematic
    public void load(LitematicaBridge.ScanResult result) {
        required.clear();
        placed.clear();

        if (result == null || !result.success()) {
            return;
        }

        for (LitematicaBridge.SchematicBlock block : result.blocks()) {
            if (block == null || block.blockId() == null) {
                continue;
            }

            required.merge(block.blockId(), 1, Integer::sum);
        }
    }

    // Tandai satu blok sebagai selesai dipasang
    public void markPlaced(String blockId) {
        if (blockId == null) {
            return;
        }

        int currentPlaced = placed.getOrDefault(blockId, 0);
        int totalRequired = required.getOrDefault(blockId, 0);

        if (currentPlaced < totalRequired) {
            placed.put(blockId, currentPlaced + 1);
        }
    }

    // Batalkan satu progres material
    public void undoPlaced(String blockId) {
        if (blockId == null) {
            return;
        }

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
                .mapToInt(Integer::intValue)
                .sum();
    }

    public int getTotalPlaced() {
        return placed.values().stream()
                .mapToInt(Integer::intValue)
                .sum();
    }

    public int getTotalRemaining() {
        return Math.max(0, getTotalRequired() - getTotalPlaced());
    }

    public float getProgress() {
        int total = getTotalRequired();

        if (total == 0) {
            return 0;
        }

        return (float) getTotalPlaced() / total * 100f;
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
    }

    public void clear() {
        required.clear();
        placed.clear();
    }
}
