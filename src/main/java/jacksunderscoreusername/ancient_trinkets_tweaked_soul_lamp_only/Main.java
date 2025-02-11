package jacksunderscoreusername.ancient_trinkets_tweaked_soul_lamp_only;

import jacksunderscoreusername.ancient_trinkets_tweaked_soul_lamp_only.payloads.*;
import jacksunderscoreusername.ancient_trinkets_tweaked_soul_lamp_only.trinkets.Trinkets;
import jacksunderscoreusername.ancient_trinkets_tweaked_soul_lamp_only.trinkets.soul_lamp.RenderGhostsPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main implements ModInitializer {
    public static final String MOD_ID = "ancient_trinkets_tweaked_soul_lamp_only";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static MinecraftServer server = null;

    public static StateSaverAndLoader state = null;

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register((s) -> {
            server = s;
            state = StateSaverAndLoader.getServerState(s);
        });

        Trinkets.initialize();

        PayloadTypeRegistry.playS2C().register(SwingHandPayload.ID, SwingHandPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(RenderGhostsPayload.ID, RenderGhostsPayload.CODEC);
    }
}