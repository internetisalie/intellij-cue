package dev.monogon.cue.lang.editor.formatter;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.PathEnvironmentVariableUtil;
import com.intellij.execution.process.CapturingProcessAdapter;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.formatting.service.AsyncDocumentFormattingService;
import com.intellij.formatting.service.AsyncFormattingRequest;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.psi.PsiFile;
import dev.monogon.cue.Messages;
import dev.monogon.cue.lang.CueLanguage;
import dev.monogon.cue.lang.psi.CueFile;
import dev.monogon.cue.settings.CueLocalSettingsService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.Set;

public final class CueExternalFormatter extends AsyncDocumentFormattingService {
    private static final Set<Feature> FEATURES = EnumSet.noneOf(Feature.class);

    private String findCueBin() throws ExecutionException {
        // Check the configured binary
        String cuePath = CueLocalSettingsService.getSettings().getCueExecutablePath();
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

    @Nullable
    @Override
    protected FormattingTask createFormattingTask(@NotNull AsyncFormattingRequest request) {
        File ioFile = request.getIOFile();
        if (ioFile == null) return null;

        String cuePath;
        try {
            cuePath = findCueBin();
        } catch (ExecutionException e) {
            CueLanguage.NOTIFICATION_GROUP
                .createNotification(e.getMessage(), NotificationType.ERROR)
                .notify(request.getContext().getProject());
            return null;
        }

        try {
            GeneralCommandLine commandLine = new GeneralCommandLine(cuePath, "fmt", "-");
            commandLine.withParentEnvironmentType(GeneralCommandLine.ParentEnvironmentType.CONSOLE);
            commandLine.withCharset(StandardCharsets.UTF_8);
            commandLine.withInput(ioFile);

            OSProcessHandler handler = new OSProcessHandler(commandLine.withCharset(StandardCharsets.UTF_8));
            return new FormattingTask() {
                @Override
                public void run() {
                    handler.addProcessListener(new CapturingProcessAdapter() {
                        @Override
                        public void processTerminated(@NotNull ProcessEvent event) {
                            int exitCode = event.getExitCode();
                            if (exitCode == 0) {
                                request.onTextReady(getOutput().getStdout());
                            }
                            else {
                                request.onError(Messages.get("lang.displayName"), getOutput().getStderr());
                            }
                        }
                    });
                    handler.startNotify();
                }

                @Override
                public boolean cancel() {
                    handler.destroyProcess();
                    return true;
                }

                @Override
                public boolean isRunUnderProgress() {
                    return true;
                }
            };
        }
        catch (ExecutionException e) {
            request.onError(Messages.get("lang.displayName"), e.getMessage());
            return null;
        }
    }

    @Override
    protected @NotNull String getNotificationGroupId() {
        return CueLanguage.NOTIFICATION_GROUP_ID;
    }

    @Override
    protected @NotNull @NlsSafe String getName() {
        return Messages.get("lang.displayName");
    }

    @Override
    public @NotNull Set<Feature> getFeatures() {
        return FEATURES;
    }

    @Override
    public boolean canFormat(@NotNull PsiFile file) {
        return file instanceof CueFile;
    }
}
