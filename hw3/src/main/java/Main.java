import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import exceptions.*;

public class Main {
    private static String[] arguments = {"init", "add", "branch", "merge", "commit", "remove_repository",
            "remove_branch", "checkout", "log", "status", "reset", "rm", "clean"};

    public static void main(String[] args) {
        Path directory = Paths.get(System.getProperty("user.dir"));

        if (args.length == 0) {
            System.out.println("Provide some arguments.");
            return;
        }

        if (args[0].equals("help")) {
            System.out.println(
                    "init - initializes VCS in current directory\n" +
                            "remove_repository - remove repository in current directory\n" +
                            "add \'path\' - add current version of file contained in \'path\' to repository\n" +
                            "commit \'message\' - commit added files to current branch\n" +
                            "branch - show name of current branch" +
                            "branch \'title\' - create a new branch with name \'title\'\n" +
                            "remove_branch \'title\' - remove branch with name \'title\'\n" +
                            "merge \'title\' - merge branch with name \'title\' into current branch\n" +
                            "checkout \'title\'- checkout branch or commit with name \'title\'\n" +
                            "log - show list of commits in current branch"
            );
            return;
        }

        boolean gotArg = false;
        for (String arg : arguments) {
            if (arg.equals(args[0])) {
                gotArg = true;
            }
        }
        if (!gotArg) {
            System.out.println("Unknown command.");
            return;
        }

        if (args[0].equals("init")) {
            if (args.length > 1) {
                System.out.println("Too many arguments.");
                return;
            }
            try {
                VCSManager.initRepository(directory);
            } catch (IOException e) {
                System.out.println("Something went wrong during reading or writing to files.\n" + "Check permissions and try again.");
                e.printStackTrace();
            } catch (RepositoryAlreadyExistsException e) {
                System.out.println("VCSManager already exists in this directory.");
            }
            return;
        }

        if (args[0].equals("remove_repository")) {
            if (args.length > 1) {
                System.out.println("Too many arguments.");
                return;
            }
            try {
                VCSManager.removeRepository(directory);
            } catch (IOException e) {
                System.out.println("Something went wrong during reading or writing to files.\n" + "Check permissions and try again.");
                e.printStackTrace();
            }
            return;
        }

        VCSManager repositoryManager;

        try {
            repositoryManager = VCSManager.getRepositoryManager(directory);
        } catch (IOException e) {
            System.out.println("Something went wrong during reading or writing to files.\n" + "Check permissions and try again.");
            e.printStackTrace();
            return;
        } catch (RepositoryNotInitializedException e) {
            System.out.println("VCSManager in this directory wasn't initialized.");
            return;
        } catch (VCSFilesBrokenException e) {
            System.out.println("VCS files are broken.");
            return;
        }

        if (args[0].equals("add")) {
            if (args.length == 1) {
                System.out.println("Too few arguments.");
                return;
            }
            if (args.length > 2) {
                System.out.println("Too many arguments.");
                return;
            }

            try {
                repositoryManager.add(Paths.get(args[1]));
            } catch (WrongFileLocationException e) {
                System.out.println("You're trying to add file from another directory.");
            } catch (IOException e) {
                System.out.println("Something went wrong during reading or writing to files.\n" + "Check permissions and try again.");
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                System.out.println("File doesn't exist.");
            } catch (NotFileProvidedException e) {
                System.out.println("You can add only files.");
            } catch (IndexFileBrokenException e) {
                System.out.println(".myvcs/index file is broken.");
            }
            return;
        }

        if (args[0].equals("checkout")) {
            if (args.length == 1) {
                System.out.println("Too few arguments.");
                return;
            }
            if (args.length > 2) {
                System.out.println("Too many arguments.");
                return;
            }

            try {
                repositoryManager.checkout(args[1]);
            } catch (FileNotFoundException e) {
                System.out.println("There is no branch or commit with name \"" + args[1] + "\"");
            } catch (IOException e) {
                System.out.println("Something went wrong during reading or writing to files.\n" + "Check permissions and try again.");
                e.printStackTrace();
            }

            return;
        }


        if (args[0].equals("commit")) {
            if (args.length == 1) {
                System.out.println("Too few arguments.");
                return;
            }
            if (args.length > 2) {
                System.out.println("Too many arguments.");
                return;
            }

            try {
                repositoryManager.commit(args[1]);
            } catch (IndexFileBrokenException e) {
                System.out.println(".myvcs/index file is broken.");
            } catch (HeadFileBrokenException e) {
                System.out.println(".myvcs/HEAD file is broken.");
            } catch (IOException e) {
                System.out.println("Something went wrong during reading or writing to files.\n" + "Check permissions and try again.");
                e.printStackTrace();
            }

            return;
        }

        if (args[0].equals("branch")) {
            if (args.length == 1) {
                try {
                    System.out.println(repositoryManager.getCurrentBranchesName());
                } catch (IOException e) {
                    System.out.println("Something went wrong during reading or writing to files.\n" + "Check permissions and try again.");
                    e.printStackTrace();
                } catch (HeadFileBrokenException e) {
                    System.out.println(".myvcs/HEAD file is broken.");
                }
                return;
            }
            if (args.length > 2) {
                System.out.println("Too many arguments.");
                return;
            }
            try {
                repositoryManager.createBranch(args[1]);
            } catch (IOException e) {
                System.out.println("Something went wrong during reading or writing to files.\n" + "Check permissions and try again.");
                e.printStackTrace();
            } catch (BranchAlreadyExistsException e) {
                System.out.println("Branch with the name \"" + args[1] + "\" already exists.");
            } catch (HeadFileBrokenException e) {
                System.out.println(".myvcs/HEAD file is broken.");
            }
            return;
        }

        if (args[0].equals("remove_branch")) {
            if (args.length == 1) {
                System.out.println("Too few arguments.");
                return;
            }
            if (args.length > 2) {
                System.out.println("Too many arguments.");
                return;
            }

            try {
                repositoryManager.removeBranch(args[1]);
            } catch (IOException e) {
                System.out.println("Something went wrong during reading or writing to files.\n" + "Check permissions and try again.");
                e.printStackTrace();
            } catch (DeletingCurrentBranchException e) {
                System.out.println("You can't delete current branch.");
            } catch (HeadFileBrokenException e) {
                System.out.println(".myvcs/HEAD file is broken.");
            }

            return;
        }

        if (args[0].equals("merge")) {
            if (args.length == 1) {
                System.out.println("Too few arguments.");
                return;
            }
            if (args.length > 2) {
                System.out.println("Too many arguments.");
                return;
            }

            try {
                repositoryManager.merge(args[1]);
            } catch (IOException e) {
                System.out.println("Something went wrong during reading or writing to files.\n" + "Check permissions and try again.");
                e.printStackTrace();
            } catch (BranchNotFoundException e) {
                System.out.println("There is no branch with name \"" + args[1] + "\"");
            } catch (HeadFileBrokenException e) {
                System.out.println(".myvcs/HEAD file is broken.");
            }

            return;
        }

        if (args[0].equals("log")) {
            if (args.length > 1) {
                System.out.println("Too many arguments.");
                return;
            }

            try {
                repositoryManager.log();
            } catch (HeadFileBrokenException e) {
                System.out.println(".myvcs/HEAD file is broken.");
            } catch (IOException e) {
                System.out.println("Something went wrong during reading or writing to files.\n" + "Check permissions and try again.");
                e.printStackTrace();
            }

        }

        if (args[0].equals("status")) {
            if (args.length > 1) {
                System.out.println("Too many arguments");
                return;
            }

            try {
                VCS_Status status = repositoryManager.status();
                for (Path path : status.getStaged()) {
                    System.out.println(path + " staged for commit");
                }
                for (Path path : status.getUnmodified()) {
                    System.out.println(path + " wasn't modified since head commit");
                }
                for (Path path : status.getModified()) {
                    System.out.println(path + " was modified since head commit");
                }
                for (Path path : status.getDeleted()) {
                    System.out.println(path + " was deleted");
                }
                for (Path path : status.getUnversioned()) {
                    System.out.println(path + " isn't versioned");
                }
            } catch (HeadFileBrokenException e) {
                System.out.println(".myvcs/HEAD file is broken.");
            } catch (IOException e) {
                System.out.println("Something went wrong during reading or writing to files.\n" + "Check permissions and try again.");
                e.printStackTrace();
            } catch (IndexFileBrokenException e) {
                System.out.println(".myvcs/index file is broken.");
            }
        }

        if (args[0].equals("reset")) {
            if (args.length < 2) {
                System.out.println("Too few arguments");
                return;
            }
            if (args.length > 2) {
                System.out.println("Too many arguments");
                return;
            }
            try {
                repositoryManager.reset(Paths.get(args[1]));
            } catch (IOException e) {
                System.out.println("Something went wrong during reading or writing to files.\n" + "Check permissions and try again.");
                e.printStackTrace();
            } catch (IndexFileBrokenException e) {
                System.out.println(".myvcs/index file is broken.");
            } catch (WrongFileLocationException e) {
                System.out.println("You're trying to reset file from another directory.");
            }
        }

        if (args[0].equals("rm")) {
            if (args.length < 2) {
                System.out.println("Too few arguments");
                return;
            }
            if (args.length > 2) {
                System.out.println("Too many arguments");
                return;
            }
            try {
                repositoryManager.remove(Paths.get(args[1]));
            } catch (WrongFileLocationException e) {
                System.out.println("You're trying to remove file from another directory.");
            } catch (IOException e) {
                System.out.println("Something went wrong during reading or writing to files.\n" + "Check permissions and try again.");
                e.printStackTrace();
            } catch (IndexFileBrokenException e) {
                System.out.println(".myvcs/index file is broken.");
            } catch (NotFileProvidedException e) {
                System.out.println("You're trying to remove directory instead of file.");
            }
        }

        if (args[0].equals("clean")) {
            if (args.length > 1) {
                System.out.println("Too many arguments");
                return;
            }
            try {
                repositoryManager.clean();
            } catch (HeadFileBrokenException e) {
                System.out.println(".myvcs/HEAD file is broken.");
            } catch (IOException e) {
                System.out.println("Something went wrong during reading or writing to files.\n" + "Check permissions and try again.");
                e.printStackTrace();
            } catch (IndexFileBrokenException e) {
                System.out.println(".myvcs/index file is broken.");
            }
        }
    }
}
