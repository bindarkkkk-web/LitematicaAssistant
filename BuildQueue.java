
package bindarkkk.litematicaassistant;

import net.minecraft.core.BlockPos;
import java.util.*;

public class BuildQueue {

    public enum Status {
        WAITING, ACTIVE, COMPLETED, SKIPPED
    }

    public record QueueEntry(
            int id,
            BlockPos position,
            String blockId,
            boolean crafter,
            Status status
    ) {}

    private final LinkedHashMap<BlockPos, QueueEntry> entries =
            new LinkedHashMap<>();

    private int nextId = 1;

    public void load(LitematicaBridge.ScanResult result) {
        clear();

        if (result == null || !result.success()) return;

        for (var block : result.blocks()) {
            if (block == null || block.pos() == null) continue;

            entries.put(block.pos(), new QueueEntry(
                    nextId++,
                    block.pos(),
                    block.blockId(),
                    block.crafter(),
                    Status.WAITING
            ));
        }

        activateNext();
    }

    public QueueEntry getNext() {
        for (QueueEntry entry : entries.values()) {
            if (entry.status() == Status.ACTIVE) return entry;
        }
        return null;
    }

    public boolean complete(BlockPos position) {
        return updateStatus(position, Status.COMPLETED);
    }

    public boolean skip(BlockPos position) {
        return updateStatus(position, Status.SKIPPED);
    }

    public boolean undo(BlockPos position) {
        QueueEntry old = entries.get(position);

        if (old == null || old.status() == Status.WAITING) {
            return false;
        }

        entries.put(position, copy(old, Status.WAITING));
        activateNext();
        return true;
    }

    private boolean updateStatus(BlockPos position, Status status) {
        QueueEntry old = entries.get(position);

        if (old == null || old.status() == Status.COMPLETED
                || old.status() == Status.SKIPPED) {
            return false;
        }

        entries.put(position, copy(old, status));
        activateNext();
        return true;
    }

    private QueueEntry copy(QueueEntry old, Status status) {
        return new QueueEntry(
                old.id(),
                old.position(),
                old.blockId(),
                old.crafter(),
                status
        );
    }

    private void activateNext() {
        BlockPos activePosition = null;

        for (QueueEntry entry : entries.values()) {
            if (entry.status() == Status.ACTIVE) {
                activePosition = entry.position();
                break;
            }
        }

        if (activePosition != null) {
            QueueEntry old = entries.get(activePosition);
            entries.put(activePosition, copy(old, Status.WAITING));
        }

        BlockPos nextPosition = null;

        for (QueueEntry entry : entries.values()) {
            if (entry.status() == Status.WAITING) {
                nextPosition = entry.position();
                break;
            }
        }

        if (nextPosition != null) {
            QueueEntry old = entries.get(nextPosition);
            entries.put(nextPosition, copy(old, Status.ACTIVE));
        }
    }

    public List<QueueEntry> getAll() {
        return Collections.unmodifiableList(
                new ArrayList<>(entries.values())
        );
    }

    public int getTotal() {
        return entries.size();
    }

    public int getCompleted() {
        return count(Status.COMPLETED);
    }

    public int getWaiting() {
        return count(Status.WAITING);
    }

    public int getSkipped() {
        return count(Status.SKIPPED);
    }

    private int count(Status status) {
        return (int) entries.values().stream()
                .filter(e -> e.status() == status)
                .count();
    }

    public void reset() {
        for (QueueEntry entry : new ArrayList<>(entries.values())) {
            entries.put(entry.position(), copy(entry, Status.WAITING));
        }
        activateNext();
    }

    public void clear() {
        entries.clear();
        nextId = 1;
    }
}
