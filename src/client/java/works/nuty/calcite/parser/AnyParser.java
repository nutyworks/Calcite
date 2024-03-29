package works.nuty.calcite.parser;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.text.Text;

public class AnyParser extends DefaultParser {
    public static final SimpleCommandExceptionType EXPECTED_KEY = new SimpleCommandExceptionType(Text.translatable("argument.nbt.expected.key"));
    public static final SimpleCommandExceptionType EXPECTED_VALUE = new SimpleCommandExceptionType(Text.translatable("argument.nbt.expected.value"));
    public static final DynamicCommandExceptionType ARRAY_INVALID = new DynamicCommandExceptionType((type) -> Text.stringifiedTranslatable("argument.nbt.array.invalid", type));

    public AnyParser(DefaultParser parent) {
        super(parent);
    }

    @Override
    public void parse() throws CommandSyntaxException {
        suggestNothing();
        parseAny();
    }

    private void parseAny() throws CommandSyntaxException {
        if (!reader().canRead()) {
            throw EXPECTED_VALUE.createWithContext(reader());
        } else {
            char c = reader().peek();
            if (c == '{') {
                parseCompound();
            } else if (c == '[') {
                this.parseArray();
            } else {
                this.parsePrimitive();
            }
        }
    }

    private void parseCompound() throws CommandSyntaxException {
        reader().expect('{');
        reader().skipWhitespace();

        while (reader().canRead() && reader().peek() != '}') {
            int i = reader().getCursor();
            String string = reader().readString();
            if (string.isEmpty()) {
                reader().setCursor(i);
                throw EXPECTED_KEY.createWithContext(reader());
            }
            reader().skipWhitespace();
            reader().expect(':');
            reader().skipWhitespace();
            parseAny();
            reader().skipWhitespace();
            if (reader().canRead() && reader().peek() == ',') {
                reader().skip();
                reader().skipWhitespace();
                continue;
            }

            if (!reader().canRead()) {
                throw EXPECTED_KEY.createWithContext(reader());
            }
        }

        reader().expect('}');
    }

    private void parseArray() throws CommandSyntaxException {
        if (reader().canRead(3) && !StringReader.isQuotedStringStart(reader().peek(1)) && reader().peek(2) == ';') {
            this.parseElementPrimitiveArray();
        } else {
            reader().skip();
            this.readArray();
        }
    }

    private void parseElementPrimitiveArray() throws CommandSyntaxException {
        reader().expect('[');
        int i = reader().getCursor();
        char c = reader().read();
        reader().read();
        reader().skipWhitespace();
        if (!reader().canRead()) {
            throw EXPECTED_VALUE.createWithContext(reader());
        } else if ((c == 'B' || c == 'L') || c == 'I') {
            this.readArray();
        } else {
            reader().setCursor(i);
            throw ARRAY_INVALID.createWithContext(reader(), String.valueOf(c));
        }
    }

    private void readArray() throws CommandSyntaxException {
        while (reader().canRead() && reader().peek() != ']') {
            parseAny();
            reader().skipWhitespace();
            if (reader().canRead() && reader().peek() == ',') {
                reader().skip();
                reader().skipWhitespace();
                if (!reader().canRead()) {
                    throw EXPECTED_VALUE.createWithContext(reader());
                }
            }
        }

        reader().expect(']');
    }

    private void parsePrimitive() throws CommandSyntaxException {
        reader().readString();
    }
}
