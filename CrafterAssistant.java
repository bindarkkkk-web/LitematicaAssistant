
package bindarkkk.litematicaassistant;

import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CrafterAssistant {

    public record Ingredient(int slot, String itemId, int count) {}

    public record CrafterInfo(
            BlockPos position,
            List<Ingredient> ingredients,
            String expectedResult,
            boolean recipeDetected
    ) {}

    private final List<CrafterInfo> crafters = new ArrayList<>();

    public void load(List<CrafterInfo> data) {
        crafters.clear();
        if (data == null) return;

        for (CrafterInfo info : data) {
            if (info != null && info.position() != null) {
                addCrafter(
                        info.position(),
                        info.ingredients(),
                        info.expectedResult()
                );
            }
        }
    }

    public void addCrafter(
            BlockPos position,
            List<Ingredient> ingredients,
            String expectedResult
    ) {
        if (position == null) return;

        List<Ingredient> safeIngredients = new ArrayList<>();

        if (ingredients != null) {
            for (Ingredient ingredient : ingredients) {
                if (ingredient != null
                        && ingredient.slot() >= 0
                        && ingredient.slot() < 9
                        && ingredient.itemId() != null
                        && !ingredient.itemId().isBlank()
                        && ingredient.count() > 0) {
                    safeIngredients.add(ingredient);
                }
            }
        }

        String result = expectedResult == null ? "" : expectedResult;

        boolean detected = !safeIngredients.isEmpty()
                && !result.isBlank();

        BlockPos safePosition = position.immutable();

        crafters.removeIf(info ->
                info.position().equals(safePosition)
        );

        crafters.add(new CrafterInfo(
                safePosition,
                Collections.unmodifiableList(safeIngredients),
                result,
                detected
        ));
    }

    public CrafterInfo getCrafter(BlockPos position) {
        if (position == null) return null;

        for (CrafterInfo info : crafters) {
            if (info.position().equals(position)) {
                return info;
            }
        }

        return null;
    }

    public List<CrafterInfo> getAllCrafters() {
        return Collections.unmodifiableList(
                new ArrayList<>(crafters)
        );
    }

    public int getCrafterCount() {
        return crafters.size();
    }

    public int getDetectedRecipeCount() {
        return (int) crafters.stream()
                .filter(CrafterInfo::recipeDetected)
                .count();
    }

    public int getUndetectedRecipeCount() {
        return getCrafterCount() - getDetectedRecipeCount();
    }

    public void removeCrafter(BlockPos position) {
        if (position == null) return;
        crafters.removeIf(info -> info.position().equals(position));
    }

    public void clear() {
        crafters.clear();
    }
}
