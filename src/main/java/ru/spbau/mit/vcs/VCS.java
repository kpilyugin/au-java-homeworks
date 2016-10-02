package ru.spbau.mit.vcs;

import ru.spbau.mit.vcs.revision.Branch;
import ru.spbau.mit.vcs.repository.Repository;
import ru.spbau.mit.vcs.revision.Revision;

import java.io.IOException;
import java.util.*;

public class VCS {
    public static final String FOLDER = ".vcs";
    public static final String DEFAULT_BRANCH = "master";

    private Branch currentBranch;
    private int currentRevision = 0;
    private int nextRevisionNumber;
    private final Set<Branch> branches;
    private final Map<Integer, Revision> revisions;
    private final Repository repository;

    public VCS() {
        this(System.getProperty("user.dir"));
    }

    public VCS(String workingDir) {
        this(new Branch(DEFAULT_BRANCH, 0), 0, new HashSet<>(), new HashMap<>(), new Repository(workingDir), 0);
        branches.add(currentBranch);
    }

    public VCS(Branch currentBranch, int currentRevision, Set<Branch> branches,
               Map<Integer, Revision> revisions, Repository repository, int maxRevision) {
        this.currentBranch = currentBranch;
        this.currentRevision = currentRevision;
        this.branches = branches;
        this.revisions = revisions;
        this.repository = repository;
        this.nextRevisionNumber = maxRevision;
    }

    public Branch getCurrentBranch() {
        return currentBranch;
    }

    public Set<Branch> getBranches() {
        return branches;
    }

    public void createBranch(String name) throws VCSException {
        Branch branch = new Branch(name, currentRevision);
        boolean added = branches.add(branch);
        if (added) {
            currentBranch = branch;
        } else {
            throw new VCSException("Branch with this name already exists");
        }
    }

    public void deleteBranch(String name) throws VCSException {
        Optional<Branch> branch = getBranch(name);
        if (branch.isPresent()) {
            branches.remove(branch.get());
        } else {
            throw new VCSException("Branch not found");
        }
    }

    public void checkout(String name) throws VCSException, IOException {
        Optional<Branch> branch = getBranch(name);
        if (branch.isPresent()) {
            currentBranch = branch.get();
            currentRevision = branch.get().getRevision();
            System.out.println("Checked out branch " + currentBranch.getName());
        } else {
            try {
                int revision = Integer.parseInt(name);
                currentRevision = revisions.get(revision).getNumber();
                System.out.println("Checked out revision " + currentRevision);
                currentBranch = null;
            } catch (Exception e) {
                throw new VCSException("Checkout failed: branch and revision not found");
            }
        }
        repository.checkoutRevision(currentRevision);
    }

    public void commit(String message) throws VCSException, IOException {
        if (currentBranch == null) {
            throw new VCSException("No tracked branch for commit");
        }
        int number = addRevision(message);
        System.out.println("Committed revision " + number + " to branch " + currentBranch.getName());
        repository.writeRevision(number);
    }

    private int addRevision(String message) throws IOException {
        int number = nextRevisionNumber;
        Revision revision = new Revision(number, message, currentRevision);
        revisions.put(number, revision);
        currentRevision = number;
        currentBranch.setRevision(number);
        nextRevisionNumber++;
        return number;
    }

    public Revision getCurrentRevision() {
        return revisions.get(currentRevision);
    }

    public Revision getRevision(int number) {
        return revisions.get(number);
    }

    public Repository getRepository() {
        return repository;
    }

    /**
     * This method finds base revision, containing in both merging-from and merging-to revisions,
     * to apply three-way merge algorithm.
     */
    public void merge(String branch, String message) throws VCSException, IOException {
        if (currentBranch == null) {
            throw new VCSException("No tracked branch for merge");
        }
        Optional<Branch> merged = getBranch(branch);
        if (!merged.isPresent()) {
            throw new VCSException("Unable to find branch");
        }
        int numFrom = merged.get().getRevision();
        Revision revisionFrom = getRevision(numFrom);
        Revision revisionTo = getCurrentRevision();
        while (revisionFrom.getNumber() != revisionTo.getNumber()) {
            if (revisionFrom.getNumber() > revisionTo.getNumber()) {
                revisionFrom = getRevision(revisionFrom.getPrevious());
            } else {
                revisionTo = getRevision(revisionTo.getPrevious());
            }
        }
        int baseRevision = revisionFrom.getNumber();
        if (numFrom <= baseRevision) {
            System.out.println("Nothing to merge: branch is up-to-date");
            return;
        }
        repository.merge(numFrom, currentRevision, revisionFrom.getNumber(), nextRevisionNumber);
        String mergeMessage = "Merged branch " + merged.get().getName() + " into " + currentBranch.getName();
        System.out.println(mergeMessage);
        addRevision(mergeMessage + (message != null ? ": " + message : ""));
        repository.checkoutRevision(currentRevision);
    }

    private Optional<Branch> getBranch(String name) {
        return branches.stream()
                .filter(b -> b.getName().equals(name))
                .findFirst();
    }
}
