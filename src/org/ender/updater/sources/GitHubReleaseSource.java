package org.ender.updater.sources;

import org.ender.updater.ItemSource;
import org.ender.updater.util.GitHubRepository;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class GitHubReleaseSource implements ItemSource {
    private final GitHubRepository repo;
    private final String fileName;
    private URL url;

    public GitHubReleaseSource(String owner, String repoName, String fileName) throws MalformedURLException {
        this(new GitHubRepository(owner, repoName), fileName);
    }

    public GitHubReleaseSource(GitHubRepository repo, String fileName) {
        this.repo = repo;
        this.fileName = fileName;
    }

    @Override
    public URL getUrl() {
        try {
            if (url == null)
                url = resolve();
            return url;
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public boolean supportsHeadRequests() {
        // HEAD requests don't work quite well with GitHub releases right now
        return false;
    }

    private URL resolve() throws IOException {
        String releaseName = repo.getLatestReleaseName();
        return repo.getAssetDownloadUrl(releaseName, fileName);
    }
}
