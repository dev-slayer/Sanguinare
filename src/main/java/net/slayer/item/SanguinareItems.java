package net.slayer.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.slayer.SanguinareMain;

public class SanguinareItems {
    public static final Item MOON = registerItem("moon", new Item(new FabricItemSettings().maxCount(0)));
    public static final Item ANCIENT_BLOOD = registerItem("ancient_blood", new Item(new FabricItemSettings().maxCount(1)));
    public static final Item ACTIVATED_ANCIENT_BLOOD = registerItem("activated_ancient_blood", new AncientBloodBottleItem(new FabricItemSettings().maxCount(1).food(FoodComponents.ACTIVATED_ANCIENT_BLOOD)));
    public static final Item UMBRELLA = registerItem("umbrella", new Item(new FabricItemSettings().maxCount(1)));
    public static final Item BLOOD_BOTTLE = registerItem("blood_bottle", new BloodBottleItem(new FabricItemSettings().maxCount(1).maxDamage(8)));



    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(SanguinareMain.MOD_ID, name), item);
    }

    private static void addItemsToIngredientItemGroup(FabricItemGroupEntries entries) {
        entries.add(ANCIENT_BLOOD);
        entries.add(ACTIVATED_ANCIENT_BLOOD);
        entries.add(UMBRELLA);
        entries.add(BLOOD_BOTTLE);
    }

    public static void registerItems() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(SanguinareItems::addItemsToIngredientItemGroup);
    }
}
