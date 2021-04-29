package core.activities.ui.shared;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Async {

    private static ExecutorService executorService = Executors.newSingleThreadExecutor();

    public static void execute(Runnable r) {
        executorService.execute(r);
    }
}
