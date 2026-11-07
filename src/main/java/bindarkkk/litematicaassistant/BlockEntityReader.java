
package bindarkkk.litematicaassistant;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class BlockEntityReader {

    public record BlockEntityData(
            String type,
            Map<String, String> data
    ) {}

    public BlockEntityData read(Object source) {
        if (source == null) return null;

        Map<String, String> values = new TreeMap<>();

        if (source instanceof Map<?, ?> map) {
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (entry.getKey() != null && entry.getValue() != null) {
                    values.put(
                            String.valueOf(entry.getKey()),
                            String.valueOf(entry.getValue())
                    );
                }
            }

            return createData(source, values);
        }

        try {
            Method keysMethod = findMethod(
                    source.getClass(),
                    "getAllKeys"
            );

            if (keysMethod == null) {
                keysMethod = findMethod(
                        source.getClass(),
                        "keySet"
                );
            }

            if (keysMethod == null) {
                return createData(source, values);
            }

            Object keysObject = keysMethod.invoke(source);

            if (!(keysObject instanceof Iterable<?> keys)) {
                return createData(source, values);
            }

            Method getMethod = findMethod(
                    source.getClass(),
                    "get",
                    String.class
            );

            if (getMethod == null) {
                return createData(source, values);
            }

            for (Object key : keys) {
                if (key == null) continue;

                String name = String.valueOf(key);
                Object value = getMethod.invoke(source, name);

                if (value != null) {
                    values.put(name, String.valueOf(value));
                }
            }

        } catch (Exception ignored) {
            values.clear();
        }

        return createData(source, values);
    }

    private Method findMethod(
            Class<?> type,
            String name,
            Class<?>... parameterTypes
    ) {
        try {
            return type.getMethod(name, parameterTypes);
        } catch (NoSuchMethodException ignored) {
            return null;
        }
    }

    private BlockEntityData createData(
            Object source,
            Map<String, String> values
    ) {
        return new BlockEntityData(
                source.getClass().getSimpleName(),
                Collections.unmodifiableMap(
                        new TreeMap<>(values)
                )
        );
    }

    public String getValue(BlockEntityData data, String key) {
        if (data == null || key == null) return null;
        return data.data().get(key);
    }

    public boolean contains(BlockEntityData data, String key) {
        return data != null
                && key != null
                && data.data().containsKey(key);
    }
}
