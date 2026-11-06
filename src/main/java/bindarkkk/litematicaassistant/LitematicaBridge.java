
package bindarkkk.litematicaassistant;

import java.lang.reflect.Method;

public class LitematicaBridge {

    public static boolean isAvailable() {
        try {
            Class.forName(
                "fi.dy.masa.litematica.data.DataManager"
            );
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static String getSelectedSchematicName() {
        try {
            Class<?> dataManager = Class.forName(
                "fi.dy.masa.litematica.data.DataManager"
            );

            Method getManager =
                dataManager.getMethod("getSchematicPlacementManager");

            Object manager = getManager.invoke(null);

            if (manager == null) return null;

            Method getSelected =
                manager.getClass().getMethod(
                    "getSelectedSchematicPlacement"
                );

            Object placement = getSelected.invoke(manager);

            if (placement == null) return null;

            Method getName =
                placement.getClass().getMethod("getName");

            return String.valueOf(getName.invoke(placement));

        } catch (Exception e) {
            return null;
        }
    }
}
