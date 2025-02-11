package jacksunderscoreusername.ancient_trinkets_tweaked_soul_lamp_only.trinkets;

import jacksunderscoreusername.ancient_trinkets_tweaked_soul_lamp_only.Main;
import jacksunderscoreusername.ancient_trinkets_tweaked_soul_lamp_only.trinkets.soul_lamp.SoulLamp;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.*;

import java.util.function.Function;

public class Trinkets {
    public static final Trinket SOUL_LAMP = register(SoulLamp.id, SoulLamp::new, SoulLamp.getSettings());

    public static Trinket register(String id, Function<Item.Settings, Item> factory, Item.Settings settings) {
        final RegistryKey<Item> registryKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Main.MOD_ID, id));
        return (Trinket) Items.register(registryKey, factory, settings);
    }

    public static void initialize() {
        AbstractModeDataComponent.initialize();
        ChargesDataComponent.initialize();
        TrinketsItemGroup.initialize();
        SOUL_LAMP.initialize();
    }
}
