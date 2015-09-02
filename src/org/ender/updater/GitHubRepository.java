package org.ender.updater;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GitHubRepository {
    private static final String REPOSITORY_URL = "https://github.com/%s/%s/";
    private static final String LATEST_RELEASE_PATH = "releases/latest";
    private static final String ASSET_DOWNLOAD_PATH = "releases/download/%s/%s";

    private final URL baseUrl;

    public GitHubRepository(String owner, String name) throws MalformedURLException {
        this(new URL(String.format(REPOSITORY_URL, owner, name)));
    }

    public GitHubRepository(URL url) {
        this.baseUrl = url;
    }

    public String getLatestReleaseName() throws IOException {
        // follow redirect from the static URL for the latest release and return destination
        URL url = new URL(baseUrl, LATEST_RELEASE_PATH);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.getInputStream();
        // releases/tag/r1 -> r1
        return new File(conn.getURL().getPath()).getName();
    }

    public URL getAssetDownloadUrl(String releaseName, String assetFileName) throws IOException {
        return new URL(baseUrl, String.format(ASSET_DOWNLOAD_PATH, releaseName, assetFileName));
    }
}
