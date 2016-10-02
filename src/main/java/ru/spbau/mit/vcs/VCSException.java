package ru.spbau.mit.vcs;

public class VCSException extends Exception {
    public VCSException(String message) {
        super(message);
    }

    public VCSException(String message, Throwable cause) {
        super(message, cause);
    }
}
