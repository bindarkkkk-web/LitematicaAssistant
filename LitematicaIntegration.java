
package bindarkkk.litematicaassistant;

public class LitematicaIntegration {

    private final BuildAssistant buildAssistant;

    private LitematicaBridge.ScanResult lastScan;

    private String status = "Belum melakukan scan";
    private boolean scanning;
    private boolean loaded;

    public LitematicaIntegration() {
        this.buildAssistant = new BuildAssistant();
    }

    public boolean isLitematicaAvailable() {
        return LitematicaBridge.isAvailable();
    }

    public boolean scan() {
        if (scanning) {
            status = "Scan sedang berjalan";
            return false;
        }

        scanning = true;
        loaded = false;

        try {
            if (!isLitematicaAvailable()) {
                status = "Litematica tidak ditemukan";
                return false;
            }

            lastScan = LitematicaBridge.scanSelectedSchematic();

            if (lastScan == null) {
                status = "Hasil scan kosong";
                return false;
            }

            if (!lastScan.success()) {
                status = lastScan.message();
                return false;
            }

            if (lastScan.blocks().isEmpty()) {
                status = "Schematic tidak memiliki block";
                return false;
            }

            buildAssistant.load(lastScan);

            loaded = true;
            status = "Berhasil memuat "
                    + lastScan.totalBlocks()
                    + " block";

            return true;

        } catch (Exception error) {
            status = "Integration error: "
                    + error.getClass().getSimpleName()
                    + " - "
                    + error.getMessage();

            return false;

        } finally {
            scanning = false;
        }
    }

    public BuildAssistant getBuildAssistant() {
        return buildAssistant;
    }

    public LitematicaBridge.ScanResult getLastScan() {
        return lastScan;
    }

    public String getStatus() {
        return status;
    }

    public String getSchematicName() {
        if (lastScan != null && lastScan.success()) {
            return lastScan.schematicName();
        }

        return LitematicaBridge.getSelectedSchematicName();
    }

    public int getTotalBlocks() {
        return lastScan == null ? 0 : lastScan.totalBlocks();
    }

    public int getCrafterCount() {
        return lastScan == null ? 0 : lastScan.crafterCount();
    }

    public boolean isScanning() {
        return scanning;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void reset() {
        lastScan = null;
        loaded = false;
        status = "Data schematic direset";
        buildAssistant.stop();
    }
}
