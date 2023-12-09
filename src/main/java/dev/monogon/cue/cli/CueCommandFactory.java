package dev.monogon.cue.cli;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.components.ServiceManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface CueCommandFactory {
    GeneralCommandLine createCueFormatCommand(List<String> args) throws ExecutionException;
    GeneralCommandLine createCueVetCommand(List<String> args) throws ExecutionException;

    @NotNull
    static CueCommandFactory getInstance() {
        return ServiceManager.getService(CueCommandFactory.class);
    }
}
