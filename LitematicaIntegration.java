
package bindarkkk.litematicaassistant;

public class LitematicaIntegration {

    public enum ConnectionStatus {
        AVAILABLE,
        UNAVAILABLE,
        SCAN_FAILED,
        READY
    }

    private ConnectionStatus status =
            ConnectionStatus.UNAVAILABLE;

    private LitematicaBridge.ScanResult lastScan;

    public ConnectionStatus connect() {
        status = LitematicaBridge.isAvailable()
                ? ConnectionStatus.AVAILABLE
                : ConnectionStatus.UNAVAILABLE;

        return status;
    }

    public LitematicaBridge.ScanResult scan() {
        if (!LitematicaBridge.isAvailable()) {
            status = ConnectionStatus.UNAVAILABLE;
            return null;
        }

        lastScan = LitematicaBridge.scanSelectedSchematic();

        if (lastScan == null || !lastScan.success()) {
            status = ConnectionStatus.SCAN_FAILED;
        } else {
            status = ConnectionStatus.READY;
        }

        return lastScan;
    }

    public String getSelectedSchematicName() {
        return LitematicaBridge.getSelectedSchematicName();
    }

    public ConnectionStatus getStatus() {
        return status;
    }

    public LitematicaBridge.ScanResult getLastScan() {
        return lastScan;
    }

    public boolean isReady() {
        return status == ConnectionStatus.READY
                && lastScan != null
                && lastScan.success();
    }

    public void clear() {
        lastScan = null;
        status = ConnectionStatus.UNAVAILABLE;
    }
}
