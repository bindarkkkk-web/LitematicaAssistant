
package bindarkkk.litematicaassistant;

public class BuildConfig {

    private boolean materialTracker = true;
    private boolean placementGuide = true;
    private boolean crafterAssistant = true;
    private boolean recipeDetection = true;
    private boolean autoIngredient = true;
    private boolean worldScanner = true;
    private boolean visualGuide = true;

    private boolean showCompletedBlocks = false;
    private boolean showRemainingBlocks = true;

    private int scanLimit = 250000;

    public boolean isMaterialTrackerEnabled() {
        return materialTracker;
    }

    public void setMaterialTrackerEnabled(boolean value) {
        materialTracker = value;
    }

    public boolean isPlacementGuideEnabled() {
        return placementGuide;
    }

    public void setPlacementGuideEnabled(boolean value) {
        placementGuide = value;
    }

    public boolean isCrafterAssistantEnabled() {
        return crafterAssistant;
    }

    public void setCrafterAssistantEnabled(boolean value) {
        crafterAssistant = value;
    }

    public boolean isRecipeDetectionEnabled() {
        return recipeDetection;
    }

    public void setRecipeDetectionEnabled(boolean value) {
        recipeDetection = value;
    }

    public boolean isAutoIngredientEnabled() {
        return autoIngredient;
    }

    public void setAutoIngredientEnabled(boolean value) {
        autoIngredient = value;
    }

    public boolean isWorldScannerEnabled() {
        return worldScanner;
    }

    public void setWorldScannerEnabled(boolean value) {
        worldScanner = value;
    }

    public boolean isVisualGuideEnabled() {
        return visualGuide;
    }

    public void setVisualGuideEnabled(boolean value) {
        visualGuide = value;
    }

    public boolean isShowCompletedBlocks() {
        return showCompletedBlocks;
    }

    public void setShowCompletedBlocks(boolean value) {
        showCompletedBlocks = value;
    }

    public boolean isShowRemainingBlocks() {
        return showRemainingBlocks;
    }

    public void setShowRemainingBlocks(boolean value) {
        showRemainingBlocks = value;
    }

    public int getScanLimit() {
        return scanLimit;
    }

    public void setScanLimit(int limit) {
        scanLimit = Math.max(1000, Math.min(1000000, limit));
    }

    public void reset() {
        materialTracker = true;
        placementGuide = true;
        crafterAssistant = true;
        recipeDetection = true;
        autoIngredient = true;
        worldScanner = true;
        visualGuide = true;

        showCompletedBlocks = false;
        showRemainingBlocks = true;
        scanLimit = 250000;
    }
}
