package works.nuty.calcite.suggestion;

import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;

public abstract class CalciteSuggestion extends Suggestion {
    private StringRange range;
    private String text;

    public CalciteSuggestion() {
        super(StringRange.at(0), "");
        this.range = StringRange.at(0);
        this.text = "";
    }

    public void setRange(StringRange range) {
        this.range = range;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String apply(String input) {
        if (range.getStart() == 0 && range.getEnd() == input.length()) {
            return text;
        }
        final StringBuilder result = new StringBuilder();
        if (range.getStart() > 0) {
            result.append(input, 0, range.getStart());
        }
        result.append(text);
        if (range.getEnd() < input.length()) {
            result.append(input.substring(range.getEnd()));
        }
        return result.toString();
    }
}
