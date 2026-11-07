
package bindarkkk.litematicaassistant;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class PlacementTransformer {

    public enum Rotation {
        NONE,
        CLOCKWISE_90,
        CLOCKWISE_180,
        COUNTERCLOCKWISE_90
    }

    public enum Mirror {
        NONE,
        LEFT_RIGHT,
        FRONT_BACK
    }

    private BlockPos origin = BlockPos.ZERO;
    private BlockPos sourceOrigin = BlockPos.ZERO;

    private Rotation rotation = Rotation.NONE;
    private Mirror mirror = Mirror.NONE;

    public void configure(
            BlockPos origin,
            Rotation rotation,
            Mirror mirror
    ) {
        configure(
                BlockPos.ZERO,
                origin,
                rotation,
                mirror
        );
    }

    public void configure(
            BlockPos sourceOrigin,
            BlockPos worldOrigin,
            Rotation rotation,
            Mirror mirror
    ) {
        this.sourceOrigin = sourceOrigin == null
                ? BlockPos.ZERO : sourceOrigin;

        this.origin = worldOrigin == null
                ? BlockPos.ZERO : worldOrigin;

        this.rotation = rotation == null
                ? Rotation.NONE : rotation;

        this.mirror = mirror == null
                ? Mirror.NONE : mirror;
    }

    public BlockPos transform(BlockPos schematicPos) {
        if (schematicPos == null) {
            return origin;
        }

        int x = schematicPos.getX() - sourceOrigin.getX();
        int y = schematicPos.getY() - sourceOrigin.getY();
        int z = schematicPos.getZ() - sourceOrigin.getZ();

        switch (mirror) {
            case LEFT_RIGHT -> z = -z;
            case FRONT_BACK -> x = -x;
            case NONE -> {}
        }

        int tx;
        int tz;

        switch (rotation) {
            case CLOCKWISE_90 -> {
                tx = -z;
                tz = x;
            }
            case CLOCKWISE_180 -> {
                tx = -x;
                tz = -z;
            }
            case COUNTERCLOCKWISE_90 -> {
                tx = z;
                tz = -x;
            }
            default -> {
                tx = x;
                tz = z;
            }
        }

        return origin.offset(tx, y, tz);
    }

    public Direction transformDirection(Direction direction) {
        if (direction == null) {
            return Direction.NORTH;
        }

        Direction result = direction;

        if (mirror == Mirror.LEFT_RIGHT
                && result.getAxis() == Direction.Axis.Z) {
            result = result.getOpposite();
        }

        if (mirror == Mirror.FRONT_BACK
                && result.getAxis() == Direction.Axis.X) {
            result = result.getOpposite();
        }

        return switch (rotation) {
            case CLOCKWISE_90 -> result.getClockWise();
            case CLOCKWISE_180 -> result.getOpposite();
            case COUNTERCLOCKWISE_90 -> result.getCounterClockWise();
            default -> result;
        };
    }

    public BlockPos getOrigin() {
        return origin;
    }

    public BlockPos getSourceOrigin() {
        return sourceOrigin;
    }

    public Rotation getRotation() {
        return rotation;
    }

    public Mirror getMirror() {
        return mirror;
    }
}
