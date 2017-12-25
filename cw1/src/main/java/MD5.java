import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * Created by KatyaKos on 30.03.2017.
 */
public class MD5 implements InterfaceMD5{

    @Override
    public byte[] getDirMD5(String directory) throws Exception {
        File dir = new File(directory);
        ArrayList<String> files = new ArrayList<String>();
        findFilesInDir(dir, files);
        MessageDigest result = MessageDigest.getInstance("MD5");
        result.update(dir.getName().getBytes());
        for (String file : files) {
            result.update(getFileMD5(file));
        }
        return result.digest();
    }

    private void findFilesInDir(File dir, ArrayList<String> files) {
        File[] filesList = dir.listFiles();
        for (File file : filesList) {
            if (file.isDirectory()) {
                findFilesInDir(file, files);
            } else {
                files.add(file.getAbsolutePath());
            }
        }
    }
}
