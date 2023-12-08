package dev.monogon.cue.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import dev.monogon.cue.Messages;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

final class CueSettingsConfigurable implements Configurable {
    private CueSettingsComponent mySettingsComponent;

    // A default constructor with no arguments is required because this implementation
    // is registered in an applicationConfigurable EP
    public CueSettingsConfigurable() {
    }

    @Override
    public String getDisplayName() {
        return Messages.get("applicationSettings.displayName");
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return mySettingsComponent.getPreferredFocusedComponent();
    }

    @Override
    public @Nullable JComponent createComponent() {
        mySettingsComponent = new CueSettingsComponent();
        return mySettingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        return !CueSettings.equals(
            CueSettingsState.getInstance(),
            mySettingsComponent
        );
    }

    @Override
    public void apply() throws ConfigurationException {
        CueSettings.copy(
            CueSettingsState.getInstance(),
            mySettingsComponent
        );
    }

    @Override
    public void reset() {
        CueSettings.copy(
            mySettingsComponent,
            CueSettingsState.getInstance()
        );
    }

    @Override
    public void disposeUIResources() {
        mySettingsComponent = null;
    }
}
