package works.nuty.calcite.suggestion;

import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import works.nuty.calcite.mixin.client.SuggestionsBuilderFields;

public class CalciteSuggestionsBuilder extends SuggestionsBuilder {
    public CalciteSuggestionsBuilder(String input, String inputLowerCase, int start) {
        super(input, inputLowerCase, start);
    }

    public static void suggestCustom(SuggestionsBuilder target, CalciteSuggestion suggestion) {
        suggestion.setRange(StringRange.between(target.getStart(), target.getInput().length()));

        ((SuggestionsBuilderFields) target).getResult().add(suggestion);
    }
}
