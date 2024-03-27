package works.nuty.calcite.parser.primitive;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import works.nuty.calcite.parser.DefaultParser;

import java.util.concurrent.CompletableFuture;

public class StringParser extends DefaultParser {
    private final DefaultParser parentParser;

    public StringParser(DefaultParser parentParser) {
        super(parentParser.reader());
        this.parentParser = parentParser;
    }

    public static CompletableFuture<Suggestions> suggestQuotes(SuggestionsBuilder builder) {
        builder.suggest("\"");
        builder.suggest("'");
        return builder.buildFuture();
    }

    public void parse() throws CommandSyntaxException {
        parentParser.suggest(StringParser::suggestQuotes);
        if (!parentParser.reader().canRead()) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedStartOfQuote().createWithContext(parentParser.reader());
        }
        final char next = parentParser.reader().peek();
        if (StringReader.isQuotedStringStart(next)) {
            parentParser.reader().skip();
            try {
                parentParser.reader().readStringUntil(next);
            } catch (CommandSyntaxException ignored) {
                parentParser.suggest((builder -> {
                    builder.suggest(Character.toString(next));
                    return builder.buildFuture();
                }));
                throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedEndOfQuote().createWithContext(parentParser.reader());
            }
        } else {
            parentParser.suggestNothing();
            parentParser.reader().readUnquotedString();
        }
    }
}
