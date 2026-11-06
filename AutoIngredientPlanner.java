
package bindarkkk.litematicaassistant;

import java.util.*;

public class AutoIngredientPlanner {

    public record IngredientNeed(
            String itemId,
            int required,
            int available,
            int missing
    ) {}

    private final Map<String, Integer> inventory = new HashMap<>();

    public void updateInventory(Map<String, Integer> items) {
        inventory.clear();

        if (items == null) return;

        items.forEach((id, count) -> {
            if (id != null && count != null && count > 0) {
                inventory.put(id, count);
            }
        });
    }

    public IngredientNeed calculate(
            String itemId,
            int required
    ) {
        int available = inventory.getOrDefault(itemId, 0);
        int missing = Math.max(0, required - available);

        return new IngredientNeed(
                itemId,
                required,
                available,
                missing
        );
    }

    public List<IngredientNeed> calculateAll(
            List<CrafterAssistant.Ingredient> ingredients
    ) {
        if (ingredients == null) return List.of();

        Map<String, Integer> totals = new TreeMap<>();

        for (var ingredient : ingredients) {
            if (ingredient == null || ingredient.itemId() == null) {
                continue;
            }

            totals.merge(
                    ingredient.itemId(),
                    ingredient.count(),
                    Integer::sum
            );
        }

        List<IngredientNeed> result = new ArrayList<>();

        totals.forEach((id, count) ->
                result.add(calculate(id, count))
        );

        return Collections.unmodifiableList(result);
    }

    public int getTotalMissing(
            List<IngredientNeed> needs
    ) {
        if (needs == null) return 0;

        return needs.stream()
                .mapToInt(IngredientNeed::missing)
                .sum();
    }

    public boolean hasEnough(
            List<IngredientNeed> needs
    ) {
        return getTotalMissing(needs) == 0;
    }

    public void clear() {
        inventory.clear();
    }
}
