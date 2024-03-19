package works.nuty.calcite.mixin.client;

import com.mojang.brigadier.ParseResults;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.command.CommandSource;
import net.minecraft.text.OrderedText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(ChatInputSuggestor.class)
public interface ChatInputSuggestorFields {
    @Accessor
    ChatInputSuggestor.SuggestionWindow getWindow();

    @Accessor
    int getMaxSuggestionSize();

    @Accessor
    ParseResults<CommandSource> getParse();

    @Accessor
    List<OrderedText> getMessages();

    @Accessor
    boolean getChatScreenSized();
}
