package org.ender.updater.sources;

import org.ender.updater.ItemSource;

import java.net.MalformedURLException;
import java.net.URL;

public class UrlSource implements ItemSource {
    private final URL url;

    public UrlSource(String url) throws MalformedURLException {
        this(new URL(url));
    }

    public UrlSource(URL url) {
        this.url = url;
    }

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public boolean supportsHeadRequests() {
        return true;
    }
}
