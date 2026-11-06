
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
    private Rotation rotation = Rotation.NONE;
    private Mirror mirror = Mirror.NONE;

    public void configure(
            BlockPos origin,
            Rotation rotation,
            Mirror mirror
    ) {
        this.origin = origin == null ? BlockPos.ZERO : origin;
        this.rotation = rotation == null ? Rotation.NONE : rotation;
        this.mirror = mirror == null ? Mirror.NONE : mirror;
    }

    public BlockPos transform(BlockPos localPos) {
        if (localPos == null) {
            return origin;
        }

        int x = localPos.getX();
        int y = localPos.getY();
        int z = localPos.getZ();

        // Mirror terlebih dahulu
        switch (mirror) {
            case LEFT_RIGHT -> z = -z;
            case FRONT_BACK -> x = -x;
            case NONE -> {}
        }

        // Kemudian rotasi
        int transformedX;
        int transformedZ;

        switch (rotation) {
            case CLOCKWISE_90 -> {
                transformedX = -z;
                transformedZ = x;
            }
            case CLOCKWISE_180 -> {
                transformedX = -x;
                transformedZ = -z;
            }
            case COUNTERCLOCKWISE_90 -> {
                transformedX = z;
                transformedZ = -x;
            }
            default -> {
                transformedX = x;
                transformedZ = z;
            }
        }

        return origin.offset(
                transformedX,
                y,
                transformedZ
        );
    }

    public Direction transformDirection(Direction direction) {
        if (direction == null) {
            return Direction.NORTH;
        }

        Direction result = direction;

        if (mirror == Mirror.LEFT_RIGHT) {
            result = result.getAxis() == Direction.Axis.Z
                    ? result.getOpposite() : result;
        } else if (mirror == Mirror.FRONT_BACK) {
            result = result.getAxis() == Direction.Axis.X
                    ? result.getOpposite() : result;
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

    public Rotation getRotation() {
        return rotation;
    }

    public Mirror getMirror() {
        return mirror;
    }
          }
            
