package works.nuty.calcite.parser.item;

import net.minecraft.component.ComponentChanges;
import works.nuty.calcite.parser.common.CompoundParser;
import works.nuty.calcite.parser.common.DefaultParser;

public class ItemComponentParser extends CompoundParser<ComponentChanges> {
    public ItemComponentParser(DefaultParser parent) {
        super(parent);
    }

    @Override
    public void registerOptions() {
        register("custom_data", new Option(ValueHandler.COMPOUND));
        register("max_stack_size", new Option(ValueHandler.INT));
        register("max_damage", new Option(ValueHandler.INT));
        register("damage", new Option(ValueHandler.INT));
//        register("unbreakable", new Option());
    }
}
