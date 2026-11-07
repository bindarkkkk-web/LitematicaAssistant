package bindarkkk.litematicaassistant;

import net.minecraft.core.BlockPos;

import java.util.*;

public class BuildEngine {

    private final List<LitematicaBridge.SchematicBlock> blocks =
        new ArrayList<>();

    private final Set<BlockPos> completed =
        new HashSet<>();

    private final Map<String, Integer> materials =
        new TreeMap<>();

    public void load(
        LitematicaBridge.ScanResult result
    ) {
        blocks.clear();
        completed.clear();
        materials.clear();

        if (result == null || !result.success()) return;

        blocks.addAll(result.blocks());

        // Urutan pengerjaan: bawah ke atas
        blocks.sort(
            Comparator
                .comparingInt((LitematicaBridge.SchematicBlock b)
                    -> b.pos().getY())
                .thenComparingInt(b -> b.pos().getZ())
                .thenComparingInt(b -> b.pos().getX())
        );

        for (var block : blocks) {
            materials.merge(block.blockId(), 1, Integer::sum);
        }
    }

    public List<LitematicaBridge.SchematicBlock> getBlocks() {
        return Collections.unmodifiableList(blocks);
    }

    public Map<String, Integer> getMaterials() {
        return Collections.unmodifiableMap(materials);
    }

    public int getTotalBlocks() {
        return blocks.size();
    }

    public int getCompletedCount() {
        return completed.size();
    }

    public int getRemainingCount() {
        return Math.max(0, blocks.size() - completed.size());
    }

    public float getProgress() {
        if (blocks.isEmpty()) return 0;

        return (float) completed.size() / blocks.size() * 100.0f;
    }

    public LitematicaBridge.SchematicBlock getNextBlock() {
        for (var block : blocks) {
            if (!completed.contains(block.pos())) {
                return block;
            }
        }

        return null;
    }

    public void markCompleted(BlockPos pos) {
        if (pos == null) return;

        for (var block : blocks) {
            if (block.pos().equals(pos)) {
                completed.add(pos);
                return;
            }
        }
    }

    public void resetProgress() {
        completed.clear();
    }

    public boolean isComplete() {
        return !blocks.isEmpty()
            && completed.size() >= blocks.size();
    }

    public int getRequiredMaterial(String blockId) {
        return materials.getOrDefault(blockId, 0);
    }

    public int getCompletedMaterial(String blockId) {
        int count = 0;

        for (var block : blocks) {
            if (
                block.blockId().equals(blockId)
                && completed.contains(block.pos())
            ) {
                count++;
            }
        }

        return count;
    }

    public int getRemainingMaterial(String blockId) {
        return Math.max(
            0,
            getRequiredMaterial(blockId)
                - getCompletedMaterial(blockId)
        );
    }
  }
