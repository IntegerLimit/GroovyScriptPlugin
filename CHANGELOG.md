<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# GroovyScriptPlugin Changelog

## [Unreleased]

## [0.2.1] - 2025-09-21

- Remove uses of Internal APIs
- Move cursor after quotes in object mapper completion
- Remove object mapper live templates

## [0.1.6] - 2025-09-12

- Support for IntelliJ Versions 2025.2.*
- Update LSP4IJ to 0.15.0

## [0.1.5] - 2025-03-01

- Bump LSP4IJ Version to 0.11.0
- Allow Install on 2025.1.*

### Internal

- Bump gradle/actions from 3 to 4 by @dependabot in https://github.com/IntegerLimit/GroovyScriptPlugin/pull/1
- Bump JetBrains/qodana-action from 2024.2 to 2024.3 by @dependabot in https://github.com/IntegerLimit/GroovyScriptPlugin/pull/13
- Bump org.jetbrains.intellij.platform from 2.1.0 to 2.2.1 by @dependabot in https://github.com/IntegerLimit/GroovyScriptPlugin/pull/15
- Bump org.jetbrains.qodana from 2024.2.3 to 2024.3.4 by @dependabot in https://github.com/IntegerLimit/GroovyScriptPlugin/pull/17
- Bump org.gradle.toolchains.foojay-resolver-convention from 0.8.0 to 0.9.0 by @dependabot in https://github.com/IntegerLimit/GroovyScriptPlugin/pull/10
- Bump codecov/codecov-action from 4 to 5 by @dependabot in https://github.com/IntegerLimit/GroovyScriptPlugin/pull/4
- Bump org.jetbrains.kotlin.jvm from 1.9.25 to 2.1.10 by @dependabot in https://github.com/IntegerLimit/GroovyScriptPlugin/pull/19
- Bump org.jetbrains.kotlinx.kover from 0.8.3 to 0.9.1 by @dependabot in https://github.com/IntegerLimit/GroovyScriptPlugin/pull/18
- Bump com.diffplug.spotless from 7.0.0.BETA4 to 7.0.2 by @dependabot in https://github.com/IntegerLimit/GroovyScriptPlugin/pull/20

## [0.1.4] - 2024-11-29

Notice: v0.1.3 has been skipped due to issues with Github Actions.

- Allow installation in editor versions 2024.3+
- Fixes texture resolving in specific edge cases
- Improves performance of textural inlay support
  - Caches previous results of same file state
  - Further improvement planned

## [0.1.1] - 2024-11-24

### Bugs

- Fixed NPE due to null file identifiers

### Features

- Added plugin icon

## [0.1.0] - 2024-11-24

### Features

- Added basic LSP support
- Added live templates for preprocessors and object mappers
- Added texture inlays
- Added texture inlay tooltips

[Unreleased]: https://github.com/IntegerLimit/GroovyScriptPlugin/compare/v0.2.1...HEAD
[0.2.1]: https://github.com/IntegerLimit/GroovyScriptPlugin/compare/v0.1.6...v0.2.1
[0.1.6]: https://github.com/IntegerLimit/GroovyScriptPlugin/compare/v0.1.5...v0.1.6
[0.1.5]: https://github.com/IntegerLimit/GroovyScriptPlugin/compare/v0.1.4...v0.1.5
[0.1.4]: https://github.com/IntegerLimit/GroovyScriptPlugin/compare/v0.1.1...v0.1.4
[0.1.1]: https://github.com/IntegerLimit/GroovyScriptPlugin/compare/v0.1.0...v0.1.1
[0.1.0]: https://github.com/IntegerLimit/GroovyScriptPlugin/commits/v0.1.0
