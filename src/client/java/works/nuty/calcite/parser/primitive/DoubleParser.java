package works.nuty.calcite.parser.primitive;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import works.nuty.calcite.parser.DefaultParser;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.regex.Pattern;

public class DoubleParser extends DefaultParser {
    private static final Pattern DOUBLE_PATTERN_EXPLICIT = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?d", Pattern.CASE_INSENSITIVE);
    private static final Pattern DOUBLE_PATTERN_IMPLICIT = Pattern.compile("[-+]?(?:[0-9]+[.]|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?", Pattern.CASE_INSENSITIVE);
    private final DefaultParser parentParser;

    public DoubleParser(DefaultParser parentParser) {
        super(parentParser.reader());
        this.parentParser = parentParser;
    }

    private static Function<SuggestionsBuilder, CompletableFuture<Suggestions>> getShortSuggestionFunction(String value) {
        return builder -> {
            builder.suggest(value + "d");
            return builder.buildFuture();
        };
    }

    public void parse() throws CommandSyntaxException {
        final int start = reader().getCursor();
        final String value = parentParser.reader().readUnquotedString();
        parentParser.suggestNothing();
        if (value.isEmpty()) {
            parentParser.reader().setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedFloat().createWithContext(parentParser.reader());
        }
        try {
            Double.parseDouble(value);
        } catch (NumberFormatException ignored) {
            parentParser.reader().setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidFloat().createWithContext(parentParser.reader(), value);
        }
        parentParser.suggest(getShortSuggestionFunction(value));
        if (!DOUBLE_PATTERN_EXPLICIT.matcher(value).matches() && !DOUBLE_PATTERN_IMPLICIT.matcher(value).matches()) {
            parentParser.reader().setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidFloat().createWithContext(parentParser.reader(), value);
        }
    }
}
