package works.nuty.calcite.parser.array;

import net.minecraft.predicate.NumberRange;
import works.nuty.calcite.parser.DefaultParser;

public class ByteArrayParser extends ArrayParser {
    public ByteArrayParser(DefaultParser parentParser, DefaultParser elementParser, NumberRange.IntRange sizeRange) {
        super(parentParser, elementParser, sizeRange, 'B');
    }
}
