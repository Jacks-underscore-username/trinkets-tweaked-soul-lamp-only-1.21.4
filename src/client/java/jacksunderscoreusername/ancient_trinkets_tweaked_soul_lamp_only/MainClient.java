package jacksunderscoreusername.ancient_trinkets_tweaked_soul_lamp_only;

import jacksunderscoreusername.ancient_trinkets_tweaked_soul_lamp_only.payloads.*;
import jacksunderscoreusername.ancient_trinkets_tweaked_soul_lamp_only.trinkets.soul_lamp.GhostEntity;
import jacksunderscoreusername.ancient_trinkets_tweaked_soul_lamp_only.trinkets.soul_lamp.RenderGhostsPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.util.Hand;

import java.util.HashSet;

public class MainClient implements ClientModInitializer {

    public static final HashSet<Integer> ghostsToRender = new HashSet<>();

    @Override
    public void onInitializeClient() {

        ClientPlayNetworking.registerGlobalReceiver(SwingHandPayload.ID, (payload, context) -> context.client().execute(() -> context.player().swingHand(payload.isMainHand() ? Hand.MAIN_HAND : Hand.OFF_HAND)));

        EntityRendererRegistry.register(GhostEntity.GHOST, GhostEntityRenderer::new);

        ClientPlayNetworking.registerGlobalReceiver(RenderGhostsPayload.ID, (payload, context) -> {
            ghostsToRender.clear();
            for (var x : ((NbtIntArray) payload.tag()).getIntArray())
                ghostsToRender.add(x);
        });
    }
}