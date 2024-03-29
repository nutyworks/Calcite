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

    public DoubleParser(DefaultParser parent) {
        super(parent);
    }

    // todo change method name
    private static Function<SuggestionsBuilder, CompletableFuture<Suggestions>> getShortSuggestionFunction(String value) {
        return builder -> {
            builder.suggest(value + "d");
            return builder.buildFuture();
        };
    }

    public void parse() throws CommandSyntaxException {
        final int start = reader().getCursor();
        final String value = reader().readUnquotedString();
        suggestNothing();
        if (value.isEmpty()) {
            reader().setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedFloat().createWithContext(reader());
        }
        try {
            Double.parseDouble(value);
        } catch (NumberFormatException ignored) {
            reader().setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidFloat().createWithContext(reader(), value);
        }
        suggest(getShortSuggestionFunction(value));
        if (!DOUBLE_PATTERN_EXPLICIT.matcher(value).matches() && !DOUBLE_PATTERN_IMPLICIT.matcher(value).matches()) {
            reader().setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidFloat().createWithContext(reader(), value);
        }
    }
}
