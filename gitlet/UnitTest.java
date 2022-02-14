package gitlet;

import ucb.junit.textui;
import org.junit.Test;

import java.io.File;

/** The suite of all JUnit tests for the gitlet package.
 *  @author
 */
public class UnitTest {

    /**
     * Run the JUnit tests in the loa package. Add xxxTest.class entries to
     * the arguments of runClasses to run other JUnit tests.
     */
    public static void main(String[] ignored) {
        System.exit(textui.runClasses(UnitTest.class));
    }

    /* Testing Utilities */

    /**
     * Current Working Directory.
     */
    static final File CWD = new File(".");

    /**
     * Main metadata folder.
     */
    static final File GITLET_FOLDER = new File(".gitlet");

    /**
     * Logs metadata folder.
     */
    static final File LOGS_FOLDER = new File(
            ".gitlet" + File.separator + "logs");

    /**
     * Objects / Blobs / File contents metadata folder.
     */
    static final File OBJECTS_FOLDER = new File(
            ".gitlet" + File.separator + "objects");

    /**
     * References metadata folder.
     * Contains the pointers of each branch and HEAD.
     */
    static final File REFS_FOLDER = new File(
            ".gitlet" + File.separator + "refs");

    /**
     * Local logs metadata folder.
     */
    static final File LOCAL_LOGS_FOLDER = Utils.join(GITLET_FOLDER, "logs");

    /**
     * Remote logs metadata folder.
     */
    static final File REMOTE_LOGS_FOLDER = Utils.join(GITLET_FOLDER, "logs");

    /**
     * Heads metadata folder within REFS_FOLDER.
     * Contains the pointers of each branch and HEAD.
     */
    static final File HEADS_FOLDER = new File(
            ".gitlet" + File.separator + "refs"
                    + File.separator + "heads");

    /**
     * Staging folder for "git add".
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

    /**
     * The set of possible characters in the first two characters of
     * SHA1 hashes.
     * Each may be in either or both the first and second position.
     */
    static final char[] COMBOS = {'a', 'b', 'c', 'd', 'e', 'f',
                                  '0', '1', '2', '3', '4', '5',
                                  '6', '7', '8', '9'};

    /* Testing Input Files */

    /**
     * The wug.txt input file in testing/src.
     */
    static final File WUG_SOURCE =
            Utils.join(CWD, "testing", "src", "wug.txt");

    /**
     * The wug2.txt input file in testing/src.
     */
    static final File WUG2_SOURCE =
            Utils.join(CWD, "testing", "src", "wug2.txt");

    /**
     * The wug3.txt input file in testing/src.
     */
    static final File WUG3_SOURCE =
            Utils.join(CWD, "testing", "src", "wug3.txt");

    /**
     * The notwug.txt input file in testing/src.
     */
    static final File NOT_WUG_SOURCE =
            Utils.join(CWD, "testing", "src", "notwug.txt");

    /**
     * The changed input file in testing/src_change.
     */
    static final File CHANGED_WUG_SOURCE =
            Utils.join(CWD, "testing", "src_change", "wug0.txt");

    /**
     * The changed notwug.txt input file in testing/src_change.
     */
    static final File CHANGED_NOT_WUG_SOURCE =
            Utils.join(CWD, "testing", "src_change", "notwug0.txt");

    /**
     * The f.txt input file in testing/src.
     */
    static final File F_SOURCE =
            Utils.join(CWD, "testing", "src", "f.txt");

    /**
     * The g.txt input file in testing/src.
     */
    static final File G_SOURCE =
            Utils.join(CWD, "testing", "src", "g.txt");


    /**
     * Sample tests folder.
     */
    static final File TESTING_FOLDER = Utils.join(CWD, "testing", "samples");

    /** Returns the SHA1 of the given file.
     * @param file The given file
     * */
    private static String getSHA(File file) {
        return Utils.sha1(readString(file));
    }

    /** Returns the contents of the file as a string.
     * @param file The file to be read
     * */
    private static String readString(File file) {
        return Utils.readContentsAsString(file);
    }

    /** Returns true if F1 and F2 have the same SHA1. */
    private static boolean sameSHA(File f1, File f2) {
        String sha1 = getSHA(f1);
        String sha2 = getSHA(f2);
        return sha1.equals(sha2);
    }

    /**
     * Checks that the given file or directory exists.
     * Returns a GitletException if it doesn't.
     *
     * @param file The file or directory you are checking for
     */
    public static void validateFile(File file, String path) {
        if (file.exists()) {
            throw new GitletException(path + " does not exist.");
        }
    }

    /**
     * Validates that the repository is not initialized.
     * Throws an error otherwise.
     */
    public static void validateNotInitialized() {
        if (GITLET_FOLDER.exists()) {
            throw new GitletException("Repository already initialized.");
        }
    }

    /**
     * Validates that the given FILE is the same as the SOURCE file.
     * Throws an error otherwise.
     */
    public static void validateSameFile(File source, File file) {
        String sourceSHA = Utils.sha1(Utils.readContentsAsString(source));
        String fileSHA = Utils.sha1(Utils.readContentsAsString(file));
        String f = Utils.readContentsAsString(file);
        if (!sourceSHA.equals(fileSHA)) {
            throw new GitletException(file.getPath()
                    + " not the same as " + source.getPath());
        }
    }

    /**
     * Returns a String array of the commands.
     */
    public static String[] commands(String... args) {
        String[] commands = new String[args.length];
        for (int pos = 0; pos < args.length; pos += 1) {
            commands[pos] = args[pos];
        }
        return commands;
    }

    /**
     * The process of prelude1.inc.
     */
    private static void prelude1() {
        Main.main(commands("init"));
    }

    /**
     * The process of setup1.inc.
     */
    private static void setup1() {
        prelude1();
        Utils.writeContents(Utils.join(CWD, "f.txt"),
                Utils.readContentsAsString(F_SOURCE));
        Utils.writeContents(Utils.join(CWD, "g.txt"),
                Utils.readContentsAsString(G_SOURCE));
        Main.main(commands("add", "g.txt"));
        Main.main(commands("add", "f.txt"));
    }

    /**
     * The process of setup2.inc.
     */
    private static void setup2() {
        setup1();
        Main.main(commands("commit", "Two files"));
    }

    /* Error-Handling Tests */

    /**
     * Checks that the program initializes correctly.
     * Essentially test01-init.in.
     */
    @Test
    public void test01Init() {
        validateNotInitialized();
        validateFile(GITLET_FOLDER, ".gitlet");
        validateFile(LOGS_FOLDER, ".gitlet" + File.separator + "logs");
        validateFile(OBJECTS_FOLDER, ".gitlet" + File.separator + "objects");
        validateFile(REFS_FOLDER, ".gitlet" + File.separator + "refs");
        validateFile(HEADS_FOLDER, ".gitlet" + File.separator + "refs"
                + File.separator + "heads");
        validateFile(STAGING_FOLDER, ".gitlet" + File.separator + "staging");
        validateFile(ADD_STAGING_FOLDER, ".gitlet" + File.separator + "staging"
                + File.separator + "add");
        validateFile(RM_STAGING_FOLDER, ".gitlet" + File.separator + "staging"
                + File.separator + "rm");
        validateFile(LOCAL_LOGS_FOLDER,
                ".gitlet" + File.separator + "refs" + File.separator + "local");
        validateFile(REMOTE_LOGS_FOLDER,
                ".gitlet" + File.separator + "refs"
                        + File.separator + "remote");
        for (int first = 0; first < COMBOS.length; first += 1) {
            for (int second = 0; second < COMBOS.length; second += 1) {
                String folderName =
                        String.format("%c%c", COMBOS[first], COMBOS[second]);
                String path = OBJECTS_FOLDER.getPath() + folderName;
                validateFile(new File(OBJECTS_FOLDER, path), path);
            }
        }
    }

    /* Correct Tests */

    /**
     * A comprehensive test that checks for adding, committing,
     * modifying, and checking out. Essentially test02-basic-checkout.in
     * but modified to make it easier to test locally and with
     * some git log stuff.
     * # A simple test of adding, committing, modifying,
     * checking out, and checking logs.
     * Tests:
     * - init
     * - add
     * - commit
     * - checkout -- [file name] (most recent commit of current branch)
     */
    @Test
    public void comprehensiveTest0() {
        Main.main(commands("init"));
        File cwdWug = Utils.join(CWD, "testWug.txt");
        Main.main(commands("status"));
        Utils.writeContents(cwdWug, Utils.readContentsAsString(WUG_SOURCE));
        Main.main(commands("status"));
        Main.main(commands("add", "testWug.txt"));
        Main.main(commands("commit", "added wug"));
        Main.main(commands("status"));
        validateSameFile(WUG_SOURCE, Utils.join(CWD, "testWug.txt"));
        Utils.writeContents(cwdWug,
                Utils.readContentsAsString(CHANGED_WUG_SOURCE));
        Main.main(commands("status"));
        validateSameFile(CHANGED_WUG_SOURCE, Utils.join(CWD, "testWug.txt"));
        Main.main(commands("add", "testWug.txt"));
        Main.main(commands("checkout", "--", "testWug.txt"));
        Main.main(commands("status"));
        validateSameFile(WUG_SOURCE, Utils.join(CWD, "testWug.txt"));
        Main.main("log");
        Main.main(commands("commit", "added CHANGED wug"));
        Main.main(commands("checkout", "--", "testWug.txt"));
        Main.main(commands("status"));
        validateSameFile(CHANGED_WUG_SOURCE, Utils.join(CWD, "testWug.txt"));
        Main.main("log");
    }

    /**
     * Essentially test02-basic-checkout.in.
     */
    @Test
    public void test02BasicCheckout() {
        Main.main(commands("init"));
        Main.main(commands("status"));
        File cwdWug = Utils.join(CWD, "wug.txt");
        Utils.writeContents(cwdWug, Utils.readContentsAsString(WUG_SOURCE));
        Main.main(commands("add", "wug.txt"));
        Main.main(commands("commit", "added wug"));
        Main.main(commands("status"));
        validateSameFile(WUG_SOURCE, Utils.join(CWD, "wug.txt"));
        Utils.writeContents(cwdWug, Utils.readContentsAsString(NOT_WUG_SOURCE));
        validateSameFile(NOT_WUG_SOURCE, Utils.join(CWD, "wug.txt"));
        Main.main(commands("checkout", "--", "wug.txt"));
        validateSameFile(WUG_SOURCE, Utils.join(CWD, "wug.txt"));
        Main.main(commands("status"));
        Main.main("log");
    }

    /**
     * Essentially test04-prev-checkout.in.
     * Tests that "checkout [commit id] -- [file name]" works.
     */
    @Test
    public void test04PrevCheckout() {
        Main.main(commands("init"));
        File cwdWug = Utils.join(CWD, "wug.txt");
        Utils.writeContents(cwdWug, Utils.readContentsAsString(WUG_SOURCE));
        Main.main(commands("add", "wug.txt"));
        Main.main(commands("commit", "version 1 of wug.txt (wug)"));
        validateSameFile(WUG_SOURCE, Utils.join(CWD, "wug.txt"));
        String ver1SHA = Main.getCurrentCommit();
        Utils.writeContents(cwdWug, Utils.readContentsAsString(NOT_WUG_SOURCE));
        Main.main(commands("add", "wug.txt"));
        Main.main(commands("commit", "version 2 of wug.txt (not wug)"));
        validateSameFile(NOT_WUG_SOURCE, Utils.join(CWD, "wug.txt"));
        String ver2SHA = Main.getCurrentCommit();
        Main.main("log");
        Main.main("checkout", ver1SHA, "--", "wug.txt");
        validateSameFile(WUG_SOURCE, Utils.join(CWD, "wug.txt"));
        Main.main("checkout", ver2SHA, "--", "wug.txt");
        validateSameFile(NOT_WUG_SOURCE, Utils.join(CWD, "wug.txt"));
    }

    /**
     * Essentially test11-basic-status.in.
     */
    @Test
    public void test11BasicStatus() {
        Main.main(commands("init"));
        Main.main(commands("status"));
    }

    /**
     * Essentially test13-remove-status.in.
     */
    @Test
    public void test13RemoveStatus() {
        setup2();
        Main.main(commands("rm", "f.txt"));
        Main.main(commands("status"));
    }

    /**
     * Essentially test14-add-remove.in.
     */
    @Test
    public void test14AddRemoveStatus() {
        setup1();
        Main.main(commands("rm", "f.txt"));
        Main.main(commands("status"));
    }

    /**
     * Essentially test15-remove-add-status.in.
     */
    @Test
    public void test15RemoveAddStatus() {
        setup2();
        Main.main(commands("rm", "f.txt"));
        Utils.writeContents(Utils.join(CWD, "f.txt"),
                Utils.readContentsAsString(F_SOURCE));
        Main.main(commands("add", "f.txt"));
        Main.main(commands("status"));
    }

    /**
     * Essentially test16-empty-commit-err.in.
     */
    @Test
    public void test16EmptyCommitErr() {
        prelude1();
        Main.main(commands("commit", "Nothing here"));
    }

    /**
     * Essentially test18-nop-add.in.
     */
    @Test
    public void test18NopAdd() {
        setup2();
        Main.main(commands("add", "f.txt"));
        Main.main(commands("status"));
    }

    /**
     * Essentially test20-status-after-commit.in.
     */
    @Test
    public void test20StatusAfterCommit() {
        setup2();
        Main.main(commands("status"));
        Main.main(commands("rm", "f.txt"));
        Main.main(commands("commit", "Removed f.txt"));
        Main.main(commands("status"));
    }

    /**
     * Essentially test22-remove-deleted-file.in.
     */
    @Test
    public void test22RemoveDeletedFile() {
        setup2();
        Utils.join(CWD, "f.txt").delete();
        Main.main(commands("rm", "f.txt"));
        Main.main(commands("status"));
    }

    /**
     * Essentially test23-global-log.in.
     */
    @Test
    public void test23GlobalLog() {
        setup2();
        Utils.writeContents(Utils.join(CWD, "h.txt"),
                Utils.readContentsAsString(WUG_SOURCE));
        Main.main(commands("add", "h.txt"));
        Main.main(commands("commit", "Add h"));
        Main.main(commands("log"));
        System.out.println("------END OF LOG---------");
        Main.main(commands("global-log"));
    }

    /**
     * Essentially test24-global-log-prev.in.
     */
    @Test
    public void test24GlobalLogPrev() {
        setup2();
        Utils.writeContents(Utils.join(CWD, "h.txt"),
                Utils.readContentsAsString(WUG_SOURCE));
        Main.main(commands("add", "h.txt"));
        Main.main(commands("commit", "Add h"));
        Main.main(commands("log"));
        System.out.println("------END OF LOG---------");
        Main.main(commands("reset"));
        Main.main(commands("global-log"));
    }

    /**
     * Essentially test29-bad-checkouts-err.in.
     */
    @Test
    public void test29BadCheckoutsErr() {
        prelude1();
        Utils.writeContents(Utils.join(CWD, "wug.txt"),
                Utils.readContentsAsString(WUG_SOURCE));
        Main.main(commands("add", "wug.txt"));
        Main.main(commands("commit", "version 1 of wug.txt"));
        Utils.writeContents(Utils.join(CWD, "wug.txt"),
                Utils.readContentsAsString(NOT_WUG_SOURCE));
        Main.main(commands("add", "wug.txt"));
        Main.main(commands("commit", "version 2 of wug.txt"));
        Main.main(commands("log"));
    }

    /**
     * Essentially test30-branches.in.
     */
    @Test
    public void test30Branches() {
        prelude1();
        Main.main(commands("branch", "other"));
        Utils.writeContents(Utils.join(CWD, "f.txt"),
                Utils.readContentsAsString(WUG_SOURCE));
        Utils.writeContents(Utils.join(CWD, "g.txt"),
                Utils.readContentsAsString(NOT_WUG_SOURCE));
        Main.main(commands("add", "g.txt"));
        Main.main(commands("add", "f.txt"));
        Main.main(commands("commit", "Main two files"));
        Main.main(commands("checkout", "other"));
        assert !Utils.join(CWD, "f.txt").exists();
        assert !Utils.join(CWD, "g.txt").exists();
        Utils.writeContents(Utils.join(CWD, "f.txt"),
                Utils.readContentsAsString(NOT_WUG_SOURCE));
        validateSameFile(Utils.join(CWD, "f.txt"), NOT_WUG_SOURCE);
        Main.main(commands("add", "f.txt"));
        Main.main(commands("commit", "Alternative file"));
        Main.main(commands("checkout", "master"));
        assert sameSHA(Utils.join(CWD, "f.txt"), WUG_SOURCE);
        assert sameSHA(Utils.join(CWD, "g.txt"), WUG_SOURCE);
        Main.main(commands("checkout", "other"));
        assert sameSHA(Utils.join(CWD, "f.txt"), NOT_WUG_SOURCE);
        assert !Utils.join(CWD, "g.txt").exists();
    }

    /**
     * Essentially test39-short-uid.in.
     */
    @Test
    public void test39ModifiedShortUID() {
        Main.main(commands("init"));
        Utils.writeContents(Utils.join(CWD, "f.txt"),
                Utils.readContentsAsString(WUG_SOURCE));
        Utils.writeContents(Utils.join(CWD, "g.txt"),
                Utils.readContentsAsString(NOT_WUG_SOURCE));
        validateSameFile(Utils.join(CWD, "f.txt"), WUG_SOURCE);
        validateSameFile(Utils.join(CWD, "g.txt"), NOT_WUG_SOURCE);
        Main.main(commands("add", "f.txt"));
        Main.main(commands("add", "g.txt"));
        Main.main(commands("commit", "(f) wug.txt, (g) notwug.txt switched"));
        Utils.writeContents(Utils.join(CWD, "f.txt"),
                Utils.readContentsAsString(NOT_WUG_SOURCE));
        Utils.writeContents(Utils.join(CWD, "g.txt"),
                Utils.readContentsAsString(WUG_SOURCE));
        validateSameFile(Utils.join(CWD, "g.txt"), WUG_SOURCE);
        validateSameFile(Utils.join(CWD, "f.txt"), NOT_WUG_SOURCE);
        Main.main(commands("add", "f.txt"));
        Main.main(commands("add", "g.txt"));
        Main.main(commands("commit", "(g) wug.txt, (f) notwug.txt switched"));
        Main.main(commands("log"));
    }

    /**
     * Essentially test33-merge-no-conflicts.in.
     */
    @Test
    public void test33MergeNoConflicts() {
        setup2();
        Main.main(commands("branch", "other"));
        Utils.writeContents(Utils.join(CWD, "h.txt"),
                Utils.readContentsAsString(WUG2_SOURCE));
        Main.main(commands("add", "h.txt"));
        Main.main(commands("rm", "g.txt"));
        Main.main(commands("commit", "Add h.txt and remove g.txt"));
        Main.main(commands("checkout", "other"));
        Main.main(commands("rm", "f.txt"));
        Utils.writeContents(Utils.join(CWD, "k.txt"),
                Utils.readContentsAsString(WUG3_SOURCE));
        Main.main(commands("add", "k.txt"));
        Main.main(commands("commit", "Add k.txt and remove f.txt"));
        Main.main(commands("checkout", "master"));
        Main.main(commands("merge", "other"));
        Main.main(commands("log"));
        Main.main(commands("status"));
    }

    /**
     * Essentially test40-special-merge-cases.in.
     */
    @Test
    public void test40SpecialMergeCases() {
        setup2();
        Main.main(commands("branch", "b1"));
        Utils.writeContents(Utils.join(CWD, "h.txt"),
                Utils.readContentsAsString(WUG2_SOURCE));
        Main.main(commands("add", "h.txt"));
        Main.main(commands("commit", "Add h.txt"));
        Main.main(commands("branch", "b2"));
        Main.main(commands("rm", "f.txt"));
        Main.main(commands("commit", "remove f.txt"));
        Main.main(commands("merge", "b1"));
    }

    /**
     * Essentially test1Mod.in.
     */
    @Test
    public void test1Mod() {
        Main.main(commands("init"));
        Utils.writeContents(Utils.join(CWD, "nct.txt"),
                Utils.readContentsAsString(WUG2_SOURCE));
        Main.main(commands("add", "nct.txt"));
        Main.main(commands("commit", "nct.txt commit"));
        Main.main(commands("branch", "yespls"));
        Utils.writeContents(Utils.join(CWD, "b.txt"),
                Utils.readContentsAsString(WUG3_SOURCE));
        Main.main(commands("add", "b.txt"));
        Main.main(commands("commit", "b.txt commit"));
        Main.main(commands("checkout", "master"));
    }

    /**
     * Essentially test34-merge-conflicts.in.
     */
    @Test
    public void test34MergeConflicts() {
        setup2();
        Main.main(commands("branch", "other"));
        Utils.writeContents(Utils.join(CWD, "h.txt"),
                Utils.readContentsAsString(WUG2_SOURCE));
        Main.main(commands("add", "h.txt"));
        Main.main(commands("rm", "g.txt"));
        Utils.writeContents(Utils.join(CWD, "f.txt"),
                Utils.readContentsAsString(WUG2_SOURCE));
        Main.main(commands("add", "f.txt"));
        Main.main(commands("commit",
                "Add h.txt, remove g.txt, and change f.txt"));
        Main.main(commands("checkout", "other"));
        Utils.writeContents(Utils.join(CWD, "f.txt"),
                Utils.readContentsAsString(NOT_WUG_SOURCE));
        Main.main(commands("add", "f.txt"));
        Utils.writeContents(Utils.join(CWD, "k.txt"),
                Utils.readContentsAsString(WUG3_SOURCE));
        Main.main(commands("add", "k.txt"));
        Main.main(commands("commit", "Add k.txt and modify f.txt"));
        Main.main(commands("checkout", "master"));
        Main.main(commands("log"));
        Main.main(commands("merge", "other"));
    }

    /**
     * Essentially test36-merge-parent2.in.
     */
    @Test
    public void test36MergeParent2() {
        prelude1();
        Main.main(commands("branch", "B1"));
        Main.main(commands("branch", "B2"));
        Main.main(commands("checkout", "B1"));
        Utils.writeContents(Utils.join(CWD, "h.txt"),
                Utils.readContentsAsString(WUG_SOURCE));
        Main.main(commands("add", "h.txt"));
        Main.main(commands("commit", "Add h.txt"));
        Main.main(commands("checkout", "B2"));
        Utils.writeContents(Utils.join(CWD, "f.txt"),
                Utils.readContentsAsString(WUG_SOURCE));
        Main.main(commands("add", "f.txt"));
        Main.main(commands("commit", "f.txt added"));
        Main.main(commands("branch", "C1"));
        Utils.writeContents(Utils.join(CWD, "g.txt"),
                Utils.readContentsAsString(NOT_WUG_SOURCE));
        Main.main(commands("add", "g.txt"));
        Main.main(commands("rm", "f.txt"));
        Main.main(commands("commit", "g.txt added, f.txt removed"));
        validateSameFile(Utils.join(CWD, "g.txt"), NOT_WUG_SOURCE);
        assert !Utils.join(CWD, "f.txt").exists();
        assert !Utils.join(CWD, "h.txt").exists();
        Main.main(commands("checkout", "B1"));
        validateSameFile(Utils.join(CWD, "h.txt"), WUG_SOURCE);
        assert !Utils.join(CWD, "f.txt").exists();
        assert !Utils.join(CWD, "g.txt").exists();
        Main.main(commands("merge", "C1"));
        validateSameFile(Utils.join(CWD, "f.txt"), WUG_SOURCE);
        validateSameFile(Utils.join(CWD, "h.txt"), WUG_SOURCE);
        assert !Utils.join(CWD, "g.txt").exists();
        Main.main(commands("merge", "B2"));
        assert !Utils.join(CWD, "f.txt").exists();
        validateSameFile(Utils.join(CWD, "g.txt"), NOT_WUG_SOURCE);
        validateSameFile(Utils.join(CWD, "h.txt"), WUG_SOURCE);
    }

    /**
     * Essentially test37-reset1.in.
     */
    @Test
    public void test37Reset1() {
        setup2();
        Main.main(commands("branch", "other"));
        Utils.writeContents(Utils.join(CWD, "h.txt"),
                Utils.readContentsAsString(WUG2_SOURCE));
        Main.main(commands("add", "h.txt"));
        Main.main(commands("rm", "g.txt"));
        Main.main(commands("commit", "Add h.txt and remove g.txt"));
        Main.main(commands("checkout", "other"));
        Main.main(commands("rm", "f.txt"));
        Utils.writeContents(Utils.join(CWD, "k.txt"),
                Utils.readContentsAsString(WUG3_SOURCE));
        Main.main(commands("add", "k.txt"));
        Main.main(commands("commit", "Add k.txt and remove f.txt"));
        Main.main(commands("log"));
        System.out.println("------END OF 'OTHER' LOG--------");
        Main.main(commands("checkout", "master"));
        Main.main(commands("log"));
        System.out.println("------END OF 'MASTER' LOG--------");
        Utils.writeContents(Utils.join(CWD, "m.txt"),
                Utils.readContentsAsString(WUG_SOURCE));
        Main.main(commands("add", "m.txt"));
    }

    /**
     * Essentially test37-reset1.in but
     * continuing after the reset to the
     * TWO FILES commit
     */
    @Test
    public void test37Reset1Pt2() {
        Main.main(commands("status"));
        Main.main(commands("log"));
        System.out.println("------END OF 'MASTER' LOG--------");
        Main.main(commands("checkout", "other"));
        Main.main(commands("log"));
        System.out.println("------END OF 'OTHER' LOG--------");
    }
}


