package works.nuty.calcite.parser;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import works.nuty.calcite.CalciteModClient;

public class EntityTypeParser extends DefaultParser {

    public EntityTypeParser(DefaultParser parent) {
        super(parent);
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
            Registries.ENTITY_TYPE.stream()
                .filter(EntityType::isSummonable)
                .map(entityType -> Registries.ENTITY_TYPE.getId(entityType).toString())
                .filter(registryId -> registryId.startsWith(prefixedId))
                .forEach(i -> builder.suggest("\"" + i + "\""));

            return builder.buildFuture();
        });

        if (Registries.ENTITY_TYPE.stream()
                .filter(EntityType::isSummonable)
                .map(entityType -> Registries.ENTITY_TYPE.getId(entityType).toString())
                .anyMatch(registryId -> registryId.equals(prefixedId))) {
            if (parent() instanceof EntityParser entityParser) {
                entityParser.setEntityType(prefixedId);
            }
            return;
        }

        CalciteModClient.LOGGER.info(prefixedId);

        reader().setCursor(cursor);
        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedStartOfQuote().createWithContext(reader());
    }
}
