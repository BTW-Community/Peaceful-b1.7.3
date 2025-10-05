import hmi.tabs.Tab;
import hmi.tabs.mods.TabGenericBlock;
import net.minecraft.src.*;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

@SuppressWarnings("unused")
public class mod_BTWPeaceful extends BaseMod {
    public Block blockFossil;

    public mod_BTWPeaceful() {
        blockFossil = new FCBlockFossil(220);
        ModLoader.AddName(blockFossil, "Fossil");
        ModLoader.RegisterBlock(blockFossil);
        blockFossil.blockIndexInTexture = ModLoader.addOverride("/terrain.png", "/btwmodtex/fossil.png");
    }

    @Override
    public String Version() {
        return "1.0.0";
    }

    /* Mod Menu Information */

    public String Name() {
        return "BTW Peaceful Addon";
    }

    public String Description() {
        return "An addon for Better Than Wolves that adds mob drops for use in peaceful";
    }

    @Override
    public void GenerateSurface(World world, Random rand, int chunkX, int chunkZ) {
        for(int i = 0; i < 20; ++i) {
            int randPosX = chunkX + rand.nextInt(16);
            int randPosY = rand.nextInt(128);
            int randPosZ = chunkZ + rand.nextInt(16);
            (new WorldGenMinable(blockFossil.blockID, 3)).generate(world, rand, randPosX, randPosY, randPosZ);
        }
    }

    @Override
    public void ModsLoaded() {
        FCCraftingManagerCauldron.getInstance().AddRecipe(
                new ItemStack(Item.slimeBall),
                new ItemStack[]{ new ItemStack(mod_FCBetterThanWolves.fcGlue), new ItemStack(Item.dyePowder, 1, 2) });

        try {
            FCCraftingManagerCauldron cm = FCCraftingManagerCauldron.getInstance();
            Field recipesField = FCCraftingManagerBulk.class.getDeclaredField("m_recipes");
            recipesField.setAccessible(true);
            ArrayList recipes = (ArrayList)recipesField.get(cm);
            ArrayList inputs = new ArrayList();
            inputs.add(new ItemStack(mod_FCBetterThanWolves.fcHellfireDust, 4));
            inputs.add(new ItemStack(Item.sugar, 4));
            recipes.add(0, new FCCraftingManagerBulkRecipe(new ItemStack(Item.gunpowder, 3), inputs));
        } catch (Exception e) {}

        ArrayList<Tab> tabs = mod_HowManyItems.getTabs();
        for (Tab tab : tabs) {
            if (tab.getTabItem().getItem().shiftedIndex == mod_FCBetterThanWolves.fcMillStone.blockID) {
                try {
                    Class tabClass = tab.getClass();
                    Field recipesField = tabClass.getDeclaredField("recipesComplete");
                    recipesField.setAccessible(true);
                    Map recipes = (Map) recipesField.get(tab);
                    recipes.put(new ItemStack(Item.porkRaw), new ItemStack(Item.dyePowder, 5, 15));
                } catch (Exception e) {}
                break;
            }
        }
    }
}
