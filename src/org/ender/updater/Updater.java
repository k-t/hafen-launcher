package org.ender.updater;

import java.util.ArrayList;
import java.util.List;

public class Updater {
    public UpdaterConfig cfg;
    private IUpdaterListener listener;
    public Updater(IUpdaterListener listener){
	this.listener = listener;
	cfg = new UpdaterConfig();
    }

    public void update() {
	Thread t = new Thread(new Runnable() {

	    @Override
	    public void run() {
		List<Item> update = new ArrayList<Item>();
		for(Item item : cfg.items){
		    if(!item.hasValidPlatform())
				continue;
		    if (item.hasUpdate()){
				listener.log(String.format("Updates found for '%s'", item.getFileName()));
				update.add(item);
		    } else {
				listener.log(String.format("No updates for '%s'", item.getFileName()));
		    }
		}
		for (Item item: update){
		    item.download(listener);
		    if (item.requiresExtraction())
				item.extract(listener);
		}
		
		listener.fisnished();
	    }
	});
	t.setDaemon(true);
	t.start();
    }
}