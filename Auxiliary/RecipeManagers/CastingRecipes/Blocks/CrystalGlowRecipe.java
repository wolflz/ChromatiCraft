package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import Reika.ChromatiCraft.Auxiliary.Interfaces.CoreRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;

public class CrystalGlowRecipe extends CastingRecipe implements CoreRecipe {

	public CrystalGlowRecipe(ItemStack out, IRecipe recipe) {
		super(out, recipe);
	}

	@Override
	public int getTypicalCraftedAmount() {
		return 8;
	}

	@Override
	public int getPenaltyThreshold() {
		return super.getPenaltyThreshold()/2;
	}

	@Override
	public float getPenaltyMultiplier() {
		return 2;
	}

	@Override
	public int getNumberProduced() {
		return 16;
	}
}
