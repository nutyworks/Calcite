package works.nuty.calcite.mixin.client;

import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(SuggestionsBuilder.class)
public interface SuggestionsBuilderFields {
    @Accessor(remap = false)
    List<Suggestion> getResult();
}
