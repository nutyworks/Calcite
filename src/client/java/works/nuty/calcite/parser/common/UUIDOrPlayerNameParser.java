package works.nuty.calcite.parser.common;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.predicate.NumberRange;
import net.minecraft.util.math.random.Random;
import works.nuty.calcite.parser.array.IntArrayParser;
import works.nuty.calcite.parser.common.DefaultParser;
import works.nuty.calcite.parser.primitive.IntParser;

import java.util.concurrent.CompletableFuture;

public class UUIDOrPlayerNameParser extends IntArrayParser {
    private final Random random = Random.create();

    public UUIDOrPlayerNameParser(DefaultParser parent) {
        super(parent, new IntParser(parent), NumberRange.IntRange.exactly(4));
    }

    public CompletableFuture<Suggestions> suggestOpenOrUUID(SuggestionsBuilder builder) {
        builder.suggest("\"");
        builder.suggest("[I;");
        builder.suggest("[I; " + random.nextInt() + ", " + random.nextInt() + ", " + random.nextInt() + ", " + random.nextInt() + "]");
        return builder.buildFuture();
    }

    public CompletableFuture<Suggestions> suggestClosingQuote(SuggestionsBuilder builder) {
        builder.suggest("\"");
        return builder.buildFuture();
    }

    @Override
    public void parse() throws CommandSyntaxException {
        if (reader().canRead()) {
            if (reader().peek() == '[') {
                super.parse();
            } else {
                suggest(this::suggestClosingQuote);
                reader().readString();
            }
        } else {
            suggest(this::suggestOpenOrUUID);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedSymbol().createWithContext(reader(), "[I;");
        }
    }
}
