package works.nuty.calcite.parser.primitive;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import works.nuty.calcite.parser.DefaultParser;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.regex.Pattern;

public class LongParser extends DefaultParser {
    private static final Pattern LONG_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)l", Pattern.CASE_INSENSITIVE);
    private final DefaultParser parentParser;

    public LongParser(DefaultParser parentParser) {
        super(parentParser.reader());
        this.parentParser = parentParser;
    }

    private static Function<SuggestionsBuilder, CompletableFuture<Suggestions>> getLongSuggestionFunction(String value) {
        return builder -> {
            builder.suggest(value + "L");
            return builder.buildFuture();
        };
    }

    private static String removeSuffix(String value) {
        if (value.endsWith("l") || value.endsWith("L")) return value.substring(0, value.length() - 1);
        return value;
    }

    public void parse() throws CommandSyntaxException {
        final int start = parentParser.reader().getCursor();
        final String value = parentParser.reader().readUnquotedString();
        parentParser.suggestNothing();
        if (value.isEmpty()) {
            parentParser.reader().setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedLong().createWithContext(parentParser.reader());
        }
        try {
            Long.parseLong(removeSuffix(value));
        } catch (NumberFormatException ignored) {
            parentParser.reader().setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidLong().createWithContext(parentParser.reader(), value);
        }
        parentParser.suggest(getLongSuggestionFunction(value));
        if (!LONG_PATTERN.matcher(value).matches()) {
            parentParser.reader().setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidLong().createWithContext(parentParser.reader(), value);
        }
    }
}
