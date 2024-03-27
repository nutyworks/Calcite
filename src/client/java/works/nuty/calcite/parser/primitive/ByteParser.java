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

public class ByteParser extends DefaultParser {
    private static final DynamicCommandExceptionType READER_INVALID_BYTE = new DynamicCommandExceptionType(value -> new LiteralMessage("Invalid byte '" + value + "'"));
    private static final SimpleCommandExceptionType READER_EXPECTED_BYTE = new SimpleCommandExceptionType(new LiteralMessage("Expected byte"));
    private static final Pattern BYTE_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)b", Pattern.CASE_INSENSITIVE);
    private final DefaultParser parentParser;

    public ByteParser(DefaultParser parentParser) {
        super(parentParser.reader());
        this.parentParser = parentParser;
    }

    private static Function<SuggestionsBuilder, CompletableFuture<Suggestions>> getByteSuggestionFunction(String value) {
        return builder -> {
            builder.suggest(value + "b");
            return builder.buildFuture();
        };
    }

    private static CompletableFuture<Suggestions> suggestBoolean(SuggestionsBuilder builder) {
        builder.suggest("true");
        builder.suggest("false");
        return builder.buildFuture();
    }

    private static String removeSuffix(String value) {
        if (value.endsWith("b") || value.endsWith("B")) return value.substring(0, value.length() - 1);
        return value;
    }

    public void parse() throws CommandSyntaxException {
        final int start = parentParser.reader().getCursor();
        final String value = parentParser.reader().readUnquotedString();
        parentParser.suggestNothing();
        if (value.isEmpty()) {
            parentParser.reader().setCursor(start);
            throw READER_EXPECTED_BYTE.createWithContext(parentParser.reader());
        }
        try {
            if (!value.startsWith("t") && !value.startsWith("f")) {
                Byte.parseByte(removeSuffix(value));
                parentParser.suggest(getByteSuggestionFunction(value));
            } else {
                parentParser.suggest(ByteParser::suggestBoolean);
            }
        } catch (NumberFormatException ignored) {
            parentParser.reader().setCursor(start);
            throw READER_INVALID_BYTE.createWithContext(parentParser.reader(), value);
        }
        if (!BYTE_PATTERN.matcher(value).matches() && !value.equals("true") && !value.equals("false")) {
            parentParser.reader().setCursor(start);
            throw READER_INVALID_BYTE.createWithContext(parentParser.reader(), value);
        }
    }
}