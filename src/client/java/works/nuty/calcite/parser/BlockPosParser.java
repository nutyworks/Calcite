package works.nuty.calcite.parser;

import net.minecraft.predicate.NumberRange;
import works.nuty.calcite.parser.array.IntArrayParser;
import works.nuty.calcite.parser.common.DefaultParser;
import works.nuty.calcite.parser.primitive.IntParser;

public class BlockPosParser extends IntArrayParser {
    public BlockPosParser(DefaultParser parent) {
        super(parent, new IntParser(parent), NumberRange.IntRange.exactly(3));
    }
}
