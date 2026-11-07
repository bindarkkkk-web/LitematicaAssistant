
package bindarkkk.litematicaassistant;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public final class LitematicaBridge {

    private static final String DATA_MANAGER =
            "fi.dy.masa.litematica.data.DataManager";

    private static final String WORLD_HANDLER =
            "fi.dy.masa.litematica.world.SchematicWorldHandler";

    private static final int MAX_BLOCKS = 250000;

    private LitematicaBridge() {}

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
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static String getSelectedSchematicName() {
        try {
            Object placement = getSelectedPlacement();

            if (placement == null) {
                return "No schematic selected";
            }

            Object name = invokeAny(
                    placement,
                    "getName",
                    "getSchematicName"
            );

            return name == null ? "Unknown schematic" : name.toString();

        } catch (Throwable ignored) {
            return "Unknown schematic";
        }
    }

    public static ScanResult scanSelectedSchematic() {
        List<SchematicBlock> blocks = new ArrayList<>();

        try {
            if (!isAvailable()) {
                return failed("Litematica tidak ditemukan");
            }

            Object placement = getSelectedPlacement();

            if (placement == null) {
                return failed("Pilih placement Litematica terlebih dahulu");
            }

            Object box = invokeAny(placement, "getEnclosingBox");

            if (box == null) {
                return failed("Bounding box schematic tidak ditemukan");
            }

            Object schematicWorld = getSchematicWorld();

            if (schematicWorld == null) {
                return failed("Schematic world belum tersedia");
            }

            int minX = readCoordinate(box, "minX", "getMinX");
            int minY = readCoordinate(box, "minY", "getMinY");
            int minZ = readCoordinate(box, "minZ", "getMinZ");

            int maxX = readCoordinate(box, "maxX", "getMaxX");
            int maxY = readCoordinate(box, "maxY", "getMaxY");
            int maxZ = readCoordinate(box, "maxZ", "getMaxZ");

            long volume =
                    ((long) maxX - minX + 1)
                    * ((long) maxY - minY + 1)
                    * ((long) maxZ - minZ + 1);

            if (volume <= 0) {
                return failed("Ukuran bounding box tidak valid");
            }

            if (volume > MAX_BLOCKS * 8L) {
                return failed(
                        "Schematic terlalu besar untuk dipindai sekaligus"
                );
            }

            Method getBlockState = findBlockStateMethod(schematicWorld);

            if (getBlockState == null) {
                return failed("Method getBlockState tidak ditemukan");
            }

            String schematicName = getSelectedSchematicName();
            int crafterCount = 0;

            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    for (int x = minX; x <= maxX; x++) {

                        if (blocks.size() >= MAX_BLOCKS) {
                            return new ScanResult(
                                    false,
                                    "Batas scan tercapai: " + MAX_BLOCKS
                                            + " block",
                                    schematicName,
                                    blocks.size(),
                                    crafterCount,
                                    List.copyOf(blocks)
                            );
                        }

                        BlockPos pos = new BlockPos(x, y, z);

                        Object state = getBlockState.invoke(
                                schematicWorld,
                                pos
                        );

                        if (state == null || isAir(state)) {
                            continue;
                        }

                        Object block = invokeAny(state, "getBlock");

                        if (block == null) {
                            continue;
                        }

                        Object key = BuiltInRegistries.BLOCK.getKey(
                                (net.minecraft.world.level.block.Block) block
                        );

                        if (key == null) {
                            continue;
                        }

                        String blockId = key.toString();
                        boolean isCrafter =
                                blockId.equals("minecraft:crafter");

                        if (isCrafter) {
                            crafterCount++;
                        }

                        blocks.add(
                                new SchematicBlock(
                                        pos,
                                        blockId,
                                        isCrafter
                                )
                        );
                    }
                }
            }

            return new ScanResult(
                    true,
                    "Scan berhasil",
                    schematicName,
                    blocks.size(),
                    crafterCount,
                    List.copyOf(blocks)
            );

        } catch (Throwable error) {
            Throwable cause = error.getCause() != null
                    ? error.getCause()
                    : error;

            return new ScanResult(
                    false,
                    "Scan gagal: " + cause.getClass().getSimpleName()
                            + " - " + String.valueOf(cause.getMessage()),
                    "Unknown",
                    blocks.size(),
                    countCrafters(blocks),
                    List.copyOf(blocks)
            );
        }
    }

    private static Object getSelectedPlacement() throws Exception {
        Class<?> dataManager = Class.forName(DATA_MANAGER);

        Object manager = dataManager
                .getMethod("getSchematicPlacementManager")
                .invoke(null);

        if (manager == null) {
            return null;
        }

        return invokeAny(
                manager,
                "getSelectedSchematicPlacement"
        );
    }

    private static Object getSchematicWorld() throws Exception {
        Class<?> handler = Class.forName(WORLD_HANDLER);

        return handler
                .getMethod("getSchematicWorld")
                .invoke(null);
    }

    private static Method findBlockStateMethod(Object world) {
        for (Method method : world.getClass().getMethods()) {
            if (!method.getName().equals("getBlockState")) {
                continue;
            }

            Class<?>[] params = method.getParameterTypes();

            if (params.length == 1
                    && params[0].isAssignableFrom(BlockPos.class)) {
                return method;
            }
        }

        return null;
    }

    private static Object invokeAny(
            Object target,
            String... methodNames
    ) throws Exception {

        if (target == null) {
            return null;
        }

        for (String name : methodNames) {
            try {
                Method method = target.getClass().getMethod(name);
                return method.invoke(target);
            } catch (NoSuchMethodException ignored) {
                // Coba nama method berikutnya.
            }
        }

        return null;
    }

    private static int readCoordinate(
            Object target,
            String fieldName,
            String methodName
    ) throws Exception {

        try {
            Method method = target.getClass().getMethod(methodName);
            Object value = method.invoke(target);

            if (value instanceof Number number) {
                return number.intValue();
            }
        } catch (NoSuchMethodException ignored) {
            // Coba baca field.
        }

        Class<?> type = target.getClass();

        while (type != null) {
            try {
                Field field = type.getDeclaredField(fieldName);
                field.setAccessible(true);

                Object value = field.get(target);

                if (value instanceof Number number) {
                    return number.intValue();
                }

            } catch (NoSuchFieldException ignored) {
                type = type.getSuperclass();
                continue;
            }

            break;
        }

        throw new NoSuchFieldException(
                "Koordinat tidak ditemukan: " + fieldName
        );
    }

    private static boolean isAir(Object state) throws Exception {
        Method method = state.getClass().getMethod("isAir");
        return Boolean.TRUE.equals(method.invoke(state));
    }

    private static int countCrafters(List<SchematicBlock> blocks) {
        int count = 0;

        for (SchematicBlock block : blocks) {
            if (block.crafter()) {
                count++;
            }
        }

        return count;
    }

    private static ScanResult failed(String message) {
        return new ScanResult(
                false,
                message,
                "Unknown",
                0,
                0,
                List.of()
        );
    }
            }
