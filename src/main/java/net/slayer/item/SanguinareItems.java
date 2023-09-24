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
    public static final Item ANCIENT_BLOOD = registerItem("ancient_blood", new Item(new FabricItemSettings().maxCount(1)));
    public static final Item ACTIVATED_ANCIENT_BLOOD = registerItem("activated_ancient_blood", new AncientBloodBottleItem(new FabricItemSettings().maxCount(1).food(FoodComponents.ACTIVATED_ANCIENT_BLOOD)));


    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(SanguinareMain.MOD_ID, name), item);
    }

    private static void addItemsToIngredientItemGroup(FabricItemGroupEntries entries) {
        entries.add(ANCIENT_BLOOD);
    }

    public static void registerItems() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(SanguinareItems::addItemsToIngredientItemGroup);
    }
}
