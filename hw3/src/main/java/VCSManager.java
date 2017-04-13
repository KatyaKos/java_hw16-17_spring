import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import exceptions.*;

/**
 * Represents a repository in VCS. Provides access to all functions that work with it.
 */
class VCSManager {
    private Path root;
    private List<VCSBranch> branches;

    private VCSManager(@NotNull Path path) {
        root = path;
        branches = new ArrayList<>();
    }

    /**
     * Creates a repository in given directory, makes initial commit and sets branch to "master".
     * @param path directory where the rep should be created
     * @return a manager that represents created rep
     * @throws IOException if IO went wrong
     * @throws RepositoryAlreadyExistsException if the rep already exists in given directory
     */
    static VCSManager initRepository(@NotNull Path path) throws IOException, RepositoryAlreadyExistsException {
        if (Files.exists(path.resolve(VCSConstants.myVCSDirectory))) {
            throw new RepositoryAlreadyExistsException();
        }
        Files.createDirectory(path.resolve(VCSConstants.myVCSDirectory));
        Files.createDirectory(path.resolve(VCSConstants.objectsDirectory));
        Files.createDirectory(path.resolve(VCSConstants.branchesDirectory));
        Files.createFile(path.resolve(VCSConstants.index));
        Files.createFile(path.resolve(VCSConstants.head));

        VCSManager repositoryManager = new VCSManager(path);
        repositoryManager.initialCommit();

        return repositoryManager;
    }

    /**
     * Removes repository in given directory.
     * @param path directory where the repository should be removed
     * @throws IOException if IO went wrong
     */
    static void removeRepository(@NotNull Path path) throws IOException {
        FileUtils.deleteDirectory(path.resolve(VCSConstants.myVCSDirectory).toFile());
    }

    /**
     * Creates a manager representing repository in given directory. Repository should be initialized already.
     * @param path directory containing a repository
     * @return a manager that represents repository
     * @throws RepositoryNotInitializedException if there is no repository in given directory
     * @throws IOException if IO went wrong
     * @throws VCSFilesBrokenException if something happened to VCS files
     */
    static VCSManager getRepositoryManager(@NotNull Path path) throws RepositoryNotInitializedException, IOException, VCSFilesBrokenException {
        if (!Files.isDirectory(path)) {
            throw new NotDirectoryException(path.toString());
        }
        Path myGitDir = path.resolve(VCSConstants.myVCSDirectory);
        Path objDir = path.resolve(VCSConstants.objectsDirectory);
        Path branchesDir = path.resolve(VCSConstants.branchesDirectory);
        Path indexFile = path.resolve(VCSConstants.index);
        Path headFile = path.resolve(VCSConstants.head);
        if (Files.notExists(myGitDir) || !Files.isDirectory(myGitDir)) {
            throw new RepositoryNotInitializedException();
        }
        if (Files.notExists(objDir) || !Files.isDirectory(objDir)
                || Files.notExists(branchesDir) || !Files.isDirectory(branchesDir)
                || Files.notExists(indexFile) || Files.isDirectory(indexFile)
                || Files.notExists(headFile) || Files.isDirectory(headFile)){
            throw new VCSFilesBrokenException();
        }
        final VCSManager repositoryManager = new VCSManager(path);
        Files.walk(branchesDir).forEach(
                (p) -> {
                    if (!Files.isDirectory(p)) {
                        try {
                            VCSBranch branch = (VCSBranch) VCSObject.read(p);
                            repositoryManager.addBranch(branch);
                        } catch (IOException e) {
                            e.printStackTrace();
                            System.out.println("Something went wrong during reading from file " + p.toString());
                        }
                    }
                }
        );
        return repositoryManager;
    }

    /**
     * Adds given file to repository.
     * Current version of the file will be saved in next commit, unless you checkout something before the commit.
     * @param path file that should be added
     * @throws IOException if IO went wrong
     * @throws WrongFileLocationException if file is in another directory
     * @throws FileNotFoundException if file that should be added doesn't exist
     * @throws NotFileProvidedException if directory instead of file was provided
     * @throws IndexFileBrokenException if something happened to index file
     */
    void add(@NotNull Path path) throws IOException, WrongFileLocationException, FileNotFoundException, NotFileProvidedException, IndexFileBrokenException {
        if (!path.startsWith(root)) {
            throw new WrongFileLocationException();
        }
        if (!Files.exists(path)) {
            throw new FileNotFoundException();
        }
        if (Files.isDirectory(path)) {
            throw new NotFileProvidedException();
        }

        VCSBlob blob = new VCSBlob(root, Files.readAllBytes(path), path.getFileName().toString());

        List<String> lines = Files.readAllLines(getIndex());
        String hash = blob.getHash();
        StringBuilder file = new StringBuilder();
        for (String line: lines) {
            String[] strings = line.split(" ");
            if (strings.length != 2) {
                throw new IndexFileBrokenException();
            }
            if (strings[0].equals(path.toString())) {
                continue;
            }
            file.append(line + "\n");
        }
        file.append(path + " " + hash + "\n");

        OutputStream outputStream = Files.newOutputStream(getIndex());
        outputStream.write(file.toString().getBytes());
        outputStream.close();
    }

    /**
     * Commits changes that were added after the last commit/checkout. Author and date are saved automatically.
     * @param message text that goes with the commit
     * @throws IOException if IO went wrong
     * @throws IndexFileBrokenException if something happened to index file
     * @throws HeadFileBrokenException if something happened to HEAD file
     */
    void commit(@NotNull String message) throws IOException, IndexFileBrokenException, HeadFileBrokenException {
        List<String> lines = Files.readAllLines(getIndex());
        List<VCSPair> pathsAndHashes = new ArrayList<>();
        for (String line : lines) {
            String[] strings = line.split(" ");
            if (strings.length != 2) {
                throw new IndexFileBrokenException();
            }
            pathsAndHashes.add(new VCSPair(Paths.get(strings[0]), strings[1]));
        }
        VCSTree tree = buildCommitTree(pathsAndHashes);
        List<String> parents = new ArrayList<>();
        parents.add(getHEADCommit().getHash());
        VCSCommit commit = new VCSCommit(root, message, parents, tree);
        getHEADBranch().setCommit(commit.getHash());
        writeToHEAD(commit.getHash());
    }

    /**
     * Checkouts commit or the last commit of a branch.
     * All the files in directory that were also saved in that commit are replaced with their versions from commit.
     * All adds that weren't commited will be erased.
     * If you checkout commit, a new branch with commit's name will be created.
     * @param name name of a branch or a commit that you want to checkout
     * @throws IOException if IO went wrong
     * @throws FileNotFoundException if there is no branch or commit with given name
     */
    void checkout(@NotNull String name) throws IOException, FileNotFoundException {
        VCSBranch branch = getBranch(name);
        if (branch == null) {
            if (Files.notExists(getObjectsDir().resolve(name))) {
                throw new FileNotFoundException();
            }
            branch = new VCSBranch(root, name, name);
            branches.add(branch);
        }
        checkoutCommit(branch.getCommit());
        writeToHEAD(branch);
    }

    /**
     * Creates new branch with given name. It will copy current state of current branch.
     * @param name name of new branch
     * @throws IOException if IO went wrong
     * @throws BranchAlreadyExistsException if branch with given name already exists
     * @throws HeadFileBrokenException if something happened to HEAD file
     */
    void createBranch(@NotNull String name) throws IOException, BranchAlreadyExistsException, HeadFileBrokenException {
        if (getBranch(name) != null) {
            throw new BranchAlreadyExistsException();
        }
        branches.add(new VCSBranch(root, name, getHEADCommit().getHash()));
    }

    /**
     * Deletes branch with given name. Cannot delete current branch.
     * @param name name of branch to delete
     * @throws IOException if IO went wrong
     * @throws DeletingCurrentBranchException if you're trying to delete current branch
     * @throws HeadFileBrokenException if something happened to HEAD file
     */
    void removeBranch(@NotNull String name) throws IOException, DeletingCurrentBranchException, HeadFileBrokenException {
        if (getHEADBranch().getName().equals(name)) {
            throw new DeletingCurrentBranchException();
        }
        VCSBranch branch = getBranch(name);
        if (branch != null) {
            branches.remove(branch);
        }
        Files.deleteIfExists(getBranchesDir().resolve(name));
    }

    /**
     * Merges the branch with given name into current branch and instantly checkouts result.
     * When both branches contain the same file, current branch has priority.
     * Last version of the file in current branch will be saved.
     * @param name branch that should be merged into current one
     * @throws IOException if IO went wrong
     * @throws BranchNotFoundException if branch with given name doesn't exist
     * @throws HeadFileBrokenException if something happened to HEAD file
     */
    void merge(@NotNull String name) throws IOException, BranchNotFoundException, HeadFileBrokenException {
        VCSBranch currentBranch = getHEADBranch();
        VCSBranch secondBranch = getBranch(name);
        if (secondBranch == null) {
            throw new BranchNotFoundException();
        }
        if (currentBranch.getName().equals(secondBranch.getName())) {
            return;
        }
        VCSCommit currentCommit = (VCSCommit) VCSObject.read(getObjectsDir().resolve(currentBranch.getCommit()));
        VCSCommit secondCommit = (VCSCommit) VCSObject.read(getObjectsDir().resolve(secondBranch.getCommit()));

        List<String> parents = new ArrayList<>();
        parents.add(currentCommit.getHash());
        parents.add(secondCommit.getHash());

        List<VCSPair> files2 = secondCommit.getTree().checkoutTree(root);
        List<VCSPair> files1 = currentCommit.getTree().checkoutTree(root);
        Set<Path> filePaths = new HashSet<>();
        for (VCSPair pair : files1) {
            filePaths.add(pair.getPath());
        }
        for (VCSPair pair : files2) {
            if (!filePaths.contains(pair.getPath())) {
                files1.add(pair);
            }
        }
        VCSTree newCommitTree = buildCommitTree(files1);

        VCSCommit newCommit = new VCSCommit(root,
                "merged branch \"" + name + "\" into \"" + currentBranch.getName() + "\"",
                parents, newCommitTree);
        currentBranch.setCommit(newCommit.getHash());
        writeToHEAD(newCommit.getHash());
        writePairsToIndex(files1);
    }

    /**
     * Writes to console list of all commits in current branch.
     * @throws IOException if IO went wrong
     * @throws HeadFileBrokenException if something happened to HEAD file
     */
    void log() throws IOException, HeadFileBrokenException {
        System.out.println("Current branch : " + getCurrentBranchesName()+ "\n");

        VCSCommit lastCommit = (VCSCommit) VCSObject.read(getObjectsDir().resolve(getHEADBranch().getCommit()));
        List<VCSCommit> commitsInLog = lastCommit.getLog();
        List<VCSCommit> uniqueCommits = new ArrayList<>();
        Set<String> hashes = new HashSet<>();
        for (VCSCommit commit : commitsInLog) {
            if (!hashes.contains(commit.getHash())) {
                uniqueCommits.add(commit);
            }
            hashes.add(commit.getHash());
        }
        uniqueCommits.sort(VCSCommit::compareTo);
        for (VCSCommit commit : uniqueCommits) {
            System.out.println("commit : " + commit.getHash());
            System.out.println(commit.getMessage());
            System.out.println("Author : " + commit.getAuthor());
            System.out.println("Date : " + commit.getDate());
            System.out.println("");
        }
    }

    /**
     * Gives statuses of all files in repository.
     * @return VCS_Status with files
     * @throws IOException if IO went wrong
     * @throws IndexFileBrokenException if something happened to index file
     * @throws HeadFileBrokenException if something happened to HEAD file
     */
    public VCS_Status status() throws IOException, IndexFileBrokenException, HeadFileBrokenException {
        VCS_Status status = new VCS_Status();
        Set<Path> processed = new HashSet<>();
        List<String> lines = Files.readAllLines(getIndex());
        for (String line: lines) {
            String[] strings = line.split(" ");
            if (strings.length != 2) {
                throw new IndexFileBrokenException();
            }
            status.addStaged(Paths.get(strings[0]));
            processed.add(Paths.get(strings[0]));
        }
        getHEADCommit().getTree().updateStatus(root, processed, status);
        List<Path> files = Files.walk(root)
                .filter(p -> !p.startsWith(getVCSDir()))
                .collect(Collectors.toList());
        for (Path path : files) {
            if (!Files.isDirectory(path) && !processed.contains(path)) {
                status.addUnversioned(path);
            }
        }
        return status;
    }

    /**
     * Removes information about file contained in path from index.
     * @param path file that should be removed from index
     * @throws IOException if IO went wrong
     * @throws IndexFileBrokenException if something happened to index file
     * @throws WrongFileLocationException if file is in another directory
     */
    public void reset(@NotNull Path path) throws IOException, IndexFileBrokenException, WrongFileLocationException {
        if (!path.startsWith(root)) {
            throw new WrongFileLocationException();
        }
        removeFromIndex(path);
    }

    /**
     * Removes information about file contained in path from index and removes it from the disk.
     * @param path file that should be removed
     * @throws WrongFileLocationException if file is in another directory
     * @throws IOException if IO went wrong
     * @throws IndexFileBrokenException if something happened to index file
     * @throws NotFileProvidedException if directory instead of file was provided
     */
    public void remove(@NotNull Path path) throws WrongFileLocationException, IOException, IndexFileBrokenException, NotFileProvidedException {
        reset(path);
        if (Files.exists(path) && Files.isDirectory(path)) {
            throw new NotFileProvidedException();
        }
        Files.deleteIfExists(path);
    }

    /**
     * Removes all unversioned files from disk.
     * @throws IOException if IO went wrong
     * @throws IndexFileBrokenException if something happened to index file
     * @throws HeadFileBrokenException if something happened to HEAD file
     */
    public void clean() throws IndexFileBrokenException, HeadFileBrokenException, IOException {
        VCS_Status status = status();
        for (Path path : status.getUnversioned()) {
            Files.deleteIfExists(path);
        }
    }

    /**
     * Returns name of current branch.
     * @return name
     * @throws IOException if IO went wrong
     * @throws HeadFileBrokenException if something happened to HEAD file
     */
    String getCurrentBranchesName() throws IOException, HeadFileBrokenException {
        return getHEADBranch().getName();
    }

    private void initialCommit() throws IOException {
        VCSCommit commit = new VCSCommit(root, "initial commit", new ArrayList<>());
        VCSBranch masterBranch = new VCSBranch(root, "master", commit.getHash());
        branches.add(masterBranch);
        writeToHEAD(masterBranch);
    }

    private void checkoutCommit(@NotNull String commitHash) throws IOException, FileNotFoundException {
        VCSCommit commit = (VCSCommit) VCSObject.read(getObjectsDir().resolve(commitHash));
        List<VCSPair> files = commit.getTree().checkoutTree(root);
        writePairsToIndex(files);
    }

    private void writePairsToIndex(@NotNull List<VCSPair> files) throws IOException {
        OutputStream outputStream = Files.newOutputStream(getIndex());
        for (VCSPair pair : files) {
            outputStream.write((pair.getPath().toString() + " " + pair.getString() + "\n").getBytes());
        }
        outputStream.close();
    }

    private void clearIndex() throws IOException {
        OutputStream outputStream = Files.newOutputStream(getIndex());
        outputStream.write(new byte[0]);
        outputStream.close();
    }

    private void removeFromIndex(@NotNull Path path) throws IOException, IndexFileBrokenException {
        List<String> lines = Files.readAllLines(getIndex());
        String file = "";
        for (String line: lines) {
            String[] strings = line.split(" ");
            if (strings.length != 2) {
                throw new IndexFileBrokenException();
            }
            if (strings[0].equals(path.toString())) {
                continue;
            }
            file += line + "\n";
        }
        OutputStream outputStream = Files.newOutputStream(getIndex());
        outputStream.write(file.getBytes());
        outputStream.close();
    }

    private void writeToHEAD(@NotNull VCSBranch branch) throws IOException {
        OutputStream outputStream = Files.newOutputStream(getHEAD());
        outputStream.write((branch.getName() + "\n").getBytes());
        outputStream.write((branch.getCommit() + "\n").getBytes());
        outputStream.close();
    }

    private void writeToHEAD(@NotNull String commitHash) throws IOException, HeadFileBrokenException {
        String name = getHEADBranch().getName();
        OutputStream outputStream = Files.newOutputStream(getHEAD());
        outputStream.write((name + "\n").getBytes());
        outputStream.write((commitHash + "\n").getBytes());
        outputStream.close();
    }

    private VCSObject readFromHEAD(int i) throws IOException, HeadFileBrokenException {
        List<String> lines = Files.readAllLines(getHEAD());
        if (lines.size() != 2) {
            throw new HeadFileBrokenException();
        }
        if (i == 0) {
            return VCSObject.read(getBranchesDir().resolve(lines.get(i)));
        } else {
            return VCSObject.read(getObjectsDir().resolve(lines.get(i)));
        }
    }

    private VCSCommit getHEADCommit() throws IOException, HeadFileBrokenException {
        return (VCSCommit) readFromHEAD(1);
    }

    private VCSBranch getHEADBranch() throws IOException, HeadFileBrokenException {
        return (VCSBranch) readFromHEAD(0);
    }

    private VCSTree buildCommitTree(@NotNull List<VCSPair> pathsAndHashes)
            throws IOException, HeadFileBrokenException {
        VCSTree tree = getHEADCommit().getTree();
        for (VCSPair pair : pathsAndHashes) {
            tree = tree.addPathToTree(root.relativize(pair.getPath()), pair.getString());
        }
        return tree;
    }

    @Nullable private VCSBranch getBranch(@NotNull String name) {
        for (VCSBranch branch : branches) {
            if (branch.getName().equals(name)) {
                return branch;
            }
        }
        return null;
    }

    private void addBranch(@NotNull VCSBranch branch) {
        branches.add(branch);
    }

    private Path getObjectsDir() {
        return root.resolve(VCSConstants.objectsDirectory);
    }

    private Path getBranchesDir() {
        return root.resolve(VCSConstants.branchesDirectory);
    }

    private Path getLogsDir() {
        return root.resolve(VCSConstants.logsDirectory);
    }

    private Path getVCSDir() {
        return root.resolve(VCSConstants.myVCSDirectory);
    }

    private Path getIndex() {
        return root.resolve(VCSConstants.index);
    }

    private Path getHEAD() {
        return root.resolve(VCSConstants.head);
    }
}
