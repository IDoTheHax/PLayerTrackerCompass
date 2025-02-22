package net.idothehax.playertrackercompass;

import com.mojang.brigadier.CommandDispatcher;
import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class Playertrackercompass implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "playertrackercompass";
    private static final Map<UUID, UUID> TRACKED_PLAYERS = new HashMap<>();
    private static ServerPlayerEntity target;

    // Create the item
    public static final TrackingCompass TRACKING_COMPASS = new TrackingCompass(new Item.Settings().fireproof().maxCount(1), Items.COMPASS);

    @Override
    public void onInitialize() {
        // Register the compass item
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "tracking_compass"), TRACKING_COMPASS);

        // Register Commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            registerCommands(dispatcher);
        });

        // On player join
        ServerPlayConnectionEvents.JOIN.register((handler,sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            for (int i = 0; i < player.getInventory().size(); i++) {
                if (!(player.getInventory().getStack(i).getItem() == TRACKING_COMPASS)) {
                    ItemStack stack = new ItemStack(TRACKING_COMPASS);
                    player.getInventory().insertStack(stack);
                    if (target != null) {
                        updatePlayersCompass(target, target.getUuid());
                    } else {
                        return;
                    }
                }
            }
        });

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            // Check if the player had a tracking compass
            boolean hadCompass = false;
            for (int i = 0; i < oldPlayer.getInventory().size(); i++) {
                if (oldPlayer.getInventory().getStack(i).getItem() == TRACKING_COMPASS) {
                    hadCompass = true;
                    updateCompassTarget(oldPlayer.getInventory().getStack(i), target);
                    break;
                }
            }

            // If they had a compass but don't anymore, give them a new one
            if (hadCompass) {
                boolean hasCompass = false;
                for (int i = 0; i < newPlayer.getInventory().size(); i++) {
                    if (newPlayer.getInventory().getStack(i).getItem() == TRACKING_COMPASS) {
                        hasCompass = true;
                        updateCompassTarget(oldPlayer.getInventory().getStack(i), target);
                        break;
                    }
                }

                if (!hasCompass) {
                    ItemStack stack = new ItemStack(TRACKING_COMPASS);
                    updateCompassTarget(stack, target);
                    newPlayer.getInventory().insertStack(stack);
                }
            }
        });
    }

    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("trackercompass")
                .requires(source -> source.hasPermissionLevel(4)) // Require OP level 4
                .then(CommandManager.argument("player", EntityArgumentType.player())
                        .executes(context -> setupTrackingCompass(
                                context.getSource(),
                                EntityArgumentType.getPlayer(context, "player")
                        ))
                )
        );
    }

    public static int setupTrackingCompass(ServerCommandSource context, ServerPlayerEntity targetPlayer) {
        // Setup Tracking Compass
        if (!(context.getEntity() instanceof ServerPlayerEntity tracker)) {
            context.sendError(Text.literal("This command can only be used by a player."));
            return 0;
        }

        target = targetPlayer;
        updatePlayersCompass(targetPlayer, targetPlayer.getUuid());
        context.sendFeedback(() -> Text.literal("Now tracking player: " + targetPlayer.getName().getString()).setStyle(Style.EMPTY.withColor(Colors.LIGHT_RED)), true);
        return 1;
    }

    private static void updatePlayersCompass(ServerPlayerEntity targetPlayer, UUID targetUUID) {
        MinecraftServer server = targetPlayer.getServer();
        if (server == null) return;

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            TRACKED_PLAYERS.put(player.getUuid(), targetUUID);
            for (int i = 0; i < player.getInventory().size(); i++) {
                ItemStack stack = player.getInventory().getStack(i);
                if (stack.getItem() == TRACKING_COMPASS) {
                    updateCompassTarget(stack, targetPlayer);  // Update compass target
                        stack.set(DataComponentTypes.CUSTOM_NAME, Text.literal("Tracking: " + targetPlayer.getName().getString())
                                .setStyle(Style.EMPTY.withColor(Colors.LIGHT_RED).withBold(true)));  // Update compass name
                }
                break;
            }
        }
    }

    private static void updateCompassTarget(ItemStack compass, ServerPlayerEntity target) {
        ComponentMap.Builder builder = ComponentMap.builder();

        // Add all existing components from the compass
        builder.addAll(compass.getComponents());

        // Create a GlobalPos for the target player's current position
        GlobalPos targetPos = GlobalPos.create(target.getWorld().getRegistryKey(), target.getBlockPos());
        LodestoneTrackerComponent tracker = new LodestoneTrackerComponent(Optional.of(targetPos), true);

        // Apply the component map back to the compass
        compass.set(DataComponentTypes.LODESTONE_TRACKER, tracker);

        // Set custom name using correct method
        compass.set(DataComponentTypes.CUSTOM_NAME,
                Text.literal("Tracking: " + target.getName().getString())
                .setStyle(Style.EMPTY.withColor(Colors.ALTERNATE_WHITE)));
    }

    public static class TrackingCompass extends SimplePolymerItem implements PolymerItem {
        public TrackingCompass(Item.Settings settings, Item virtualItem) {
            super(settings, virtualItem);
        }

        @Override
        public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
            if (!world.isClient && user instanceof ServerPlayerEntity serverPlayer) {
                UUID targetUuid = TRACKED_PLAYERS.get(user.getUuid());
                if (targetUuid != null) {
                    ServerPlayerEntity targetPlayer = serverPlayer.getServer().getPlayerManager().getPlayer(targetUuid);
                    if (targetPlayer != null) {
                        updateCompassTarget(user.getStackInHand(hand), targetPlayer);
                        user.sendMessage(Text.literal("Tracking Pos Updated For: " + targetPlayer.getName().getString()), true);
                    } else {
                        user.sendMessage(Text.literal("Target player is offline.").setStyle(Style.EMPTY.withColor(Colors.RED)), true);
                    }
                } else {
                    user.sendMessage(Text.literal("Use /trackercompass to set a target first.").setStyle(Style.EMPTY.withColor(Colors.YELLOW)), true);
                }
            }
            return TypedActionResult.success(user.getStackInHand(hand));
        }

        @Override
        public boolean hasGlint(ItemStack stack) {
            return true;
        }
    }
}