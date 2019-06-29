package net.sagaoftherealms.tools.snes.assembler.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URI;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import net.sagaoftherealms.tools.snes.assembler.definition.opcodes.OpCode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.ErrorNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.MultiFileParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.Node;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.visitor.Visitor;

/**
 * A project contains all of the files, configurations, etc for a WLA project. What is important is
 * that Project, as opposed to {@link
 * net.sagaoftherealms.tools.snes.assembler.pass.parse.MultiFileParser}, reasons about projects and
 * has more flexibility and configuration.
 */
public class Project {

  private final Map<String, List<ErrorNode>> errorNodes = new HashMap<>();

  private static final java.util.logging.Logger LOG = Logger.getLogger(Project.class.getName());

  private final Retro retro;

  private MultiFileParser parser;

  private URI projectRootUri;

  public Project(URI projectRootUri) {
    this.projectRootUri = projectRootUri;
    String projectRoot = new File(projectRootUri).getAbsolutePath();
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
    return parser.getParsedFiles();
  }

  public List<Node> getNodes(String includedFile) {
    return parser.getNodes(includedFile);
  }

  public List<ErrorNode> getErrors(String fileName) {
    LOG.info("getFilesWithErrors:" + fileName);
    return parser.getErrors(fileName);
  }

  public List<String> getFilesWithErrors() {
    return parser.getFilesWithErrors();
  }

  public static class Builder {

    private final URI projectRoot;

    /**
     * Project builder
     *
     * @param projectRoot a relative directory path which contains a retro.json file
     */
    public Builder(URI projectRoot) {
      this.projectRoot = projectRoot;
    }

    private final Set<Visitor> visitors = new HashSet<>();

    public Project build() {
      var project = new Project(projectRoot);
      try {
        project.launch(visitors);
      } catch (Exception e) {
        Logger.getAnonymousLogger().log(Level.SEVERE, e.getMessage(), e);
      }
      return project;
    }

    public Builder addVisitor(Visitor visitor) {
      visitors.add(visitor);
      return this;
    }
  }

  /**
   * This parses a file.
   *
   * @param sourceDirectory
   * @param rootSourceFile
   */
  public void parseFile(URI sourceDirectory, String rootSourceFile) {
    try {
      parser.parse(sourceDirectory, rootSourceFile);
    } catch (Exception ex) {
      LOG.log(Level.SEVERE, "Error parsing", ex);
    }
  }

  /** This method starts parsing a project per rules in its retro.json file. It is asynchronous. */
  private void launch(Set<Visitor> visitors) {
    OpCode[] opcodes = OpCode.from(retro.getMainArch());
    this.parser = new MultiFileParser(opcodes);
    parser.addVisitor(visitors);
    try {
      parser.parse(this.projectRootUri, retro.getMain());
    } catch (Exception ex) {
      LOG.log(Level.SEVERE, "Error parsing", ex);
    }
  }
}
