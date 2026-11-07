
package bindarkkk.litematicaassistant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class RecipeDetector {

    public record RecipeIngredient(String itemId, int count) {}

    public record Recipe(
            String recipeId,
            List<RecipeIngredient> ingredients,
            String resultId,
            int resultCount
    ) {}

    public record DetectionResult(
            boolean matched,
            Recipe recipe,
            String message
    ) {}

    private final List<Recipe> recipes = new ArrayList<>();

    public void registerRecipe(Recipe recipe) {
        if (!isValidRecipe(recipe)) return;

        List<RecipeIngredient> safeIngredients =
                Collections.unmodifiableList(
                        new ArrayList<>(recipe.ingredients())
                );

        Recipe safeRecipe = new Recipe(
                recipe.recipeId(),
                safeIngredients,
                recipe.resultId(),
                recipe.resultCount()
        );

        recipes.removeIf(existing ->
                existing.recipeId().equals(safeRecipe.recipeId())
        );

        recipes.add(safeRecipe);
    }

    public void registerRecipes(List<Recipe> data) {
        if (data == null) return;

        for (Recipe recipe : data) {
            registerRecipe(recipe);
        }
    }

    private boolean isValidRecipe(Recipe recipe) {
        return recipe != null
                && recipe.recipeId() != null
                && !recipe.recipeId().isBlank()
                && recipe.resultId() != null
                && !recipe.resultId().isBlank()
                && recipe.resultCount() > 0
                && recipe.ingredients() != null
                && recipe.ingredients().size() == 9;
    }

    public DetectionResult detect(List<RecipeIngredient> input) {
        if (input == null || input.size() != 9) {
            return new DetectionResult(
                    false, null, "Grid harus berisi 9 slot"
            );
        }

        for (Recipe recipe : recipes) {
            if (matches(recipe.ingredients(), input)) {
                return new DetectionResult(
                        true,
                        recipe,
                        "Resep ditemukan: " + recipe.resultId()
                );
            }
        }

        return new DetectionResult(
                false, null, "Tidak ada resep yang cocok"
        );
    }

    private boolean matches(
            List<RecipeIngredient> expected,
            List<RecipeIngredient> actual
    ) {
        for (int i = 0; i < 9; i++) {
            RecipeIngredient a = expected.get(i);
            RecipeIngredient b = actual.get(i);

            boolean aEmpty = isEmpty(a);
            boolean bEmpty = isEmpty(b);

            if (aEmpty && bEmpty) continue;
            if (aEmpty || bEmpty) return false;

            if (!Objects.equals(a.itemId(), b.itemId())) {
                return false;
            }

            if (b.count() < a.count()) {
                return false;
            }
        }

        return true;
    }

    private boolean isEmpty(RecipeIngredient ingredient) {
        return ingredient == null
                || ingredient.itemId() == null
                || ingredient.itemId().isBlank()
                || ingredient.count() <= 0;
    }

    public List<Recipe> getRecipes() {
        return Collections.unmodifiableList(
                new ArrayList<>(recipes)
        );
    }

    public int getRecipeCount() {
        return recipes.size();
    }

    public void clear() {
        recipes.clear();
    }
}
