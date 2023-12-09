package dev.monogon.cue.lang.editor.formatter;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.CapturingProcessAdapter;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.formatting.service.AsyncDocumentFormattingService;
import com.intellij.formatting.service.AsyncFormattingRequest;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.psi.PsiFile;
import dev.monogon.cue.Messages;
import dev.monogon.cue.Notifications;
import dev.monogon.cue.cli.CueCommandFactory;
import dev.monogon.cue.lang.CueLanguage;
import dev.monogon.cue.lang.psi.CueFile;
import dev.monogon.cue.settings.CueSettingsState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Set;

public final class CueExternalFormatter extends AsyncDocumentFormattingService {
    private static final Set<Feature> FEATURES = EnumSet.noneOf(Feature.class);

    @Nullable
    @Override
    protected FormattingTask createFormattingTask(@NotNull AsyncFormattingRequest request) {
        File ioFile = request.getIOFile();
        if (ioFile == null) return null;

        var args = new ArrayList<String>();
        if (CueSettingsState.getInstance().getFormatIgnoreErrors()) {
            args.add("-i");
        }
        if (CueSettingsState.getInstance().getFormatSimplifyOutput()) {
            args.add("-s");
        }
        args.add("-");

        GeneralCommandLine command;
        try {
            command = CueCommandFactory.getInstance().createCueFormatCommand(args);
        } catch (ExecutionException e) {
            Notifications.error(request.getContext().getProject(), e.getMessage());
            return null;
        }

        command.withInput(ioFile);

        try {
            OSProcessHandler handler = new OSProcessHandler(command);
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
        return Notifications.NOTIFICATION_GROUP_ID;
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
