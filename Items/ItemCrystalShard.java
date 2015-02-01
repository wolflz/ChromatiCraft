/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaFontRenderer;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Auxiliary.Interfaces.TieredItem;
import Reika.ChromatiCraft.Base.ItemCrystalBasic;
import Reika.ChromatiCraft.Block.BlockActiveChroma.TileEntityChroma;
import Reika.ChromatiCraft.Magic.CrystalPotionController;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityFlareFX;
import Reika.ChromatiCraft.Render.Particle.EntityRuneFX;
import Reika.DragonAPI.Interfaces.AnimatedSpritesheet;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaArrayHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemCrystalShard extends ItemCrystalBasic implements AnimatedSpritesheet, TieredItem {

	private static final int[] offsets;

	public ItemCrystalShard(int tex) {
		super(tex);
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem ei) {
		int x = MathHelper.floor_double(ei.posX);
		int y = MathHelper.floor_double(ei.posY);
		int z = MathHelper.floor_double(ei.posZ);
		int dmg = ei.getEntityItem().getItemDamage();
		Block b = ei.worldObj.getBlock(x, y, z);
		if (b == ChromaBlocks.CHROMA.getBlockInstance()) {
			if (ei.worldObj.getBlockMetadata(x, y, z) == 0) {
				if (dmg < 16) {
					TileEntity te = ei.worldObj.getTileEntity(x, y, z);
					if (te instanceof TileEntityChroma) {
						TileEntityChroma tc = (TileEntityChroma)te;
						if (tc.isFullyActive()) {
							ei.lifespan = Integer.MAX_VALUE;
							NBTTagCompound tag = ei.getEntityData().getCompoundTag("chroma");
							int tick = tag.getInteger("tick");
							int age = tag.getInteger("age");
							if (ei.worldObj.isRemote)
								this.tickEffects(ei, tick);
							if (age > ei.age) { //items were combined
								tick = 0;
								//ReikaJavaLibrary.pConsole(ei);
								tag.setInteger("tick", tick+1);
								tag.setInteger("age", ei.age);
								ei.getEntityData().setTag("chroma", tag);
							}
							else if (tick >= 6000) {
								ItemStack is = ChromaItems.SHARD.getCraftedMetadataProduct(ei.getEntityItem().stackSize, 16+dmg);
								EntityItem ei2 = new EntityItem(ei.worldObj, ei.posX, ei.posY, ei.posZ, is);
								if (!ei.worldObj.isRemote) {
									ei.worldObj.spawnEntityInWorld(ei2);
									ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.SHARDBOOST.ordinal(), ei.worldObj, x, y, z, ei.getEntityId());
									ChromaSounds.INFUSE.playSoundAtBlock(ei.worldObj, x, y, z);
								}
								ei.setDead();
								tc.clear();
							}
							else {
								tag.setInteger("tick", tick+1);
								tag.setInteger("age", ei.age);
								ei.getEntityData().setTag("chroma", tag);
								//ReikaJavaLibrary.pConsole(tick, Side.SERVER);
							}
						}
					}
				}
				else {
					ei.lifespan = Integer.MAX_VALUE;
				}
			}
		}
		return false;
	}

	@SideOnly(Side.CLIENT)
	private void tickEffects(EntityItem ei, int tick) {
		if (tick%16 == 0) {
			CrystalElement e = CrystalElement.elements[ei.getEntityItem().getItemDamage()];
			double rx = ReikaRandomHelper.getRandomPlusMinus(ei.posX, 0.5);
			double ry = ei.posY;//ReikaRandomHelper.getRandomPlusMinus(ei.posY+1, 0.5);
			double rz = ReikaRandomHelper.getRandomPlusMinus(ei.posZ, 0.5);
			//ReikaParticleHelper.REDSTONE.spawnAt(ei.worldObj, rx, ry, rz, e.getRed(), e.getGreen(), e.getBlue());
			double vy = ReikaRandomHelper.getRandomPlusMinus(0.0625, 0.03125);
			Minecraft.getMinecraft().effectRenderer.addEffect(new EntityRuneFX(ei.worldObj, rx, ry, rz, 0, vy, 0, e));
		}
	}

	@SideOnly(Side.CLIENT)
	public static void spawnEffects(EntityItem ei) {
		for (int i = 0; i < 16; i++) {
			double rx = ei.posX;
			double ry = ei.posY;
			double rz = ei.posZ;
			CrystalElement e = CrystalElement.elements[ei.getEntityItem().getItemDamage()];
			double vx = ReikaRandomHelper.getRandomPlusMinus(0, 0.125);
			double vy = ReikaRandomHelper.getRandomPlusMinus(0.125, 0.0625);
			double vz = ReikaRandomHelper.getRandomPlusMinus(0, 0.125);
			Minecraft.getMinecraft().effectRenderer.addEffect(new EntityFlareFX(e, ei.worldObj, rx, ry, rz, vx, vy, vz));
		}
	}

	@Override
	public int getItemSpriteIndex(ItemStack item) {
		return super.getItemSpriteIndex(item)-item.getItemDamage()+item.getItemDamage()%16;
	}

	@Override
	public boolean isPotionIngredient(ItemStack is)
	{
		return false;
	}

	@Override
	public int getNumberTypes() {
		return CrystalElement.elements.length*2;
	}

	//@Override
	//public boolean hasEffect(ItemStack is) {
	//	return is.getItemDamage() >= 16;
	//}

	@Override
	public String getPotionEffect(ItemStack is)
	{
		String ret = "";
		CrystalElement dye = CrystalElement.elements[is.getItemDamage()];
		switch(dye) {
		case BLACK:
			ret += PotionHelper.fermentedSpiderEyeEffect;
			break;
		case BLUE:
			ret += PotionHelper.goldenCarrotEffect;
			break;
		case BROWN:
			ret += PotionHelper.redstoneEffect;
			break;
		case CYAN: //water breathing
			ret += "";
			break;
		case GRAY: //slowness
			ret += PotionHelper.sugarEffect;
			break;
		case GREEN:
			ret += PotionHelper.spiderEyeEffect;
			break;
		case LIGHTBLUE:
			ret += PotionHelper.sugarEffect;
			break;
		case LIGHTGRAY: //weakness
			ret += PotionHelper.blazePowderEffect;
			break;
		case LIME: //jump boost
			ret += "";
			break;
		case MAGENTA:
			ret += PotionHelper.ghastTearEffect;
			break;
		case ORANGE:
			ret += PotionHelper.magmaCreamEffect;
			break;
		case PINK:
			ret += PotionHelper.blazePowderEffect;
			break;
		case PURPLE: //xp -> level2?
			ret += PotionHelper.glowstoneEffect;
			break;
		case RED: //resistance
			ret += "";
		case WHITE:
			ret += PotionHelper.goldenCarrotEffect;
			break;
		case YELLOW: //haste
			ret += "";
			break;
		default:
			ret += "";
			break;
		}
		if (is.getItemDamage() >= 16) {
			ret += "+5+6-7"; //level II and extended
		}
		return ret;
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean verbose)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Good for ");
		CrystalElement color = CrystalElement.elements[is.getItemDamage()%16];
		sb.append(CrystalPotionController.getPotionName(color));
		sb.append(" Potions");
		li.add(sb.toString());
		if (is.getItemDamage() >= 16)
			li.add(EnumChatFormatting.LIGHT_PURPLE.toString()+"Gives level II enhanced potions");
		else
			li.add(EnumChatFormatting.GOLD.toString()+"Gives ordinary potions");

		if (!ProgressionManager.instance.hasPlayerDiscoveredColor(ep, color)) {
			ArrayList<String> li2 = new ArrayList();
			for (Object o : li) {
				if (o instanceof String)
					li2.add(ChromaFontRenderer.FontType.OBFUSCATED.id+o);
			}
			li.clear();
			li.addAll(li2);
		}
	}

	@Override
	public boolean useAnimatedRender(ItemStack is) {
		return is.getItemDamage() >= 16;
	}

	@Override
	public int getFrameSpeed() {
		return 3;
	}

	@Override
	public int getColumn(ItemStack is) {
		return is.getItemDamage()%16;
	}

	@Override
	public int getFrameCount() {
		return 16;
	}

	@Override
	public int getBaseRow(ItemStack is) {
		return 0;
	}

	@Override
	public String getTexture(ItemStack is) {
		return is.getItemDamage() >= 16 ? "/Reika/ChromatiCraft/Textures/Items/shardglow.png" : super.getTexture(is);
	}

	@Override
	public int getFrameOffset(ItemStack is) {
		return offsets[is.getItemDamage()%16];
	}

	static {
		offsets = ReikaArrayHelper.getLinearArray(16);
		ReikaArrayHelper.shuffleArray(offsets);
	}

	@Override
	public ProgressStage getDiscoveryTier(ItemStack is) {
		return ProgressStage.CHROMA;
	}

	@Override
	public boolean isTiered(ItemStack is) {
		return is.getItemDamage() >= 16;
	}
}
