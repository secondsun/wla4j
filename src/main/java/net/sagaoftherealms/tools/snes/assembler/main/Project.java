package net.sagaoftherealms.tools.snes.assembler.main;

import io.reactivex.Observable;
import io.reactivex.subjects.AsyncSubject;
import java.util.concurrent.Callable;


/**
 * A project contains all of the files, configurations, etc for a WLA project.  What is important is
 * that Project, as opposed to {@link net.sagaoftherealms.tools.snes.assembler.pass.parse.MultiFileParser},
 * reasons about projects and has more flexibility and configuration.
 */
public class Project {

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
      return new Project();
    }
  }
}
