package org.ender.updater;

import org.ender.updater.tasks.TaskListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Item {
    private final String arch;
    private final String os;
    private final File file;
    private final ItemSource src;
    private final File extract;
    private long size = 0;

    public Item(String arch, String os, File file, File extract, ItemSource src) {
        this.arch = arch;
        this.os = os;
        this.file = file;
        this.extract = extract;
        this.src = src;
    }

    public long getLastModified() {
        return file.exists() ? file.lastModified() : 0;
    }

    public String getFileName() {
        return file.getName();
    }

    public boolean requiresExtraction() {
        return extract != null;
    }

    public boolean hasValidPlatform() {
        String os = System.getProperty("os.name");
        String arch = System.getProperty("os.arch");
        return (os.indexOf(this.os) >= 0) && (arch.equals(this.arch) || this.arch.length() == 0);
    }

    public boolean hasUpdate() {
        try {
            boolean useHeadRequest = src.supportsHeadRequests();
            URL url = src.getUrl();
            if (url == null)
                // TODO: throw exception?
                return false;
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            if (useHeadRequest) {
                conn.setRequestMethod("HEAD");
                conn.setIfModifiedSince(getLastModified());
            }
            try {
                int response = conn.getResponseCode();
                if (response == HttpURLConnection.HTTP_OK && (useHeadRequest || conn.getLastModified() > getLastModified())) {
                    size = Long.parseLong(conn.getHeaderField("Content-Length"));
                    return true;
                }
            } catch (NumberFormatException e) {
            }
            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void download(TaskListener listener) {
        listener.step(String.format("Downloading %s...", file.getName()));
        try {
            ReadableByteChannel rbc = Channels.newChannel(src.getUrl().openStream());
            FileOutputStream fos = new FileOutputStream(file);
            long position = 0;
            int step = 20480;
            listener.progress(position, size);
            while(position < size){
                position += fos.getChannel().transferFrom(rbc, position, step);
                listener.progress(position, size);
            }
            listener.progress(0, size);
            fos.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void extract(TaskListener listener) {
        listener.step(String.format("Unpacking %s...", file.getName()));
        try {
            ZipFile zip;
            zip = new ZipFile(file);
            Enumeration<? extends ZipEntry> contents = zip.entries();
            while (contents.hasMoreElements()) {
                ZipEntry file = (ZipEntry)contents.nextElement();
                String name = file.getName();
                if(name.indexOf("META-INF") == 0){continue;}
                listener.log("\t"+name);
                ReadableByteChannel rbc = Channels.newChannel(zip.getInputStream(file));
                FileOutputStream fos = new FileOutputStream(new File(extract, name));
                long position = 0;
                long size = file.getSize();
                int step = 20480;
                while(position < size){
                    position += fos.getChannel().transferFrom(rbc, position, step);
                }
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
