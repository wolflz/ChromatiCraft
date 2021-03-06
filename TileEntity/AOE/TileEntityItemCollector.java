/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.AOE;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.TileEntity.InventoriedRelayPowered;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Interfaces.TileEntity.LocationCached;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class TileEntityItemCollector extends InventoriedRelayPowered implements LocationCached {

	private int experience = 0;
	public boolean canIntake = false;

	public static final int MAXRANGE = 24;
	public static final int MAXYRANGE = 4;

	private ItemStack[] filter = new ItemStack[2*9];
	private final StepTimer scanTimer = new StepTimer(200);

	private static final ElementTagCompound required = new ElementTagCompound();

	private static final Collection<WorldLocation> cache = new HashSet();

	static {
		required.addTag(CrystalElement.LIME, 25);
		required.addTag(CrystalElement.BLACK, 5);
	}

	@Override
	protected ElementTagCompound getRequiredEnergy() {
		return required.copy();
	}

	public int getExperience() {
		return experience;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack is, int side) {
		return true;
	}

	@Override
	public int getSizeInventory() {
		return 27;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is) {
		return false;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.ITEMCOLLECTOR;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		if (canIntake && !world.isRemote) {
			scanTimer.update();
			if (scanTimer.checkCap()) {
				this.doScan(world, x, y, z);
			}
		}
	}

	private void doScan(World world, int x, int y, int z) {
		AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x-MAXRANGE, y-MAXYRANGE, z-MAXRANGE, x+MAXRANGE+1, y+MAXYRANGE+1, z+MAXRANGE+1);
		List<Entity> li = world.selectEntitiesWithinAABB(Entity.class, box, ReikaEntityHelper.itemOrXPSelector);
		for (Entity e : li) {
			if (this.checkAbsorb(e)) {
				e.setDead();
			}
		}
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		WorldLocation loc = new WorldLocation(this);
		if (!cache.contains(loc))
			cache.add(loc);
		canIntake = true;
	}

	@Override
	public void breakBlock() {
		WorldLocation loc = new WorldLocation(this);
		cache.remove(loc);
	}

	public static boolean absorbItem(Entity e) {
		for (WorldLocation loc : cache) {
			TileEntity te = loc.getTileEntity();
			if (te instanceof TileEntityItemCollector) {
				if (((TileEntityItemCollector)te).checkAbsorb(e))
					return true;
			}
			else {
				ChromatiCraft.logger.logError("Incorrect tile ("+te+") @ "+loc+" in Item Collector cache!?");
			}
		}
		return false;
	}

	public boolean checkAbsorb(Entity e) {
		int x = xCoord;
		int y = yCoord;
		int z = zCoord;
		if (!this.isInWorld())
			return false;
		if (!canIntake)
			return false;
		if (e.worldObj.provider.dimensionId != worldObj.provider.dimensionId)
			return false;
		if (!energy.containsAtLeast(required))
			return false;
		if (e instanceof EntityItem || e instanceof EntityXPOrb) {
			if (Math.abs(e.posX-x) <= MAXRANGE && Math.abs(e.posY-y) <= MAXYRANGE && Math.abs(e.posZ-z) <= MAXRANGE) {
				if (e instanceof EntityItem) {
					EntityItem ei = (EntityItem)e;
					if (this.canAbsorbItem(ei.getEntityItem())) {
						return this.absorbItem(worldObj, x, y, z, ei);
					}
				}
				else {
					this.absorbXP(worldObj, x, y, z, (EntityXPOrb)e);
					this.drainEnergy(required);
					return true;
				}
			}
		}
		return false;
	}

	private boolean canAbsorbItem(ItemStack is) {
		for (int i = 0; i < filter.length; i++) {
			if (ReikaItemHelper.matchStacks(is, filter[i]) && ItemStack.areItemStackTagsEqual(is, filter[i])) {
				return true;
			}
			else {

			}
		}
		return false;
	}

	private boolean absorbItem(World world, int x, int y, int z, EntityItem ent) {
		ItemStack is = ent.getEntityItem();

		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			TileEntity te = this.getAdjacentTileEntity(dir);
			if (te instanceof IInventory) {
				if (ReikaInventoryHelper.addToIInv(is, (IInventory)te))
					return true;
			}
		}

		int targetslot = this.checkForStack(is);
		if (targetslot != -1) {
			if (inv[targetslot] == null)
				inv[targetslot] = is.copy();
			else
				inv[targetslot].stackSize += is.stackSize;
		}
		else {
			return false;
		}
		world.playSoundEffect(x+0.5, y+0.5, z+0.5, "random.pop", 0.1F+0.5F*rand.nextFloat(), rand.nextFloat());
		ent.playSound("random.pop", 0.5F, 2F);
		this.drainEnergy(required);
		return true;
	}

	private void absorbXP(World world, int x, int y, int z, EntityXPOrb xp) {
		int val = xp.getXpValue();
		experience += val;
		world.playSoundEffect(x+0.5, y+0.5, z+0.5, "random.orb", 0.1F, 0.5F * ((rand.nextFloat() - rand.nextFloat()) * 0.7F + 1.8F));
		xp.playSound("random.pop", 0.5F, 2F);
	}

	private int checkForStack(ItemStack is) {
		int target = -1;
		Item id = is.getItem();
		int meta = is.getItemDamage();
		int size = is.stackSize;
		int firstempty = -1;

		for (int k = 0; k < inv.length; k++) { //Find first empty slot
			if (inv[k] == null) {
				firstempty = k;
				k = inv.length;
			}
		}
		for (int j = 0; j < inv.length; j++) {
			if (inv[j] != null) {
				if (ReikaItemHelper.matchStacks(is, inv[j])) {
					if (ItemStack.areItemStackTagsEqual(is, inv[j])) {
						if (inv[j].stackSize+size <= this.getInventoryStackLimit()) {
							target = j;
							j = inv.length;
						}
						else {
							int diff = is.getMaxStackSize() - inv[j].stackSize;
							inv[j].stackSize += diff;
							is.stackSize -= diff;
						}
					}
				}
			}
		}

		if (target == -1)
			target = firstempty;
		return target;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT)
	{
		super.readSyncTag(NBT);
		experience = NBT.getInteger("xp");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT)
	{
		super.writeSyncTag(NBT);
		NBT.setInteger("xp", experience);
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		NBTTagCompound fil = new NBTTagCompound();
		for (int i = 0; i < filter.length; i++) {
			ItemStack is = filter[i];
			if (is != null) {
				NBTTagCompound tag = new NBTTagCompound();
				is.writeToNBT(tag);
				fil.setTag("filter_"+i, tag);
			}
		}
		NBT.setTag("filter", fil);
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		filter = new ItemStack[filter.length];
		NBTTagCompound fil = NBT.getCompoundTag("filter");
		for (int i = 0; i < filter.length; i++) {
			String name = "filter_"+i;
			if (fil.hasKey(name)) {
				NBTTagCompound tag = fil.getCompoundTag(name);
				ItemStack is = ItemStack.loadItemStackFromNBT(tag);
				filter[i] = is;
			}
		}
	}

	@Override
	public boolean isAcceptingColor(CrystalElement e) {
		return required.contains(e);
	}

	@Override
	public int getMaxStorage(CrystalElement e) {
		return 48000;
	}

	@Override
	protected boolean canReceiveFrom(CrystalElement e, ForgeDirection dir) {
		return true;
	}

	public void setMapping(int slot, ItemStack is) {
		filter[slot] = is;
		this.syncAllData(true);
	}

	public ItemStack getMapping(int slot) {
		return filter[slot] != null ? filter[slot].copy() : null;
	}

}
