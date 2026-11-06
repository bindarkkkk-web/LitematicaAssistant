
package bindarkkk.litematicaassistant;

import java.util.*;

public class SchematicAnalyzer {

    public record Analysis(
            int totalBlocks,
            int uniqueBlocks,
            int crafterCount,
            int minY,
            int maxY,
            Map<String, Integer> blockCounts,
            Map<Integer, Integer> layerCounts
    ) {}

    public Analysis analyze(
            LitematicaBridge.ScanResult result
    ) {
        if (result == null || !result.success()) {
            return empty();
        }

        Map<String, Integer> blockCounts = new TreeMap<>();
        Map<Integer, Integer> layerCounts = new TreeMap<>();

        int crafterCount = 0;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (var block : result.blocks()) {
            if (block == null || block.pos() == null) continue;

            blockCounts.merge(
                    block.blockId(), 1, Integer::sum
            );

            layerCounts.merge(
                    block.pos().getY(), 1, Integer::sum
            );

            if (block.crafter()) crafterCount++;

            minY = Math.min(minY, block.pos().getY());
            maxY = Math.max(maxY, block.pos().getY());
        }

        int total = result.blocks().size();

        return new Analysis(
                total,
                blockCounts.size(),
                crafterCount,
                total == 0 ? 0 : minY,
                total == 0 ? 0 : maxY,
                Collections.unmodifiableMap(blockCounts),
                Collections.unmodifiableMap(layerCounts)
        );
    }

    private Analysis empty() {
        return new Analysis(
                0, 0, 0, 0, 0,
                Map.of(),
                Map.of()
        );
    }
}
