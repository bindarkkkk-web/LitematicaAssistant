
package bindarkkk.litematicaassistant;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class LitematicaBridge {

    private static final String DATA_MANAGER =
        "fi.dy.masa.litematica.data.DataManager";

    private static final String WORLD_HANDLER =
        "fi.dy.masa.litematica.world.SchematicWorldHandler";

    private static final int MAX_BLOCKS = 250000;

    public record SchematicBlock(
        BlockPos pos,
        String blockId,
        boolean crafter
    ) {}

    public record ScanResult(
        boolean success,
        String message,
        String schematicName,
        int totalBlocks,
        int crafterCount,
        List<SchematicBlock> blocks
    ) {}

    public static boolean isAvailable() {
        try {
            Class.forName(DATA_MANAGER);
            Class.forName(WORLD_HANDLER);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static Object invoke(
        Object target,
        String methodName
    ) throws Exception {

        Method method = target.getClass()
            .getMethod(methodName);

        method.setAccessible(true);

        return method.invoke(target);
    }

    private static Object getSelectedPlacement()
        throws Exception {

        Class<?> managerClass =
            Class.forName(DATA_MANAGER);

        Object manager = managerClass
            .getMethod("getSchematicPlacementManager")
            .invoke(null);

        if (manager == null) return null;

        return invoke(
            manager,
            "getSelectedSchematicPlacement"
        );
    }

    public static String getSelectedSchematicName() {
        try {
            Object placement = getSelectedPlacement();

            if (placement == null) {
                return null;
            }

            Object name = invoke(
                placement,
                "getName"
            );

            return name == null
                ? null
                : name.toString();

        } catch (Exception e) {
            return null;
        }
    }

    private static int getCoordinate(
        Object box,
        String name
    ) throws Exception {

        String capitalized =
            Character.toUpperCase(name.charAt(0))
            + name.substring(1);

        String[] methods = {
            "get" + capitalized,
            name
        };

        for (String methodName : methods) {
            try {
                Object value = invoke(
                    box,
                    methodName
                );

                return ((Number) value).intValue();

            } catch (Exception ignored) {
            }
        }

        Class<?> type = box.getClass();

        while (type != null) {
            try {
                Field field =
                    type.getDeclaredField(name);

                field.setAccessible(true);

                return ((Number) field.get(box))
                    .intValue();

            } catch (Exception ignored) {
                type = type.getSuperclass();
            }
        }

        throw new NoSuchFieldException(name);
    }

    private static Object getSchematicWorld()
        throws Exception {

        Class<?> handler =
            Class.forName(WORLD_HANDLER);

        return handler
            .getMethod("getSchematicWorld")
            .invoke(null);
    }

    public static ScanResult scanSelectedSchematic() {

        List<SchematicBlock> blocks =
            new ArrayList<>();

        try {
            Object placement =
                getSelectedPlacement();

            if (placement == null) {
                return failed("No schematic selected");
            }

            String name = String.valueOf(
                invoke(placement, "getName")
            );

            Object box = invoke(
                placement,
                "getEnclosingBox"
            );

            if (box == null) {
                return failed("Schematic bounds unavailable");
            }

            int minX = getCoordinate(box, "minX");
            int minY = getCoordinate(box, "minY");
            int minZ = getCoordinate(box, "minZ");

            int maxX = getCoordinate(box, "maxX");
            int maxY = getCoordinate(box, "maxY");
            int maxZ = getCoordinate(box, "maxZ");

            long volume =
                ((long) maxX - minX + 1)
                * ((long) maxY - minY + 1)
                * ((long) maxZ - minZ + 1);

            if (volume <= 0 || volume > MAX_BLOCKS) {
                return failed(
                    "Schematic too large: " + volume
                    + " positions. Limit: " + MAX_BLOCKS
                );
            }

            Object world = getSchematicWorld();

            if (world == null) {
                return failed("Schematic world unavailable");
            }

            Method getBlockState = null;

            for (Method method :
                world.getClass().getMethods()) {

                if (
                    method.getName().equals("getBlockState")
                    && method.getParameterCount() == 1
                    && method.getParameterTypes()[0]
                        == BlockPos.class
                ) {
                    getBlockState = method;
                    break;
                }
            }

            if (getBlockState == null) {
                return failed("BlockState method unavailable");
            }

            int crafterCount = 0;

            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    for (int x = minX; x <= maxX; x++) {

                        BlockPos pos =
                            new BlockPos(x, y, z);

                        BlockState state =
                            (BlockState) getBlockState.invoke(
                                world,
                                pos
                            );

                        if (state == null || state.isAir()) {
                            continue;
                        }

                        boolean crafter =
                            state.is(Blocks.CRAFTER);

                        if (crafter) {
                            crafterCount++;
                        }

                        String blockId =
                            BuiltInRegistries.BLOCK
                                .getKey(state.getBlock())
                                .toString();

                        blocks.add(
                            new SchematicBlock(
                                pos,
                                blockId,
                                crafter
                            )
                        );
                    }
                }
            }

            return new ScanResult(
                true,
                "Schematic scanned successfully",
                name,
                blocks.size(),
                crafterCount,
                List.copyOf(blocks)
            );

        } catch (Exception e) {
            return failed(
                "Scan failed: "
                + e.getClass().getSimpleName()
                + ": " + e.getMessage()
            );
        }
    }

    private static ScanResult failed(String message) {
        return new ScanResult(
            false,
            message,
            null,
            0,
            0,
            List.of()
        );
    }
    }
