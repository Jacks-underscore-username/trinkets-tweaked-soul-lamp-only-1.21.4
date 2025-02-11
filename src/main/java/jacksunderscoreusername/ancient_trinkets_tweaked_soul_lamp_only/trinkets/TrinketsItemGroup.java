package jacksunderscoreusername.ancient_trinkets_tweaked_soul_lamp_only.trinkets;

import jacksunderscoreusername.ancient_trinkets_tweaked_soul_lamp_only.Main;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class TrinketsItemGroup {

    public static final ItemGroup TRINKETS_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(Trinkets.SOUL_LAMP))
            .displayName(Text.translatable("itemGroup." + Main.MOD_ID + ".trinket_group"))
            .entries((context, entries) -> {
                entries.add(Trinkets.SOUL_LAMP);
            })
            .build();

    public static void initialize() {
        Registry.register(Registries.ITEM_GROUP, Identifier.of(Main.MOD_ID, "trinket_group"), TRINKETS_GROUP);
    }
}
