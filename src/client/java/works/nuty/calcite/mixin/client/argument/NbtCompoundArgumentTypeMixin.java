package works.nuty.calcite.mixin.client.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.NbtCompoundArgumentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import works.nuty.calcite.parser.EntityParser;

import java.util.concurrent.CompletableFuture;

@Mixin(NbtCompoundArgumentType.class)
public abstract class NbtCompoundArgumentTypeMixin implements ArgumentType<NbtCompound> {
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if (((LiteralCommandNode<S>) context.getLastChild().getNodes().get(0).getNode()).getLiteral().equals("summon")) {
            return listEntityStringNbtSuggestions(context, builder);
        } else {
            return Suggestions.empty();
        }
    }

    @Unique
    private <S> CompletableFuture<Suggestions> listEntityStringNbtSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        String entityRegistryId = context.getLastChild().getArgument("entity", RegistryEntry.Reference.class).getIdAsString();
        Object object = context.getSource();
        if (object instanceof CommandSource) {
            StringReader reader = new StringReader(builder.getInput());
            reader.setCursor(builder.getStart());
            EntityParser parser = new EntityParser(reader, entityRegistryId);
            try {
                parser.parse();
            } catch (CommandSyntaxException ignored) {
            }
            return parser.fillSuggestions(builder);
        }
        return Suggestions.empty();
    }
}
