package works.nuty.calcite.parser.array;

import net.minecraft.predicate.NumberRange;
import works.nuty.calcite.parser.DefaultParser;

public class LongArrayParser extends ArrayParser {
    public LongArrayParser(DefaultParser parentParser, DefaultParser elementParser, NumberRange.IntRange sizeRange) {
        super(parentParser, elementParser, sizeRange, 'L');
    }
}
