package space.peetseater.rename.gui.workers;

import java.util.List;

public interface WorkerListener<WorkerInProgressType> {
    void onChunk(List<WorkerInProgressType> chunk);
}
