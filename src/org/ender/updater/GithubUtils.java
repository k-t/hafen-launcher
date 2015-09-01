package org.ender.updater;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class GithubUtils {
    public static String getLatestReleaseName(String user, String repoName) throws IOException {
        // follow redirect from the static URL for the latest release and return destination
        URL url = new URL(String.format("https://github.com/%s/%s/releases/latest", user, repoName));
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.getInputStream();
        // releases/tag/r1 -> r1
        return new File(conn.getURL().getPath()).getName();
    }

    public static URL getAssetDownloadUrl(
            String user,
            String repoName,
            String releaseName,
            String assetFileName) throws IOException {
        return new URL(String.format("https://github.com/%s/%s/releases/download/%s/%s",
                user, repoName, releaseName, assetFileName));
    }
}
