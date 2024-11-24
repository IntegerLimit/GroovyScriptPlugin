# GroovyScript

![Build](https://github.com/IntegerLimit/GroovyScriptPlugin/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/25915-groovyscript.svg)](https://plugins.jetbrains.com/plugin/25915-groovyscript)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/25915-groovyscript.svg)](https://plugins.jetbrains.com/plugin/25915-groovyscript)

<!-- Plugin description -->
This IntelliJ IDEA Plugin provides auto-completion, signature hints, inlay support and some live templates for the Minecraft 1.12 Mod [GroovyScript](https://github.com/CleanroomMC/GroovyScript/tree/master).

You must enable the GroovyScript Language Server in your instance. See the [GroovyScript Docs](https://cleanroommc.com/groovy-script/getting_started/editors#start-the-language-server) for how to do this.

This plugin utilizes the [Language Server Protocol](https://microsoft.github.io/language-server-protocol/specification) to provide this support, through the [LSP4IJ](https://github.com/RedhatDevtools/lsp4ij) plugin.

LSP4IJ is a hard dependency of this Plugin. IntelliJ's own support has not been used due to limited feature sets and support being restricted to Ultimate versions.

For support with GroovyScript versions pre-1.2.0, please disable the `GroovyScript texture` inlay hint, in the `Other -> Groovy` category. See the [IntelliJ Docs](https://www.jetbrains.com/help/idea/inlay-hints.html#enable_inlay_hints) for how to do this.

**This plugin is still in development. Future planned features are in the [roadmap](https://github.com/IntegerLimit/GroovyScriptPlugin#roadmap).**
<!-- Plugin description end -->

## Installation

- Using the IDE built-in plugin system:
  
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "GroovyScriptPlugin"</kbd> >
  <kbd>Install</kbd>
  
- Using JetBrains Marketplace:

  Go to [JetBrains Marketplace](https://plugins.jetbrains.com/plugin/25915-groovyscript) and install it by clicking the <kbd>Install to ...</kbd> button in case your IDE is running.

  You can also download the [latest release](https://plugins.jetbrains.com/plugin/25915-groovyscript/versions) from JetBrains Marketplace and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

- Manually:

  Download the [latest release](https://github.com/IntegerLimit/GroovyScriptPlugin/releases/latest) or the [latest nightly build](https://nightly.link/IntegerLimit/GroovyScriptPlugin/workflows/build/main?preview) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>


---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
[docs:plugin-description]: https://plugins.jetbrains.com/docs/intellij/plugin-user-experience.html#plugin-description-and-presentation

## Roadmap
### Textures
- [x] Texture display
- [x] Texture tooltips
- [ ] Texture settings: Improve preview

### Other
- [ ] Allow localization
- [ ] Fix completion removing quotes before
- [ ] Fix completion not working with object mappers on normal input (maybe see if IntelliJ's Typescript support can help as a base, it provides completion for object keys)
- [ ] Add shortcut snippets (crafting shapeless, etc.)
- [ ] Fix bad error highlighting
- [ ] Inspections (e.g. favoring `item('rl', meta)` over `item('rl:meta')` or formatting from `item` to `metaitem`)
  - Add to GrS's LSP?
- [ ] Look at sources, check for groovyblacklist annotation
