package dev.monogon.cue.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.RoamingType;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@State(
    name = "dev.monogon.cue.settings.CueSettingsState",
    storages = @Storage(value = "cue-application.xml", roamingType = RoamingType.DISABLED)
)
public class CueSettingsState implements CueSettings, PersistentStateComponent<CueSettingsState> {
    @Nullable
    private volatile String cueExecutablePath;
    private volatile boolean formatIgnoreErrors;
    private volatile boolean formatSimplifyOutput;

    @Nullable
    public String getCueExecutablePath() {
        return StringUtil.nullize(cueExecutablePath);
    }

    public void setCueExecutablePath(@Nullable String path) {
        this.cueExecutablePath = path;
    }

    public void setFormatSimplifyOutput(boolean formatSimplifyOutput) {
        this.formatSimplifyOutput = formatSimplifyOutput;
    }

    public boolean getFormatSimplifyOutput() {
        return formatSimplifyOutput;
    }

    public void setFormatIgnoreErrors(boolean formatIgnoreErrors) {
        this.formatIgnoreErrors = formatIgnoreErrors;
    }

    public boolean getFormatIgnoreErrors() {
        return formatIgnoreErrors;
    }

    public static CueSettingsState getInstance() {
        return ApplicationManager.getApplication().getService(CueSettingsState.class);
    }

    @Nullable
    @Override
    public CueSettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull CueSettingsState state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
