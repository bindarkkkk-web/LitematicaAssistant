
package bindarkkk.litematicaassistant;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WorldScanner {

    public enum MatchStatus {
        MATCH,
        MISSING,
        DIFFERENT
    }

    public record WorldResult(
            BlockPos schematicPosition,
            BlockPos worldPosition,
            String expectedBlock,
            String actualBlock,
            MatchStatus status
    ) {}

    public record ScanSummary(
            int total,
            int matched,
            int missing,
            int different,
            List<WorldResult> results
    ) {}

    public ScanSummary scan(
            ClientLevel level,
            List<LitematicaBridge.SchematicBlock> blocks,
            PlacementTransformer transformer
    ) {
        if (level == null || blocks == null || transformer == null) {
            return new ScanSummary(0, 0, 0, 0, List.of());
        }

        List<WorldResult> results = new ArrayList<>();

        int matched = 0;
        int missing = 0;
        int different = 0;

        for (var block : blocks) {
            if (block == null || block.pos() == null) continue;

            BlockPos worldPos = transformer.transform(block.pos());

            String actual = BuiltInRegistries.BLOCK
                    .getKey(level.getBlockState(worldPos).getBlock())
                    .toString();

            MatchStatus status;

            if (actual.equals(block.blockId())) {
                status = MatchStatus.MATCH;
                matched++;
            } else if (actual.equals("minecraft:air")) {
                status = MatchStatus.MISSING;
                missing++;
            } else {
                status = MatchStatus.DIFFERENT;
                different++;
            }

            results.add(new WorldResult(
                    block.pos(),
                    worldPos,
                    block.blockId(),
                    actual,
                    status
            ));
        }

        return new ScanSummary(
                results.size(),
                matched,
                missing,
                different,
                Collections.unmodifiableList(results)
        );
    }
          }
