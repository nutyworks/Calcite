package works.nuty.calcite.parser.common;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import works.nuty.calcite.parser.common.DefaultParser;

import java.util.Objects;
import java.util.function.Predicate;

public class RegistryParser<T> extends DefaultParser {

    private final Registry<T> registry;
    private Predicate<T> filter = ignored -> true;

    public RegistryParser(DefaultParser parent, Registry<T> registry, Predicate<T> filter) {
        this(parent, registry);
        this.filter = filter;
    }

    public RegistryParser(DefaultParser parent, Registry<T> registry) {
        super(parent);
        this.registry = registry;
    }

    @Override
    public void parse() throws CommandSyntaxException {
        int cursor = reader().getCursor();
        boolean quoted = reader().canRead() && (reader().peek() == '\'' || reader().peek() == '"');
        boolean closed = false;

        try {
            reader().readString();
            closed = true;
        } catch (CommandSyntaxException ignored) {
        }

        final String id = reader().getString().substring(cursor + (quoted ? 1 : 0), reader().getCursor() - (quoted && closed ? 1 : 0));
        final String prefixedId = (id.contains(":") || (quoted && "minecraft:".startsWith(id))) ? id : "minecraft:" + id;

        suggest(builder -> {
            registry.stream()
                .filter(filter)
                .map(registry::getId)
                .filter(Objects::nonNull)
                .map(Identifier::toString)
                .filter(registryId -> registryId.startsWith(prefixedId))
                .forEach(i -> builder.suggest("\"" + i + "\""));

            return builder.buildFuture();
        });

        if (registry.stream()
                .filter(filter)
                .map(registry::getId)
                .filter(Objects::nonNull)
                .map(Identifier::toString)
                .anyMatch(registryId -> registryId.equals(prefixedId))) {
            return;
        }

        reader().setCursor(cursor);
        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedStartOfQuote().createWithContext(reader());
    }
}
