package works.nuty.calcite.parser.array;

import net.minecraft.predicate.NumberRange;
import works.nuty.calcite.parser.common.DefaultParser;

public class IntArrayParser extends ArrayParser {
    public IntArrayParser(DefaultParser parent, DefaultParser elementParser, NumberRange.IntRange sizeRange) {
        super(parent, elementParser, sizeRange, 'I');
    }
}
