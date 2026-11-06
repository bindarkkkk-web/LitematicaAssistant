
package bindarkkk.litematicaassistant;

import java.util.*;

public class RecipeRegistry {

    private final Map<String, RecipeDetector.Recipe> recipes =
            new TreeMap<>();

    public boolean register(RecipeDetector.Recipe recipe) {
        if (recipe == null || recipe.recipeId() == null
                || recipe.resultId() == null
                || recipe.ingredients() == null
                || recipe.ingredients().size() != 9) {
            return false;
        }

        recipes.put(recipe.recipeId(), recipe);
        return true;
    }

    public void registerAll(
            List<RecipeDetector.Recipe> data
    ) {
        if (data == null) return;

        for (var recipe : data) {
            register(recipe);
        }
    }

    public RecipeDetector.Recipe get(String id) {
        return recipes.get(id);
    }

    public List<RecipeDetector.Recipe> findByResult(
            String resultId
    ) {
        List<RecipeDetector.Recipe> result = new ArrayList<>();

        for (var recipe : recipes.values()) {
            if (recipe.resultId().equals(resultId)) {
                result.add(recipe);
            }
        }

        return Collections.unmodifiableList(result);
    }

    public List<RecipeDetector.Recipe> getAll() {
        return Collections.unmodifiableList(
                new ArrayList<>(recipes.values())
        );
    }

    public int size() {
        return recipes.size();
    }

    public void remove(String id) {
        recipes.remove(id);
    }

    public void clear() {
        recipes.clear();
    }
}
