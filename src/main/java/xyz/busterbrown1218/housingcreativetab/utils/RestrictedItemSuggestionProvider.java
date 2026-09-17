package xyz.busterbrown1218.housingcreativetab.utils;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import xyz.busterbrown1218.housingcreativetab.client.HousingCreativeTabClient;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class RestrictedItemSuggestionProvider implements SuggestionProvider<FabricClientCommandSource> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<FabricClientCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {

        Collection<String> itemIds;

        if (Minecraft.getInstance().level.getScoreboard().getObjectiveNames().contains("housing")) {
            itemIds = HousingCreativeTabClient.ALLOWED_1_8_9_ITEMS.stream()
                    .filter(item -> !HousingCreativeTabClient.DATA_VALUES.containsKey(BuiltInRegistries.ITEM.getKey(item)))
                    .map(item -> BuiltInRegistries.ITEM.getKey(item).toString()).collect(Collectors.toSet());
        } else {
            itemIds = BuiltInRegistries.ITEM.keySet().stream().map(id -> id.getNamespace() + ":" + id.getPath()).collect(Collectors.toSet());
        }

        for (String id : itemIds) {
            builder.suggest(id);
        }

        return builder.buildFuture();
    }
}
