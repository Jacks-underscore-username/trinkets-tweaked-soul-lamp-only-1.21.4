package jacksunderscoreusername.ancient_trinkets_tweaked_soul_lamp_only.trinkets.soul_lamp;

import jacksunderscoreusername.ancient_trinkets_tweaked_soul_lamp_only.Main;
import jacksunderscoreusername.ancient_trinkets_tweaked_soul_lamp_only.StateSaverAndLoader;
import jacksunderscoreusername.ancient_trinkets_tweaked_soul_lamp_only.events.LivingEntityDeathEvent;
import jacksunderscoreusername.ancient_trinkets_tweaked_soul_lamp_only.payloads.SwingHandPayload;
import jacksunderscoreusername.ancient_trinkets_tweaked_soul_lamp_only.trinkets.*;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class SoulLamp extends Trinket implements TrinketWithModes {
    public static String id = "soul_lamp";
    public static String name = "Soul Lamp";

    public String getId() {
        return id;
    }

    public static Settings getSettings() {
        Settings settings = new Settings();
        settings = settings
                .maxCount(1)
                .rarity(Rarity.EPIC)
                .component(AbstractModeDataComponent.ABSTRACT_MODE, new AbstractModeDataComponent.AbstractMode(0))
                .component(ChargesDataComponent.CHARGES, new ChargesDataComponent.Charges(0));
        return settings;
    }

    public SoulLamp(Settings settings) {
        super(settings);
    }

    @Override
    public int getMaxModes() {
        return 4;
    }

    @Override
    public String getModeName(int mode) {
        return switch (mode) {
            case 0 -> "Manual Targeting";
            case 1 -> "Entity Type Targeting";
            case 2 -> "Current Area Targeting";
            case 3 -> "Continuous Area Targeting";
            default -> "";
        };
    }

    public void initialize() {
        GhostEntity.initialize();
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, killingEntity, deadEntity) -> {
            if (killingEntity instanceof ServerPlayerEntity player) {
                if (player.getMainHandStack().isOf(Trinkets.SOUL_LAMP)) {
                    player.getMainHandStack().set(ChargesDataComponent.CHARGES, new ChargesDataComponent.Charges(player.getMainHandStack().getOrDefault(ChargesDataComponent.CHARGES, new ChargesDataComponent.Charges(0)).charges() + 1));
                } else if (player.getOffHandStack().isOf(Trinkets.SOUL_LAMP)) {
                    player.getOffHandStack().set(ChargesDataComponent.CHARGES, new ChargesDataComponent.Charges(player.getOffHandStack().getOrDefault(ChargesDataComponent.CHARGES, new ChargesDataComponent.Charges(0)).charges() + 1));
                }
            }
        });
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient) {
            return ActionResult.PASS;
        }
        ItemStack itemStack = hand.equals(Hand.MAIN_HAND) ? user.getMainHandStack() : user.getOffHandStack();
        if (!itemStack.isOf(Trinkets.SOUL_LAMP)) {
            return ActionResult.PASS;
        }
        if (user.isSneaking()) {
            nextMode(itemStack, (ServerPlayerEntity) user);
            return ActionResult.SUCCESS;
        }

        StateSaverAndLoader.StoredData.soulLampEntry oldGroup = Main.state.data.soulLampGroups.get(user.getUuid());
        if (oldGroup != null && getMode(itemStack) != oldGroup.mode) {
            oldGroup.mode = getMode(itemStack);
            if (getMode(itemStack) == 2)
                for (var newTarget : world.getEntitiesByClass(LivingEntity.class, new Box(user.getPos().subtract(GhostEntity.ENTITY_SEARCH_RADIUS), user.getPos().add(GhostEntity.ENTITY_SEARCH_RADIUS)), entity -> !oldGroup.playerUuid.equals(entity.getUuid()) && !oldGroup.members.contains(entity.getUuid()) && !oldGroup.targets.contains(entity.getUuid())))
                    oldGroup.targets.add(newTarget.getUuid());

            world.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1.0F, 1.0F);
            ServerPlayNetworking.send(Objects.requireNonNull(Main.server.getPlayerManager().getPlayer(user.getUuid())), new SwingHandPayload(hand.equals(Hand.MAIN_HAND)));
            return ActionResult.SUCCESS;
        }
        int charges = itemStack.getOrDefault(ChargesDataComponent.CHARGES, new ChargesDataComponent.Charges(0)).charges();
        if (charges == 0) {
            return ActionResult.PASS;
        }
        itemStack.set(ChargesDataComponent.CHARGES, new ChargesDataComponent.Charges(charges - 1));
        StateSaverAndLoader.StoredData.soulLampEntry group = Main.state.data.soulLampGroups.getOrDefault(user.getUuid(), new StateSaverAndLoader.StoredData.soulLampEntry(user.getUuid(), new HashSet<>(), new HashSet<>(), new HashSet<>(), getMode(itemStack)));
        Main.state.data.soulLampGroups.put(user.getUuid(), group);
        GhostEntity ghost = GhostEntity.GHOST.spawn((ServerWorld) world, user.getBlockPos(), SpawnReason.MOB_SUMMONED);
        assert ghost != null;
        group.members.add(ghost.getUuid());
        ghost.group = group;
        if (getMode(itemStack) == 2)
            for (var newTarget : world.getEntitiesByClass(LivingEntity.class, new Box(user.getPos().subtract(GhostEntity.ENTITY_SEARCH_RADIUS), user.getPos().add(GhostEntity.ENTITY_SEARCH_RADIUS)), entity -> !group.playerUuid.equals(entity.getUuid()) && !group.members.contains(entity.getUuid()) && !group.targets.contains(entity.getUuid())))
                group.targets.add(newTarget.getUuid());
        world.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_ELDER_GUARDIAN_CURSE, SoundCategory.PLAYERS, 1.0F, 0.5F);
        ServerPlayNetworking.send(Objects.requireNonNull(Main.server.getPlayerManager().getPlayer(user.getUuid())), new SwingHandPayload(hand.equals(Hand.MAIN_HAND)));
        return ActionResult.SUCCESS;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        Formatting color = Formatting.LIGHT_PURPLE;

        int mode = Objects.requireNonNull(stack.get(AbstractModeDataComponent.ABSTRACT_MODE)).mode();

        ChargesDataComponent.Charges data = stack.getOrDefault(ChargesDataComponent.CHARGES, new ChargesDataComponent.Charges(0));

        tooltip.add(Text.literal("Mode: ").append(Text.literal(getModeName(mode)).formatted(color, Formatting.ITALIC)));
        tooltip.add(Text.literal("Charges: ").append(Text.literal(String.valueOf(data.charges())).formatted(color, Formatting.ITALIC)));

        tooltip.add(Text.literal("Hold this while killing an entity to hold their soul,").formatted(color));
        tooltip.add(Text.literal("then right click to release it").formatted(color));
    }
}