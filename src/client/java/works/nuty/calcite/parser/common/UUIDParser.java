package works.nuty.calcite.parser.common;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.predicate.NumberRange;
import net.minecraft.util.math.random.Random;
import works.nuty.calcite.parser.array.IntArrayParser;
import works.nuty.calcite.parser.primitive.IntParser;

import java.util.concurrent.CompletableFuture;

public class UUIDParser extends IntArrayParser {
    public UUIDParser(DefaultParser parent) {
        super(parent, new IntParser(parent), NumberRange.IntRange.exactly(4));
    }

    public static CompletableFuture<Suggestions> suggestRandomUUID(SuggestionsBuilder builder) {
        Random random = Random.create();
        builder.suggest("[I; " + random.nextInt() + ", " + random.nextInt() + ", " + random.nextInt() + ", " + random.nextInt() + "]");
        return builder.buildFuture();
    }

    @Override
    public void parse() throws CommandSyntaxException {
        if (reader().canRead() && reader().peek() == '[') {
            super.parse();
        } else {
            suggest(UUIDParser::suggestRandomUUID);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedSymbol().createWithContext(reader(), "[I;");
        }
    }
}
