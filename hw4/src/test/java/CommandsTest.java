import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import server.CommandGet;
import server.CommandList;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

public class CommandsTest {

    private TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void codesTest() throws Exception {
        CommandList list = new CommandList();
        CommandGet get = new CommandGet();
        assertTrue(list.getCode() != get.getCode());
    }


    @Test
    public void listTest() throws Exception {
        folder.create();
        Path file1 = folder.newFile("file1").toPath();
        Path file2 = folder.newFile("file2").toPath();
        Path dir = folder.newFolder("dir").toPath();

        String path = folder.getRoot().getAbsolutePath();
        CommandList command = new CommandList();
        byte[] content = command.execute(Paths.get(path));
        List<String> list = CommandList.fromBytes(content);

        assertEquals(3, list.size());
        assertTrue(list.contains(file1.getFileName().toString()));
        assertTrue(list.contains(file2.getFileName().toString()));
        assertTrue(list.contains(dir.getFileName().toString()));
    }

    @Test
    public void getTest() throws Exception {
        folder.create();
        Path file = folder.newFile("file").toPath();
        byte[] data = new byte[10000];
        new Random().nextBytes(data);
        Files.write(file, data);

        String path = file.toAbsolutePath().toString();
        CommandGet command = new CommandGet();
        byte[] content = command.execute(Paths.get(path));

        assertArrayEquals(data, content);
    }
}
