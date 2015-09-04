package org.ender.updater;

import org.ender.updater.sources.*;
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

    private final UpdaterConfig config;

    public ItemFactory(UpdaterConfig config) {
        this.config = config;
    }

    public Item create(Element el) throws IOException {
        String os = el.getAttribute(OS);
        String arch = el.getAttribute(ARCH);
        ItemSource src = getSource(el);

        String e = el.getAttribute(EXTRACT);
        File extract = e.length() > 0 ? new File(config.dir, e) : null;

        File file;
        if (el.hasAttribute(FILE)) {
            file = new File(config.dir, el.getAttribute(FILE));
        } else {
            String url = src.getUrl().toString();
            int i = url.lastIndexOf("/");
            file = new File(config.dir, url.substring(i + 1));
        }

        return new Item(arch, os, file, extract, src);
    }

    private ItemSource getSource(Element el) throws IOException {
        String type = getType(el);
        if (type.equals(TYPE_GITHUB_RELEASE)) {
            String owner = el.getAttribute("owner");
            String repoName = el.getAttribute("repo");
            String fileName = el.getAttribute("file");
            return new GitHubReleaseSource(owner, repoName, fileName);
        } else {
            String url = el.getAttribute(LINK);
            return new UrlSource(url);
        }
    }

    private String getType(Element el) {
        return el.hasAttribute(TYPE) ? el.getAttribute(TYPE) : TYPE_LINK;
    }
}
