
package bindarkkk.litematicaassistant;

import net.minecraft.core.BlockPos;

public class PlacementValidator {

    public enum ValidationStatus {
        VALID,
        OCCUPIED,
        NO_SUPPORT,
        OUT_OF_WORLD,
        INVALID
    }

    public record ValidationResult(
            ValidationStatus status,
            String message,
            BlockPos position
    ) {
        public boolean isValid() {
            return status == ValidationStatus.VALID;
        }
    }

    public ValidationResult validate(
            BlockPos position,
            boolean insideWorld,
            boolean targetEmpty,
            boolean replaceable,
            boolean hasSupport
    ) {
        if (position == null) {
            return result(
                    ValidationStatus.INVALID,
                    "Posisi tidak tersedia",
                    null
            );
        }

        if (!insideWorld) {
            return result(
                    ValidationStatus.OUT_OF_WORLD,
                    "Posisi di luar batas dunia",
                    position
            );
        }

        if (!targetEmpty && !replaceable) {
            return result(
                    ValidationStatus.OCCUPIED,
                    "Posisi sudah ditempati blok lain",
                    position
            );
        }

        if (!hasSupport) {
            return result(
                    ValidationStatus.NO_SUPPORT,
                    "Blok pendukung belum tersedia",
                    position
            );
        }

        return result(
                ValidationStatus.VALID,
                "Posisi memenuhi pemeriksaan dasar",
                position
        );
    }

    private ValidationResult result(
            ValidationStatus status,
            String message,
            BlockPos position
    ) {
        return new ValidationResult(status, message, position);
    }
}
