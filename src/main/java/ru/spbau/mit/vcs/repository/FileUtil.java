package ru.spbau.mit.vcs.repository;

import org.apache.commons.io.filefilter.AbstractFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import ru.spbau.mit.vcs.VCS;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;

import static org.apache.commons.io.FileUtils.listFiles;

public class FileUtil {

    public static String calculateSha1(File file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            try (InputStream in = new FileInputStream(file)) {
                byte[] buffer = new byte[8192];
                int n = in.read(buffer);
                while (n != -1) {
                    digest.update(buffer, 0, n);
                    n = in.read(buffer);
                }
            }
            return new String(digest.digest());
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Collection<File> listExternalFiles(File directory) {
        return listFiles(directory, TrueFileFilter.INSTANCE, new AbstractFileFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return !VCS.FOLDER.equals(name);
            }
        });
    }
}
