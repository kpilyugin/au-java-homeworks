package ru.spbau.mit.vcs.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import ru.spbau.mit.vcs.context.VCSContext;
import ru.spbau.mit.vcs.exception.VCSException;

import java.util.List;

@Parameters(commandDescription = "Print current branch, create or delete branch")
public class Branch implements VCSCommand {
    @Parameter(description = "Branch name")
    private List<String> branchName;

    @Parameter(names = "-d", description = "Delete branch")
    private boolean delete;

    @Parameter(names = "-a", description = "Print all branches")
    private boolean all;

    @Override
    public void execute(VCSContext context) throws VCSException {
        if (branchName == null) {
            printBranch(context);
            return;
        }
        String name = branchName.get(0);
        if (delete) {
            context.deleteBranch(name);
        } else {
            context.createBranch(name);
            context.checkout(name);
        }
    }

    private void printBranch(VCSContext context) {
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
