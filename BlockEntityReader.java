
package bindarkkk.litematicaassistant;

import java.lang.reflect.Method;
import java.util.*;

public class BlockEntityReader {

    public record BlockEntityData(
            String type,
            Map<String, String> data
    ) {}

    public BlockEntityData read(Object source) {
        if (source == null) return null;

        Map<String, String> values = new TreeMap<>();

        try {
            Method getAllKeys = source.getClass().getMethod("getAllKeys");
            Object keysObject = getAllKeys.invoke(source);

            if (keysObject instanceof Iterable<?> keys) {
                Method getMethod = source.getClass()
                        .getMethod("get", String.class);

                for (Object key : keys) {
                    String name = String.valueOf(key);
                    Object value = getMethod.invoke(source, name);

                    if (value != null) {
                        values.put(name, String.valueOf(value));
                    }
                }
            }

            return new BlockEntityData(
                    source.getClass().getSimpleName(),
                    Collections.unmodifiableMap(values)
            );

        } catch (Exception ignored) {
            return new BlockEntityData(
                    source.getClass().getSimpleName(),
                    Map.of()
            );
        }
    }

    public String getValue(
            BlockEntityData data,
            String key
    ) {
        if (data == null || key == null) return null;
        return data.data().get(key);
    }

    public boolean contains(
            BlockEntityData data,
            String key
    ) {
        return data != null && data.data().containsKey(key);
    }
}
