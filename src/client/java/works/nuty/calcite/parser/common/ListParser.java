package works.nuty.calcite.parser.common;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.predicate.NumberRange;

import java.util.concurrent.CompletableFuture;

public class ListParser extends DefaultParser {
    private final DefaultParser elementParser;
    private final NumberRange.IntRange sizeRange;

    public ListParser(DefaultParser parent, DefaultParser elementParser, NumberRange.IntRange sizeRange) {
        super(parent);
        this.elementParser = elementParser;
        this.sizeRange = sizeRange;
    }

    private static CompletableFuture<Suggestions> suggestListOpen(SuggestionsBuilder builder) {
        builder.suggest("[");
        return builder.buildFuture();
    }

    private static CompletableFuture<Suggestions> suggestNext(SuggestionsBuilder builder) {
        builder.suggest(",");
        return builder.buildFuture();
    }

    private static CompletableFuture<Suggestions> suggestListCloseOrNext(SuggestionsBuilder builder) {
        builder.suggest(",");
        builder.suggest("]");
        return builder.buildFuture();
    }

    private static CompletableFuture<Suggestions> suggestListClose(SuggestionsBuilder builder) {
        builder.suggest("]");
        return builder.buildFuture();
    }

    public void parse() throws CommandSyntaxException {
        suggest(ListParser::suggestListOpen);
        reader().expect('[');
        reader().skipWhitespace();
        int i = 0;
        do {
            ++i;
            suggestNothing();
            elementParser.parse();
            this.reader().skipWhitespace();

            if (i < sizeRange.min().orElse(0)) {
                suggest(ListParser::suggestNext);
                reader().expect(',');
            } else if (i < sizeRange.max().orElse(Integer.MAX_VALUE)) {
                suggest(ListParser::suggestListCloseOrNext);
                if (!reader().canRead())
                    throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedSymbol().createWithContext(reader(), ',');
                if (reader().peek() == ',') {
                    reader().skip();
                } else {
                    reader().expect(']');
                    return;
                }
            } else {
                suggest(ListParser::suggestListClose);
                reader().expect(']');
                return;
            }
            reader().skipWhitespace();
        } while (true);
    }
}
