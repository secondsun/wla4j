[![SONAR GATE](https://sonarcloud.io/api/project_badges/measure?project=snes-dev-tools%3Anet.saga.snes.dev&metric=alert_status)](https://sonarcloud.io/dashboard?id=snes-dev-tools%3Anet.saga.snes.dev)


# SNES Dev Tools

This is a collection of dev tools for snes rom development.  For now I am starting with porting the wla assembler tooling to Java and will move on to making IDE integrations.  From there I will iterate with various basic custom ROM tasks.

# Getting started

## Setup

You will need at their latest versions
 * maven 
 * java


Currently I'm using ages-disasm for one of my large tests.  You will need to open the src/test/resrouces/ages-disasm folder and make that project per its README.md before you can run the build.

## Building

To build this project

```bash
mvn test-compile install 
```

Which will make a library available in your maven path.

# Progress

Currently, this is a work in progress project.  The current work is focusing on reverse engineering the wla rules for scanning, parsing, etc to generate a AST for a collection of SNES code which includes directives and assembly sources for the 65816, SPC700, and Super FX chip.  So far the project is progressing on supporting directives common to all architectures followed by supporting directives specific to each architecture.  There may be a spike afterwards to include GBC directives.

# Roadmap to 1.0

## Features implemented
  * SourceParser can parse ages-disasm.

 ## Features to be implemented
  * A library that can turn assembly inputs into syntax trees.
  * A maven plugin that can generate either wla files to be consumed by wlalink or a snes binary (TBD)

# WLA-Language-Server
  * A language server implementation to allow this project to be used in editors such as Visual Studio Code.
  * https://github.com/secondsun/wla-language-server
  
  [![VS CODE DEMO](https://img.youtube.com/vi/LOv05pIG0Fc/0.jpg)](https://www.youtube.com/watch?v=LOv05pIG0Fc)
