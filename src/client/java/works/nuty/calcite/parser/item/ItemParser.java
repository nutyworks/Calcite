package works.nuty.calcite.parser.item;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import works.nuty.calcite.parser.common.CompoundParser;
import works.nuty.calcite.parser.common.DefaultParser;
import works.nuty.calcite.parser.common.RegistryParser;

public class ItemParser extends CompoundParser<ItemStack> {
    public ItemParser(DefaultParser parent) {
        super(parent);
    }

    @Override
    public void parse() throws CommandSyntaxException {

    }

    @Override
    public void registerOptions() {
        register("id", new Option(parser -> new RegistryParser<>(parser, Registries.ITEM).parse()));
        register("count", new Option(ValueHandler.INT));
        register("components", new Option(parser -> new ItemComponentParser(parser).parse()));
    }
}
