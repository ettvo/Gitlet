# Gitlet Design Document

**Name**: Evelyn Vo

## Classes and Data Structures

### Class Main.java
description

### Fields
1. (File) CWD: the current working directory.
2. (File) GITLET_FOLDER: the main metadata folder. Contained within CWD.
3. (File) LOGS_FOLDER: contains the logs in files corresponding to the branch name (in file name). Contained within GITLET_FOLDER.
4. (File) OBJECTS_FOLDER: contains the blobs / file contents. Is organized into subdirectories of the first two letters of each file's SHA1 contents. Files within are named with the characters after the first two. Contained within GITLET_FOLDER.
5. (File) REFS_FOLDER: contains the references / pointers of the HEADs of each branch, remote or local. Contained within GITLET_FOLDER.
6. (File) HEADS_FOLDER: contains the references / pointers of the local branches such as "master". Contained within REFS_FOLDER.
7. (File) HEADS_POINTER: the HEAD pointer within REFS_FOLDER. Located outside of the HEADS_FOLDER as it may point to remote commits.
8. (File) MASTER_POINTER: the MASTER pointer for the MASTER branch within HEADS_FOLDER.
9. (File) STAGING_FOLDER: contains the files that are staged. Contained within GITLET_FOLDER.
10. (File) ADD_STAGING_FOLDER: contains the files that are staged for addition. Contained within STAGING_FOLDER.
11. (File) RM_STAGING_FOLDER: contains the files that are staged for removal. Contained within STAGING_FOLDER.
12. (char[]) COMBOS: a set of characters used in the first two characters of any SHA1. For convenience when creating the OBJECTS_FOLDER subdirectories.


### Functions

#### Main Functionality
1. (void) main(String args[]): takes in args[] runs the program according to the given commands.
2. (void) initialize(String args[]):
3. (void) add(String args[]):
4. (void) commit(String args[]):
5. (void) checkout(String args[]):
6. (void) log(String args[]):
7. (void) globalLog(String args[]):
8. (void) status(String args[]):
9. (void) find(String args[]):
10. (void) branch(String args[]):
11. (void) reset(String[] args)
12. (void) rm(String args[]):
13. (void) rmBranch(String args[]):
14. (void) merge(String args[]):


#### Supporting Utilities in Main.java
Note: **check** functions return a boolean while **validate** functions throw exceptions.

1. (File) accessObjectContents(String sha1):
> Returns the commit file (contents) corresponding to the given SHA1 id.
3. (File) addStage(String fName):
> Returns Utils.join(ADD_STAGING_FOLDER, fName).
2. (void) basicVal(String[] args, int exp):
> Runs the basic validation of checking if the directory is initialized and if the number of arguments is correct.
4. (boolean) canDelete(String f):
> Specific to testing. Returns if the file F is not one of the files that can't be deleted (makes the IDE break).
5. (boolean) checkInitialize(): 
> Returns TRUE if a Gitlet repository has been initialized in the current working directory. Does not check for subdirectories and files as the repository should be created iff it is initialized.
12. (boolean) checkNumArgs(String[] args, int n):
13. (void) checkBranchAndCommit(HashMap<String, String> currBFiles,
    HashMap<String, String> mergeBFiles, HashMap<String, String> splitFiles,
    String currComCurr, String currComMerge, String splitSHA,
    String mergeBranch, Date commitDate):
> Essentially does most of the work of "gitlet merge". Created because the method got too long.
14. (void) checkUntrackError():
> Checks the untracked file error.
15. void checkUntrackError(String SHA):
> Checks the untracked file error for the given commit.
16. (void) clearDirectory(File dir):
> Clears the given directory but does not delete the given directory.
4. (String) copyCommitFiles(HashMap<String, String> files):
> Creates copies of the files in FILES in the corresponding OBJECTS folder. Returns the contents of the commit (the committed files and their names).
18. void copyCommittedFiles(HashMap<String, String> files):
> Copies files with the given SHA in FILES from the OBJECTS folder to the CWD.
9. (void) createMetadata(String parent1, String parent2, String sha1, String commitContents):
> Creates the metadata file of the commit with the given SHA1. The metadata file contains the pointers of the parents.
8. void deleteFile(File file):
> Deletes the given file in the CWD. Does not delete directories.
9. (void) exitWithError(String error)
10. (String) formatLog(String parent1, String parent2, String sha1, Date date, String message):
> Formats and returns a log message.
18. (HashMap<String, String>) getAllFileSHAFromCommit(File commit, String fileName):
> Returns a HashMap<File Name, File SHA1> containing the names and SHA1 ids of all files from the given commit.
19. (String) getBranchDir(String branch):
> Returns the directory of the pointer of the given branch.
20. (String) getCommitBranch(String sha):
> Returns the branch of the given commit.
20. (String) getCurrentBranch()
21. (String) getCurrentCommit(String branchName):
> Returns the SHA1 of the most recent commit.
15. (String) getCurrentCommit():
> Returns the SHA1 of the most recent commit tracked by HEAD.
17. (File) getCommitVersion(String commitSHA, String fileName):
> Returns the version of FILENAME in the commit with the ID commitSHA.
19. List<String> getFiles(File dir):
> Calls Utils.plainFilesnamesIn(dir). Returns the non-directory files in DIR as a list.
19. (String) getFileSHAFromCommit(File commit, String fileName):
> Returns the SHA1 id of the given FILENAME from COMMIT (metadata).
5. (HashMap<String, String>) getAllFileSHAFromCommit(File commitMeta):
> Returns a HashMap<File Name, File SHA1> containing the names and SHA1 ids of all files from the given commit when given the commit's metadata.
7. (HashMap<String, String>) getAllFileSHAFromCommit(String comSHA):
> Returns getAllFileSHAFromCommit(accessObjectContents(commitMeta)).
8. ArrayList<String> getAllFileNmFromCommit(File com):
> Returns an ArrayList<String> containing the names of all files from the given commit.
7. (HashMap<String, String>) getAllFileSHAFromCWD():
> Returns a HashMap<File Name, File SHA1> containing the names and SHA1 ids of all files from the CWD.
6. (HashMap<String, String>) getAllFileSHAFromDir(File dir):
> Returns a HashMap<File Name, File SHA1> containing the names and SHA1 ids of all files from the given directory.
7. (String) getConflictMsg(String currBr, String merBr):
> Returns the contents of the file after a merge conflict.
8. (String) getSHA(File file):
> Calls Utils.sha1(readString(file)). Returns the SHA1 of the given file.
9. (String) getSplitPoint(String b1, String b2):
> 
10. (File) getSplitPtFile(String b1, String b2):
> Returns the commit of the split point for branches B1, B2.
11. (String[]) getPrevComSha(String commit):
> Returns the SHA of the (previous) parent commits of the given commit.
10. HashMap<String, Integer> getAllCommitParents(String sha):
>  Returns a HashMap<String, Integer> containing all the commit parents of a given commit. The Integer is the number of commits from the given commit.
11. String readCommit(String sha):
> Returns the result of readString(accessObjectContents(sha)).
12. (String) readString(File file):
> Calls Utils.readContentsAsString(file). Returns the contents of the file as a string.
3. (File) rmStage(String fName):
> Returns Utils.join(RM_STAGING_FOLDER, fName).
8. (boolean) sameSHA(File f1, File f2):
> Returns true if F1 and F2 have the same SHA1.
9. (String) segmentMessage(String segment, List<String> files):
> Used in "git status". Given a SEGMENT of the form "=== SegmentName ===", sorts files into alphabetical order and returns a String of the form:
> (segment)
> (files)
> \n
9. (boolean) verTrackedByCurrentCommit(String fileName):
> Returns whether the current version of the file is being tracked by the current commit.
10. (boolean) nameTrackedByCurrentCommit(String fileName):
> Returns whether the file (name) is being tracked by the current commit.
11. (boolean) trackedByCurrentCommit(String fileName, String fileSHA):
> Returns whether the given version of the file is being tracked by the current commit.
10. boolean trackedByGivenCommit(String comSHA, String name, String fSHA):
11. 
12. (boolean) trackedByCommit(String commitSHA, String fileName, String fileSHA):
> Returns whether the given version of the file is being tracked by the given commit.
13. (void) updateHead(String branchName, String branchDir, String pointer):
> Updates the HEAD pointer to point to the given POINTER.
11. (void) updateHead(String branchName, String branchDir):
> Updates the HEAD pointer to point to same commit as the given branch.
6. (void) updateLog(String parent1, String parent2, String sha1, String message, Date date, File saveLoc)
> Updates the commit log at SAVELOC.
7. (void) updateLog(String message, File saveLoc):
> Updates the commit log at SAVELOC.
8. (void) updatePointer(String branchDir, String pointer):
> Updates a pointer file (i.e. MASTER) to point to the given POINTER.
9. (void) updateMergeMetadata(String currComCurr, String currComMerge,
   String splitSHA, String mergeContents, String currBranch,
   String mergeBranch, Date commitDate):
> Handles the updating of pointers and logs for "gitlet merge".
10. (void) writeFile(File file, String input):
> Runs Utils.writeContents(file, input).
11. (void) validateInitialize():
> Errors and exits program with code 0 if a Gitlet repository has not been initialized in the current working directory.
5. (void) validateNumArgs(String func, String[] args, int n): 
> Checks the number of arguments against the expected number. Errors and exits program with code 0 if they don't match.



### Algorithms / Procedures for Functions
1. (void) main(String args[]):
> a. Checks if the number of arguments is nonzero.
> 
> b. Using a switch statement, calls the corresponding function for a command. If none correspond to the first keyword, throws an error.
2. (void) initialize(String args[]):
> a. Checks if a repository is already initialized to prevent overwriting.
> 
> b. Creates all the fields.
3. (void) add(String args[]):
> a. Checks if the given file path exists. Throws an error if not.
> 
> b. Checks if the file is unchanged from the previous commit. If so, it is removed from STAGING_FOLDER.
> 
> c. Checks if a copy of the given file exists in STAGING_FOLDER. If a copy exists, checks if the SHA1 of the copy and the file are equal. If the SHA1s are equal, the files are equal, and no change occurs. If they are different, the file overwrites the copy in STAGING_FOLDER.
> 
> d. If the SHA1 of the copy and the file differ, or if there does not exist a copy of the file in STAGING_FOLDER, adds the file to the STAGING_FOLDER as-is.
>
> Note: For this project, wildcards such as "*" do not need to be handled. Additionally, we do not need to consider the processing of folders.
4. (void) commit(String args[]):
> The SHA1 of each commit is calculated with Utils.sha1 (every file in the commit and directory).
> Each commit is identified by its SHA1 id, which must include the file (blob) references of its files, parent reference, log message, and commit time.
> After each commit, update CURRENT_COMMIT to match the most recent (parent) commit.
> 
> The file of the commit will have its name be its SHA1 (minus the first two characters). Each line in the resulting file will have [file's name] [file's SHA1].
>
> Every file in the OBJECTS_FOLDER that is not a commit is a "blob". Its name will be the SHA1 ID (minus the first two characters) and its contents will entirely be the original's contents. The name will come from the commit file.
>
> Note: "master" always points to the most recent commit while "HEAD" may point to something else
>
> Each commit will have:
>> (first parent) (second parent)
>>
>> The name of the file is its hash after the first two characters (due to how it is stored)
>>
>> Be careful that each commit points to its parent(s).
> 
> Adds a copy of the file as it currently exists to the staging area.
>
> Staging an already-staged file overwrites the previous entry in the staging area with the new contents. The staging area should be somewhere in .gitlet.
>
> If the current working version of the file is identical to the version in the current commit, do not stage it to be added, and remove it from the staging area if it is already there.
>
> The file will no longer be staged for removal (see gitlet rm), if it was at the time of the command.
6. (void) checkout(String args[]):
7. (void) log(String args[]):
> Takes the form:
>> ===
>>
>> commit a0da1ea5a15ab613bf9961fd86f010cf74c7ee48
>>
>> Date: Thu Nov 9 20:00:05 2017 -0800
>>
>> A commit message.
>
>> ===
>>
>> commit 3e8bf1d794ca2e9ef8a4007275acf3751c7170ff
>>
>> Date: Thu Nov 9 17:01:33 2017 -0800
>> 
>> Another commit message.
>>
>> ===
>>
>> commit e881c9575d180a215d1a636545b8fd9abfb1d2bb
>>
>> Date: Wed Dec 31 16:00:00 1969 -0800
>>
>> initial commit
>
>>  ===
>>
>> commit 3e8bf1d794ca2e9ef8a4007275acf3751c7170ff 
>>
>> Merge: 4975af1 2c1ead1
>>
>> Date: Sat Nov 11 12:30:00 2017 -0800
>>
>> Merged development into master.
>
> Each commit requires the SHA1 id, the first 7 characters of the SHA1 id of its parents (if merged), the date of commit, and the commit message.
> 
> Uses the current time zone (PST) with java.util.Date.
> Formatted using java.util.Formatter.
> Each log entry will take the form:
>> (parent commit) (current commit) (user who committed) (time / other metadata)

8. (void) globalLog(String args[]):
9. (void) status(String args[]):
10. (void) find(String args[]):
11. (void) branch(String args[]):
12. (void) rm(String args[]):
13. (void) rmBranch(String args[]):
14. (void) merge(String args[]):


### Class Utils.java
description

### Fields
1. (int) UID_LENGTH: The length of a complete SHA-1 UID as a hexadecimal numeral
2. (FilenameFilter) PLAIN_FILES: Filters out all but plain files.

### Supporting Utilities in Main.java
1. (String) sha1((Object... vals)): Returns the SHA-1 hash of the concatenation of VALS, which may be any mixture of byte arrays and Strings.
2. (String) sha1(List<Object> vals): Returns the SHA-1 hash of the concatenation of the strings in VALS.
3. (boolean) restrictedDelete(File file): Deletes FILE if it exists and is not a directory.  Returns true if FILE was deleted, and false otherwise.  Refuses to delete FILE and throws IllegalArgumentException unless the directory designated by FILE also contains a directory named .gitlet.
4. (boolean) restrictedDelete(String file): Runs restrictedDelete(File file) using the file path of a file.
5. (byte[]) readContents(File file): Return the entire contents of FILE as a byte array. FILE must be a normal file.  
6. (String) readContentsAsString(File file): Runs readContents(File file) and returns the concatenated byte[] as a String. FILE must be a normal file.
7. (void) writeContents(File file, Object... contents): Write the result of concatenating the bytes in CONTENTS to FILE, creating or overwriting it as needed.  Each object in CONTENTS may be either a String or a byte array.
8. (<T extends Serializable> T) readObject(File file, Class<T> expectedClass): Return an object of type T read from FILE, casting it to EXPECTEDCLASS.
9. (void) writeObject(File file, Serializable obj): Runs writeContents(File file, Object... contents). Writes OBJ to FILE.
10. List<String> plainFilenamesIn(File dir): Returns a list of the names of all plain files in the directory DIR, in lexicographic order as Java Strings.  Returns null if DIR does not denote a directory.
11. (List<String>) plainFilenamesIn(String dir): Runs plainFilenamesIn(File dir) given a file directory.
12. (File) join(String first, String... others): Return the concatenation of FIRST and OTHERS into a File designator.
13. (File) join(File first, String... others): Runs join(String first, String... others) on a given File and String[] others.
14. (byte[]) serialize(Serializable obj): Returns a byte array containing the serialized contents of OBJ.
15. (GitletException) error(String msg, Object... args): Return a GitletException whose message is composed from MSG and ARGS as for the String.format method.
16. (void) message(String msg, Object... args): Print a message composed from MSG and ARGS as for the String.format method, followed by a newline.


## Algorithms
1. explain each thing

## How to do Commits and Logs
every commit points to parent --> should have SHA1 for both first
and second parent

## Persistence

1) Needs to be able to store old copies of files and other metadata in a directory called ".gitlet". 


information on whether you need to save info 
and how

## Bug Triggering Conditions
> A Gitlet system is considered "initialized" in a particular location if it has a .gitlet directory there.
Most Gitlet commands (except for the init command) only need to work when used from a directory where a Gitlet system has been initialized.
The files that aren't in your .gitlet directory (which are copies of files from the repository that you are using and editing, as well as files you plan to add to the repository) are referred to as the files in your working directory.

> need to follow runtime or memory usage requirements
(Some of the runtimes are described as constant "relative to any significant measure". The significant measures are: any measure of number or size of files, any measure of number of commits. 
You can ignore time required to serialize or deserialize, with the one caveat that your serialization time cannot depend in any way on the total size of files that have been added, committed, etc (what is serialization? You'll see later in the spec). You can also pretend that getting from a hash table is constant time.

> Some errors have specific messages for the situation.
If your program ever encounters one of these failure cases, it must print the error message and not change anything else. You don't need to handle any other error cases except the ones listed as failure cases.

Gitlet Specific Bugs:

Note: probably (numOperands) bug comes before (initialized) bug

> If a user doesn't input any arguments, print the message Please enter a command. and exit.
(check)

> If a user inputs a command that doesn't exist, print the message No command with that name exists. and exit.
(check)

> If a user inputs a command with the wrong number or format of operands, print the message Incorrect operands. and exit.


> If a user inputs a command that requires being in an initialized Gitlet working directory (i.e., one containing a .gitlet subdirectory), but is not in such a directory, print the message Not in an initialized Gitlet directory.
(check)

> Do NOT print out anything except for what the spec says. Some of our autograder tests will break if you print anything more than necessary.

> All error messages must end with a period.

> Always exit with exit code 0, even in the presence of errors.

> Be careful not to overwrite current versions when restoring previous versions of files

## Expected Runtime of Commands

> init --> Should be constant relative to any significant measure.

> add --> In the worst case, should run in linear time relative to the size of the file being added and lgN, for N the number of files in the commit. 

> commit --> 
Runtime should be constant with respect to any measure of number of commits. Runtime must be no worse than linear with respect to the total size of files the commit is tracking.
Memory requirement: Committing must increase the size of the .gitlet directory by no more than the total size of the files staged for addition at the time of commit, not including additional metadata. This means don't store redundant copies of versions of files that a commit receives from its parent. You are allowed to save whole additional copies of files; don't worry about only saving diffs, or anything like that.

> rm --> Should run in constant time relative to any significant measure.

> log --> Should be linear with respect to the number of nodes in head's history.

> global-log --> Linear with respect to the number of commits ever made.

> find --> Should be linear relative to the number of commits.

> status --> Make sure this depends only on the amount of data in the working directory plus the number of files staged to be added or deleted plus the number of branches.

> checkout -->
Should be linear relative to the size of the file being checked out.
Should be linear with respect to the total size of the files in the commit's snapshot. Should be constant with respect to any measure involving number of commits. Should be constant with respect to the number of branches.

> branch --> Should be constant relative to any significant measure.

> rm-branch --> Should be constant relative to any significant measure.

> reset --> Should be linear with respect to the total size of files tracked by the given commit's snapshot. Should be constant with respect to any measure involving number of commits.

> merge --> O(N lg(N) + D), where N is the total number of ancestor commits for the two branches and D is the total amount of data in all the files under these commits.

## Things to Avoid

> Since you are likely to keep various information in files (such as commits), you might be tempted to use apparently convenient file-system operations (such as listing a directory) to sequence through all of them. Be careful. Methods such as File.list and File.listFiles produce file names in an undefined order. If you use them to implement the log command, in particular, you can get random results.
    
> Windows users especially should beware that the file separator character is / on Unix (or MacOS) and '\' on Windows. So if you form file names in your program by concatenating some directory names and a file name together with explicit /s or \s, you can be sure that it won't work on one system or the other. Java provides a system-dependent file separator character File.separator, as in ".gitlet" + File.separator + "something", or the multi-argument constructors to File, as in \ new File(".gitlet", "something"), which you can use in place of ".gitlet/something").
    
> Be careful using a HashMap when serializing! The order of things within the HashMap is non-deterministic. The solution is to use a TreeMap which will always have the same order. More details here


## Checkpoint

You pass the sample tests from the skeleton: testing/samples/*.in. These require you to implement:
1) init (still need to create initial commit)
2) add 
3) commit 
4) checkout -- [file name]
5) checkout [commit id] -- [file name]
6) log.

