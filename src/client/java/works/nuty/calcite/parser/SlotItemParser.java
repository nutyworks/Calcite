package works.nuty.calcite.parser;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import works.nuty.calcite.parser.common.DefaultParser;

import java.util.concurrent.CompletableFuture;

public class SlotItemParser extends DefaultParser {
    public SlotItemParser(DefaultParser parent) {
        super(parent);
    }

    @Override
    public void parse() throws CommandSyntaxException {

    }

    private static CompletableFuture<Suggestions> suggestSlotItemOpen(SuggestionsBuilder builder) {
        builder.suggest("{");
        return builder.buildFuture();
    }
}
