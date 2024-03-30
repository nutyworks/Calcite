package works.nuty.calcite.parser.primitive;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import works.nuty.calcite.parser.common.DefaultParser;

import java.util.concurrent.CompletableFuture;

public class BooleanParser extends DefaultParser {
    public BooleanParser(DefaultParser parent) {
        super(parent);
    }

    private static CompletableFuture<Suggestions> suggestBoolean(SuggestionsBuilder builder) {
        builder.suggest("true");
        builder.suggest("false");
        return builder.buildFuture();
    }

    public void parse() throws CommandSyntaxException {
        suggest(BooleanParser::suggestBoolean);
        final int start = reader().getCursor();
        final String value = reader().readString();
        if (value.isEmpty()) {
            reader().setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedBool().createWithContext(reader());
        }
        if (!value.equals("true") && !value.equals("false")) {
            reader().setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedBool().createWithContext(reader());
        }
    }
}
