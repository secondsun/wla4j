# SNES Dev Tools

This is a collection of dev tools for snes rom development.  For now I am starting with porting the wla assembler tooling to Java and will move on to making IDE integrations.  From there I will iterate with various basic custom ROM tasks.

# Progress

Currently, this is a work in progress project.  The current work is focusing on reverse engineering the wla rules for scanning, parsing, etc to generate a AST for a collection of SNES code which includes directives and assembly sources for the 65816, SPC700, and Super FX chip.  So far the project is progressing on supporting directives common to all architectures followed by supporting directives specific to each architecture.  There may be a spike afterwards to include GBC directives.

# Roadmap to 1.0

## Features implemented
  * None yet

 ## Features to be implemented
  * A library that can turn assembly inputs into syntax trees.
  * A maven plugin that can generate either wla files to be consumed by wlalink or a snes binary (TBD)
  * A IntelliJ or Netbeans plugin that can be used to as a basis for future work, but provides basic editing, building, etc support.

# Post 1.0
  * A language server implementation to allow this project to be used in editors such as Visual Studio Code.
