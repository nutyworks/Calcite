package works.nuty.calcite.parser.primitive;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import works.nuty.calcite.CalciteModClient;
import works.nuty.calcite.parser.DefaultParser;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.regex.Pattern;

public class FloatParser extends DefaultParser {
    private static final Pattern FLOAT_PATTERN = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?f", Pattern.CASE_INSENSITIVE);

    public FloatParser(DefaultParser parent) {
        super(parent);
    }

    private static Function<SuggestionsBuilder, CompletableFuture<Suggestions>> getShortSuggestionFunction(String value) {
        return builder -> {
            builder.suggest(value + "f");
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
            Float.parseFloat(value);
        } catch (NumberFormatException ignored) {
            reader().setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidFloat().createWithContext(reader(), value);
        }
        suggest(getShortSuggestionFunction(value));
        if (!FLOAT_PATTERN.matcher(value).matches()) {
            reader().setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidFloat().createWithContext(reader(), value);
        }
    }
}
