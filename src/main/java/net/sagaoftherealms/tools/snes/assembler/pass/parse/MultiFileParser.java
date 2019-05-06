package net.sagaoftherealms.tools.snes.assembler.pass.parse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;
import net.sagaoftherealms.tools.snes.assembler.definition.opcodes.OpCode;
import net.sagaoftherealms.tools.snes.assembler.main.InputData;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.macro.MacroNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.visitor.MacroDefinitionVisitor;

/**
 * This class was an old way to do quick and dirty parsing. I'm replacing this with a version that
 * is much more thought out.
 */
public class MultiFileParser {

  private static final java.util.logging.Logger LOG =
      Logger.getLogger(MultiFileParser.class.getName());

  private final OpCode[] opcodes;

  private final Map<String, List<Node>> parsedFiles = new HashMap<>();
  private final Set<String> filesToParse = new HashSet<>();
  private final Map<String, Optional<MacroNode>> macroNames = new HashMap<>();
  private final Map<String, List<ErrorNode>> errorNodes = new HashMap<>();

  public MultiFileParser(OpCode[] opcodes) {
    this.opcodes = opcodes;
  }

  public Set<String> getParsedFiles() {
    return parsedFiles.keySet();
  }

  public void parse(final String sourceDirectory, final String rootSourceFile) {
    parseFile(sourceDirectory, rootSourceFile);
    while (!filesToParse.isEmpty()) {
      List<String> filesList = new ArrayList<>(filesToParse);
      filesToParse.clear();
      for (String fileToParse : filesList) {
        parseFile(sourceDirectory, fileToParse);
      }
    }
    new HashSet<String>(parsedFiles.keySet())
        .stream()
        .forEach(
            key -> {
              if (needsReparse(getNodes(key))) {
                parseFile(sourceDirectory, key.replace(sourceDirectory + "/", ""));
              }
            });
  }

  private void parseFile(String sourceDirectory, String rootSourceFile) {

    var fileName = sourceDirectory + File.separator + rootSourceFile;

    var parser = makeParser(sourceDirectory, rootSourceFile);
    var macroDefinitionVisitor = new MacroDefinitionVisitor();

    parser.addVisitor(macroDefinitionVisitor);

    List<Node> fileNodes = new ArrayList<>();
    Node node = parser.nextNode();

    while (node != null) {
      if (node.getType().equals(NodeTypes.DIRECTIVE)
          && ((DirectiveNode) node).getDirectiveType().equals(AllDirectives.INCLUDE)) {
        scheduleParse((DirectiveNode) node);
      }
      fileNodes.add(node);
      node = parser.nextNode();
    }

    macroNames.putAll(macroDefinitionVisitor.getMacroNames());
    if (needsReparse(fileNodes)) {
      parseFile(sourceDirectory, rootSourceFile);
    } else {
      errorNodes.put(fileName, parser.getErrors());
      parsedFiles.put(fileName, fileNodes);
    }
  }

  private boolean needsReparse(List<Node> fileNodes) {
    if (fileNodes == null) {
      return false;
    }
    for (Node fileNode : fileNodes) {
      if (fileNode == null) {
        continue;
      }
      if (needsReparse(fileNode.getChildren())) {
        return true;
      }
      if (fileNode.getType().equals(NodeTypes.LABEL_DEFINITION)) {
        LabelDefinitionNode node = (LabelDefinitionNode) fileNode;
        if (macroNames.containsKey(node.getLabelName())) {
          return true;
        }
      }
    }
    return false;
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

    var data = new InputData();
    try {
      data.includeFile(stream, rootSourceFile, 0);
    } catch (Exception e) {
      throw new RuntimeException(
          "Could not include file " + fileName + " with root " + rootSourceFile, e);
    }
    var scanner = data.startRead(opcodes);
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
    return errorNodes.getOrDefault(fileName, new ArrayList<>());
  }

  public List<String> getFilesWithErrors() {

    LOG.info(errorNodes.keySet().stream().collect(Collectors.joining("\n")));
    return new ArrayList<String>(errorNodes.keySet());
  }
}
