
package bindarkkk.litematicaassistant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class RecipeRegistry {

    private final Map<String, RecipeDetector.Recipe> recipes =
            new TreeMap<>();

    public boolean register(RecipeDetector.Recipe recipe) {
        if (recipe == null
                || recipe.recipeId() == null
                || recipe.recipeId().isBlank()
                || recipe.resultId() == null
                || recipe.resultId().isBlank()
                || recipe.resultCount() <= 0
                || recipe.ingredients() == null
                || recipe.ingredients().size() != 9) {
            return false;
        }

        recipes.put(recipe.recipeId(), recipe);
        return true;
    }

    public void registerAll(List<RecipeDetector.Recipe> data) {
        if (data == null) return;

        for (RecipeDetector.Recipe recipe : data) {
            register(recipe);
        }
    }

    public RecipeDetector.Recipe get(String id) {
        if (id == null) return null;
        return recipes.get(id);
    }

    public List<RecipeDetector.Recipe> findByResult(String resultId) {
        if (resultId == null) return List.of();

        List<RecipeDetector.Recipe> result = new ArrayList<>();

        for (RecipeDetector.Recipe recipe : recipes.values()) {
            if (resultId.equals(recipe.resultId())) {
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
        if (id != null) {
            recipes.remove(id);
        }
    }

    public void clear() {
        recipes.clear();
    }
}
