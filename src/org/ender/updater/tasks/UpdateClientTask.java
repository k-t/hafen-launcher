package org.ender.updater.tasks;

import org.ender.updater.*;

import java.util.ArrayList;
import java.util.List;

public class UpdateClientTask implements Task {
    private final UpdaterConfig config;
    private int updatedFileCount;

    public UpdateClientTask(UpdaterConfig config) {
        this.config = config;
    }

    public int getUpdatedFileCount() {
        return updatedFileCount;
    }

    @Override
    public void run(TaskListener listener) {
        listener.step("Checking for updates...");

        List<Item> update = new ArrayList<Item>();
        updatedFileCount = 0;

        for(Item item : config.items){
            if(!item.hasValidPlatform())
                continue;
            if (item.hasUpdate()){
                listener.log(String.format("Updates found for '%s'", item.getFileName()));
                update.add(item);
            } else {
                listener.log(String.format("No updates for '%s'", item.getFileName()));
            }
        }
        for (Item item: update) {
            item.download(listener);
            if (item.requiresExtraction())
                item.extract(listener);
            updatedFileCount++;
        }
    }
}
