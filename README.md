# CUE Language Support

**CUE Language** support for the IntelliJ platform, https://plugins.jetbrains.com/plugin/16126-cue.

## Usage

The CUE plugin is compatible with **any JetBrains IDE 2020.3**. Only IntelliJ-based IDEs are supported, i.e. it's incompatible with
ReSharper.

### Features

**Please note that this plugin is in an early state.**

- Complete support for the current language specification
- Syntax highlighting with settings
- Code folding
- Brace matching
- Formatting with `cue fmt`

#### Formatter

Formatting of CUE files with `cue fmt` is supported.

Please make sure that `cue` is available in `$PATH`. If the executable can't be found, then no content is modified.

Only complete files can be supported. If content is selected before invoking the format action, then no content is modified.

#### Settings
The application settings allow to configure the path to the `cue` binary. You can download it at [github.com/cuelang](https://github.com/cuelang/cue/releases).

### Bug Reports & Feature Requests

Please report your issues at [github.com/nexantic/intellij-cue](https://github.com/nexantic/intellij-cue).

## Development

- Java JDK 11 is required

### IDE

Development is best in IntelliJ IDEA.

The following plugins are required for development:

- [GrammarKit 2020.3.1](https://plugins.jetbrains.com/plugin/6606-grammar-kit)
- Gradle
- Kotlin, for Gradle build file support

### Building

After a build, the plugin is available as a ZIP file at `build/distributions/`.

Building with tests:

```bash
./gradlew clean build
```

Building without tests:

```bash
./gradlew clean build -x test
```

### Executing

You could build the plugin (see above) and install it into your IDE of choice. Alternatively, you can run the plugin in a sandbox:

```bash
./gradlew runIde
```

### Lexer

The lexer is generated by JFlex. The definition is at `src/grammar/cue.flex`.

The following command regenerates the lexer:

```bash
./gradlew generateLexer
```

### Parser

The parser is generated with JetBrains' GrammarKit. GrammarKit is a plugin for IntelliJ IDEA. The definition is at `src/grammar/cue.bnf`.

To update the parser and all related classes, open the `cue.bnf` file in your IDE and choose `Generate Parser` in the context menu of the
editor.

## Useful Link

- [CUE on the JetBrains Marketplace](https://plugins.jetbrains.com/plugin/16126-cue)
- [CUE Language Website](https://cuelang.org/)
- [The CUE Language Specification](https://cuelang.org/docs/references/spec/)

## Copyright

&copy; 2021 Nexantic GmbH / Monogon