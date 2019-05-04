package net.sagaoftherealms.tools.snes.assembler.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;
import net.sagaoftherealms.tools.snes.assembler.definition.opcodes.OpCode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.*;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.macro.MacroNode;
import net.sagaoftherealms.tools.snes.assembler.util.SourceScanner;

/**
 * A project contains all of the files, configurations, etc for a WLA project. What is important is
 * that Project, as opposed to {@link
 * net.sagaoftherealms.tools.snes.assembler.pass.parse.MultiFileParser}, reasons about projects and
 * has more flexibility and configuration.
 */
public class Project {

  private final Map<String, List<Node>> parsedFiles = new HashMap<>();
  private final Set<String> filesToParse = new HashSet<>();
  private final Map<String, Optional<MacroNode>> macroNames = new HashMap<>();
  private final Map<String, List<ErrorNode>> errorNodes = new HashMap<>();

  private static final java.util.logging.Logger LOG = Logger.getLogger(Project.class.getName());

  private final Retro retro;
  private final String projectRoot;
  private MultiFileParser parser;

  public Project(String projectRoot) {
    this.projectRoot = projectRoot;
    try {
      JsonReader jsonReader =
          Json.createReader(new FileReader(projectRoot + File.separatorChar + "retro.json"));
      JsonObject retroObject = jsonReader.readObject();
      this.retro = Retro.fromJson(retroObject);
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  public Retro getRetro() {
    return retro;
  }

  public List<Node> getParseTree(String includedFile) {
    return parser.getNodes(includedFile);
  }

  public Set<String> getParsedFiles() {
    return parsedFiles.keySet();
  }

  public void parse(final String sourceDirectory, final String rootSourceFile) {
    preParse(sourceDirectory, rootSourceFile, new HashSet<>());
    reparseFile(sourceDirectory, rootSourceFile);
    while (!filesToParse.isEmpty()) {
      List<String> filesList = new ArrayList<>(filesToParse);
      filesToParse.clear();
      for (String fileToParse : filesList) {
        reparseFile(sourceDirectory, fileToParse);
      }
    }
    parsedFiles.keySet().stream().forEach(key -> LOG.info(key));
  }

  /**
   * Scans the entire source path and sets maco names for lookup during parsing.
   *
   * @param sourceDirectory directory relative to pwd
   * @param rootSourceFile the filename
   */
  private void preParse(
      String sourceDirectory, String rootSourceFile, HashSet<String> scannedIncludes) {
    var parser = makeParser(sourceDirectory, rootSourceFile);

    var includesToScan = parser.getIncludes();
    macroNames.putAll(parser.getMacroMap());

    includesToScan.forEach(
        fileName -> {
          if (!scannedIncludes.contains(fileName)) {
            preParse(sourceDirectory, fileName, scannedIncludes);
            scannedIncludes.add(fileName);
          }
        });
  }

  public void reparseFile(String sourceDirectory, String rootSourceFile) {

    var fileName = sourceDirectory + File.separator + rootSourceFile;

    var parser = makeParser(sourceDirectory, rootSourceFile);

    List<Node> newList = new ArrayList<>();
    Node node = parser.nextNode();

    while (node != null) {
      if (node.getType().equals(NodeTypes.DIRECTIVE)
          && ((DirectiveNode) node).getDirectiveType().equals(AllDirectives.INCLUDE)) {
        scheduleParse((DirectiveNode) node);
      }
      newList.add(node);
      node = parser.nextNode();
    }
    errorNodes.put(fileName, parser.getErrors());
    parsedFiles.put(fileName, newList);
  }

  private SourceParser makeParser(String sourceDirectory, String rootSourceFile) {
    LOG.info(sourceDirectory);

    var fileName = sourceDirectory + File.separator + rootSourceFile;
    LOG.info(fileName);

    var stream = getClass().getClassLoader().getResourceAsStream(fileName);
    if (stream == null) {
      try {
        stream = new FileInputStream(fileName);
      } catch (FileNotFoundException e) {
        LOG.severe(e.getMessage());
      }
    }
    final String outfile = "test.out";

    var data = new InputData();

    data.includeFile(stream, rootSourceFile, 0);

    var scanner = data.startRead(OpCode.from(this.retro.getMainArch()));
    var parser = new SourceParser(scanner, macroNames);
    return parser;
  }

  private void scheduleParse(DirectiveNode includeDirectiveNode) {
    String filename = includeDirectiveNode.getArguments().getString(0);
    if (parsedFiles.get(includeDirectiveNode) == null) {
      filesToParse.add(filename);
    }
  }

  public List<Node> getNodes(String includedFile) {
    return parsedFiles.get(includedFile);
  }

  public List<ErrorNode> getErrors(String fileName) {
    LOG.info("getErrors:" + fileName);
    LOG.info(errorNodes.keySet().stream().collect(Collectors.joining("\n")));
    return errorNodes.getOrDefault(fileName, new ArrayList<>());
  }

  public static class Builder {

    private final String projectRoot;

    /**
     * Project builder
     *
     * @param projectRoot a relative directory path which contains a retro.json file
     */
    public Builder(String projectRoot) {
      this.projectRoot = projectRoot;
    }

    public Project build() {
      var project = new Project(projectRoot);
      project.launch();
      return project;
    }
  }

  /**
   * This method starts parsing a project per rules in its retro.json file.  It is asynchronous.
   */
  private void launch() {
    OpCode[] opcodes = OpCode.from(retro.getMainArch());
    this.parser = new MultiFileParser(opcodes);
    parser.parse(this.projectRoot, retro.getMain());

  }

}
