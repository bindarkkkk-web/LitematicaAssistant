
package bindarkkk.litematicaassistant;

import net.minecraft.core.BlockPos;

public class BuildAssistant {

    public enum State {
        IDLE, READY, RUNNING, PAUSED, COMPLETED
    }

    private final MaterialTracker materials = new MaterialTracker();
    private final PlacementGuide guide = new PlacementGuide();
    private final BuildQueue queue = new BuildQueue();
    private final ProgressTracker progress = new ProgressTracker();
    private final CrafterAssistant crafters = new CrafterAssistant();

    private LitematicaBridge.ScanResult loadedSchematic;
    private State state = State.IDLE;

    public boolean load(LitematicaBridge.ScanResult result) {
        if (result == null || !result.success()) return false;

        loadedSchematic = result;

        materials.load(result);
        guide.load(result);
        queue.load(result);
        progress.load(result);
        crafters.clear();

        state = result.totalBlocks() > 0
                ? State.READY : State.IDLE;

        return true;
    }

    public void start() {
        if (state == State.READY || state == State.PAUSED) {
            state = State.RUNNING;
        }
    }

    public void pause() {
        if (state == State.RUNNING) {
            state = State.PAUSED;
        }
    }

    public void stop() {
        if (loadedSchematic == null) {
            state = State.IDLE;
            return;
        }

        materials.reset();
        guide.reset();
        queue.reset();
        progress.reset();

        // Muat ulang progress agar total block tetap terjaga.
        progress.load(loadedSchematic);

        state = State.READY;
    }

    public boolean markCompleted(BlockPos position, String blockId) {
        if (state != State.RUNNING) return false;

        if (!progress.markCompleted(position, blockId)) {
            return false;
        }

        materials.markPlaced(position, blockId);
        queue.complete(position);
        guide.markCompleted(position);

        if (progress.isFinished()) {
            state = State.COMPLETED;
        }

        return true;
    }

    public boolean reset() {
        if (loadedSchematic == null) return false;
        return load(loadedSchematic);
    }

    public State getState() {
        return state;
    }

    public MaterialTracker getMaterials() {
        return materials;
    }

    public PlacementGuide getGuide() {
        return guide;
    }

    public BuildQueue getQueue() {
        return queue;
    }

    public ProgressTracker getProgress() {
        return progress;
    }

    public CrafterAssistant getCrafters() {
        return crafters;
    }
}
