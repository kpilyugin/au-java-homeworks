package ru.spbau.mit.vcs.revision;

public class Revision {
    private final int number;
    private final String commitMessage;
    private final int previous;

    public Revision(int number, String commitMessage, int previous) {
        this.number = number;
        this.commitMessage = commitMessage;
        this.previous = previous;
    }

    public int getNumber() {
        return number;
    }

    public String getCommitMessage() {
        return commitMessage;
    }

    public int getPrevious() {
        return previous;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Revision)) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        Revision other = (Revision) obj;
        return number == other.number;
    }

    @Override
    public int hashCode() {
        return number;
    }
}
