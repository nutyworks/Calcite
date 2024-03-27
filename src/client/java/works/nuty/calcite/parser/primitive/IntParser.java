package works.nuty.calcite.parser.primitive;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import works.nuty.calcite.parser.DefaultParser;

public class IntParser extends DefaultParser {
    private final DefaultParser parentParser;

    public IntParser(DefaultParser parentParser) {
        super(parentParser.reader());
        this.parentParser = parentParser;
    }


    public void parse() throws CommandSyntaxException {
        final int start = parentParser.reader().getCursor();
        final String value = parentParser.reader().readUnquotedString();
        parentParser.suggestNothing();
        if (value.isEmpty()) {
            parentParser.reader().setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedInt().createWithContext(parentParser.reader());
        }
        try {
            Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            parentParser.reader().setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidInt().createWithContext(parentParser.reader(), value);
        }
    }
}
