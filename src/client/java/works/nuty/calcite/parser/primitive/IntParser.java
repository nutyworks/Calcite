package works.nuty.calcite.parser.primitive;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import works.nuty.calcite.parser.common.DefaultParser;

public class IntParser extends DefaultParser {
    public IntParser(DefaultParser parent) {
        super(parent);
    }


    public void parse() throws CommandSyntaxException {
        final int start = reader().getCursor();
        final String value = reader().readUnquotedString();
        suggestNothing();
        if (value.isEmpty()) {
            reader().setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedInt().createWithContext(reader());
        }
        try {
            Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            reader().setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidInt().createWithContext(reader(), value);
        }
    }
}
