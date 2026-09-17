package xyz.busterbrown1218.housingcreativetab.client;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import org.jetbrains.annotations.UnknownNullability;
import xyz.busterbrown1218.housingcreativetab.utils.*;

import java.util.ArrayList;
import java.util.List;

//? if >= 26.2 {
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;
//?} else {
/*import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
*///?}
public class ItemCommands {
    private static List<Component> loreClipboard;

    public static void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, unused) -> {
            dispatcher.register(literal("rename")
                    .then(argument("name", StringArgumentType.greedyString())
                            .executes(itemCommand((context, item) -> {
                                item.set(DataComponents.CUSTOM_NAME, Component.literal(StringArgumentType.getString(context, "name").replaceAll("&([a-f0-9klmnor])", "§$1")));
                                return item;
                            }))));

            dispatcher.register(literal("itemmodel")
                    .then(argument("id", ClientIdentifierArgumentType.identifier())
                            .suggests(new ItemSuggestionProvider())
                            .executes(itemCommand(((context, item) -> {
                                item.set(DataComponents.ITEM_MODEL, ClientIdentifierArgumentType.getIdentifier(context, "id"));
                                return item;
                            })))));

            dispatcher.register(literal("damage")
                    .then(argument("amount", IntegerArgumentType.integer(0))
                            .executes(itemCommand(((context, item) -> {
                                item.set(DataComponents.DAMAGE, IntegerArgumentType.getInteger(context, "amount"));
                                return item;
                            })))));

            dispatcher.register(literal("unbreakable")
                    .then(argument("value", BoolArgumentType.bool())
                            .executes(itemCommand((context, item) -> {
                                if (BoolArgumentType.getBool(context, "value")) item.set(DataComponents.UNBREAKABLE, Unit.INSTANCE);
                                else item.remove(DataComponents.UNBREAKABLE);
                                return item;
                            }))));

            dispatcher.register(literal("lore")
                    .then(literal("add")
                            .then(argument("value", StringArgumentType.greedyString())
                                    .executes(itemCommand((context, item) -> {
                                        ItemLore lore = item.get(DataComponents.LORE);

                                        List<Component> newLines = new ArrayList<>();
                                        if (lore != null) {
                                            newLines.addAll(lore.lines());
                                        }

                                        newLines.add(Component.literal(StringArgumentType.getString(context, "value").replaceAll("&([a-f0-9klmnor])", "§$1")));

                                        item.set(DataComponents.LORE, new ItemLore(newLines));
                                        return item;
                                    }))))

                    .then(literal("remove")
                            .then(argument("index", IntegerArgumentType.integer(0))
                                    .executes(itemCommand((context, item) -> {
                                        ItemLore lore = item.get(DataComponents.LORE);
                                        if (lore == null || lore.lines().isEmpty()) return item;

                                        List<Component> newLines = new ArrayList<>(lore.lines());
                                        int index = IntegerArgumentType.getInteger(context, "index");

                                        if (index < newLines.size()) {
                                            newLines.remove(index);
                                            item.set(DataComponents.LORE, new ItemLore(newLines));
                                        }
                                        return item;
                                    }))))

                    .then(literal("edit")
                            .then(argument("index", IntegerArgumentType.integer(0))
                                    .then(argument("value", StringArgumentType.greedyString())
                                            .executes(itemCommand(((context, item) -> {
                                                ItemLore lore = item.get(DataComponents.LORE);
                                                if (lore == null || lore.lines().isEmpty()) return item;

                                                List<Component> newLines = new ArrayList<>(lore.lines());
                                                int index = IntegerArgumentType.getInteger(context, "index");

                                                if (index < newLines.size()) {
                                                    newLines.set(index, Component.literal(StringArgumentType.getString(context, "value").replaceAll("&([a-f0-9klmnor])", "§$1")));
                                                    item.set(DataComponents.LORE, new ItemLore(newLines));
                                                }
                                                return item;
                                            }))))))
            );

            dispatcher.register(literal("itemtype")
                    .then(argument("id", ClientIdentifierArgumentType.identifier())
                            .suggests(new RestrictedItemSuggestionProvider())
                            .executes(itemCommand((context, item) -> {
                                item = item.transmuteCopy(new ItemStack(BuiltInRegistries.ITEM.getValue(ClientIdentifierArgumentType.getIdentifier(context, "id"))).getItem());
                                return item;
                            }))));

            dispatcher.register(literal("armorcolor")
                    .then(argument("hex", StringArgumentType.word())
                            .executes(itemCommand(((context, item) -> {
                                String value = StringArgumentType.getString(context, "hex");
                                if (!value.matches("[a-fA-F0-9]{6}")) {
                                    Compat.sendPlayerMessage(context.getSource().getPlayer(), Component.literal("Invalid hex format! Digits must be (0-9, a-f). §8Example: §fFFFFFF"));
                                    return item;
                                }
                                int color = Integer.parseInt(value, 16);
                                item.set(DataComponents.DYED_COLOR, new DyedItemColor(color));
                                return item;
                            })))));

            dispatcher.register(literal("armortrim")
                    .then(argument("template", ClientIdentifierArgumentType.identifier())
                            .suggests(new TrimSuggestionProvider())
                            .then(argument("material", ClientIdentifierArgumentType.identifier())
                                    .suggests(new TrimMaterialSuggestionProvider())
                                    .executes(itemCommand(((context, item) -> {
                                        Identifier templateId = ClientIdentifierArgumentType.getIdentifier(context, "template");
                                        Identifier materialId = ClientIdentifierArgumentType.getIdentifier(context, "material");

                                        Registry<TrimMaterial> materialRegistry = Compat.getLevel(context).registryAccess().lookup(Registries.TRIM_MATERIAL).get();
                                        Registry<TrimPattern> patternRegistry = Compat.getLevel(context).registryAccess().lookup(Registries.TRIM_PATTERN).get();

                                        TrimPattern patternEntry = patternRegistry.getValue(templateId);
                                        TrimMaterial materialEntry = materialRegistry.getValue(materialId);

                                        if (patternEntry == null || materialEntry == null) return item;

                                        item.set(DataComponents.TRIM, new ArmorTrim(materialRegistry.wrapAsHolder(materialEntry), patternRegistry.wrapAsHolder(patternEntry)));
                                        return item;
                                    }))))));

            dispatcher.register(literal("count")
                    .then(argument("amount", IntegerArgumentType.integer(1, 64))
                            .executes(itemCommand((context, item) -> {
                                item.setCount(IntegerArgumentType.getInteger(context, "amount"));
                                return item;
                            }))));

            dispatcher.register(literal("copyLore")
                    .executes(itemCommand((context, item) -> {
                        loreClipboard = new ArrayList<>(item.get(DataComponents.LORE).lines());
                        return item;
                    })));

            dispatcher.register(literal("pasteLore")
                    .executes(itemCommand(((context, item) -> {
                        item.set(DataComponents.LORE, new ItemLore(loreClipboard));
                        return item;
                    }))));
        });
    }

    private static boolean cannotEdit(@UnknownNullability Player player) {
        if (!player.isCreative()) {
            Compat.sendPlayerMessage(player, Component.literal("You must be in creative to edit an item!"));
            return true;
        } else if (player.getActiveItem().isEmpty()) {
            Compat.sendPlayerMessage(player, Component.literal("You must be holding an item to edit!"));
            return true;
        }
        return false;
    }

    private static void setItem(Minecraft client, Player player, ItemStack item) {
        if (client.getConnection() != null) {
            client.getConnection().send(
                    new ServerboundSetCreativeModeSlotPacket(player.getInventory().getSelectedSlot() + 36, item)
            );
        }
    }

    @FunctionalInterface
    interface ItemCommandAction {
        ItemStack run(CommandContext<FabricClientCommandSource> context, ItemStack item);
    }

    private static Command<FabricClientCommandSource> itemCommand(ItemCommandAction action) {
        return context -> {
            Minecraft client = Minecraft.getInstance();
            Player player = client.player;

            if (cannotEdit(player)) return Command.SINGLE_SUCCESS;

            ItemStack item = player.getActiveItem();

            item = action.run(context, item);

            setItem(client, player, item);

            return Command.SINGLE_SUCCESS;
        };
    }
}
