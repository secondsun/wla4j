package net.sagaoftherealms.tools.snes.assembler.pass.parse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;
import net.sagaoftherealms.tools.snes.assembler.definition.opcodes.OpCode;
import net.sagaoftherealms.tools.snes.assembler.main.InputData;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.macro.MacroNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.visitor.MacroDefinitionVisitor;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.visitor.Visitor;

/**
 * This class was an old way to do quick and dirty parsing. I'm replacing this with a version that
 * is much more thought out.
 */
public class MultiFileParser {

    private static final java.util.logging.Logger LOG =
            Logger.getLogger(MultiFileParser.class.getName());

    private final OpCode[] opcodes;

    private final Set<Visitor> visitors = new HashSet<>();
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

    public void parse(final URI sourceDirectory, final String rootSourceFile) {
        LOG.info("MultiFileParser.parse " + sourceDirectory + " " + rootSourceFile);
        parseFile(sourceDirectory, rootSourceFile);

        while (!filesToParse.isEmpty()) {
            List<String> filesList = new ArrayList<>(filesToParse);
            filesToParse.clear();
            for (String fileToParse : filesList) {
                LOG.info("MultiFileParser.parse fileToParse " + sourceDirectory + " " + fileToParse);

                parseFile(sourceDirectory, fileToParse);
            }
        }
        new HashSet<String>(parsedFiles.keySet())
                .stream()
                .forEach(
                        key -> {
                            if (needsReparse(getNodes(key))) {
                                LOG.info(
                                        "MultiFileParser.parse needs reparse "
                                                + sourceDirectory
                                                + " "
                                                + key
                                                + " but is actually "
                                                + key.replace(sourceDirectory + File.separator, ""));
                                parseFile(sourceDirectory, key.replace(sourceDirectory + File.separator, ""));
                            }
                        });
    }

    private void parseFile(URI sourceDirectory, String rootSourceFile) {

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
            LOG.info("MultiFileParser.parseFile needs reparse " + sourceDirectory + " " + rootSourceFile);
            parseFile(sourceDirectory, rootSourceFile);
        } else {
            errorNodes.put(rootSourceFile, parser.getErrors());
            parsedFiles.put(rootSourceFile, fileNodes);
        }
    }

    private boolean needsReparse(List<Node> fileNodes) {

        if (fileNodes == null) {
            return false;
        }

        fileNodes = fileNodes.stream()
                      .filter(fileNode -> fileNode != null)
                      .filter(fileNode -> fileNode.getType().equals(NodeTypes.LABEL_DEFINITION)
                                          || !fileNode.getChildren().isEmpty())
                      .collect(Collectors.toList());

        for (Node fileNode : fileNodes) {
            if (fileNode == null) {
                continue;
            }
            if (needsReparse(fileNode.getChildren())) {
                return true;
            }
            if (fileNode.getType().equals(NodeTypes.LABEL_DEFINITION)) {
                //Note : When we first parse a file macroNames that are from files that haven't been parsed are resolved
                //as labels.  After parsing all files we look for labels that should be Macronames and reparse those files
                //so the label definitions are correctly identified as macro calls.
                LabelDefinitionNode node = (LabelDefinitionNode) fileNode;
                if (macroNames.containsKey(node.getLabelName())) {
                    return true;
                }
            }
        }
        return false;
    }

    private SourceParser makeParser(URI sourceDirectory, String rootSourceFile) {
        var fileName = Paths.get(sourceDirectory).resolve(rootSourceFile).toString();
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
        visitors.forEach(parser::addVisitor);
        return parser;
    }

    private void scheduleParse(DirectiveNode includeDirectiveNode) {
        String filename = includeDirectiveNode.getArguments().getString(0);
        if (parsedFiles.get(filename) == null) {
            filesToParse.add(filename);
        }
    }

    /**
     * @param includedFile the included file relative to the project working directory.
     * @return
     */
    public List<Node> getNodes(String includedFile) {
        return parsedFiles.get(includedFile);
    }

    public List<ErrorNode> getErrors(String fileName) {
        return errorNodes.getOrDefault(fileName, new ArrayList<>());
    }

    public List<String> getFilesWithErrors() {

        return new ArrayList<String>(errorNodes.keySet());
    }

    public void addVisitor(Set<Visitor> visitors) {
        this.visitors.addAll(visitors);
    }
}
