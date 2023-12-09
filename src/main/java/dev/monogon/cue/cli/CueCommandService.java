package dev.monogon.cue.cli;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.components.ServiceManager;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * Service to execute CueCommand instances.
 */
public interface CueCommandService {

    void executeForeground(DataContext context, String title, GeneralCommandLine cmd) throws ExecutionException;
    String executeBackground(GeneralCommandLine cmd, int timeoutMillis) throws ExecutionException;

    @NotNull
    static CueCommandService getInstance() {
        return ServiceManager.getService(CueCommandService.class);
    }

    static int shortTimeoutMillis = (int) TimeUnit.SECONDS.toMillis(5);
    static int mediumTimeoutMillis = (int) TimeUnit.SECONDS.toMillis(30);
    static int longTimeoutMillis = (int) TimeUnit.SECONDS.toMillis(600);
}
