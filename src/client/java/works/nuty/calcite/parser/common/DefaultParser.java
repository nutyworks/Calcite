package works.nuty.calcite.parser.common;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public abstract class DefaultParser implements Parser {
    private static final Function<SuggestionsBuilder, CompletableFuture<Suggestions>> SUGGEST_NOTHING = SuggestionsBuilder::buildFuture;
    private DefaultParser parent = null;
    private final StringReader reader;
    private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestions = SUGGEST_NOTHING;
    private boolean hasSuggestion = false;

    public DefaultParser(DefaultParser parent) {
        this(parent.reader());
        this.parent = parent;
    }

    public DefaultParser(StringReader reader) {
        this.reader = reader;
    }

    public StringReader reader() {
        return this.reader;
    }

    public Function<SuggestionsBuilder, CompletableFuture<Suggestions>> getSuggestions() {
        if (this.parent != null) return parent.suggestions;
        return suggestions;
    }

    public void suggest(Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestions) {
        if (this.parent != null) parent.suggest(suggestions);
        else this.suggestions = suggestions;

        this.hasSuggestion = true;
    }

    public void suggestNothing() {
        if (this.parent != null) parent.suggestNothing();
        else this.suggestions = SUGGEST_NOTHING;

        this.hasSuggestion = false;
    }

    public boolean hasSuggestions() {
        return this.hasSuggestion;
    }

    public DefaultParser parent() {
        return this.parent;
    }
}
