import exceptions.*;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import org.junit.Before;
import org.junit.Test;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by KatyaKos on 23.03.2017.
 */
public class VCSManagerTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private Path root;
    private byte[] byte1;
    private byte[] byte2;

    @Before
    public void setup() throws Exception {
        byte1 = new byte[10];
        byte2 = new byte[10];
        for (int i = 0; i < 10; i++) {
            byte1[i] = 0;
            byte2[i] = 1;
        }
        root = folder.getRoot().toPath();
        Files.createFile(root.resolve("file"));
        OutputStream outputStream = Files.newOutputStream(root.resolve("file"));
        outputStream.write(byte1);
        outputStream.close();
        Files.createDirectory(root.resolve("dir"));
        Files.createFile(root.resolve("dir").resolve("file"));
        outputStream = Files.newOutputStream(root.resolve("dir").resolve("file"));
        outputStream.write(byte2);
        outputStream.close();
    }

    @Test
    public void initRepository() throws Exception {
        VCSManager.initRepository(root);
        assertTrue(Files.exists(root.resolve(VCSConstants.myVCSDirectory)));
        assertTrue(Files.isDirectory(root.resolve(VCSConstants.myVCSDirectory)));
        assertTrue(Files.exists(root.resolve(VCSConstants.branchesDirectory)));
        assertTrue(Files.isDirectory(root.resolve(VCSConstants.branchesDirectory)));
        assertTrue(Files.exists(root.resolve(VCSConstants.objectsDirectory)));
        assertTrue(Files.isDirectory(root.resolve(VCSConstants.objectsDirectory)));
        assertTrue(Files.exists(root.resolve(VCSConstants.head)));
        assertFalse(Files.isDirectory(root.resolve(VCSConstants.head)));
        assertTrue(Files.exists(root.resolve(VCSConstants.index)));
        assertFalse(Files.isDirectory(root.resolve(VCSConstants.index)));
    }

    private void addCreatedFiles(VCSManager repositoryManager) throws Exception {
        repositoryManager.add(root.resolve("file"));
        repositoryManager.add(root.resolve("dir").resolve("file"));
    }

    @Test(expected = RepositoryAlreadyExistsException.class)
    public void initRepositoryTwoTimes() throws Exception {
        VCSManager.initRepository(root);
        VCSManager.initRepository(root);
    }

    @Test
    public void deleteRepository() throws Exception {
        VCSManager.initRepository(root);
        VCSManager.removeRepository(root);
        assertFalse(Files.exists(root.resolve(VCSConstants.myVCSDirectory)));
        VCSManager.initRepository(root);
    }

    @Test(expected = RepositoryNotInitializedException.class)
    public void getRepositoryManagerInNotInitialized() throws Exception {
        VCSManager repositoryManager = VCSManager.getRepositoryManager(root);
    }

    @Test(expected = FileNotFoundException.class)
    public void addNotExistingFile() throws Exception {
        VCSManager repositoryManager = VCSManager.initRepository(root);
        repositoryManager.add(root.resolve("fil"));
    }

    @Test(expected = WrongFileLocationException.class)
    public void addFromAnotherDirectory() throws Exception {
        VCSManager repositoryManager = VCSManager.initRepository(root);
        repositoryManager.add(Paths.get("file"));
    }

    @Test(expected = NotFileProvidedException.class)
    public void addDirectory() throws Exception {
        VCSManager repositoryManager = VCSManager.initRepository(root);
        repositoryManager.add(root.resolve("dir"));
    }

    @Test
    public void addSeveralFilesAndCheckIndex() throws Exception {
        VCSManager.initRepository(root);
        addCreatedFiles(VCSManager.getRepositoryManager(root));

        List<String> lines = Files.readAllLines(root.resolve(VCSConstants.index));
        assertEquals(2, lines.size());
        String[] line1 = lines.get(0).split(" ");
        assertEquals(2, line1.length);
        assertEquals(root.resolve("file").toString(), line1[0]);
        String[] line2 = lines.get(1).split(" ");
        assertEquals(2, line2.length);
        assertEquals(root.resolve("dir").resolve("file").toString(), line2[0]);
    }

    @Test
    public void addSeveralFilesAndCommit() throws Exception {
        VCSManager.initRepository(root);
        addCreatedFiles(VCSManager.getRepositoryManager(root));
        List<String> lines = Files.readAllLines(root.resolve(VCSConstants.index));
        assertEquals(2, lines.size());
        String[] line1 = lines.get(0).split(" ");
        assertEquals(2, line1.length);
        assertEquals(root.resolve("file").toString(), line1[0]);
        String[] line2 = lines.get(1).split(" ");
        assertEquals(2, line2.length);
        assertEquals(root.resolve("dir").resolve("file").toString(), line2[0]);

        VCSManager.getRepositoryManager(root).commit("first commit");
    }

    @Test(expected = FileNotFoundException.class)
    public void checkoutNotExistingBranch() throws Exception {
        VCSManager repositoryManager = VCSManager.initRepository(root);
        addCreatedFiles(VCSManager.getRepositoryManager(root));
        repositoryManager.commit("first commit");
        repositoryManager.checkout("not_master");
    }

    @Test
    public void deleteFilesAndReturnWithCheckout() throws Exception {
        VCSManager.initRepository(root);
        addCreatedFiles(VCSManager.getRepositoryManager(root));
        VCSManager.getRepositoryManager(root).commit("first commit");
        Files.delete(root.resolve("file"));
        Files.delete(root.resolve("dir").resolve("file"));
        Files.delete(root.resolve("dir"));
        VCSManager.getRepositoryManager(root).checkout("master");
        assertTrue(Files.exists(root.resolve("file")));
        assertTrue(Files.exists(root.resolve("dir").resolve("file")));
    }

    @Test(expected = BranchAlreadyExistsException.class)
    public void createBranchWithExistingName() throws Exception {
        VCSManager.initRepository(root);
        VCSManager.getRepositoryManager(root).createBranch("master");
    }

    @Test
    public void createAndRemoveBranch() throws Exception {
        VCSManager.initRepository(root);
        VCSManager.getRepositoryManager(root).createBranch("second");
        VCSManager.getRepositoryManager(root).removeBranch("second");
    }

    @Test
    public void createBranchCheckoutAndAddSomeFiles() throws Exception {
        VCSManager.initRepository(root);
        VCSManager.getRepositoryManager(root).createBranch("second");
        VCSManager.getRepositoryManager(root).checkout("second");
        addCreatedFiles(VCSManager.getRepositoryManager(root));
        VCSManager.getRepositoryManager(root).commit("commit in second branch");
    }

    @Test
    /* Tests that files appear after checkout. */
    public void backAndForthCheckout() throws Exception {
        VCSManager.initRepository(root);
        VCSManager.getRepositoryManager(root).createBranch("second");
        VCSManager.getRepositoryManager(root).checkout("second");
        addCreatedFiles(VCSManager.getRepositoryManager(root));
        VCSManager.getRepositoryManager(root).commit("commit in second branch");
        VCSManager.getRepositoryManager(root).checkout("master");
        Files.delete(root.resolve("file"));
        Files.delete(root.resolve("dir").resolve("file"));
        Files.delete(root.resolve("dir"));
        VCSManager.getRepositoryManager(root).checkout("second");
        assertTrue(Files.exists(root.resolve("file")));
        assertTrue(Files.exists(root.resolve("dir").resolve("file")));
    }

    @Test
     /* Tests that files from another branch appear after merge. */
    public void checkoutAndMergeTest() throws Exception {
        VCSManager.initRepository(root);
        VCSManager.getRepositoryManager(root).createBranch("second");
        VCSManager.getRepositoryManager(root).checkout("second");
        addCreatedFiles(VCSManager.getRepositoryManager(root));
        VCSManager.getRepositoryManager(root).commit("commit in second branch");
        VCSManager.getRepositoryManager(root).checkout("master");
        Files.delete(root.resolve("file"));
        Files.delete(root.resolve("dir").resolve("file"));
        Files.delete(root.resolve("dir"));
        VCSManager.getRepositoryManager(root).merge("second");
        assertTrue(Files.exists(root.resolve("file")));
        assertTrue(Files.exists(root.resolve("dir").resolve("file")));
    }

    @Test
    public void testClean() throws Exception {
        VCSManager.initRepository(root);
        VCSManager.getRepositoryManager(root).add(root.resolve("file"));
        VCSManager.getRepositoryManager(root).clean();
        assertTrue(Files.exists(root.resolve("file")));
        assertFalse(Files.exists(root.resolve("dir").resolve("file")));
    }

}