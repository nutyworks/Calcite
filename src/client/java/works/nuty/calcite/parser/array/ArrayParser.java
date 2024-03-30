package works.nuty.calcite.parser.array;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.predicate.NumberRange;
import works.nuty.calcite.parser.common.DefaultParser;

import java.util.concurrent.CompletableFuture;

public class ArrayParser extends DefaultParser {
    private final DefaultParser elementParser;
    private final NumberRange.IntRange sizeRange;
    private final char prefix;

    public ArrayParser(DefaultParser parent, DefaultParser elementParser, NumberRange.IntRange sizeRange, char prefix) {
        super(parent);
        this.elementParser = elementParser;
        this.sizeRange = sizeRange;
        this.prefix = prefix;
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
        suggest(this::suggestArrayOpen);
        int start = reader().getCursor();
        if (!reader().canRead(3)) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedSymbol().createWithContext(reader(), "[" + prefix + ";");
        }
        try {
            reader().expect('[');
            reader().expect(prefix);
            reader().expect(';');
        } catch (CommandSyntaxException e) {
            reader().setCursor(start);
            throw e;
        }
        reader().skipWhitespace();
        int i = 0;
        do {
            ++i;
            this.suggestNothing();
            elementParser.parse();
            this.reader().skipWhitespace();

            if (i < sizeRange.min().orElse(0)) {
                suggest(ArrayParser::suggestNext);
                reader().expect(',');
            } else if (i < sizeRange.max().orElse(Integer.MAX_VALUE)) {
                suggest(ArrayParser::suggestListCloseOrNext);
                if (!reader().canRead())
                    throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedSymbol().createWithContext(reader(), ',');
                if (reader().peek() == ',') {
                    reader().skip();
                } else {
                    reader().expect(']');
                    return;
                }
            } else {
                suggest(ArrayParser::suggestListClose);
                reader().expect(']');
                return;
            }
            reader().skipWhitespace();
        } while (true);
    }

    private CompletableFuture<Suggestions> suggestArrayOpen(SuggestionsBuilder builder) {
        builder.suggest("[" + prefix + ";");
        return builder.buildFuture();
    }
}
