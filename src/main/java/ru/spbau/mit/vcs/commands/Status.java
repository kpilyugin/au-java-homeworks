package ru.spbau.mit.vcs.commands;

import com.beust.jcommander.Parameters;
import ru.spbau.mit.vcs.VCS;
import ru.spbau.mit.vcs.VCSException;
import ru.spbau.mit.vcs.repository.Repository;
import ru.spbau.mit.vcs.repository.Snapshot;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Parameters(commandDescription = "Print status of changed/added/deleted files")
public class Status implements Command {

    @Override
    public void execute(VCS vcs) throws VCSException, IOException {
        Repository repo = vcs.getRepository();
        Snapshot previous = repo.getSnapshot(vcs.getCurrentRevision().getNumber());
        Snapshot current = repo.getCurrentSnapshot();
        Set<String> trackedFiles = repo.getTrackedFiles();

        List<String> added = trackedFiles.stream()
                .filter(file -> current.contains(file) && !previous.contains(file))
                .collect(Collectors.toList());
        printResult("Added files: ", added);

        List<String> deleted = previous.keySet().stream()
                .filter(file -> !current.contains(file))
                .collect(Collectors.toList());
        printResult("Deleted files: ", deleted);

        List<String> modified = previous.keySet().stream()
                .filter(file -> current.contains(file) && !current.get(file).equals(previous.get(file)))
                .collect(Collectors.toList());
        printResult("Modified files: ", modified);

        List<String> untracked = current.keySet().stream()
                .filter(file -> !trackedFiles.contains(file))
                .collect(Collectors.toList());
        printResult("Untracked files: ", untracked);
    }

    private void printResult(String header, List<String> result) {
        if (!result.isEmpty()) {
            System.out.println(header);
            result.forEach(System.out::println);
        }
    }
}
