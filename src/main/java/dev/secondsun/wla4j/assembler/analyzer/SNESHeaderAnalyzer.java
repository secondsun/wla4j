package dev.secondsun.wla4j.assembler.analyzer;

import java.util.ArrayList;
import java.util.List;

import dev.secondsun.wla4j.assembler.pass.parse.directive.snesheader.SnesDefinitionNode;
import dev.secondsun.wla4j.assembler.definition.directives.AllDirectives;
import dev.secondsun.wla4j.assembler.pass.parse.ErrorNode;
import dev.secondsun.wla4j.assembler.pass.parse.Node;
import dev.secondsun.wla4j.assembler.pass.parse.NodeTypes;
import dev.secondsun.wla4j.assembler.pass.parse.ParseException;
import dev.secondsun.wla4j.assembler.pass.parse.directive.DirectiveNode;

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
      if (snesNode.getKey().equalsIgnoreCase("ID")) {
        String id = snesNode.getStringValue().get();
        if (id.length() < 1 || id.length() > 4) {
          errors.add(new ErrorNode(snesNode.getSourceToken(),
              new ParseException("ID must be between 1 and 4 characters",
                  snesNode.getSourceToken())));
        }
        if (context.getSnesHeaderId() != null) {
          errors.add(new ErrorNode(snesNode.getSourceToken(),
              new ParseException("ID already Defined", snesNode.getSourceToken())));
        } else {
          context.setSnesHeaderId(snesNode.getStringValue().get());
        }
      } else if (snesNode.getKey().equalsIgnoreCase("NAME")) {
        String id = snesNode.getStringValue().get();
        if (id.length() < 1 || id.length() > 21) {
          errors.add(new ErrorNode(snesNode.getSourceToken(),
              new ParseException("NAME must be between 1 and 21 characters",
                  snesNode.getSourceToken())));
        }
        if (context.getSnesHeaderName() != null) {
          errors.add(new ErrorNode(snesNode.getSourceToken(),
              new ParseException("NAME already Defined", snesNode.getSourceToken())));
        } else {
          context.setSnesHeaderName(snesNode.getStringValue().get());
        }
      } else if (SNESRomMode.asCollection().contains(snesNode.getKey().toUpperCase())) {
        try {
          var romMode = SNESRomMode.valueOf(snesNode.getKey().toUpperCase());
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
      } else if (SNESRomSpeed.asCollection().contains(snesNode.getKey().toUpperCase())) {
        try {
          var romMode = SNESRomSpeed.valueOf(snesNode.getKey().toUpperCase());
          if (context.getSnesRomSpeed() == null) {
            context.setSnesRomSpeed(romMode);
          } else {
            errors.add(new ErrorNode(snesNode.getSourceToken(),
                    new ParseException("Rom speed already Defined", snesNode.getSourceToken())));
          }
        } catch (Exception ex) {
          errors.add(new ErrorNode(snesNode.getSourceToken(),
                  new ParseException(ex.getMessage(), snesNode.getSourceToken())));
        }
      } else if (snesNode.getKey().equalsIgnoreCase("CARTRIDGETYPE")) {
        Integer type = snesNode.getNumericValue().evaluate();
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

      } else if (snesNode.getKey().equalsIgnoreCase("ROMSIZE")) {
        Integer romSize = snesNode.getNumericValue().evaluate();
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
      } else if (snesNode.getKey().equalsIgnoreCase("SRAMSIZE")) {
        Integer sramSize = snesNode.getNumericValue().evaluate();
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
      } else if (snesNode.getKey().equalsIgnoreCase("COUNTRY")) {
        Integer country = snesNode.getNumericValue().evaluate();
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
      } else if (snesNode.getKey().equalsIgnoreCase("LICENSEECODE")) {
        Integer license = snesNode.getNumericValue().evaluate();
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
      } else if (snesNode.getKey().equalsIgnoreCase("VERSION")) {
        Integer version = snesNode.getNumericValue().evaluate();
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
