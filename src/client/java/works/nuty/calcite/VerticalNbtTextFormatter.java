package works.nuty.calcite;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteOpenHashSet;
import net.minecraft.nbt.*;
import net.minecraft.nbt.visitor.NbtElementVisitor;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;

import java.util.*;
import java.util.regex.Pattern;

public class VerticalNbtTextFormatter
    implements NbtElementVisitor {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int field_33271 = 8;
    private static final ByteCollection SINGLE_LINE_ELEMENT_TYPES = new ByteOpenHashSet(Arrays.asList((byte) 1, (byte) 2, (byte) 3, (byte) 4, (byte) 5, (byte) 6));
    private static final Formatting NAME_COLOR = Formatting.AQUA;
    private static final Formatting STRING_COLOR = Formatting.GREEN;
    private static final Formatting NUMBER_COLOR = Formatting.GOLD;
    private static final Formatting TYPE_SUFFIX_COLOR = Formatting.RED;
    private static final Pattern SIMPLE_NAME = Pattern.compile("[A-Za-z0-9._+-]+");
    private static final String KEY_VALUE_SEPARATOR = String.valueOf(':');
    private static final String ENTRY_SEPARATOR = String.valueOf(',');
    private static final String SQUARE_OPEN_BRACKET = "[";
    private static final String SQUARE_CLOSE_BRACKET = "]";
    private static final String SEMICOLON = ";";
    private static final String SPACE = " ";
    private static final String CURLY_OPEN_BRACKET = "{";
    private static final String CURLY_CLOSE_BRACKET = "}";
    private final String prefix;
    private final int indentationLevel;
    private final List<Text> resultList = new ArrayList<>();
    private MutableText result = Text.literal("");

    public VerticalNbtTextFormatter(String prefix, int indentationLevel) {
        this.prefix = prefix;
        this.indentationLevel = indentationLevel;
    }

    protected static Text escapeName(String name) {
        if (SIMPLE_NAME.matcher(name).matches()) {
            return Text.literal(name).formatted(NAME_COLOR);
        }
        String string = NbtString.escape(name);
        String string2 = string.substring(0, 1);
        MutableText text = Text.literal(string.substring(1, string.length() - 1)).formatted(NAME_COLOR);
        return Text.literal(string2).append(text).append(string2);
    }

    /**
     * {@return the textified NBT {@code element}}
     */
    public List<Text> apply(NbtElement element) {
        element.accept(this);
        newLine();
        return this.resultList;
    }

    public void newLine() {
        resultList.add(this.result);
        this.result = Text.literal("");
    }

    public void append(String string) {
        this.result.append(Text.literal(string));
    }

    public void append(Text text) {
        this.result.append(text);
    }

    public void append(List<Text> texts) {
        if (texts.isEmpty()) return;
        this.result.append(texts.get(0));
        for (int i = 1; i < texts.size(); i++) {
            newLine();
            this.result.append(texts.get(i));
        }

    }

    @Override
    public void visitString(NbtString element) {
        String string = NbtString.escape(element.asString());
        String string2 = string.substring(0, 1);
        MutableText text = Text.literal(string.substring(1, string.length() - 1)).formatted(STRING_COLOR);
        append(Text.literal(string2).append(text).append(string2));
    }

    @Override
    public void visitByte(NbtByte element) {
        MutableText text = Text.literal("b").formatted(TYPE_SUFFIX_COLOR);
        append(Text.literal(String.valueOf(element.numberValue())).append(text).formatted(NUMBER_COLOR));
    }

    @Override
    public void visitShort(NbtShort element) {
        MutableText text = Text.literal("s").formatted(TYPE_SUFFIX_COLOR);
        append(Text.literal(String.valueOf(element.numberValue())).append(text).formatted(NUMBER_COLOR));
    }

    @Override
    public void visitInt(NbtInt element) {
        append(Text.literal(String.valueOf(element.numberValue())).formatted(NUMBER_COLOR));
    }

    @Override
    public void visitLong(NbtLong element) {
        MutableText text = Text.literal("L").formatted(TYPE_SUFFIX_COLOR);
        append(Text.literal(String.valueOf(element.numberValue())).append(text).formatted(NUMBER_COLOR));
    }

    @Override
    public void visitFloat(NbtFloat element) {
        MutableText text = Text.literal("f").formatted(TYPE_SUFFIX_COLOR);
        append(Text.literal(String.valueOf(element.floatValue())).append(text).formatted(NUMBER_COLOR));
    }

    @Override
    public void visitDouble(NbtDouble element) {
        MutableText text = Text.literal("d").formatted(TYPE_SUFFIX_COLOR);
        append(Text.literal(String.valueOf(element.doubleValue())).append(text).formatted(NUMBER_COLOR));
    }

    @Override
    public void visitByteArray(NbtByteArray element) {
        MutableText text = Text.literal("B").formatted(TYPE_SUFFIX_COLOR);
        MutableText mutableText = Text.literal(SQUARE_OPEN_BRACKET).append(text).append(SEMICOLON);
        byte[] bs = element.getByteArray();
        for (int i = 0; i < bs.length; ++i) {
            MutableText mutableText2 = Text.literal(String.valueOf(bs[i])).formatted(NUMBER_COLOR);
            mutableText.append(SPACE).append(mutableText2).append(text);
            if (i == bs.length - 1) continue;
            mutableText.append(ENTRY_SEPARATOR);
        }
        mutableText.append(SQUARE_CLOSE_BRACKET);
        append(mutableText);
    }

    @Override
    public void visitIntArray(NbtIntArray element) {
        MutableText text = Text.literal("I").formatted(TYPE_SUFFIX_COLOR);
        MutableText mutableText = Text.literal(SQUARE_OPEN_BRACKET).append(text).append(SEMICOLON);
        int[] is = element.getIntArray();
        for (int i = 0; i < is.length; ++i) {
            mutableText.append(SPACE).append(Text.literal(String.valueOf(is[i])).formatted(NUMBER_COLOR));
            if (i == is.length - 1) continue;
            mutableText.append(ENTRY_SEPARATOR);
        }
        mutableText.append(SQUARE_CLOSE_BRACKET);
        append(mutableText);
    }

    @Override
    public void visitLongArray(NbtLongArray element) {
        MutableText text = Text.literal("L").formatted(TYPE_SUFFIX_COLOR);
        MutableText mutableText = Text.literal(SQUARE_OPEN_BRACKET).append(text).append(SEMICOLON);
        long[] ls = element.getLongArray();
        for (int i = 0; i < ls.length; ++i) {
            MutableText text2 = Text.literal(String.valueOf(ls[i])).formatted(NUMBER_COLOR);
            mutableText.append(SPACE).append(text2).append(text);
            if (i == ls.length - 1) continue;
            mutableText.append(ENTRY_SEPARATOR);
        }
        mutableText.append(SQUARE_CLOSE_BRACKET);
        append(mutableText);
    }

    @Override
    public void visitList(NbtList element) {
        if (element.isEmpty()) {
            append(Text.literal("[]"));
            newLine();
            return;
        }
        if (SINGLE_LINE_ELEMENT_TYPES.contains(element.getHeldType()) && element.size() <= 8) {
            String string = ENTRY_SEPARATOR + SPACE;
            append(Text.literal(SQUARE_OPEN_BRACKET));
            for (int i = 0; i < element.size(); ++i) {
                if (i != 0) {
                    append(string);
                }
                append(new VerticalNbtTextFormatter(this.prefix, this.indentationLevel).apply(element.get(i)));
            }
            append(SQUARE_CLOSE_BRACKET);
            return;
        }
        append(Text.literal(SQUARE_OPEN_BRACKET));
        if (!this.prefix.isEmpty()) {
            newLine();
        }
        for (int j = 0; j < element.size(); ++j) {
            append(Text.literal(Strings.repeat(this.prefix, this.indentationLevel + 1)));
            append(new VerticalNbtTextFormatter(this.prefix, this.indentationLevel + 1).apply(element.get(j)));
            if (j != element.size() - 1) {
                append(ENTRY_SEPARATOR);
                newLine();
            }
        }
        newLine();
        append(Strings.repeat(this.prefix, this.indentationLevel));
        append(SQUARE_CLOSE_BRACKET);
    }

    @Override
    public void visitCompound(NbtCompound compound) {
        if (compound.isEmpty()) {
            append(Text.literal("{}"));
            return;
        }
        append(Text.literal(CURLY_OPEN_BRACKET));
        Collection<String> collection = compound.getKeys();
        if (LOGGER.isDebugEnabled()) {
            ArrayList<String> list = Lists.newArrayList(compound.getKeys());
            Collections.sort(list);
            collection = list;
        }
        if (!this.prefix.isEmpty()) {
            newLine();
        }
        Iterator iterator = collection.iterator();
        while (iterator.hasNext()) {
            String string = (String) iterator.next();
            append(Text.literal(Strings.repeat(this.prefix, this.indentationLevel + 1)).append(VerticalNbtTextFormatter.escapeName(string)).append(KEY_VALUE_SEPARATOR).append(SPACE));
            append(new VerticalNbtTextFormatter(this.prefix, this.indentationLevel + 1).apply(compound.get(string)));
            if (iterator.hasNext()) {
                append(ENTRY_SEPARATOR);
                newLine();
            }
        }
        if (!this.prefix.isEmpty()) {
            newLine();
            append(Strings.repeat(this.prefix, this.indentationLevel));
        }
        append(CURLY_CLOSE_BRACKET);
    }

    @Override
    public void visitEnd(NbtEnd element) {
        this.result = Text.literal("");
    }
}
