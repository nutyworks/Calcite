package works.nuty.calcite.parser.array;

import net.minecraft.predicate.NumberRange;
import works.nuty.calcite.parser.DefaultParser;

public class LongArrayParser extends ArrayParser {
    public LongArrayParser(DefaultParser parent, DefaultParser elementParser, NumberRange.IntRange sizeRange) {
        super(parent, elementParser, sizeRange, 'L');
    }
}
