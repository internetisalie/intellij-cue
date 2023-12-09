package dev.monogon.cue.cli;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.PtyCommandLine;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.process.CapturingProcessHandler;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.execution.runners.ExecutionEnvironmentBuilder;
import com.intellij.ide.actions.runAnything.commands.RunAnythingCommandCustomizer;
import com.intellij.ide.actions.runAnything.execution.RunAnythingRunProfile;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.registry.Registry;
import dev.monogon.cue.Notifications;

public class DefaultCueCommandService implements CueCommandService {

    @Override
    public String executeBackground(GeneralCommandLine cmd, int timeoutMillis) throws ExecutionException {
        CapturingProcessHandler processHandler = new CapturingProcessHandler(cmd);

        ProcessOutput output;
        var indicator = ProgressManager.getGlobalProgressIndicator();
        if (indicator != null) {
            output = processHandler.runProcessWithProgressIndicator(indicator, timeoutMillis, true);
        }
        else {
            output = processHandler.runProcess(timeoutMillis, true);
        }

        if (output.isTimeout()) {
            throw new ExecutionException("CUE command timed out");
        }
        else if (!output.isExitCodeSet()) {
            throw new ExecutionException("CUE command failed to start");
        }
        else if (output.getExitCode() != 0) {
            throw new ExecutionException(String.format(
                "CUE command failed with code %d",
                output.getExitCode()
            ));
        }

        return output.getStdout();
    }

    public void executeForeground(DataContext dataContext, String title, GeneralCommandLine cmd) throws ExecutionException {
        final Executor executor = DefaultRunExecutor.getRunExecutorInstance();

        final Project project = dataContext.getData(CommonDataKeys.PROJECT);
        if (project == null) {
            throw new ExecutionException("Failed to retrieve project");
        }

        var workDirectory = project.getBasePath();
        if (workDirectory == null) {
            throw new ExecutionException("Failed to retrieve workspace");
        }
        cmd.withWorkDirectory(workDirectory);

        dataContext = RunAnythingCommandCustomizer.customizeContext(dataContext);

        try {
            RunAnythingRunProfile runAnythingRunProfile = new RunAnythingRunProfile(
                Registry.is("run.anything.use.pty", false) ? new PtyCommandLine(cmd) : cmd,
                title
            );
            ExecutionEnvironmentBuilder.create(project, executor, runAnythingRunProfile)
                .dataContext(dataContext)
                .buildAndExecute();
        }
        catch (ExecutionException e) {
            Notifications.error(project, e.getMessage());
        }
    }
}
