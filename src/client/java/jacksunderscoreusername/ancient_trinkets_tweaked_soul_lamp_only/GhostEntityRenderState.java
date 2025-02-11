package jacksunderscoreusername.ancient_trinkets_tweaked_soul_lamp_only;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.ArmedEntityRenderState;

@Environment(EnvType.CLIENT)
public class GhostEntityRenderState extends ArmedEntityRenderState {
    public boolean charging;
    public boolean shouldRender;
}
