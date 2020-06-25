package dev.secondsun.wla4j.assembler.analyzer;

import java.util.List;
import java.util.stream.Collectors;
import dev.secondsun.wla4j.assembler.definition.directives.AllDirectives;
import dev.secondsun.wla4j.assembler.pass.parse.ErrorNode;
import dev.secondsun.wla4j.assembler.pass.parse.directive.DirectiveNode;

public abstract class AbstractAnalyzer {
  protected final Context context;

  protected AbstractAnalyzer(Context context) {
    this.context = context;
  }

  public abstract List<? extends ErrorNode> checkDirective(DirectiveNode node);

  /**
   * Node must be in the types of validDirectives or a illegal argument exception will be thrown
   *
   * @param node a node
   * @param validDirectives directives
   */
  protected void enforceDirectiveType(DirectiveNode node, AllDirectives... validDirectives) {
    var directives = List.of(validDirectives);
    if (!directives.contains(node.getDirectiveType())) {
      throw new IllegalArgumentException(
          String.format(
              "Node was of type %s and expected %s",
              node.getDirectiveType().getName(),
              directives.stream().map(AllDirectives::getName).collect(Collectors.joining(","))));
    }
  }
}
