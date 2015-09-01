package org.ender.updater;

import org.w3c.dom.Element;

import java.io.File;
import java.net.MalformedURLException;

public class ItemFactory {
    private static final String EXTRACT = "extract";
    private static final String ARCH = "arch";
    private static final String OS = "os";
    private static final String FILE = "file";
    private static final String LINK = "link";

    public Item create(Element el) throws MalformedURLException {

        String os = el.getAttribute(OS);
        String arch = el.getAttribute(ARCH);

        String e = el.getAttribute(EXTRACT);
        File extract = e.length() > 0 ? new File(UpdaterConfig.dir, e) : null;

        String url = el.getAttribute(LINK);

        File file;
        if (el.hasAttribute(FILE)) {
            file = new File(UpdaterConfig.dir, el.getAttribute(FILE));
        } else {
            int i = url.lastIndexOf("/");
            file = new File(UpdaterConfig.dir, url.substring(i + 1));
        }

        return new Item(arch, os, file, extract, url);
    }
}
