/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Auxiliary.Interfaces.NBTTile;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Render.ISBRH.CrystalRenderer;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;

public class ChromaItemRenderer implements IItemRenderer {


	public ChromaItemRenderer() {

	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		float a = 0; float b = 0;

		RenderBlocks rb = (RenderBlocks)data[0];
		if (type == type.ENTITY) {
			a = -0.5F;
			b = -0.5F;
			GL11.glScalef(0.5F, 0.5F, 0.5F);
		}
		if (item.getItemDamage() >= ChromaTiles.TEList.length)
			return;
		ChromaTiles machine = item.getItem() == ChromaItems.RIFT.getItemInstance() ? ChromaTiles.RIFT : ChromaTiles.TEList[item.getItemDamage()];
		boolean entity = type == ItemRenderType.ENTITY || type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON;
		if (machine.hasRender() && !machine.hasBlockRender()) {
			TileEntity te = machine.createTEInstanceForRender();
			if (machine.hasNBTVariants()) {
				((NBTTile)te).setDataFromItemStackTag(item);
			}
			TileEntityRendererDispatcher.instance.renderTileEntityAt(te, a, -0.1D, b, entity ? -1 : 0);
		}
		else {
			ReikaTextureHelper.bindTerrainTexture();
			if (entity && machine == ChromaTiles.CRYSTAL) {
				GL11.glTranslated(-0.5, -0.5, -0.5);
				CrystalRenderer.renderAllArmsInInventory = true;
			}
			rb.renderBlockAsItem(machine.getBlock(), machine.getBlockMetadata(), 1);
			CrystalRenderer.renderAllArmsInInventory = false;
		}
	}
}
