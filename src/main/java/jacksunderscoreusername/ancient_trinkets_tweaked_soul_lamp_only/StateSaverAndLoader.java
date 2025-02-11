package jacksunderscoreusername.ancient_trinkets_tweaked_soul_lamp_only;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

import java.io.*;
import java.util.*;

public class StateSaverAndLoader extends PersistentState {

    public static Object soulLampGroupsSync = new Object();

    public static class StoredData implements Serializable {
        @Serial
        private static final long serialVersionUID = 4284636239471626404L;

        public static class soulLampEntry {
            public soulLampEntry(
                    UUID playerUuid, HashSet<UUID> targets, HashSet<UUID> priorityTargets,
                    HashSet<UUID> members, int mode) {
                this.playerUuid = playerUuid;
                this.targets = targets;
                this.priorityTargets = priorityTargets;
                this.members = members;
                this.mode = mode;
            }

            public UUID playerUuid;
            public HashSet<UUID> targets;
            public HashSet<UUID> priorityTargets;
            public HashSet<UUID> members;
            public int mode;
        }

        public HashMap<UUID, soulLampEntry> soulLampGroups = new HashMap<>();
    }

    public StoredData data = new StoredData();

    public static StateSaverAndLoader createFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        StateSaverAndLoader state = new StateSaverAndLoader();

        StoredData data = state.data;

        if (tag.contains("soulLampGroups")) {
            NbtCompound compound = tag.getCompound("soulLampGroups");
            for (var key : compound.getKeys()) {
                NbtCompound subCompound = compound.getCompound(key);
                HashSet<UUID> targets = new HashSet<>();
                for (var subKey : subCompound.getCompound("targets").getKeys())
                    targets.add(UUID.fromString(subKey));
                HashSet<UUID> priorityTargets = new HashSet<>();
                for (var subKey : subCompound.getCompound("priorityTargets").getKeys())
                    priorityTargets.add(UUID.fromString(subKey));
                HashSet<UUID> members = new HashSet<>();
                for (var subKey : subCompound.getCompound("members").getKeys())
                    members.add(UUID.fromString(subKey));
                data.soulLampGroups.put(UUID.fromString(key), new StoredData.soulLampEntry(
                        subCompound.getUuid("playerUuid"),
                        targets,
                        priorityTargets,
                        members,
                        subCompound.getInt("mode")
                ));
            }
        }

        return state;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        if (data.soulLampGroups != null) {
            NbtCompound compound = new NbtCompound();
            for (var entry : data.soulLampGroups.entrySet()) {
                NbtCompound subCompound = new NbtCompound();
                subCompound.putUuid("playerUuid", entry.getValue().playerUuid);
                NbtCompound targetsCompound = new NbtCompound();
                for (var target : entry.getValue().targets)
                    targetsCompound.putByte(target.toString(), (byte) 1);
                subCompound.put("targets", targetsCompound);
                NbtCompound priorityTargetsCompound = new NbtCompound();
                for (var target : entry.getValue().priorityTargets)
                    priorityTargetsCompound.putByte(target.toString(), (byte) 1);
                subCompound.put("priorityTargets", targetsCompound);
                NbtCompound membersCompound = new NbtCompound();
                for (var target : entry.getValue().members)
                    membersCompound.putByte(target.toString(), (byte) 1);
                subCompound.put("members", membersCompound);
                subCompound.putInt("mode", entry.getValue().mode);
                compound.put(entry.getKey().toString(), subCompound);
            }
            nbt.put("soulLampGroups", compound);
        }

        return nbt;
    }

    private static final Type<StateSaverAndLoader> type = new Type<>(StateSaverAndLoader::new,
            StateSaverAndLoader::createFromNbt,
            null
    );

    public static StateSaverAndLoader getServerState(MinecraftServer server) {
        PersistentStateManager persistentStateManager = Objects.requireNonNull(server.getWorld(World.OVERWORLD)).getPersistentStateManager();

        StateSaverAndLoader state = persistentStateManager.getOrCreate(type, Main.MOD_ID);

        ServerLifecycleEvents.BEFORE_SAVE.register((server2, flush, force) -> state.markDirty());

        return state;
    }
}
