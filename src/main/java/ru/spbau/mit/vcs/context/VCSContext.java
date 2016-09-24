package ru.spbau.mit.vcs.context;

import ru.spbau.mit.vcs.exception.VCSException;
import ru.spbau.mit.vcs.data.Content;
import ru.spbau.mit.vcs.revision.Branch;
import ru.spbau.mit.vcs.revision.Revision;

import java.util.*;

public class VCSContext {
    private static final String DEFAULT_BRANCH = "master";

    private Branch currentBranch;
    private int currentRevision = 0;
    private int maxRevision;
    private final Set<Branch> branches;
    private final Map<Integer, Revision> revisions;
    private final Content trackedContent;

    public VCSContext() {
        this(new Branch(DEFAULT_BRANCH, 0), 0, new HashSet<>(), new HashMap<>(), new Content(), 1);
        branches.add(currentBranch);
    }

    public VCSContext(Branch currentBranch, int currentRevision, Set<Branch> branches,
                      Map<Integer, Revision> revisions, Content trackedContent, int maxRevision) {
        this.currentBranch = currentBranch;
        this.currentRevision = currentRevision;
        this.branches = branches;
        this.revisions = revisions;
        this.trackedContent = trackedContent;
        this.maxRevision = maxRevision;
    }

    public Branch getCurrentBranch() {
        return currentBranch;
    }

    public Set<Branch> getBranches() {
        return branches;
    }

    public void createBranch(String name) throws VCSException {
        boolean added = branches.add(new Branch(name, currentRevision));
        if (!added) {
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

    public void checkout(String name) throws VCSException {
        Optional<Branch> branch = getBranch(name);
        if (branch.isPresent()) {
            currentBranch = branch.get();
            currentRevision = branch.get().getRevision();
        } else {
            currentBranch = null;
            int revision = Integer.parseInt(name);
            if (revisions.containsKey(revision)) {
                currentRevision = revision;
            } else {
                throw new VCSException("Checkout failed: branch and revision not found");
            }
        }
        trackedContent.checkoutRevision(currentRevision);
    }

    private Optional<Branch> getBranch(String name) {
        return branches.stream()
                .filter(b -> b.getName().equals(name))
                .findFirst();
    }

    public void commit(String message) {
        int number = maxRevision;
        Revision revision = new Revision(number, message, currentRevision);
        revisions.put(number, revision);
        currentRevision = number;
        currentBranch.setRevision(number);
        trackedContent.writeRevision(number);
        maxRevision++;
    }

    public Revision getCurrentRevision() {
        return revisions.get(currentRevision);
    }

    public Revision getRevision(int number) {
        return revisions.get(number);
    }

    public void addTrackedContent(List<String> files) {
        trackedContent.addContent(files);
    }
}
