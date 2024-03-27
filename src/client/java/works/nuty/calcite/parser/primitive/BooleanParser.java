package works.nuty.calcite.parser.primitive;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import works.nuty.calcite.parser.DefaultParser;

import java.util.concurrent.CompletableFuture;

public class BooleanParser extends DefaultParser {
    private final DefaultParser parentParser;

    public BooleanParser(DefaultParser parentParser) {
        super(parentParser.reader());
        this.parentParser = parentParser;
    }

    private static CompletableFuture<Suggestions> suggestBoolean(SuggestionsBuilder builder) {
        builder.suggest("true");
        builder.suggest("false");
        return builder.buildFuture();
    }

    public void parse() throws CommandSyntaxException {
        parentParser.suggest(BooleanParser::suggestBoolean);
        final int start = parentParser.reader().getCursor();
        final String value = parentParser.reader().readString();
        if (value.isEmpty()) {
            parentParser.reader().setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedBool().createWithContext(parentParser.reader());
        }
        if (!value.equals("true") && !value.equals("false")) {
            parentParser.reader().setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedBool().createWithContext(parentParser.reader());
        }
    }
}
