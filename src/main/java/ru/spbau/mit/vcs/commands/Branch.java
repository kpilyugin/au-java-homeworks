package ru.spbau.mit.vcs.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import ru.spbau.mit.vcs.VCS;
import ru.spbau.mit.vcs.VCSException;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Parameters(commandDescription = "Print current branch, create or delete branch")
public class Branch implements Command {
    @Parameter(description = "Branch name")
    private List<String> branchName;

    @Parameter(names = "-d", description = "Delete branch")
    private boolean delete;

    @Parameter(names = "-a", description = "Print all branches")
    private boolean all;

    @Override
    public void execute(VCS vcs) throws VCSException, IOException {
        if (branchName == null) {
            printBranch(vcs);
            return;
        }
        String name = branchName.stream().collect(Collectors.joining(" "));
        if (delete) {
            vcs.deleteBranch(name);
        } else {
            vcs.createBranch(name);
            vcs.checkout(name);
        }
    }

    private void printBranch(VCS context) {
        String current = context.getCurrentBranch().getName();
        if (all) {
            System.out.println("All branches: ");
            context.getBranches().forEach(branch -> {
                String name = branch.getName();
                System.out.println(name + (name.equals(current) ? " (*) " : ""));
            });
        } else {
            System.out.println("Current branch: " + current);
        }
    }

}
