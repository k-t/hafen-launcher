package org.ender.updater;

import java.net.URL;

public interface ItemSource {
    boolean supportsHeadRequests();
    URL getUrl();
}
