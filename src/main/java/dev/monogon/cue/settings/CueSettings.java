package dev.monogon.cue.settings;

import java.util.Objects;

interface CueSettings {
    String getCueExecutablePath();
    void setCueExecutablePath(String cueExecutablePath);

    boolean getFormatIgnoreErrors();
    void setFormatIgnoreErrors(boolean formatIgnoreErrors);

    boolean getFormatSimplifyOutput();
    void setFormatSimplifyOutput(boolean formatSimplifyOutput);

    static void copy(CueSettings to, CueSettings from) {
        to.setFormatIgnoreErrors(from.getFormatIgnoreErrors());
        to.setFormatSimplifyOutput(from.getFormatSimplifyOutput());
        to.setCueExecutablePath(from.getCueExecutablePath());
    }

    static boolean equals(CueSettings a, CueSettings b) {
        if (a.getFormatIgnoreErrors() != b.getFormatIgnoreErrors()) {
            return false;
        }
        if (a.getFormatSimplifyOutput() != b.getFormatSimplifyOutput()) {
            return false;
        }
        return Objects.equals(a.getCueExecutablePath(), b.getCueExecutablePath());
    }
}
