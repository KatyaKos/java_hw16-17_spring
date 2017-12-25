import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * Created by KatyaKos on 30.03.2017.
 */
public class ForkJoinMD5 implements InterfaceMD5{

    @Override
    public byte[] getDirMD5(String directory) throws Exception {
        File dir = new File(directory);
        RecursiveTaskMD5 task = new RecursiveTaskMD5(dir);
        ForkJoinPool pool = new ForkJoinPool();
        return pool.invoke(task);

    }

    private class RecursiveTaskMD5 extends RecursiveTask<byte[]> {

        private File dir;

        RecursiveTaskMD5(File dir) {
            this.dir = dir;
        }

        @Override
        protected byte[] compute() {
            try {
                MessageDigest result = MessageDigest.getInstance("MD5");
                result.update(dir.getName().toString().getBytes());
                Files.list(dir.toPath()).forEach((p) -> {
                            RecursiveTaskMD5 subtask = new RecursiveTaskMD5(p.toFile());
                            subtask.fork();
                            result.update(subtask.join());
                });
                return result.digest();
            } catch(Exception e) {
                e.printStackTrace();
                System.out.println("Cannot read file");
            }
            return new byte[0];
        }
    }


}
