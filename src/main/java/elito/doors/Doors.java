package elito.doors;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.block.ShapeContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import static net.minecraft.server.command.CommandManager.*;
import net.minecraft.server.command.SummonCommand;

public class Doors implements ModInitializer {

    @Override
    public void onInitialize() {

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
                                                            Vec3d destination = origin.add(Vec3d.fromPolar(source.getRotation()).add(128));

                                                            BlockHitResult blockHitResult = world.raycast(new RaycastContext(origin, destination, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, ShapeContext.absent()));
                                                            Vec3d on = blockHitResult.getPos();

                                                            DisplayEntity.ItemDisplayEntity seekEye = SummonCommand.summon(context, )
                                                            ItemStack item = new ItemStack(Items.STICK);
                                                            item.set(DataComponentTypes.ITEM_MODEL, Identifier.of("doors:entity/seek/eye"));
                                                            seekEye.setItemStack(item);
                                                            seekEye.setPosition(on);
                                                            seekEye.addCommandTag("seek_eye");
                                                            return 1;
                                                        }
                                                )
                                )
                )
        ));

    }
}
