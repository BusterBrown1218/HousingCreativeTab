package xyz.busterbrown1218.housingcreativetab.client;

import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import xyz.busterbrown1218.housingcreativetab.HousingCreativeTab;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HousingCreativeTabClient implements ClientModInitializer {
    public static final Set<Item> ALLOWED_1_8_9_ITEMS = new HashSet<>();
    public static final HashMap<Identifier, Pair<Identifier, Integer>> DATA_VALUES = new HashMap<>();
    public static boolean hideItems = false;
    public static boolean hasSetHideItems = false;

    @Override
    public void onInitializeClient() {
        FabricLoader.getInstance().getModContainer("housingcreativetab").ifPresent(modContainer -> {
            ResourceLoader.registerBuiltinPack(Identifier.fromNamespaceAndPath("housingcreativetab", "protool_ids"), modContainer, Component.literal("Protool IDs"), PackActivationType.NORMAL);
        });

        Path configPath = FabricLoader.getInstance().getConfigDir().resolve("housingcreativetab.json");
        if (!Files.exists(configPath)) {
            try {
                Files.createDirectories(configPath.getParent());
                Files.writeString(configPath, "{\"itemCommands\": true}");

                ItemCommands.registerCommands();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                JsonObject object = JsonParser.parseReader(Files.newBufferedReader(configPath)).getAsJsonObject();

                if (object.has("itemCommands") && object.get("itemCommands").getAsBoolean()) ItemCommands.registerCommands();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Gson gson = new Gson();
        loadItems(gson.fromJson(new InputStreamReader(HousingCreativeTab.class.getResourceAsStream("/assets/housingcreativetab/items.json"), StandardCharsets.UTF_8), JsonArray.class));
        loadDataValues(gson.fromJson(new InputStreamReader(HousingCreativeTab.class.getResourceAsStream("/assets/housingcreativetab/data_values.json"), StandardCharsets.UTF_8), JsonObject.class));
    }

    private void loadItems(JsonArray jsonArray) {
        for (JsonElement element : jsonArray) {
            Identifier id = Identifier.parse(element.getAsString());

            if (BuiltInRegistries.ITEM.containsKey(id)) {
                ALLOWED_1_8_9_ITEMS.add(BuiltInRegistries.ITEM.getValue(id));
            }
        }
    }

    private void loadDataValues(JsonObject jsonObject) {
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            JsonObject value = (JsonObject) entry.getValue();
            DATA_VALUES.put(Identifier.parse(entry.getKey()), new Pair<>(Identifier.parse(value.get("identifier").getAsString()), value.get("data_value").getAsInt()));
        }
    }
}
