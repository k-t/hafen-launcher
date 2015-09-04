package org.ender.updater.tasks;

import org.ender.updater.*;

import java.util.ArrayList;
import java.util.List;

public class UpdateClientTask implements UpdaterTask {
    private final UpdaterConfig config;

    public UpdateClientTask(UpdaterConfig config) {
        this.config = config;
    }

    @Override
    public void run(UpdaterListener listener) {
        List<Item> update = new ArrayList<Item>();

        listener.step("Checking for updates...");

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
        }
    }
}
