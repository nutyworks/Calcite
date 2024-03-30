package works.nuty.calcite.parser.primitive;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import works.nuty.calcite.parser.common.DefaultParser;

import java.util.concurrent.CompletableFuture;

public class StringParser extends DefaultParser {
    public StringParser(DefaultParser parent) {
        super(parent);
    }

    public static CompletableFuture<Suggestions> suggestQuotes(SuggestionsBuilder builder) {
        builder.suggest("\"");
        builder.suggest("'");
        return builder.buildFuture();
    }

    public void parse() throws CommandSyntaxException {
        suggest(StringParser::suggestQuotes);
        if (!reader().canRead()) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedStartOfQuote().createWithContext(reader());
        }
        final char next = reader().peek();
        if (StringReader.isQuotedStringStart(next)) {
            reader().skip();
            try {
                reader().readStringUntil(next);
            } catch (CommandSyntaxException ignored) {
                suggest((builder -> {
                    builder.suggest(Character.toString(next));
                    return builder.buildFuture();
                }));
                throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedEndOfQuote().createWithContext(reader());
            }
        } else {
            suggestNothing();
            reader().readUnquotedString();
        }
    }
}
