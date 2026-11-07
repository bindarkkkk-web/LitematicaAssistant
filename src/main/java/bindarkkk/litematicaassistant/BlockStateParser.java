
package bindarkkk.litematicaassistant;

import java.util.*;

public class BlockStateParser {

    public record ParsedBlock(
            String blockId,
            Map<String, String> properties
    ) {}

    public ParsedBlock parse(String input) {
        if (input == null || input.isBlank()) return null;

        String value = input.trim();
        int start = value.indexOf('[');

        if (start < 0) {
            return new ParsedBlock(value, Map.of());
        }

        if (!value.endsWith("]")) return null;

        String id = value.substring(0, start).trim();
        String propertyText =
                value.substring(start + 1, value.length() - 1);

        Map<String, String> properties = new TreeMap<>();

        if (!id.contains(":") || id.isBlank()) return null;

        if (!propertyText.isBlank()) {
            for (String entry : propertyText.split(",")) {
                String[] pair = entry.trim().split("=", 2);

                if (pair.length != 2) return null;

                properties.put(pair[0].trim(), pair[1].trim());
            }
        }

        return new ParsedBlock(
                id,
                Collections.unmodifiableMap(properties)
        );
    }

    public boolean matches(
            ParsedBlock expected,
            ParsedBlock actual
    ) {
        if (expected == null || actual == null) return false;

        return expected.blockId().equals(actual.blockId())
                && expected.properties().equals(actual.properties());
    }
}
