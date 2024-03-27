package works.nuty.calcite.parser;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public abstract class DefaultParser implements Parser {
    private static final Function<SuggestionsBuilder, CompletableFuture<Suggestions>> SUGGEST_NOTHING = SuggestionsBuilder::buildFuture;
    private final StringReader reader;
    private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestions = SUGGEST_NOTHING;

    public DefaultParser(StringReader reader) {
        this.reader = reader;
    }

    public StringReader reader() {
        return this.reader;
    }

    public Function<SuggestionsBuilder, CompletableFuture<Suggestions>> getSuggestions() {
        return suggestions;
    }

    public void suggest(Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestions) {
        this.suggestions = suggestions;
    }

    public void suggestNothing() {
        this.suggestions = SUGGEST_NOTHING;
    }
}
