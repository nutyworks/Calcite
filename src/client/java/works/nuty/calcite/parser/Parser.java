package works.nuty.calcite.parser;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

public interface Parser {
    void parse() throws CommandSyntaxException;
}
