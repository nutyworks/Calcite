package works.nuty.calcite.parser.primitive;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import works.nuty.calcite.parser.DefaultParser;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.regex.Pattern;

public class FloatParser extends DefaultParser {
    private static final Pattern FLOAT_PATTERN = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?f", Pattern.CASE_INSENSITIVE);
    private final DefaultParser parentParser;

    public FloatParser(DefaultParser parentParser) {
        super(parentParser.reader());
        this.parentParser = parentParser;
    }

    private static Function<SuggestionsBuilder, CompletableFuture<Suggestions>> getShortSuggestionFunction(String value) {
        return builder -> {
            builder.suggest(value + "f");
            return builder.buildFuture();
        };
    }

    private static String removeSuffix(String value) {
        if (value.endsWith("f") || value.endsWith("F")) return value.substring(0, value.length() - 1);
        return value;
    }

    public void parse() throws CommandSyntaxException {
        final int start = parentParser.reader().getCursor();
        final String value = parentParser.reader().readUnquotedString();
        parentParser.suggestNothing();
        if (value.isEmpty()) {
            parentParser.reader().setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedFloat().createWithContext(parentParser.reader());
        }
        try {
            Float.parseFloat(removeSuffix(value));
        } catch (NumberFormatException ignored) {
            parentParser.reader().setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidFloat().createWithContext(parentParser.reader(), value);
        }
        parentParser.suggest(getShortSuggestionFunction(value));
        if (!FLOAT_PATTERN.matcher(value).matches()) {
            parentParser.reader().setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidFloat().createWithContext(parentParser.reader(), value);
        }
    }
}
