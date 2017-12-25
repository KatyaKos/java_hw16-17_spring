import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;

/**
 * Created by KatyaKos on 30.03.2017.
 */
public class MD5Test {
    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    private MessageDigest md;
    private MD5 simpleMD = new MD5();

    @Test
    public void simpleFileTest() throws IOException {
        File oneFile = tmp.newFile("file1");
        Files.write(oneFile.toPath(), "Hello!".getBytes());
        byte[] res = null;
        try {
            md = MessageDigest.getInstance("MD5");
            res = simpleMD.getFileMD5(oneFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        md.update("Hello!".getBytes());
        assertArrayEquals(md.digest(), res);
    }

    @Test
    public void simpleDirTest() throws IOException {
        File root = tmp.newFolder("root");
        byte[] res = null;
        try {
            md = MessageDigest.getInstance("MD5");
            res = simpleMD.getDirMD5(root.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        md.update("root".getBytes());
        assertArrayEquals(md.digest(), res);
    }
}