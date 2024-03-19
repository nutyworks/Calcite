package works.nuty.calcite.mixin.client;

import com.mojang.brigadier.suggestion.Suggestion;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.util.math.Rect2i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(ChatInputSuggestor.SuggestionWindow.class)
public interface SuggestionWindowFields {
    @Accessor
    Rect2i getArea();

    @Accessor
    List<Suggestion> getSuggestions();
}
