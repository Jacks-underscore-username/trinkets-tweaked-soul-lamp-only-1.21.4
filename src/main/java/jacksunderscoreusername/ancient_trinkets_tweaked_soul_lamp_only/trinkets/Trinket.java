package jacksunderscoreusername.ancient_trinkets_tweaked_soul_lamp_only.trinkets;

import jacksunderscoreusername.ancient_trinkets_tweaked_soul_lamp_only.Main;
import net.minecraft.item.Item;

abstract public class Trinket extends Item {
    public Trinket(Settings settings) {
        super(settings);
    }

    abstract public String getId();

    public void initialize() {
        Main.LOGGER.warn("Trinket type \"{}\" has no initializeCreationHandler", this.getId());
    }
}


