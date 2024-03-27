package works.nuty.calcite.parser.primitive;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import works.nuty.calcite.parser.DefaultParser;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.regex.Pattern;

public class ShortParser extends DefaultParser {
    private static final DynamicCommandExceptionType READER_INVALID_SHORT = new DynamicCommandExceptionType(value -> new LiteralMessage("Invalid byte '" + value + "'"));
    private static final SimpleCommandExceptionType READER_EXPECTED_SHORT = new SimpleCommandExceptionType(new LiteralMessage("Expected byte"));
    private static final Pattern SHORT_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)s", Pattern.CASE_INSENSITIVE);
    private final DefaultParser parentParser;

    public ShortParser(DefaultParser parentParser) {
        super(parentParser.reader());
        this.parentParser = parentParser;
    }

    private static Function<SuggestionsBuilder, CompletableFuture<Suggestions>> getShortSuggestionFunction(String value) {
        return builder -> {
            builder.suggest(value + "s");
            return builder.buildFuture();
        };
    }

    private static String removeSuffix(String value) {
        if (value.endsWith("s") || value.endsWith("S")) return value.substring(0, value.length() - 1);
        return value;
    }

    public void parse() throws CommandSyntaxException {
        final int start = parentParser.reader().getCursor();
        final String value = parentParser.reader().readUnquotedString();
        parentParser.suggestNothing();
        if (value.isEmpty()) {
            parentParser.reader().setCursor(start);
            throw READER_EXPECTED_SHORT.createWithContext(parentParser.reader());
        }
        try {
            Short.parseShort(removeSuffix(value));
        } catch (NumberFormatException ignored) {
            parentParser.reader().setCursor(start);
            throw READER_INVALID_SHORT.createWithContext(parentParser.reader(), value);
        }
        parentParser.suggest(getShortSuggestionFunction(value));
        if (!SHORT_PATTERN.matcher(value).matches()) {
            parentParser.reader().setCursor(start);
            throw READER_INVALID_SHORT.createWithContext(parentParser.reader(), value);
        }
    }
}
