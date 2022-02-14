package gitlet;

import java.io.File;
import java.io.IOException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Evelyn Vo
 */
public class Main {

    /**
     * Current Working Directory.
     */
    static final File CWD = new File(".");

    /**
     * Main metadata folder.
     */
    static final File GITLET_FOLDER = Utils.join(CWD, ".gitlet");

    /**
     * Logs metadata folder.
     */
    static final File LOGS_FOLDER = Utils.join(GITLET_FOLDER, "logs");

    /**
     * Local logs metadata folder.
     */
    static final File LOCAL_LOGS_FOLDER = Utils.join(LOGS_FOLDER, "local");

    /**
     * Remote logs metadata folder.
     */
    static final File REMOTE_LOGS_FOLDER = Utils.join(LOGS_FOLDER, "remote");

    /**
     * Objects / Blobs / File contents metadata folder.
     */
    static final File OBJECTS_FOLDER = Utils.join(GITLET_FOLDER, "objects");

    /**
     * References metadata folder.
     * Contains the pointers of each branch and HEAD.
     */
    static final File REFS_FOLDER = Utils.join(GITLET_FOLDER, "refs");

    /**
     * Heads metadata folder within REFS_FOLDER.
     * Contains the pointers of each branch and HEAD.
     */
    static final File HEADS_FOLDER = Utils.join(REFS_FOLDER, "heads");

    /**
     * The HEAD pointer within REFS_FOLDER. Located outside of the HEADS_FOLDER
     * as it may point to remote commits.
     */
    static final File HEADS_POINTER = Utils.join(REFS_FOLDER, "HEAD");

    /**
     * The HEAD pointer within REFS_FOLDER. Located outside of the HEADS_FOLDER
     * as it may point to remote commits.
     */
    static final File HEADS_LOG = Utils.join(LOGS_FOLDER, "HEAD");

    /**
     * The MASTER pointer within HEADS_FOLDER.
     */
    static final File MASTER_POINTER = Utils.join(HEADS_FOLDER, "master");

    /**
     * The MASTER pointer within HEADS_FOLDER.
     */
    static final File MASTER_LOG = Utils.join(LOCAL_LOGS_FOLDER, "master");

    /**
     * Staging area folder.
     */
    static final File STAGING_FOLDER = Utils.join(GITLET_FOLDER, "staging");

    /**
     * Staging folder for "git add".
     */
    static final File ADD_STAGING_FOLDER = Utils.join(STAGING_FOLDER, "add");

    /**
     * Staging folder for "git rm".
     */
    static final File RM_STAGING_FOLDER = Utils.join(STAGING_FOLDER, "rm");

    /** The initial commit SHA.
     *
     */
    static final String INITIAL = "0000000000000000000000000000000000000000";

    /** The set of possible characters in the first two characters
     * of SHA1 hashes. Each may be in either or both the
     * first and second position. */
    static final char[] COMBOS = {'a', 'b', 'c', 'd', 'e', 'f',
                                  '0', '1', '2', '3', '4', '5', '6',
                                  '7', '8', '9'};

    /** The full length of a SHA1 id. */
    static final int SHA1_LEN = 40;

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND> .... */
    public static void main(String... args) {
        try {
            run(args);
            return;
        } catch (GitletException exception) {
            System.out.println(exception.getMessage());
        } catch (IOException exception) {
            System.out.println("IO Exception " + exception.getMessage());
        }
        System.exit(0);
    }

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> ....
     *  Exits the system with exit code 0 when errors occur. */
    public static void run(String[] args) throws IOException {
        if (args.length == 0) {
            exitWithError("Please enter a command.");
        }
        switch (args[0]) {
        case "init":
            initialize(args);
            break;
        case "add":
            add(args);
            break;
        case "commit":
            commit(args);
            break;
        case "checkout":
            checkout(args);
            break;
        case "log":
            log(args);
            break;
        case "global-log":
            globalLog(args);
            break;
        case "status":
            status(args);
            break;
        case "find":
            find(args);
            break;
        case "reset":
            reset(args);
            break;
        case "branch":
            branch(args);
            break;
        case "rm":
            rm(args);
            break;
        case "rm-branch":
            rmBranch(args);
            break;
        case "merge":
            merge(args);
            break;
        default:
            run2(args);
        }
    }

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> ....
     *  Exits the system with exit code 0 when errors occur.
     *  Created since run(String[] args) is too long.
     *  A continuation. */
    public static void run2(String[] args) throws IOException {
        switch (args[0]) {
        case "add-remote":
            addRemote(args);
            break;
        case "rm-remote":
            rmRemote(args);
            break;
        case "push":
            push(args);
            break;
        case "fetch":
            fetch(args);
            break;
        case "pull":
            pull(args);
            break;
        default:
            exitWithError("No command with that name exists.");
        }
    }

    /** Initializes a gitlet repository in the current working directory.
     * Assumes that there is not a gitlet repository in the current working
     * directory.
     * @param args The input to the program
     * */
    private static void initialize(String[] args) throws IOException {
        validateNumArgs(args, 1);
        if (checkInitialized()) {
            exitWithError("A Gitlet version-control system already"
                          + " exists in the current directory.");
        }
        GITLET_FOLDER.mkdir();
        LOGS_FOLDER.mkdir();
        OBJECTS_FOLDER.mkdir();
        REFS_FOLDER.mkdir();
        LOCAL_LOGS_FOLDER.mkdir();
        REMOTE_LOGS_FOLDER.mkdir();
        HEADS_FOLDER.mkdir();
        STAGING_FOLDER.mkdir();
        ADD_STAGING_FOLDER.mkdir();
        RM_STAGING_FOLDER.mkdir();
        HEADS_POINTER.createNewFile();
        MASTER_POINTER.createNewFile();
        HEADS_LOG.createNewFile();
        MASTER_LOG.createNewFile();
        for (int first = 0; first < COMBOS.length; first += 1) {
            for (int second = 0; second < COMBOS.length; second += 1) {
                Utils.join(OBJECTS_FOLDER,
                        String.format("%c%c",
                                      COMBOS[first], COMBOS[second])).mkdir();
            }
        }
        String initial = INITIAL;
        File initialCommit = Utils.join(OBJECTS_FOLDER,
                "00", "00000000000000000000000000000000000000");
        initialCommit.createNewFile();
        createMetadata("master", null, null, initial, "");
        updateHead("master", getBranchDir("master"), initial);
        updatePointer("master", initial);
        updateLog(null, null, initial, "initial commit",
                new Date(0), MASTER_LOG);
        updateLog(null, null, initial, "initial commit",
                new Date(0), HEADS_LOG);
    }

    /** Adds a file to the staging area. Does not add the file if it matches
     * an already staged version of the same file.
     * @param args The input to the program
     * */
    private static void add(String[] args) throws IOException {
        basicVal(args, 2);
        File cwdFile = Utils.join(CWD, args[1]);
        if (!cwdFile.exists()) {
            exitWithError("File does not exist.");
        }
        File stagingCopy = addStage(cwdFile.getName());
        File rmCopy = rmStage(cwdFile.getName());
        if (stagingCopy.exists()) {
            if (!sameSHA(stagingCopy, cwdFile)) {
                writeFile(stagingCopy, readString(cwdFile));
            }
        } else if (rmCopy.exists()) {
            if (!sameSHA(rmCopy, cwdFile)) {
                writeFile(stagingCopy, readString(cwdFile));
            }
            rmCopy.delete();
        } else {
            if (nameTrackedByCurrentCommit(args[1])) {
                File currComVer = getCommitVersion(getCurrentCommit(), args[1]);
                if (!sameSHA(cwdFile, currComVer)) {
                    writeFile(stagingCopy, readString(cwdFile));
                }
            } else {
                writeFile(stagingCopy, readString(cwdFile));
            }
        }
    }

    /** Commits a file.
     * @param args The input to the program
     * */
    private static void commit(String[] args) throws IOException {
        basicVal(args, 2);
        if (emptyStagingArea()) {
            exitWithError("No changes added to the commit.");
        }
        if (args[1].equals("") || Pattern.matches("[\\s]+", args[1])) {
            exitWithError("Please enter a commit message.");
        }
        String[] headPointerContent = readString(HEADS_POINTER).split("\n");
        Date commitDate = new Date();
        String parentSHA = headPointerContent[2];
        File parentCommitFile = accessObjectContents(parentSHA);
        String[] pcmContents = readString(parentCommitFile).split("\n");
        HashMap<String, String> childCommit = new HashMap<>();
        HashMap<String, String> unchangedFiles = new HashMap<>();
        for (int pos = 3; pos < pcmContents.length; pos += 1) {
            String[] fileInfo = pcmContents[pos].split(" ");
            if (Utils.join(CWD, fileInfo[0]).exists()
                    && !addStage(fileInfo[0]).exists()) {
                unchangedFiles.put(fileInfo[0], fileInfo[1]);
            } else {
                if (!(!Utils.join(CWD, fileInfo[0]).exists()
                        && !addStage(fileInfo[0]).exists())) {
                    childCommit.put(fileInfo[0], fileInfo[1]);
                }
            }
        }
        for (String file: getFiles(ADD_STAGING_FOLDER)) {
            String fileSHA = getSHA(addStage(file));
            if (childCommit.containsKey(file)
                && childCommit.get(file).equals(fileSHA)) {
                unchangedFiles.put(file, childCommit.get(file));
                childCommit.remove(file);
            } else {
                childCommit.put(file, fileSHA);
            }
        }
        String contents = copyStagedFiles(childCommit);
        for (Map.Entry<String, String> file: unchangedFiles.entrySet()) {
            contents += file.getKey() + " " + file.getValue() + "\n";
        }
        clearDirectory(ADD_STAGING_FOLDER);
        clearDirectory(RM_STAGING_FOLDER);
        Formatter formatter = new Formatter();
        String date = formatter.format(
                "Date: %1$ta %1$tb %1$td %1$tH:%1$tM:%1$tS %1$tY %1$tz",
                commitDate).toString();
        String commitSHA = Utils.sha1(parentSHA + "\n\n" + date + "\n"
                + args[1] + "\n" + contents);
        String currBranch = getCurrentBranch();
        updateHead(currBranch, getBranchDir(currBranch), commitSHA);
        updatePointer(currBranch, commitSHA);
        createMetadata(getCurrentBranch(), parentSHA,
                null, commitSHA, contents);
        updateLog(parentSHA, null, commitSHA,
                args[1] + "\n\n", commitDate,
                Utils.join(LOCAL_LOGS_FOLDER, currBranch));
        updateLog(parentSHA, null, commitSHA,
                args[1] + "\n\n", commitDate, HEADS_LOG);
    }

    /** Checks out a commit.
     * Overwrites the version of fileName in the current
     * directory with the version
     * of fileName in the commit with the given SHA1 id.
     * Creates a copy of fileName in the current directory
     * if fileName does not exist.
     * @param args The input to the program
     */
    private static void checkout(String[] args) {
        validateInitialized();
        if (checkNumArgs(args, 2)) {
            String currentBranch = getCurrentBranch();
            if (args[1].equals(currentBranch)) {
                exitWithError("No need to checkout the current branch.");
            }
            if (!getBranchPointer(args[1]).exists()) {
                exitWithError("No such branch exists.");
            }
            HashMap<String, String> currentFiles =
                    getAllFileSHAFromCommit(
                            accessObjectContents(getCurrentCommit()));
            for (String f: getFiles(CWD)) {
                File file = Utils.join(CWD, f);
                if (file.isFile() && !file.isHidden()
                        && !f.equals("Makefile") && !f.equals("proj3.iml")) {
                    boolean fileInCom = currentFiles.containsKey(f);
                    boolean fileInStage = addStage(f).exists()
                            || rmStage(f).exists();
                    if (!fileInCom && !fileInStage) {
                        if (canDelete(f)) {
                            exitWithError("There is an untracked file in"
                                    + "the way; delete it, or "
                                    + "add and commit it first.");
                        }
                    }
                }
            }
            HashMap<String, String> overwriteFiles =
                    getAllFileSHAFromCommit(
                       accessObjectContents(getCurrentCommit(args[1])));
            copyCommittedFiles(overwriteFiles);
            for (String file: currentFiles.keySet()) {
                if (!overwriteFiles.containsKey(file)
                    && Utils.join(CWD, file).exists()
                    && canDelete(file)) {
                    Utils.join(CWD, file).delete();
                }
            }
            updateHead(args[1], getBranchDir(args[1]));
        } else if (checkNumArgs(args, 3)) {
            if (!args[1].equals("--")) {
                exitWithError("Incorrect operands.");
            }
            File file = getCommitVersion(getCurrentCommit("HEAD"), args[2]);
            writeFile(Utils.join(CWD, args[2]), readString(file));
        } else if (checkNumArgs(args, 4)) {
            if (!(args[1].length() <= SHA1_LEN)
                || !args[2].equals("--")) {
                exitWithError("Incorrect operands.");
            }
            File file = getCommitVersion(args[1], args[3]);
            writeFile(Utils.join(CWD, args[3]), readString(file));
        } else {
            exitWithError("Incorrect operands.");
        }
    }

    /** Prints out the commit log for the current branch.
     * @param args The input to the program */
    private static void log(String[] args) {
        basicVal(args, 1);
        String branch = getCurrentBranch();
        System.out.println(
                readString(Utils.join(LOCAL_LOGS_FOLDER, branch)));
    }

    /** Prints out the global log.
     * @param args The input to the program
     * */
    private static void globalLog(String[] args) {
        basicVal(args, 1);
        System.out.println(readString(HEADS_LOG));
    }

    /** Prints out the status of the staging area and commit stage.
     * @param args The input to the program
     * */
    private static void status(String[] args) {
        basicVal(args, 1);
        String branches = "=== Branches ===\n";
        ArrayList<String> branchNames = getFiles(HEADS_FOLDER);
        String currBranch = getCurrentBranch();
        branchNames.remove(currBranch);
        branchNames.add("*" + currBranch);
        branches = segmentMessage(branches, branchNames, false);
        ArrayList<String> rmNames = getFiles(RM_STAGING_FOLDER);
        String rmFiles = segmentMessage("=== Removed Files ===\n",
                rmNames, false);
        HashMap<String, String> parentCommit =
                getAllFileSHAFromCommit(
                        accessObjectContents(getCurrentCommit()));
        HashMap<String, String> cwdVers = getAllFileSHAFromCWD();
        HashMap<String, String> addStaging =
                getAllFileSHAFromDir(ADD_STAGING_FOLDER);
        ArrayList<String> stagedNames = new ArrayList<>();
        ArrayList<String> modifNames = new ArrayList<>();
        ArrayList<String> untrackNames = new ArrayList<>();
        for (Map.Entry<String, String> file : cwdVers.entrySet()) {
            String fileName = file.getKey();
            String fileSHA = file.getValue();
            if (addStaging.containsKey(fileName)) {
                if (!addStaging.get(fileName).equals(fileSHA)) {
                    modifNames.add(fileName + " (modified)");
                } else {
                    stagedNames.add(fileName);
                }
            } else if (parentCommit.containsKey(fileName)) {
                if (!parentCommit.get(fileName).equals(fileSHA)) {
                    modifNames.add(fileName + " (modified)");
                }
            } else {
                untrackNames.add(fileName);
            }
        }
        for (Map.Entry<String, String> file : addStaging.entrySet()) {
            if (!cwdVers.containsKey(file.getKey())) {
                modifNames.add(file.getKey() + " (deleted)");
            }
        }
        for (Map.Entry<String, String> file : parentCommit.entrySet()) {
            if (!cwdVers.containsKey(file.getKey())
                    && nameTrackedByCurrentCommit(file.getKey())
                    && !rmNames.contains(file.getKey())) {
                modifNames.add(file.getKey() + " (deleted)");
            }
        }
        String stagedFiles = segmentMessage(
                "=== Staged Files ===\n", stagedNames, false);
        String modifNotStaged = segmentMessage(
                "=== Modifications Not Staged For Commit ===\n",
                modifNames, false);
        String untrackedFiles = segmentMessage(
                "=== Untracked Files ===\n", untrackNames, true);
        System.out.print(branches
                + stagedFiles + rmFiles
                + modifNotStaged + untrackedFiles);
    }

    /** Finds all the commits with the given commit message.
     * @param args The input to the program
     * */
    private static void find(String[] args) {
        basicVal(args, 2);
        String message = args[1];
        String[] allCommits = readString(HEADS_LOG).split(
                "===\ncommit ");
        String ret = "";
        for (String commit: allCommits) {
            int pos = commit.indexOf(message);
            if (pos != -1) {
                ret += commit.substring(0, SHA1_LEN) + "\n";
            }
        }
        if (ret.equals("")) {
            exitWithError("Found no commit with that message.");
        }
        System.out.println(ret);
    }

    /** Resets the CWD to the given commit.
     * HEAD and the pointer of the commit's branch
     * are updated to point to the commit.
     * Currently only lets you reset to a previous commit
     * and not to a later commit.
     * @param args The input to the program
     * */
    private static void reset(String[] args) {
        basicVal(args, 2);
        File commit = accessObjectContents(args[1]);
        if (!commit.exists()) {
            exitWithError("No commit with that id exists.");
        }
        checkUntrackError(args[1]);
        clearDirectory(CWD);
        clearDirectory(ADD_STAGING_FOLDER);
        clearDirectory(RM_STAGING_FOLDER);
        String currBranch = getCommitBranch(args[1]);
        updatePointer(currBranch, args[1]);
        updateHead(currBranch, getBranchDir(currBranch), args[1]);
        String[] logs = readString(Utils.join(
                LOCAL_LOGS_FOLDER, currBranch)).split("===");
        int start = -1;
        for (int pos = 0; pos < logs.length && start == -1; pos += 1) {
            int val = logs[pos].indexOf(args[1]);
            if (val > -1) {
                start = pos;
            }
        }
        String logUpdate = "";
        for (int pos = start; pos < logs.length; pos += 1) {
            logUpdate += "===" + logs[pos];
        }
        writeFile(Utils.join(LOCAL_LOGS_FOLDER, currBranch), logUpdate);
        ArrayList<String> resetFiles = getAllFileNmFromCommit(commit);
        for (String fileName: resetFiles) {
            File file = getCommitVersion(args[1], fileName);
            writeFile(Utils.join(CWD, fileName), readString(file));
        }
    }

    /** Creates a new branch where the first node is the current HEAD node.
     * @param args The input to the program
     * */
    private static void branch(String[] args) {
        basicVal(args, 2);
        String branchName = args[1];
        File branchPointer = getBranchPointer(branchName);
        if (branchPointer.exists()) {
            exitWithError("A branch with that name already exists.");
        }
        writeFile(branchPointer, getCurrentCommit());
        writeFile(Utils.join(LOCAL_LOGS_FOLDER, branchName),
                readString(Utils.join(LOCAL_LOGS_FOLDER, getCurrentBranch())));
    }

    /** Removes a file from the staging area.
     * @param args The input to the program
     * */
    private static void rm(String[] args) {
        basicVal(args, 2);
        String fileName = args[1];
        File stagedFile = addStage(fileName);
        HashMap<String, String> currentCommit =
                getAllFileSHAFromCommit(
                        accessObjectContents(getCurrentCommit()));
        File currFile = Utils.join(CWD, fileName);
        boolean fileIsTracked = currentCommit.containsKey(fileName);
        boolean fileIsStaged = stagedFile.exists()
                && sameSHA(Utils.join(CWD, fileName), stagedFile);
        if (!fileIsStaged && !fileIsTracked) {
            exitWithError("No reason to remove the file.");
        }
        File rmFile = rmStage(fileName);
        if (fileIsStaged && !fileIsTracked) {
            stagedFile.delete();
        } else if (!fileIsStaged && fileIsTracked && currFile.exists()) {
            writeFile(rmFile, readString(currFile));
            Utils.restrictedDelete(currFile);
        } else if (currFile.exists()) {
            writeFile(rmFile, readString(currFile));
            stagedFile.delete();
            Utils.restrictedDelete(currFile);
        } else {
            File currComVer = getCommitVersion(getCurrentCommit(), fileName);
            writeFile(rmFile, readString(currComVer));
        }
    }

    /** Removes a branch.
     * @param args The input to the program
     * */
    private static void rmBranch(String[] args) {
        basicVal(args, 2);
        String branchName = args[1];
        File branch = getBranchPointer(branchName);
        if (!branch.exists()) {
            exitWithError("A branch with that name does not exist.");
        } else if (branchName.equals(getCurrentBranch())) {
            exitWithError("Cannot remove the current branch.");
        }
        branch.delete();
    }

    /** Merges two branches.
     * @param args The input to the program
     * */
    private static void merge(String[] args) throws IOException {
        basicVal(args, 2);
        String mergeBranch = args[1];
        File branch = getBranchPointer(mergeBranch);
        if (!branch.exists()) {
            exitWithError("A branch with that name does not exist.");
        }
        if (!emptyStagingArea()) {
            exitWithError("You have uncommitted changes.");
        }
        checkUntrackError();
        String currBranch = getCurrentBranch();
        if (currBranch.equals(mergeBranch)) {
            exitWithError("Cannot merge a branch with itself.");
        }
        String splitSHA = getSplitPoint(mergeBranch, currBranch);
        String currComMerge = getCurrentCommit(mergeBranch);
        String currComCurr = getCurrentCommit();
        if (splitSHA.equals(currComMerge)) {
            exitWithError("Given branch is an ancestor of the current branch.");
        }
        if (splitSHA.equals(currComCurr)) {
            HashMap<String, String> currentFiles =
                    getAllFileSHAFromCommit(
                            accessObjectContents(getCurrentCommit()));
            HashMap<String, String> overwriteFiles =
                    getAllFileSHAFromCommit(
                            accessObjectContents(getCurrentCommit(args[1])));
            copyCommittedFiles(overwriteFiles);
            for (String file: currentFiles.keySet()) {
                if (!overwriteFiles.containsKey(file)
                        && Utils.join(CWD, file).exists()
                        && canDelete(file)) {
                    Utils.join(CWD, file).delete();
                }
            }
            updateHead(args[1], getBranchDir(args[1]));
            exitWithError("Current branch fast-forwarded.");
        }
        HashMap<String, String> splitFiles =
                getAllFileSHAFromCommit(splitSHA);
        HashMap<String, String> currBFiles =
                getAllFileSHAFromCommit(currComCurr);
        HashMap<String, String> mergeBFiles =
                getAllFileSHAFromCommit(currComMerge);
        checkBranchAndCommit(currBFiles, mergeBFiles, splitFiles,
                splitSHA, mergeBranch);
    }

    /** Adds remotely.
     * @param args The system input.
     * */
    private static void addRemote(String[] args) {
        basicVal(args, 3);
        String[] splitDir = args[3].split("[\\\\/]+");
        String fullDir = "";
        for (String split: splitDir) {
            fullDir += split + File.pathSeparator;
        }
        fullDir = fullDir.substring(0, fullDir.length() - 1);
    }

    /** Removes remotely.
     * @param args The system input.
     * */
    private static void rmRemote(String[] args) {
        basicVal(args, 2);

    }

    /** Pushes remotely.
     * @param args The system input.
     * */
    private static void push(String[] args) {
        basicVal(args, 2);
    }

    /** Fetches remotely.
     * @param args The system input.
     * */
    private static void fetch(String[] args) {
        basicVal(args, 2);

    }

    /** Pulls remotely.
     * @param args The system input.
     * */
    private static void pull(String[] args) {
        basicVal(args, 2);
    }

    /* Utilities */

    /** Returns a HashMap<String, Integer> containing all the commit parents
     * of a given commit. The Integer is the number of commits from the
     * given commit.
     * @param sha The SHA of the commit
     * @param startVal The distance from the front of the branch of the SHA
     * @return
     */
    private static HashMap<String, Integer> getAllCommitParents(
            String sha, int startVal) {
        HashMap<String, Integer> commits = new HashMap<>();
        commits.put(sha, startVal);
        if (sha.equals("")) {
            return null;
        } else if (sha.equals(INITIAL)) {
            return commits;
        }
        String currSHA = sha;
        while (!commits.containsKey(INITIAL)) {
            String[] parents = getPrevComSha(currSHA);
            HashMap<String, Integer> side1 =
                    getAllCommitParents(parents[0], startVal + 1);
            HashMap<String, Integer> side2 =
                    getAllCommitParents(parents[1], startVal + 1);
            if (side1 != null && side1.size() > 0) {
                for (Map.Entry<String, Integer> commit: side1.entrySet()) {
                    if (!commits.containsKey(commit.getKey())) {
                        commits.put(commit.getKey(), commit.getValue());
                    }
                }
            }
            if (side2 != null && side2.size() > 0) {
                for (Map.Entry<String, Integer> commit: side2.entrySet()) {
                    if (!commits.containsKey(commit.getKey())) {
                        commits.put(commit.getKey(), commit.getValue());
                    }
                }
            }
        }
        return commits;
    }

    /** Returns the commit SHA of the split point for branches B1, B2.
     * @param b1 The name of the first branch
     * @param b2 The name of the second branch
     * */
    private static String getSplitPoint(String b1, String b2) {
        String b1Com = getCurrentCommit(b1);
        String b2Com = getCurrentCommit(b2);
        String initial = INITIAL;
        if (b1Com.equals(b2Com)
                || (b1Com.equals(initial) && b2Com.equals(initial))) {
            return b1Com;
        }
        HashMap<String, Integer> b1Coms = getAllCommitParents(b1Com, 0);
        HashMap<String, Integer> b2Coms = getAllCommitParents(b2Com, 0);
        String ret = initial;
        int smallest = Integer.MAX_VALUE;
        Map.
        for (Map.Entry<String, Integer> commit: b1Coms.entrySet()) {
            String currCom = commit.getKey();
            int currDist = commit.getValue();
            if (b2Coms.containsKey(currCom)) {
                if (currDist < smallest) {
                    ret = currCom;
                    smallest = currDist;
                }
            }
        }
        return ret;
    }

    /** Returns the commit of the split point for branches B1, B2. */
    private static File getSplitPtFile(String b1, String b2) {
        return accessObjectContents(getSplitPoint(b1, b2));
    }

    /** Returns the SHA of the (previous) parent commits of
     * the given commit.
     * @param commit The SHA of the given commit
     * @return
     */
    private static String[] getPrevComSha(String commit) {
        String[] prevCom = new String[2];
        String[] commitContents = readString(
                accessObjectContents(commit)).split("\n");
        prevCom[0] = commitContents[1];
        prevCom[1] = commitContents[2];
        return prevCom;
    }

    /** Returns TRUE if a Gitlet repository has been initialized in the current
     * working directory. Does not check for subdirectories and files as
     * the repository should be created iff it is initialized. */
    private static boolean checkInitialized() {
        return GITLET_FOLDER.exists();
    }

    /** Clears the given directory. Does not delete the directory itself
     * or any subfolders and their contents.
     * @param dir The directory to be cleared
     * */
    private static void clearDirectory(File dir) {
        for (String f: getFiles(dir)) {
            File file = Utils.join(dir, f);
            if (file.isFile() && !file.isHidden()
                && canDelete(f)) {
                file.delete();
            }
        }
    }

    /** Specific to testing. Can't delete these
     * two files or the program IDE breaks.
     * @param f The name of the file
     * @return
     * */
    private static boolean canDelete(String f) {
        return !f.equals("Makefile")
                && !f.equals("proj3.iml");
    }

    /** Updates the commit log.
     * @param parent1 The first parent of the commit
     * @param parent2 The second parent of the commit
     * @param sha1 The SHA1 of the commit
     * @param message The commit message
     * @param date The time and date of the commit
     * @param saveLoc The file to which the log entry
     *                is to be saved
     * */
    private static void updateLog(
            String parent1, String parent2, String sha1,
            String message, Date date, File saveLoc) {
        String entry = formatLog(parent1, parent2, sha1, date, message);
        String oldEntries = readString(saveLoc);
        entry = entry + oldEntries;
        writeFile(saveLoc, entry);
    }

    /** Updates the commit log.
     * @param message The commit message
     * @param saveLoc The file to which the log entry
     *                is to be saved
     * */
    private static void updateLog(
            String message, File saveLoc) {
        String oldEntries = readString(saveLoc);
        writeFile(saveLoc, message + oldEntries);
    }

    /**
     * Copies files in the staging area into the corresponding OBJECTS folder.
     * Assumes that the files are all validated as being modifications
     * to existing files or new files. Returns a String containing
     * the contents of the commit
     * (the committed files' SHA1 and their names).
     * @param files The files that are staged that are
     *              to be copied in the OBJECTS folder
     * */
    private static String copyStagedFiles(
            HashMap<String, String> files)
            throws IOException {
        String contents = "";
        if (files == null || files.size() == 0) {
            return contents;
        }
        for (Map.Entry<String, String> file : files.entrySet()) {
            String fileName = file.getKey();
            String fileSHA = file.getValue();
            File origFile = addStage(fileName);
            File copyFile = accessObjectContents(fileSHA);
            copyFile.createNewFile();
            String origContents = readString(origFile);
            writeFile(copyFile, origContents);
            contents += fileName + " " + fileSHA + "\n";
        }
        return contents;
    }

    /**
     * Copies files with the given SHA in FILES from the
     * OBJECTS folder to the CWD.
     * @param files The files whose copies are to be made in
     *              the OBJECTS folder
     */
    private static void copyCommittedFiles(HashMap<String, String> files) {
        for (Map.Entry<String, String> file : files.entrySet()) {
            String fileName = file.getKey();
            String fileSHA = file.getValue();
            File cwdCopy = Utils.join(CWD, fileName);
            File copyFile = accessObjectContents(fileSHA);
            writeFile(cwdCopy, readString(copyFile));
        }
    }

    /** Creates the metadata file of the commit with the given SHA1.
     * The metadata file contains the pointers of the parents and
     * the tracked files and their SHA at the time of the commit.
     * @param branch The branch for the given commit
     * @param parent1 The first parent of the commit
     * @param parent2 The second parent of the commit
     * @param sha1 The SHA1 of the commit
     * @param commitContents The files and their corresponding SHA1
     *                       that the commit tracks
     * */
    private static void createMetadata(String branch,
            String parent1, String parent2, String sha1, String commitContents)
            throws IOException {
        File metadata = accessObjectContents(sha1);
        metadata.createNewFile();
        if (parent1 == null) {
            parent1 = "";
        }
        if (parent2 == null) {
            parent2 = "";
        }
        writeFile(metadata, branch + "\n"
                + parent1 + "\n" + parent2 + "\n" + commitContents);
    }

    /** Formats and returns a log message
     * using parent1, parent2, the SHA1 of the current commit,
     * date, and message.
     * @param parent1 The first parent of the commit
     * @param parent2 The second parent of the commit
     * @param sha1 The SHA1 of the commit
     * @param date The time and date of the commit
     * @param message The commit message
     * */
    private static String formatLog(
            String parent1, String parent2, String sha1,
            Date date, String message) {
        Formatter formatter = new Formatter();
        formatter.format("===\ncommit %s\n", sha1);
        if (parent1 != null && parent2 != null) {
            formatter.format(
                    "Merge: %s %s\n", parent1.substring(0, 7),
                    parent2.substring(0, 7));
        }
        formatter.format(
                "Date: %1$ta %1$tb %1$td %1$tH:%1$tM:%1$tS %1$tY %1$tz\n",
                date);
        formatter.format("%s", message);
        return formatter.toString();
    }

    /** Updates a pointer file (i.e. MASTER) to point to the given POINTER.
     * Does not work for HEAD.
     * @param branchName The name of the branch
     * @param pointer The commit to which the branch will be pointing
     * */
    private static void updatePointer(String branchName, String pointer) {
        writeFile(getBranchPointer(branchName), pointer);
    }

    /** Updates the HEAD pointer to point to the given POINTER.
     * The HEAD pointer has the branch directory (to deal with remote commits)
     * on the first line and the commit SHA1 / pointer on the second line.
     * @param branchName The name of the branch
     * @param branchDir The directory of the branch pointer
     * @param pointer The SHA1 of the commit that HEAD will be
     *                made to point to
     * */
    private static void updateHead(
            String branchName, String branchDir, String pointer) {
        writeFile(HEADS_POINTER,
                branchName + "\n" + branchDir + "\n" + pointer);
    }

    /** Updates the HEAD pointer to point to the same commit
     * as the given branch. The HEAD pointer has the branch
     * directory (to deal with remote commits) on the first
     * line and the commit SHA1 / pointer on the second line.
     * @param branchName The name of the branch
     * @param branchDir The directory of the branch pointer
     * */
    private static void updateHead(String branchName, String branchDir) {
        String pointer = readString(new File(branchDir));
        writeFile(HEADS_POINTER,
                branchName + "\n" + branchDir + "\n" + pointer);
    }

    /** Returns the file corresponding to the given SHA1 id. */
    private static File accessObjectContents(String sha1) {
        if (sha1.length() == SHA1_LEN) {
            return Utils.join(
                    OBJECTS_FOLDER, sha1.substring(0, 2), sha1.substring(2));
        } else if (sha1.length() < SHA1_LEN) {
            File directory = Utils.join(OBJECTS_FOLDER, sha1.substring(0, 2));
            String remainingSHA = sha1.substring(2);
            for (String f : getFiles(directory)) {
                File file = Utils.join(directory, f);
                String fileSubstr = f.substring(0,
                        SHA1_LEN - (SHA1_LEN - sha1.length()) - 2);
                if (fileSubstr.equals(remainingSHA)) {
                    return file;
                }
            }
            exitWithError("No commit with that id exists.");
            return null;
        }
        exitWithError("No commit with that id exists.");
        return null;
    }

    /** Returns the name of the current branch. */
    private static String getCurrentBranch() {
        String[] headPointerContent =
                readString(HEADS_POINTER).split("\n");
        return headPointerContent[0];
    }

    /** Returns the SHA1 of the most recent commit of the given branch.
     * @param branchName The name of the branch from which
     *                   the most recent commit's SHA1 is being
     *                   returned
     * */
    static String getCurrentCommit(String branchName) {
        if (branchName.equals("HEAD")) {
            String[] headPointerContent =
                    readString(HEADS_POINTER).split("\n");
            return headPointerContent[2];
        } else {
            File pointer = getBranchPointer(branchName);
            if (!pointer.exists()) {
                exitWithError("No such branch exists.");
            }
            return readString(pointer);
        }
    }

    /** Returns the SHA1 of the most recent commit of the given branch. */
    static String getCurrentCommit() {
        String[] headPointerContent =
                readString(HEADS_POINTER).split("\n");
        return headPointerContent[2];
    }

    /** Returns the version of FILENAME in the commit with the ID
     * commitSHA.
     * @param commitSHA The commit whose fileName is overwriting fileName in
     *                  the current directory
     * @param fileName The name of the file being copied over
     */
    private static File getCommitVersion(
            String commitSHA, String fileName) {
        File commitMetadata = accessObjectContents(commitSHA);
        if (!commitMetadata.exists()) {
            exitWithError("No commit with that id exists.");
        }
        String fileSHA = getFileSHAFromCommit(commitMetadata, fileName);
        File file = accessObjectContents(fileSHA);
        return file;
    }

    /** Returns whether the given version of the file is being tracked by
     * the given commit.
     * @param comSHA The SHA of the commit
     * @param fNm The name of the file
     * @param fSHA The SHA of the file (contents)
     * @return
     */
    private static boolean trackedByCommit(
            String comSHA, String fNm, String fSHA) {
        if (fNm.charAt(0) == '.') {
            return false;
        }
        File commit = accessObjectContents(comSHA);
        String[] lines = readString(commit).split("\n");
        for (int pos = 3; pos < lines.length; pos += 1) {
            String[] data = lines[pos].split(" ");
            if (data[0].equals(fNm)) {
                return data[1].equals(fSHA);
            }
        }
        return false;
    }

    /** Returns whether the given version of the file is being tracked by
     * the current commit.
     * @param name The name of the file
     * @param fSHA The SHA of the file (contents)
     * @return
     */
    private static boolean trackedByCurrentCommit(String name, String fSHA) {
        return trackedByCommit(getCurrentCommit(), name, fSHA);
    }

    /** Returns whether the current version of the file is being tracked by
     * the current commit.
     * @param fileName The name of the file
     * @param fileSHA The SHA1 of the given file
     * @return
     */
    private static boolean verTrackedByCurrentCommit(String fileName,
                                                     String fileSHA) {
        if (rmStage(fileName).exists()) {
            return false;
        }
        if (nameTrackedByCurrentCommit(fileName)) {
            return trackedByCurrentCommit(fileName, fileSHA);
        }
        return false;
    }

    /** Returns whether the current version of the file is being tracked by
     * the current commit.
     * @param fileName The name of the file
     * @return
     */
    private static boolean nameTrackedByCurrentCommit(String fileName) {
        if (fileName.charAt(0) == '.') {
            return false;
        }
        if (rmStage(fileName).exists()) {
            return false;
        }
        HashMap<String, String> currentCommit =
                getAllFileSHAFromCommit(
                        accessObjectContents(getCurrentCommit()));
        return currentCommit.containsKey(fileName);
    }

    /**
     * Returns the SHA1 id of the given FILENAME from COMMIT (metadata).
     * Assumes that COMMIT exists.
     */
    private static String getFileSHAFromCommit(File commit, String fileName) {
        String[] lines = readString(commit).split("\n");
        for (int pos = 3; pos < lines.length; pos += 1) {
            String[] data = lines[pos].split(" ");
            if (data[0].equals(fileName)) {
                return data[1];
            }
        }
        exitWithError("File does not exist in that commit.");
        return null;
    }

    /**
     * Returns a HashMap<String FileName, String SHA1>
     * containing the names and SHA1 ids of all
     * files from the given commit when given the commit's metadata.
     * Does not include directories.
     * Assumes that COMMIT exists.
     * @param com The commit file
     */
    private static HashMap<String, String> getAllFileSHAFromCommit(File com) {
        String[] lines = readString(com).split("\n");
        HashMap<String, String> files = new HashMap<String, String>();
        for (int pos = 3; pos < lines.length; pos += 1) {
            String[] data = lines[pos].split(" ");
            files.put(data[0], data[1]);
        }
        return files;
    }

    /**
     * Returns a HashMap<String FileName, String SHA1>
     * containing the names and SHA1 ids of all
     * files from the given commit when given the commit's metadata.
     * Does not include directories.
     * Assumes that COMMIT exists.
     * @param comSHA The commit SHA
     */
    private static HashMap<String, String> getAllFileSHAFromCommit(
            String comSHA) {
        return getAllFileSHAFromCommit(accessObjectContents(comSHA));
    }

    /**
     * Returns an ArrayList<String>
     * containing the names of all
     * files from the given commit when given the commit's metadata.
     * Does not include directories.
     * Assumes that COMMIT exists.
     * @param com The commit file
     */
    private static ArrayList<String> getAllFileNmFromCommit(File com) {
        String[] lines = readString(com).split("\n");
        ArrayList<String> files = new ArrayList<>();
        for (int pos = 3; pos < lines.length; pos += 1) {
            String[] data = lines[pos].split(" ");
            files.add(data[0]);
        }
        return files;
    }

    /**
     * Returns a HashMap<String FileName, String SHA1>
     * containing the names and SHA1 ids of all
     * files from the CWD. Does not include directories.
     * Assumes that COMMIT exists.
     */
    private static HashMap<String, String> getAllFileSHAFromCWD() {
        return getAllFileSHAFromDir(CWD);
    }

    /**
     * Returns a HashMap<String FileName, String SHA1> containing
     * the names and SHA1 ids of all files from the given directory.
     * Does not include directories.
     * Assumes that COMMIT exists.
     * @param dir The directory from which all files SHAs are derived from
     *            for the return HashMap
     */
    private static HashMap<String, String> getAllFileSHAFromDir(File dir) {
        HashMap<String, String> files = new HashMap<>();
        for (String name: getFiles(dir)) {
            String sha1 = getSHA(Utils.join(dir, name));
            files.put(name, sha1);
        }
        return files;
    }

    /** Returns a String of the form:
     * (segment)
     * (files)
     * \n
     * Where SEGMENT is of the form "=== SegmentName ===".
     * The files are sorted into alphabetical order.
     * Used in "git status".
     * @param segment The title of the segment
     *                surrounded by === on both sides
     * @param files The files meant to be put under segment
     * @param isLast Whether the segment is the last in the
     *               entire message
     * */
    private static String segmentMessage(String segment,
                                         ArrayList<String> files,
                                         boolean isLast) {
        Collections.sort(files);
        for (String name: files) {
            segment += name + "\n";
        }
        if (!isLast) {
            segment += "\n";
        }
        return segment;
    }

    /** Errors and exits program with code 0 if a Gitlet repository has not been
     * initialized in the current working directory. */
    private static void validateInitialized() {
        if (!checkInitialized()) {
            exitWithError("Not in an initialized Gitlet directory.");
        }
    }

    /**
     * Checks the number of arguments against the expected
     * number. Errors and exits program with code 0 if they don't match.
     * The number of arguments should include the keyword + flags
     * and other arguments.
     * @param args Argument array from command line
     * @param n    Number of expected arguments
     */
    public static void validateNumArgs(String[] args, int n) {
        if (args.length != n) {
            exitWithError("Incorrect operands.");
        }
    }

    /**
     * Checks the number of arguments against the expected
     * number. Returns false if they do not match.
     * The number of arguments should include the keyword + flags
     * and other arguments.
     * @param args Argument array from command line
     * @param n    Number of expected arguments
     */
    public static boolean checkNumArgs(String[] args, int n) {
        if (args.length != n) {
            return false;
        }
        return true;
    }

    /** Gets the branch of the given commit.
     * @param sha The SHA1 of the given commit
     * @return The branch of the given commit
     * */
    private static String getCommitBranch(String sha) {
        File commit = accessObjectContents(sha);
        return readString(commit).split("\n")[0];
    }

    /** Returns the pointer file of the given branch.
     * @param branch The given branch
     * */
    private static File getBranchPointer(String branch) {
        return Utils.join(HEADS_FOLDER, branch);
    }

    /** Returns the pointer file of the given branch.
     * @param branch The given branch
     * */
    private static String getBranchDir(String branch) {
        return getBranchPointer(branch).getAbsolutePath();
    }

    /** Returns the SHA1 of the given file.
     * @param file The given file
     * */
    private static String getSHA(File file) {
        return Utils.sha1(readString(file));
    }

    /**
     * Prints the given error message and exits the program with system code 0.
     * @param error The error message
     */
    public static void exitWithError(String error) {
        System.out.print(error);
        System.exit(0);
    }

    /** Returns the contents of the file as a string.
     * @param file The file to be read
     * */
    private static String readString(File file) {
        return Utils.readContentsAsString(file);
    }

    /** Returns the non-directory files in DIR
     * as a list.
     * @param dir The directory to be iterated over
     * */
    private static ArrayList<String> getFiles(File dir) {
        List<String> files = Utils.plainFilenamesIn(dir);
        if (files != null) {
            return (new ArrayList<String>(Utils.plainFilenamesIn(dir)));
        }
        return new ArrayList<String>();
    }

    /** Adds every non-directory file in DIR to FILES.
     * @param dir The directory to be iterated over
     * @param files The List<File> to be added to
     * */
    private static void getFiles(File dir, List<File> files) {
        for (String fileName: getFiles(dir)) {
            files.add(Utils.join(dir, fileName));
        }
    }

    /** Returns true if F1 and F2 have the same SHA1. */
    private static boolean sameSHA(File f1, File f2) {
        String sha1 = getSHA(f1);
        String sha2 = getSHA(f2);
        return sha1.equals(sha2);
    }

    /** Returns true if the staging area is empty. */
    private static boolean emptyStagingArea() {
        ArrayList<String> addFiles = getFiles(ADD_STAGING_FOLDER);
        ArrayList<String> rmFiles = getFiles(RM_STAGING_FOLDER);
        if (addFiles == null && rmFiles == null) {
            return true;
        } else if (addFiles != null && rmFiles != null) {
            if (addFiles.size() == 0 && rmFiles.size() == 0) {
                return true;
            }
        }
        return false;
    }

    /** Returns the file corresponding to
     * Utils.join(ADD_STAGING_FOLDER, fName).
     * @param fName The name of the file
     * @return
     */
    private static File addStage(String fName) {
        return Utils.join(ADD_STAGING_FOLDER, fName);
    }

    /** Returns the file corresponding to
     * Utils.join(RM_STAGING_FOLDER, fName).
     * @param fName The name of the file
     * @return
     */
    private static File rmStage(String fName) {
        return Utils.join(RM_STAGING_FOLDER, fName);
    }

    /** Runs the basic validation of checking
     * if the directory is initialized and
     * if the number of arguments is correct.
     * @param args The system input
     * @param exp The expected number of arguments
     */
    private static void basicVal(String[] args, int exp) {
        validateInitialized();
        validateNumArgs(args, exp);
    }

    /** Checks the current branch commit in "gitlet merge".
     * @param currBFiles The files in the current branch's
     *                   current commit
     * @param mergeBFiles The files in the given branch's
     *                   current commit
     * @param splitFiles The files in the split point commit
     * @param splitSHA The SHA of the split point
     * @param mergeBranch The given branch
     */
    private static void checkBranchAndCommit(HashMap<String, String> currBFiles,
           HashMap<String, String> mergeBFiles,
           HashMap<String, String> splitFiles, String splitSHA,
           String mergeBranch) throws IOException {
        boolean inConflict = false;
        boolean doesNothing = true;
        String mergeContents = "";
        for (Map.Entry<String, String> file: currBFiles.entrySet()) {
            String fName = file.getKey();
            String fSHA = file.getValue();
            if (!mergeBFiles.containsKey(fName)) {
                if (!splitFiles.containsKey(fName)) {
                    mergeContents += fName + " " + fSHA + "\n";
                } else {
                    if (fSHA.equals(splitFiles.get(fName))) {
                        doesNothing = false;
                        if (Utils.join(CWD, fName).exists()
                            && canDelete(fName)) {
                            Utils.join(CWD, fName).delete();
                        }
                    } else {
                        doesNothing = false;
                        inConflict = true;
                        String confContents = getConflictMsg(
                                readCommit(fSHA), "");
                        writeFile(Utils.join(CWD, fName), confContents);
                        mergeContents += fName + " "
                                + Utils.sha1(confContents) + "\n";
                    }
                }
            } else {
                String mergeSHA = mergeBFiles.get(fName);
                if (splitFiles.containsKey(fName)) {
                    if (!fSHA.equals(mergeSHA)) {
                        doesNothing = false;
                        inConflict = true;
                        String confContents = getConflictMsg(readCommit(fSHA),
                                readCommit(mergeSHA));
                        writeFile(Utils.join(CWD, fName), confContents);
                        mergeContents += fName + " "
                                + Utils.sha1(confContents) + "\n";
                    } else if (fSHA.equals(splitFiles.get(fName))) {
                        if (!fSHA.equals(mergeSHA)) {
                            doesNothing = false;
                            mergeContents += fName + " " + mergeSHA + "\n";
                            File copy = accessObjectContents(mergeSHA);
                            writeFile(Utils.join(CWD, fName), readString(copy));
                        }
                    } else if (!fSHA.equals(splitFiles.get(fName))
                            && splitFiles.get(fName).equals(mergeSHA)) {
                        mergeContents += fName + " " + fSHA + "\n";
                    } else if (!fSHA.equals(splitFiles.get(fName))
                            && fSHA.equals(mergeSHA)) {
                        mergeContents += fName + " " + fSHA + "\n";
                    }
                } else {
                    mergeContents += fName + " " + fSHA + "\n";
                }
            }
        }
        checkMergeBranchAndCommit(currBFiles, mergeBFiles, splitFiles,
                splitSHA, mergeBranch, mergeContents, doesNothing, inConflict);
    }

    /** Handles the updating of pointers and logs
     * for "gitlet merge".
     * @param currComCurr The current branch's commit SHA
     * @param currComMerge The given branch's commit SHA
     * @param splitSHA The SHA of the split point
     * @param mergeBranch The given branch
     * @param commitDate The date of the merge
     * @param mergeContents The merge contents (files and file SHAs)
     * @param currBranch The current branch
     */
    private static void updateMergeMetadata(String currComCurr,
                 String currComMerge, String splitSHA, String mergeContents,
                 String currBranch, String mergeBranch,
                 Date commitDate) throws IOException {
        String mergeMsg = String.format("Merged %s into %s.",
                mergeBranch, currBranch);
        String commitSHA = Utils.sha1(currComCurr
                + currComMerge + splitSHA + mergeContents);
        updateHead(currBranch, getBranchDir(currBranch), commitSHA);
        updatePointer(currBranch, commitSHA);
        createMetadata(currBranch, currComCurr,
                currComMerge, commitSHA, mergeContents);
        updateLog(currComCurr, currComMerge, commitSHA,
                mergeMsg + "\n\n", commitDate,
                Utils.join(LOCAL_LOGS_FOLDER, currBranch));
        updateLog(currComCurr, currComMerge, commitSHA,
                mergeMsg + "\n\n", commitDate, HEADS_LOG);
    }

    /** Checks the current branch commit in "gitlet merge".
     * @param currBFiles The files in the current branch's
     *                   current commit
     * @param mergeBFiles The files in the given branch's
     *                   current commit
     * @param splitFiles The files in the split point commit
     * @param splitSHA The SHA of the split point
     * @param mergeBranch The given branch
     * @param mergeContents The current merge contents (file and SHA)
     * @param doesNothing Whether the merge does nothing
     * @param inConflict Whether the merge is in conflict
     */
    private static void checkMergeBranchAndCommit(HashMap<String, String>
           currBFiles, HashMap<String, String> mergeBFiles,
           HashMap<String, String> splitFiles,
           String splitSHA, String mergeBranch, String mergeContents,
           boolean doesNothing, boolean inConflict) throws IOException {
        String currBranch = getCurrentBranch();
        String currComCurr = getCurrentCommit();
        String currComMerge = getCurrentCommit(mergeBranch);
        for (Map.Entry<String, String> file: mergeBFiles.entrySet()) {
            String fName = file.getKey();
            String fSHA = file.getValue();
            if (!currBFiles.containsKey(fName)) {
                if (!splitFiles.containsKey(fName)) {
                    doesNothing = false;
                    String copy = readCommit(fSHA);
                    writeFile(Utils.join(CWD, fName), copy);
                    mergeContents += fName + " " + fSHA + "\n";
                } else if (!file.getValue()
                        .equals(splitFiles.get(file.getKey()))) {
                    doesNothing = false;
                    inConflict = true;
                    String confContents = getConflictMsg("", readCommit(fSHA));
                    writeFile(Utils.join(CWD, fName), confContents);
                    mergeContents += fName + " "
                            + Utils.sha1(confContents) + "\n";
                }
            }
        }
        if (doesNothing) {
            exitWithError("No changes added to the commit.");
        }
        if (inConflict) {
            System.out.println("Encountered a merge conflict.");
        }
        updateMergeMetadata(currComCurr, currComMerge, splitSHA,
                mergeContents, currBranch, mergeBranch, new Date());

    }

    /** Returns the result of readString(accessObjectContents(sha)).
     * @param sha The SHA of the given commit. */
    private static String readCommit(String sha) {
        return readString(accessObjectContents(sha));
    }

    /** Runs Utils.writeContents(file, input).
     * @param file The file
     * @param input The input
     * */
    private static void writeFile(File file, String input) {
        Utils.writeContents(file, input);
    }

    /** Checks the untracked file error. */
    private static void checkUntrackError() {
        for (String file: getFiles(CWD)) {
            if (file.charAt(0) != '.'
                    && canDelete(file)) {
                if ((!nameTrackedByCurrentCommit(file)
                        || !verTrackedByCurrentCommit(file,
                        getSHA(Utils.join(CWD, file))))
                        && !Utils.join(ADD_STAGING_FOLDER, file).exists()) {
                    exitWithError("There is an untracked file in the way; "
                            + "delete it, or add and commit it first.");
                }
            }
        }
    }

    /** Checks the untracked file error.
     * @param cSHA The given SHA for which CWD is being checked */
    private static void checkUntrackError(String cSHA) {
        for (String file: getFiles(CWD)) {
            if (file.charAt(0) != '.'
                    && canDelete(file)) {
                if ((!nameTrackedByCurrentCommit(file)
                        || !trackedByCommit(cSHA, file,
                            getSHA(Utils.join(CWD, file))))
                    && !Utils.join(ADD_STAGING_FOLDER, file).exists()
                    && !verTrackedByCurrentCommit(file,
                        getSHA(Utils.join(CWD, file)))) {
                    exitWithError("There is an untracked file in the way; "
                            + "delete it, or add and commit it first.");
                }
            }
        }
    }

    /** Returns the contents of the file after a merge conflict.
     * @param currBr The contents of the file in the current branch
     * @param merBr The contents of the file in the given branch
     * */
    private static String getConflictMsg(String currBr, String merBr) {
        return "<<<<<<< HEAD\n" + currBr
                + "=======\n" + merBr
                + ">>>>>>>\n";
    }
}
