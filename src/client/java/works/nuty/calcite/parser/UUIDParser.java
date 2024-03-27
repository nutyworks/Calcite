package works.nuty.calcite.parser;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.predicate.NumberRange;
import net.minecraft.util.math.random.Random;
import works.nuty.calcite.parser.array.IntArrayParser;
import works.nuty.calcite.parser.primitive.IntParser;

import java.util.concurrent.CompletableFuture;

public class UUIDParser extends IntArrayParser {
    private final DefaultParser parentParser;

    public UUIDParser(DefaultParser parentParser) {
        super(parentParser, new IntParser(parentParser), NumberRange.IntRange.exactly(4));
        this.parentParser = parentParser;
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
            parentParser.suggest(UUIDParser::suggestRandomUUID);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedSymbol().createWithContext(reader(), "[I;");
        }
    }
}
