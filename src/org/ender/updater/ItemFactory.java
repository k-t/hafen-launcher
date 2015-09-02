package org.ender.updater;

import org.w3c.dom.Element;

import java.io.File;
import java.io.IOException;

public class ItemFactory {
    private static final String EXTRACT = "extract";
    private static final String ARCH = "arch";
    private static final String OS = "os";
    private static final String FILE = "file";
    private static final String LINK = "link";
    private static final String TYPE = "type";

    private static final String TYPE_LINK = "link";
    private static final String TYPE_GITHUB_RELEASE = "github-release";

    public Item create(Element el) throws IOException {
        String os = el.getAttribute(OS);
        String arch = el.getAttribute(ARCH);
        String url = getUrl(el);
        // HEAD requests don't work quite well with GitHub releases
        boolean useHeadRequest = !getType(el).equals(TYPE_GITHUB_RELEASE);

        String e = el.getAttribute(EXTRACT);
        File extract = e.length() > 0 ? new File(UpdaterConfig.dir, e) : null;

        File file;
        if (el.hasAttribute(FILE)) {
            file = new File(UpdaterConfig.dir, el.getAttribute(FILE));
        } else {
            int i = url.lastIndexOf("/");
            file = new File(UpdaterConfig.dir, url.substring(i + 1));
        }

        return new Item(arch, os, file, extract, url, useHeadRequest);
    }

    private String getUrl(Element el) throws IOException {
        String type = getType(el);
        if (type.equals(TYPE_GITHUB_RELEASE)) {
            String owner = el.getAttribute("owner");
            String repoName = el.getAttribute("repo");
            String fileName = el.getAttribute("file");
            GitHubRepository repo = new GitHubRepository(owner, repoName);
            String releaseName = repo.getLatestReleaseName();
            return repo.getAssetDownloadUrl(releaseName, fileName).toString();
        }
        return el.getAttribute(LINK);
    }

    private String getType(Element el) {
        return el.hasAttribute(TYPE) ? el.getAttribute(TYPE) : TYPE_LINK;
    }
}
