package dev.monogon.cue.cli;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.PathEnvironmentVariableUtil;
import dev.monogon.cue.Messages;
import dev.monogon.cue.settings.CueSettingsState;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DefaultCueCommandFactory implements CueCommandFactory {
    @Override
    public GeneralCommandLine createCueFormatCommand(List<String> args) throws ExecutionException {
        return createCueCommand(List.of("fmt"), args);
    }

    @Override
    public GeneralCommandLine createCueVetCommand(List<String> args) throws ExecutionException {
        return createCueCommand(List.of("vet"), args);
    }

    private GeneralCommandLine createCueCommand(List<String> firstArgs, List<String> restArgs) throws ExecutionException {
        var cuePath = findCueBin();

        var allArgs = new ArrayList<String>();
        allArgs.add(cuePath);
        allArgs.addAll(firstArgs);
        allArgs.addAll(restArgs);

        GeneralCommandLine commandLine = new GeneralCommandLine(allArgs);
        commandLine.withParentEnvironmentType(GeneralCommandLine.ParentEnvironmentType.CONSOLE);
        commandLine.withCharset(StandardCharsets.UTF_8);

        return commandLine;
    }

    private String findCueBin() throws ExecutionException {
        // Check the configured binary
        String cuePath = CueSettingsState.getInstance().getCueExecutablePath();
        if (cuePath != null && !cuePath.isEmpty()) {
            if (!Files.isExecutable(Paths.get(cuePath))) {
                throw new ExecutionException(Messages.get("formatter.userPathNotFound"));
            }
        }

        // Check PATH
        if (cuePath == null || cuePath.isEmpty()) {
            var envPath = PathEnvironmentVariableUtil.findInPath("cue");
            if (envPath != null && envPath.canExecute()) {
                cuePath = envPath.getAbsolutePath();
            }
        }

        // Check GOBIN
        if (cuePath == null || cuePath.isEmpty()) {
            var goBinPath = System.getenv("GOBIN");
            if (goBinPath != null && !goBinPath.isEmpty()) {
                var goBinCueFile = Paths.get(goBinPath, "cue").toFile();
                if (goBinCueFile.canExecute()) {
                    cuePath = goBinCueFile.getAbsolutePath();
                }
            }
        }

        // Check GOPATH/bin
        if (cuePath == null || cuePath.isEmpty()) {
            var goPath = System.getenv("GOPATH");
            if (goPath != null && !goPath.isEmpty()) {
                var goBinCueFile = Paths.get(goPath, "bin", "cue").toFile();
                if (goBinCueFile.canExecute()) {
                    cuePath = goBinCueFile.getAbsolutePath();
                }
            }
        }

        // Check HOME/go/bin
        if (cuePath == null || cuePath.isEmpty()) {
            var homePath = System.getenv("HOME");
            if (homePath != null && !homePath.isEmpty()) {
                var homeGoBinCueFile = Paths.get(homePath, "go", "bin", "cue").toFile();
                if (homeGoBinCueFile.canExecute()) {
                    cuePath = homeGoBinCueFile.getAbsolutePath();
                }
            }
        }

        if (cuePath == null || cuePath.isEmpty()) {
            throw new ExecutionException(Messages.get("formatter.exeNotFound"));
        }

        return cuePath;
    }

}
