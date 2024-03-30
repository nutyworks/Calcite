package works.nuty.calcite.parser.primitive;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import works.nuty.calcite.parser.common.DefaultParser;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.regex.Pattern;

public class LongParser extends DefaultParser {
    private static final Pattern LONG_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)l", Pattern.CASE_INSENSITIVE);

    public LongParser(DefaultParser parent) {
        super(parent);
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
        final int start = reader().getCursor();
        final String value = reader().readUnquotedString();
        suggestNothing();
        if (value.isEmpty()) {
            reader().setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedLong().createWithContext(reader());
        }
        try {
            Long.parseLong(removeSuffix(value));
        } catch (NumberFormatException ignored) {
            reader().setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidLong().createWithContext(reader(), value);
        }
        suggest(getLongSuggestionFunction(value));
        if (!LONG_PATTERN.matcher(value).matches()) {
            reader().setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidLong().createWithContext(reader(), value);
        }
    }
}
