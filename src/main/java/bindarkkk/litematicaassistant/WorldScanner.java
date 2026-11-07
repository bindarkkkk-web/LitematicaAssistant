
package bindarkkk.litematicaassistant;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WorldScanner {

    public enum MatchStatus {
        MATCH,
        MISSING,
        DIFFERENT,
        UNAVAILABLE
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
            if (block == null || block.pos() == null) {
                continue;
            }

            BlockPos schematicPos = block.pos();
            BlockPos worldPos = transformer.transform(schematicPos);

            if (!level.isInWorldBounds(worldPos)
                    || !level.hasChunkAt(worldPos)) {

                results.add(new WorldResult(
                        schematicPos,
                        worldPos,
                        block.blockId(),
                        "unavailable",
                        MatchStatus.UNAVAILABLE
                ));

                continue;
            }

            BlockState actualState = level.getBlockState(worldPos);

            String actual = BuiltInRegistries.BLOCK
                    .getKey(actualState.getBlock())
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
                    schematicPos,
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
