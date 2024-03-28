package works.nuty.calcite.parser.options;

import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.predicate.NumberRange;
import works.nuty.calcite.parser.DefaultParser;
import works.nuty.calcite.parser.EntityParser;
import works.nuty.calcite.parser.ListParser;
import works.nuty.calcite.parser.UUIDParser;
import works.nuty.calcite.parser.primitive.*;

import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;

public class EntityOptions {
    private static final Map<String, Option> OPTIONS = ImmutableMap.<String, Option>builder()
        .put("Pos", new Option(Entity.class, parser -> new ListParser(parser, new DoubleParser(parser), NumberRange.IntRange.exactly(3)).parse()))
        .put("Motion", new Option(Entity.class, parser -> new ListParser(parser, new DoubleParser(parser), NumberRange.IntRange.exactly(3)).parse()))
        .put("Rotation", new Option(Entity.class, parser -> new ListParser(parser, new FloatParser(parser), NumberRange.IntRange.exactly(2)).parse()))
        .put("FallDistance", new Option(Entity.class, parser -> new FloatParser(parser).parse()))
        .put("Fire", new Option(Entity.class, parser -> new ShortParser(parser).parse()))
        .put("Air", new Option(Entity.class, parser -> new ShortParser(parser).parse()))
        .put("OnGround", new Option(Entity.class, parser -> new BooleanParser(parser).parse()))
        .put("Invulnerable", new Option(Entity.class, parser -> new BooleanParser(parser).parse()))
        .put("PortalCooldown", new Option(Entity.class, parser -> new IntParser(parser).parse()))
        .put("UUID", new Option(Entity.class, parser -> new UUIDParser(parser).parse()))
        .put("CustomName", new Option(Entity.class, parser -> new StringParser(parser).parse()))
        .put("CustomNameVisible", new Option(Entity.class, parser -> new BooleanParser(parser).parse()))
        .put("Silent", new Option(Entity.class, parser -> new BooleanParser(parser).parse()))
        .put("NoGravity", new Option(Entity.class, parser -> new BooleanParser(parser).parse()))
        .put("Glowing", new Option(Entity.class, parser -> new BooleanParser(parser).parse()))
        .put("TicksFrozen", new Option(Entity.class, parser -> new IntParser(parser).parse()))
        .put("HasVisualFire", new Option(Entity.class, parser -> new BooleanParser(parser).parse()))
        .put("Tags", new Option(Entity.class, parser -> new ListParser(parser, new StringParser(parser), NumberRange.IntRange.atMost(1024)).parse()))
        .put("test", new Option(Entity.class, parser -> new ByteParser(parser).parse()))

        .build();

    public static void suggestKeys(EntityParser parser, SuggestionsBuilder builder) {
        String key = builder.getRemaining().toLowerCase(Locale.ROOT);
        for (var option : OPTIONS.entrySet()) {
            if (!option.getValue().shouldSuggest(parser) || !option.getKey().toLowerCase(Locale.ROOT).startsWith(key))
                continue;
            builder.suggest(option.getKey() + ":");
        }
    }

    public static boolean isPotentialKey(EntityParser parser, String key) {
        boolean value = false;
        for (var option : OPTIONS.entrySet()) {
            if (!option.getValue().shouldSuggest(parser)) continue;
            if (option.getKey().equals(key)) return false;
            if (option.getValue().shouldSuggest(parser)
                && option.getKey().toLowerCase(Locale.ROOT).startsWith(key.toLowerCase(Locale.ROOT))) {
                value = true;
            }

        }
        return value;
    }

    public static ValueHandler getValueHandler(EntityParser parser, String key, int cursor) {
        Option option = OPTIONS.get(key);
        if (option != null) {
            if (option.shouldSuggest(parser)) {
                return option.valueHandler;
            }
        }
        parser.reader().setCursor(cursor);
        // todo return nbt element handler by default
        return null;
    }

    public interface ValueHandler {
        void handle(DefaultParser parser) throws CommandSyntaxException;
    }

    static final class Option {
        final Class<?> requiresClass;
        final ValueHandler valueHandler;
        final Predicate<EntityParser> predicate;

        Option(Class<? extends Entity> requiresClass, ValueHandler valueHandler) {
            this(requiresClass, valueHandler, ignored -> true);
        }

        Option(Class<?> requiresClass, ValueHandler valueHandler, Predicate<EntityParser> predicate) {
            this.requiresClass = requiresClass;
            this.valueHandler = valueHandler;
            this.predicate = predicate;
        }

        boolean shouldSuggest(EntityParser parser) {
            if (!predicate.test(parser)) return false;
            Class<?> entityClass = parser.getEntityClass();
            while (!entityClass.equals(Object.class)) {
                if (entityClass.equals(requiresClass)) {
                    return true;
                }
                entityClass = entityClass.getSuperclass();
            }
            return false;
        }
    }
}
