import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.TileEntity;

public class FCTileEntityMillStone extends TileEntity implements IInventory {
    private final int iMillStoneInventorySize = 3;
    private final int iMillStoneStackSizeLimit = 64;
    private final double dMillStoneMaxPlayerInteractionDist = 64.0;
    private final int iMillStoneTimeToGrind = 200;
    private ItemStack[] millStoneContents = new ItemStack[3];
    private int iMillStoneGrindCounter = 0;

    public FCTileEntityMillStone() {
    }

    public int getSizeInventory() {
        return 3;
    }

    public ItemStack getStackInSlot(int iSlot) {
        return this.millStoneContents[iSlot];
    }

    public ItemStack decrStackSize(int iSlot, int iAmount) {
        return FCUtilsInventory.DecrStackSize(this, iSlot, iAmount);
    }

    public void setInventorySlotContents(int iSlot, ItemStack itemstack) {
        this.millStoneContents[iSlot] = itemstack;
        if (itemstack != null && itemstack.stackSize > this.getInventoryStackLimit()) {
            itemstack.stackSize = this.getInventoryStackLimit();
        }

        this.onInventoryChanged();
    }

    public String getInvName() {
        return "MillStone";
    }

    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        NBTTagList nbttaglist = nbttagcompound.getTagList("Items");
        this.millStoneContents = new ItemStack[this.getSizeInventory()];

        for(int i = 0; i < nbttaglist.tagCount(); ++i) {
            NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.tagAt(i);
            int j = nbttagcompound1.getByte("Slot") & 255;
            if (j >= 0 && j < this.millStoneContents.length) {
                this.millStoneContents[j] = new ItemStack(nbttagcompound1);
            }
        }

        if (nbttagcompound.hasKey("grindCounter")) {
            this.iMillStoneGrindCounter = nbttagcompound.getInteger("grindCounter");
        }

    }

    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        NBTTagList nbttaglist = new NBTTagList();

        for(int i = 0; i < this.millStoneContents.length; ++i) {
            if (this.millStoneContents[i] != null) {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Slot", (byte)i);
                this.millStoneContents[i].writeToNBT(nbttagcompound1);
                nbttaglist.setTag(nbttagcompound1);
            }
        }

        nbttagcompound.setTag("Items", nbttaglist);
        nbttagcompound.setInteger("grindCounter", this.iMillStoneGrindCounter);
    }

    public int getInventoryStackLimit() {
        return 64;
    }

    public boolean canInteractWith(EntityPlayer entityplayer) {
        if (this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this) {
            return false;
        } else {
            return entityplayer.getDistanceSq((double)this.xCoord + 0.5, (double)this.yCoord + 0.5, (double)this.zCoord + 0.5) <= 64.0;
        }
    }

    public int GetUnmilledItemInventoryIndex() {
        for(int tempIndex = 0; tempIndex < 3; ++tempIndex) {
            if (this.millStoneContents[tempIndex] != null) {
                Item tempItem = this.millStoneContents[tempIndex].getItem();
                if (tempItem != null && (tempItem.shiftedIndex == Item.wheat.shiftedIndex || tempItem.shiftedIndex == Item.leather.shiftedIndex || tempItem.shiftedIndex == mod_FCBetterThanWolves.fcHemp.shiftedIndex || tempItem.shiftedIndex == mod_FCBetterThanWolves.fcCompanionCube.blockID || tempItem.shiftedIndex == Item.reed.shiftedIndex || tempItem.shiftedIndex == Block.netherrack.blockID || tempItem.shiftedIndex == Item.bone.shiftedIndex || tempItem.shiftedIndex == Item.coal.shiftedIndex || tempItem.shiftedIndex == Block.plantRed.blockID || tempItem.shiftedIndex == Block.plantYellow.blockID || tempItem.shiftedIndex == Item.porkRaw.shiftedIndex)) {
                    return tempIndex;
                }
            }
        }

        return -1;
    }

    public int getGrindProgressScaled(int iScale) {
        return this.iMillStoneGrindCounter * iScale / 200;
    }

    public boolean IsGrinding() {
        return this.iMillStoneGrindCounter > 0;
    }

    public void updateEntity() {
        int iUnmilledItemIndex = this.GetUnmilledItemInventoryIndex();
        if (iUnmilledItemIndex >= 0) {
            if (((FCBlockMillStone)mod_FCBetterThanWolves.fcMillStone).IsBlockOn(this.worldObj, this.xCoord, this.yCoord, this.zCoord)) {
                ++this.iMillStoneGrindCounter;
                int iUnmilledItemID = this.millStoneContents[iUnmilledItemIndex].getItem().shiftedIndex;
                ItemStack milledStack;
                if (this.iMillStoneGrindCounter >= 200) {
                    milledStack = null;
                    if (iUnmilledItemID == Item.wheat.shiftedIndex) {
                        milledStack = new ItemStack(mod_FCBetterThanWolves.fcFlour.shiftedIndex, 1, 0);
                    } else if (iUnmilledItemID == Item.leather.shiftedIndex) {
                        milledStack = new ItemStack(mod_FCBetterThanWolves.fcScouredLeather.shiftedIndex, 1, 0);
                    } else if (iUnmilledItemID == mod_FCBetterThanWolves.fcHemp.shiftedIndex) {
                        milledStack = new ItemStack(mod_FCBetterThanWolves.fcHempFibers.shiftedIndex, 4, 0);
                    } else if (iUnmilledItemID == Item.reed.shiftedIndex) {
                        milledStack = new ItemStack(Item.sugar.shiftedIndex, 1, 0);
                    } else if (iUnmilledItemID == mod_FCBetterThanWolves.fcCompanionCube.blockID) {
                        milledStack = new ItemStack(mod_FCBetterThanWolves.fcWolfRaw.shiftedIndex, 1, 0);
                        FCBlockCompanionCube.SpawnHearts(this.worldObj, this.xCoord, this.yCoord, this.zCoord);
                        if (this.millStoneContents[iUnmilledItemIndex].getItemDamage() == 0) {
                            this.worldObj.playSoundEffect((double)((float)this.xCoord + 0.5F), (double)((float)this.yCoord + 0.5F), (double)((float)this.zCoord + 0.5F), "mob.wolf.whine", 0.5F, 2.6F + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.8F);
                        }
                    } else if (iUnmilledItemID == Block.netherrack.blockID) {
                        milledStack = new ItemStack(mod_FCBetterThanWolves.fcGroundNetherrack.shiftedIndex, 1, 0);
                    } else if (iUnmilledItemID == Item.bone.shiftedIndex) {
                        milledStack = new ItemStack(Item.dyePowder, 3, 15);
                    } else if (iUnmilledItemID == Item.coal.shiftedIndex) {
                        milledStack = new ItemStack(mod_FCBetterThanWolves.fcCoalDust.shiftedIndex, 1, 0);
                    } else if (iUnmilledItemID == Block.plantRed.blockID) {
                        milledStack = new ItemStack(Item.dyePowder, 2, 1);
                    } else if (iUnmilledItemID == Block.plantYellow.blockID) {
                        milledStack = new ItemStack(Item.dyePowder, 2, 11);
                    } else if (iUnmilledItemID == Item.porkRaw.shiftedIndex) {
                        milledStack = new ItemStack(Item.dyePowder, 5, 15);
                    }

                    if (milledStack != null) {
                        this.decrStackSize(iUnmilledItemIndex, 1);
                        this.EjectStack(milledStack);
                    }

                    this.iMillStoneGrindCounter = 0;
                } else if (iUnmilledItemID == mod_FCBetterThanWolves.fcCompanionCube.blockID) {
                    if (this.millStoneContents[iUnmilledItemIndex].getItemDamage() == 0 && this.worldObj.rand.nextInt(10) == 0) {
                        this.worldObj.playSoundEffect((double)((float)this.xCoord + 0.5F), (double)((float)this.yCoord + 0.5F), (double)((float)this.zCoord + 0.5F), "mob.wolf.hurt", 2.0F, (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.2F + 1.0F);
                    }

                    if (this.worldObj.rand.nextInt(20) == 0) {
                        milledStack = new ItemStack(Item.silk);
                        this.EjectStack(milledStack);
                    }

                    if (this.worldObj.rand.nextInt(60) == 0) {
                        milledStack = new ItemStack(Item.dyePowder.shiftedIndex, 1, 1);
                        this.EjectStack(milledStack);
                    }
                } else if (iUnmilledItemID == Block.netherrack.blockID && this.worldObj.rand.nextInt(10) == 0) {
                    this.worldObj.playSoundEffect((double)this.xCoord + 0.5, (double)this.yCoord + 0.5, (double)this.zCoord + 0.5, "mob.ghast.scream", 0.25F, this.worldObj.rand.nextFloat() * 0.4F + 0.8F);
                }
            }
        } else {
            this.iMillStoneGrindCounter = 0;
        }

    }

    public void EjectStack(ItemStack stack) {
        FCBlockPos targetPos = new FCBlockPos(this.xCoord, this.yCoord, this.zCoord);
        int iDirection = 2 + this.worldObj.rand.nextInt(4);
        targetPos.AddFacingAsOffset(iDirection);
        FCUtilsMisc.EjectStackWithRandomOffset(this.worldObj, targetPos.i, targetPos.j, targetPos.k, stack);
    }

    public boolean AddSingleItemToInventory(int iItemShiftedIndex) {
        ItemStack itemStack = new ItemStack(iItemShiftedIndex, 1, 0);
        return this.addItemStackToInventory(itemStack);
    }

    public boolean addItemStackToInventory(ItemStack itemstack) {
        if (!itemstack.isItemDamaged()) {
            itemstack.stackSize = this.storePartialItemStack(itemstack);
            if (itemstack.stackSize == 0) {
                return true;
            }
        }

        int i = this.getFirstEmptyStack();
        if (i >= 0) {
            this.setInventorySlotContents(i, itemstack);
            return true;
        } else {
            return false;
        }
    }

    private int getFirstEmptyStack() {
        for(int i = 0; i < this.getSizeInventory(); ++i) {
            if (this.getStackInSlot(i) == null) {
                return i;
            }
        }

        return -1;
    }

    private int storePartialItemStack(ItemStack itemstack) {
        int i = itemstack.itemID;
        int j = itemstack.stackSize;
        int k = this.storeItemStack(itemstack);
        if (k < 0) {
            k = this.getFirstEmptyStack();
        }

        if (k < 0) {
            return j;
        } else {
            if (this.getStackInSlot(k) == null) {
                this.setInventorySlotContents(k, new ItemStack(i, 0, itemstack.getItemDamage()));
            }

            int l = j;
            ItemStack tempStack = this.getStackInSlot(k);
            if (l > tempStack.getMaxStackSize() - tempStack.stackSize) {
                l = tempStack.getMaxStackSize() - tempStack.stackSize;
            }

            if (l > this.getInventoryStackLimit() - tempStack.stackSize) {
                l = this.getInventoryStackLimit() - tempStack.stackSize;
            }

            if (l == 0) {
                return j;
            } else {
                j -= l;
                tempStack.stackSize += l;
                this.setInventorySlotContents(k, tempStack);
                return j;
            }
        }
    }

    private int storeItemStack(ItemStack itemstack) {
        for(int i = 0; i < this.getSizeInventory(); ++i) {
            ItemStack tempStack = this.getStackInSlot(i);
            if (tempStack != null && tempStack.itemID == itemstack.itemID && tempStack.isStackable() && tempStack.stackSize < tempStack.getMaxStackSize() && tempStack.stackSize < this.getInventoryStackLimit() && (!tempStack.getHasSubtypes() || tempStack.getItemDamage() == itemstack.getItemDamage())) {
                return i;
            }
        }

        return -1;
    }

    public boolean IsWholeCompanionCubeInInventory() {
        for(int tempIndex = 0; tempIndex < 3; ++tempIndex) {
            if (this.millStoneContents[tempIndex] != null) {
                Item tempItem = this.millStoneContents[tempIndex].getItem();
                if (tempItem != null && tempItem.shiftedIndex == mod_FCBetterThanWolves.fcCompanionCube.blockID && this.millStoneContents[tempIndex].getItemDamage() == 0) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean IsWholeCompanionCubeNextToBeProcessed() {
        int iUnmilledItemIndex = this.GetUnmilledItemInventoryIndex();
        if (iUnmilledItemIndex >= 0) {
            int iUnmilledItemID = this.millStoneContents[iUnmilledItemIndex].getItem().shiftedIndex;
            if (iUnmilledItemID == mod_FCBetterThanWolves.fcCompanionCube.blockID && this.millStoneContents[iUnmilledItemIndex].getItemDamage() == 0) {
                return true;
            }
        }

        return false;
    }

    public void onInventoryChanged() {
        if (this.IsWholeCompanionCubeInInventory()) {
            this.worldObj.playSoundEffect((double)((float)this.xCoord + 0.5F), (double)((float)this.yCoord + 0.5F), (double)((float)this.zCoord + 0.5F), "mob.wolf.whine", 0.5F, 2.6F + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.8F);
        }

    }
}
