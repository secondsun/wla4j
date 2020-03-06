package net.sagaoftherealms.tools.snes.assembler.analyzer;

import java.util.ArrayList;
import java.util.List;
import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.ErrorNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.Node;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.NodeTypes;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.ParseException;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.snesheader.SnesDefinitionNode;

public class SNESHeaderAnalyzer extends AbstractAnalyzer {

  protected SNESHeaderAnalyzer(Context context) {
    super(context);
  }

  @Override
  public List<? extends ErrorNode> checkDirective(DirectiveNode node) {
    enforceDirectiveType(node, AllDirectives.SNESHEADER);
    var errors = new ArrayList<ErrorNode>();
    if (context.getSnesDefined()) {
      errors.add(
          new ErrorNode(
              node.getSourceToken(),
              new ParseException("SNESHeader must be defined only once", node.getSourceToken())));
      return errors;
    } else {
      context.setSnesDefined(true);
    }

    for (Node headerNode : node.getBody().getChildren()) {
      if (!headerNode.getType().equals(NodeTypes.SNES_HEADER_DEFINITION)) {
        throw new ParseException("Unexpected Node", headerNode.getSourceToken());
      }

      SnesDefinitionNode snesNode = (SnesDefinitionNode) headerNode;
      if (snesNode.getLabel().equalsIgnoreCase("ID")) {
        String id = snesNode.getName().get();
        if (id.length() < 1 || id.length() > 4) {
          errors.add(new ErrorNode(snesNode.getSourceToken(),
              new ParseException("ID must be between 1 and 4 characters",
                  snesNode.getSourceToken())));
        }
        if (context.getSnesHeaderId() != null) {
          errors.add(new ErrorNode(snesNode.getSourceToken(),
              new ParseException("ID already Defined", snesNode.getSourceToken())));
        } else {
          context.setSnesHeaderId(snesNode.getName().get());
        }
      } else if (snesNode.getLabel().equalsIgnoreCase("NAME")) {
        String id = snesNode.getName().get();
        if (id.length() < 1 || id.length() > 21) {
          errors.add(new ErrorNode(snesNode.getSourceToken(),
              new ParseException("NAME must be between 1 and 21 characters",
                  snesNode.getSourceToken())));
        }
        if (context.getSnesHeaderName() != null) {
          errors.add(new ErrorNode(snesNode.getSourceToken(),
              new ParseException("NAME already Defined", snesNode.getSourceToken())));
        } else {
          context.setSnesHeaderName(snesNode.getName().get());
        }
      } else if (SNESRomMode.asCollection().contains(snesNode.getLabel().toUpperCase())) {
        try {
          var romMode = SNESRomMode.valueOf(snesNode.getLabel().toUpperCase());
          if (context.getSnesRomMode() == null) {
            context.setSnesRomMode(romMode);
          } else {
            errors.add(new ErrorNode(snesNode.getSourceToken(),
                new ParseException("Rom mode already Defined", snesNode.getSourceToken())));
          }
        } catch (Exception ex) {
          errors.add(new ErrorNode(snesNode.getSourceToken(),
              new ParseException(ex.getMessage(), snesNode.getSourceToken())));
        }
      } else if (snesNode.getLabel().equalsIgnoreCase("CARTRIDGETYPE")) {
        Integer type = snesNode.getSize().evaluate();
        if (context.getCartridgeType() == null) {
          context.setCartridgeType(type);
        } else {
          errors.add(new ErrorNode(snesNode.getSourceToken(),
              new ParseException("CARTRIDGETYPE already Defined", snesNode.getSourceToken())));
        }

        if (type < -128 || type > 255) {
          errors.add(new ErrorNode(snesNode.getSourceToken(),
              new ParseException("CARTRIDGETYPE must be 8-bit", snesNode.getSourceToken())));
        }

      } else if (snesNode.getLabel().equalsIgnoreCase("ROMSIZE")) {
        Integer romSize = snesNode.getSize().evaluate();
        if (context.getRomSize() == null) {
          context.setRomSize(romSize);
        } else {
          errors.add(new ErrorNode(snesNode.getSourceToken(),
              new ParseException("ROMSIZE already Defined", snesNode.getSourceToken())));
        }

        if (romSize < -128 || romSize > 255) {
          errors.add(new ErrorNode(snesNode.getSourceToken(),
              new ParseException("ROMSIZE must be 8-bit", snesNode.getSourceToken())));
        }
      } else if (snesNode.getLabel().equalsIgnoreCase("SRAMSIZE")) {
        Integer sramSize = snesNode.getSize().evaluate();
        if (context.getSramSize() == null) {
          context.setSramSize(sramSize);
        } else {
          errors.add(new ErrorNode(snesNode.getSourceToken(),
              new ParseException("SRAMSIZE already Defined", snesNode.getSourceToken())));
        }

        if (sramSize < -128 || sramSize > 255) {
          errors.add(new ErrorNode(snesNode.getSourceToken(),
              new ParseException("SRAMSIZE must be 8-bit", snesNode.getSourceToken())));
        }
      } else if (snesNode.getLabel().equalsIgnoreCase("COUNTRY")) {
        Integer country = snesNode.getSize().evaluate();
        if (context.getCountry() == null) {
          context.setCountry(country);
        } else {
          errors.add(new ErrorNode(snesNode.getSourceToken(),
              new ParseException("COUNTRY already Defined", snesNode.getSourceToken())));
        }

        if (country < -128 || country > 255) {
          errors.add(new ErrorNode(snesNode.getSourceToken(),
              new ParseException("COUNTRY must be 8-bit", snesNode.getSourceToken())));
        }
      } else if (snesNode.getLabel().equalsIgnoreCase("LICENSEECODE")) {
        Integer license = snesNode.getSize().evaluate();
        if (context.getLicense() == null) {
          context.setLicense(license);
        } else {
          errors.add(new ErrorNode(snesNode.getSourceToken(),
              new ParseException("LICENSEECODE already Defined", snesNode.getSourceToken())));
        }

        if (license < -128 || license > 255) {
          errors.add(new ErrorNode(snesNode.getSourceToken(),
              new ParseException("LICENSEECODE must be 8-bit", snesNode.getSourceToken())));
        }
      } else if (snesNode.getLabel().equalsIgnoreCase("VERSION")) {
        Integer version = snesNode.getSize().evaluate();
        if (context.getVersion() == null) {
          context.setVersion(version);
        } else {
          errors.add(new ErrorNode(snesNode.getSourceToken(),
              new ParseException("VERSION already Defined", snesNode.getSourceToken())));
        }

        if (version < -128 || version > 255) {
          errors.add(new ErrorNode(snesNode.getSourceToken(),
              new ParseException("VERSION must be 8-bit", snesNode.getSourceToken())));
        }
      }


    }

    return errors;
  }

}
