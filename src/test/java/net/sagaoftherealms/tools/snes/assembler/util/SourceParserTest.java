package net.sagaoftherealms.tools.snes.assembler.util;


import net.sagaoftherealms.tools.snes.assembler.definition.opcodes.Opcodes65816;
import net.sagaoftherealms.tools.snes.assembler.main.Flags;
import net.sagaoftherealms.tools.snes.assembler.main.InputData;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.ParseException;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.EnumNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.NodeTypes;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.SourceParser;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static net.sagaoftherealms.tools.snes.assembler.util.TestUtils.$;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class SourceParserTest {

    @Test
    public void testAnonymousLabelNode() {
        fail("This test will test that when - or + are used as labels the parser identifies them as such instead of as parts of a arthimetic operation");
    }

    @Test
    public void testShiftVsGetByteNode() {
        fail("This test will test >,<, >>,<< are handled as get byte and bit shift nodes");
    }

    /**
     * This test tests single directive tokens and makes sure that we can match them.
     * <p>
     * Validation directives is another test.
     *
     * @param sourceLine        the source code line
     * @param expectedDirective the expected directive sourceLine parses to.
     */
    @ParameterizedTest
    @CsvSource({"'.DBCOS 0.2, 10, 3.2, 120.0, 1.3', DBCOS, '[.2,10,3.2,120.0,1.3]'"
    })
    public void testParseDirectiveWithArgumentsToken(String sourceLine, String expectedDirective, @ConvertWith(DoubleArrayConverter.class) List<Double> arguments) {
        final String outfile = "test.out";
        final String inputFile = "test.s";
        final int lineNumber = 0;

        var data = new InputData(new Flags(outfile));
        data.includeFile($(sourceLine), inputFile, lineNumber);

        var scanner = data.startRead(Opcodes65816.opt_table);
        var parser = new SourceParser(scanner);

        DirectiveNode node = (DirectiveNode) parser.nextNode();


        assertEquals(arguments.size(), node.getArguments().size());
    }

    @ParameterizedTest
    @CsvSource({".DBCOS 0.2"
    })
    public void testParsingDirectivesFailWithTooFewArgumentsToken(String sourceLine) {
        final String outfile = "test.out";
        final String inputFile = "test.s";
        final int lineNumber = 0;

        var data = new InputData(new Flags(outfile));
        data.includeFile($(sourceLine), inputFile, lineNumber);

        var scanner = data.startRead(Opcodes65816.opt_table);
        var parser = new SourceParser(scanner);

        assertThrows(ParseException.class, ()->parser.nextNode());

    }


    @Test
    public void testLabelFailsIfOutputLibrary() {
        fail("See pass_1.c#788");
    }

    @Test
    public void testLabelFailsIfNoMemoryPosition() {
        fail("See pass_1.c#792");
    }

    @Test
    public void testLabelFailsIfInBankHeaderSection() {
        fail("See pass_1.c#802");
    }


    @Test
    public void testLabelInActiveMacro() {
        fail("See pass_1.c#807");
    }


    @Test
    public void testDecodeOtherArchOpcodeToken() {
        fail("This will deal with checking types and such on opcodes");
    }


    @Test
    public void testParseRamSectionToken() {
        fail("This test should test that the ramsection directive starts a statement style block that respects ramsections.");//see pass_1.c#776
    }


    @Test
    public void firstStringTokenWithExpandedMacro() {
        fail("See pass_1.c#649");
    }


    @Test
    public void testParseEnumToken() {
        //see pass_1.c#776
        fail("This test should test that the enum directive starts a statement style block that respects enums.");
    }

    @Test
    public void parseBasicEnum() {
        final String enumSource = ".ENUM $C000\n" +
                ".ENDE";
        final String outfile = "test.out";
        final String inputFile = "test.s";
        final int lineNumber = 0;

        var data = new InputData(new Flags(outfile));
        data.includeFile($(enumSource), inputFile, lineNumber);

        var scanner = data.startRead(Opcodes65816.opt_table);

        SourceParser parser = new SourceParser(scanner);
        EnumNode enumNode = (EnumNode) parser.nextNode();

        assertEquals(NodeTypes.ENUM, enumNode.getType());
        assertEquals("49152", enumNode.getAddress());

    }

    @Test
    public void parseBasicEnumBody() {
        final String enumSource = ".ENUM $C000\n" +
                "\tSEASON_SPRING\tdb ; $00\n" +
                "\tSEASON_SUMMER\tdb ; $01\n" +
                "\tSEASON_FALL\tdb ; $02\n" +
                "\tSEASON_WINTER\tdb ; $03\n" +
                ".ENDE";
        final String outfile = "test.out";
        final String inputFile = "test.s";
        final int lineNumber = 0;

        var data = new InputData(new Flags(outfile));
        data.includeFile($(enumSource), inputFile, lineNumber);

        var scanner = data.startRead(Opcodes65816.opt_table);

        SourceParser parser = new SourceParser(scanner);
        EnumNode enumNode = (EnumNode) parser.nextNode();

        assertEquals(NodeTypes.ENUM, enumNode.getType());
        assertEquals("49152", enumNode.getAddress());

    }


    @Test
    public void parseEnumAndRamsectionTypes() {
        fail("See https://wla-dx.readthedocs.io/en/latest/asmdiv.html#enum-c000 and #ramsection-vars-bank-0-slot-1-align-4.  Enum can have information in its types");
    }


    @Test
    public void exceptionIfNoEnde() {

        final String enumSource = ".ENUM $C000\n";
        final String outfile = "test.out";
        final String inputFile = "test.s";
        final int lineNumber = 0;

        var data = new InputData(new Flags(outfile));
        data.includeFile($(enumSource), inputFile, lineNumber);

        var scanner = data.startRead(Opcodes65816.opt_table);

        SourceParser parser = new SourceParser(scanner);
        Assertions.assertThrows(ParseException.class, ()->parser.nextNode());


    }

}
