package net.sagaoftherealms.tools.snes.assembler.main;

import io.reactivex.Observable;
import java.io.FileNotFoundException;
import java.io.FileReader;
import javax.json.Json;
import javax.json.JsonReader;

/**
 * A project contains all of the files, configurations, etc for a WLA project.  What is important is
 * that Project, as opposed to {@link net.sagaoftherealms.tools.snes.assembler.pass.parse.MultiFileParser},
 * reasons about projects and has more flexibility and configuration.
 */
public class Project {

  public Project(String projectRoot) {
    try {
      JsonReader jsonReader = Json.createReader(new FileReader(projectRoot));
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }


  }

  /**
   * This will begin scanning and building the internal structures used by tools.  This operation is
   * asynchronous as it may take a long time.
   *
   * @return the task doing the work.
   */
  public Observable<Boolean> prepare() {
    return Observable.fromCallable(() -> true);
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
      return new Project(projectRoot);
    }
  }
}
