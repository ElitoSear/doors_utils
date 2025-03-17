package elito.doors;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static net.minecraft.server.command.CommandManager.literal;

public class Doors implements ModInitializer {

    String MOD_ID = "doors";
    Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {


        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (!world.isClient()) {
                ItemStack stack = player.getStackInHand(hand);

                if (stack.contains(DataComponentTypes.CUSTOM_DATA)) {
                    NbtCompound nbt = Objects.requireNonNull(stack.get(DataComponentTypes.CUSTOM_DATA)).copyNbt();

                    String command = nbt.getString("command");

                    if (nbt.contains("command")) {
                        CommandManager commandManager = Objects.requireNonNull(player.getServer()).getCommandManager();
                        ServerCommandSource commandSource = new ServerCommandSource(
                                Objects.requireNonNull(player.getServer()),
                                player.getPos(),
                                player.getRotationClient(),
                                world instanceof ServerWorld ? (ServerWorld) world : null,
                                4,
                                player.getName().getString(),
                                player.getDisplayName(),
                                player.getServer(),
                                player
                        );
                        commandManager.executeWithPrefix(commandSource, command);

                        return ActionResult.SUCCESS;
                    }
                }

            }
            return ActionResult.PASS;
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("doors")
                .then(
                        literal("raycast")
                                .then(
                                        literal("seek_eye")
                                                .executes(
                                                        context ->  {
                                                            ServerCommandSource source = context.getSource();
                                                            ServerWorld world = source.getWorld();

                                                            Vec3d origin = source.getPosition();
                                                            Vec2f rotation = source.getRotation();

                                                            BlockHitResult hit = Raycast.cast(origin, rotation, world);

                                                            Vec3d on = hit.getPos();
                                                            Vec2f newRotation = Raycast.getRotationWhenCollided(hit.getSide());

                                                            on = Raycast.moveForward(on, newRotation, 0.05);

                                                            DisplayEntity.ItemDisplayEntity seekEye = new DisplayEntity.ItemDisplayEntity(EntityType.ITEM_DISPLAY, world);
                                                            seekEye.updatePositionAndAngles(on.x, on.y, on.z, newRotation.x, newRotation.y);
                                                            world.spawnEntity(seekEye);

                                                            ItemStack item = new ItemStack(Items.STICK);
                                                            item.set(DataComponentTypes.ITEM_MODEL, Identifier.of("doors:entity/seek/eye"));
                                                            seekEye.setItemStack(item);
                                                            seekEye.addCommandTag("seek_eye");
                                                            return 1;
                                                        }
                                                )
                                )
                )
        ));

    }
}
