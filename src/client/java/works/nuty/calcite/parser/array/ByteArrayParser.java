package works.nuty.calcite.parser.array;

import net.minecraft.predicate.NumberRange;
import works.nuty.calcite.parser.common.DefaultParser;

public class ByteArrayParser extends ArrayParser {
    public ByteArrayParser(DefaultParser parent, DefaultParser elementParser, NumberRange.IntRange sizeRange) {
        super(parent, elementParser, sizeRange, 'B');
    }
}
