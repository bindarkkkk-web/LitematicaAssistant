
package bindarkkk.litematicaassistant;

public class BuildController {

    private final BuildAssistant assistant;

    public BuildController(BuildAssistant assistant) {
        if (assistant == null) {
            throw new IllegalArgumentException(
                    "BuildAssistant tidak boleh null"
            );
        }

        this.assistant = assistant;
    }

    public boolean start() {
        if (assistant.getState() != BuildAssistant.State.READY
                && assistant.getState() != BuildAssistant.State.PAUSED) {
            return false;
        }

        assistant.start();

        return assistant.getState() == BuildAssistant.State.RUNNING;
    }

    public boolean pause() {
        if (assistant.getState() != BuildAssistant.State.RUNNING) {
            return false;
        }

        assistant.pause();

        return assistant.getState() == BuildAssistant.State.PAUSED;
    }

    public void stop() {
        assistant.stop();
    }

    public void reset() {
        assistant.reset();
    }

    public boolean isRunning() {
        return assistant.getState() == BuildAssistant.State.RUNNING;
    }

    public boolean isPaused() {
        return assistant.getState() == BuildAssistant.State.PAUSED;
    }

    public boolean isFinished() {
        return assistant.getState() == BuildAssistant.State.COMPLETED;
    }

    public BuildAssistant.State getState() {
        return assistant.getState();
    }

    public BuildAssistant getAssistant() {
        return assistant;
    }
}
