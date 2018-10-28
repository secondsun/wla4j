package net.sagaoftherealms.tools.snes.assembler.util;


import net.sagaoftherealms.tools.snes.assembler.definition.opcodes.Opcodes65816;
import net.sagaoftherealms.tools.snes.assembler.main.Flags;
import net.sagaoftherealms.tools.snes.assembler.main.InputData;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.AddressNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.Node;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.NodeTypes;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.SourceParser;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static net.sagaoftherealms.tools.snes.assembler.util.TestUtils.$;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
    @CsvSource({"'.DBCOS 0.2, 10, 3.2, 120, 1.3', DBCOS, '[.2,10,3.2,120,1.3]'"
    })
    public void testParseDirectiveWithArgumentsToken(String sourceLine, String expectedDirective, @ConvertWith(DoubleArrayConverter.class) List<Double> arguments) {
        final String outfile = "test.out";
        final String inputFile = "test.s";
        final int lineNumber = 0;

        var data = new InputData(new Flags(outfile));
        data.includeFile($(sourceLine), inputFile, lineNumber);

        var scanner = data.startRead(Opcodes65816.opt_table);

        var token = scanner.getNextToken();

        assertEquals(TokenTypes.DIRECTIVE, token.getType());
        assertEquals("." + expectedDirective, token.getString());
        //This is going to fail for a while.  Basically tokens shouldn't have argument info.
        //I will need to rewrite this as a "Node" that is exported during parsing.
        assertEquals(arguments.size(), token.getArgumentsCount());
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
        Node enumNode = parser.nextNode();
        Node addressnode = parser.nextNode();
        Node endenumnode = parser.nextNode();

        assertEquals(NodeTypes.ENUM, enumNode.getType());
        assertEquals(NodeTypes.ADDRESS_NUMBER, (addressnode.getType()));
        assertEquals(0xc000, ((AddressNode)addressnode).getAddress());
        assertEquals(NodeTypes.END_ENUM, endenumnode.getType());


    }

}
