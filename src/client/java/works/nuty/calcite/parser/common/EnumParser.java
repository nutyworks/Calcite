package works.nuty.calcite.parser.common;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.Set;

public class EnumParser extends DefaultParser {

    private final Set<String> values;

    public EnumParser(DefaultParser parent, Set<String> values) {
        super(parent);
        this.values = values;
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

        suggest(builder -> {
            values.stream()
                .filter(registryId -> registryId.startsWith(id))
                .forEach(i -> builder.suggest("\"" + i + "\""));

            return builder.buildFuture();
        });

        if (values.stream().anyMatch(registryId -> registryId.equals(id))) {
            return;
        }

        reader().setCursor(cursor);
        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedStartOfQuote().createWithContext(reader());
    }
}
