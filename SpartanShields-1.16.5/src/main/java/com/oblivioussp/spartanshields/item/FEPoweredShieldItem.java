package com.oblivioussp.spartanshields.item;

import java.util.List;

import javax.annotation.Nullable;

import com.oblivioussp.spartanshields.ModSpartanShields;
import com.oblivioussp.spartanshields.client.ClientHelper;
import com.oblivioussp.spartanshields.util.Config;
import com.oblivioussp.spartanshields.util.EnergyCapabilityProviderItem;
import com.oblivioussp.spartanshields.util.PowerUnit;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class FEPoweredShieldItem extends ShieldBaseItem implements IDamageShield, IItemPoweredFE
{
	public static final String NBT_ENERGY = "Energy";
	
	protected int energyCapacity;
	protected int maxEnergyReceive;
	protected String modName;
	protected PowerUnit preferredEnergyUnit;

	public FEPoweredShieldItem(String unlocName, int capacity, int maxReceive, String modName, PowerUnit preferredUnit, Item.Properties prop)
	{
		super(unlocName, 0, prop);
		this.energyCapacity = capacity;
		this.maxEnergyReceive = maxReceive;
		this.modName = modName;
		this.preferredEnergyUnit = preferredUnit;

		if(FMLEnvironment.dist.isClient())
			ClientHelper.registerPoweredShieldPropertyOverrides(this);
	}
    
    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    @Override
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> subItems)
    {
    	if(this.allowdedIn(group))
    	{
	    	ItemStack fullShield = new ItemStack(this);
	    	fullShield.getOrCreateTag().putInt("Energy", energyCapacity);
	    	
	        subItems.add(new ItemStack(this));
	        subItems.add(fullShield);
    	}
    }
	
	@Override
	public void setDamage(ItemStack stack, int damage)
	{
		super.setDamage(stack, 0);
	}
	
	@Override
	public double getDurabilityForDisplay(ItemStack stack)
	{
		int energy = stack.getOrCreateTag().getInt("Energy");
		return ((double)energyCapacity - energy) / (double)energyCapacity;
	}
	
	/**
     * Returns the packed int RGB value used to render the durability bar in the GUI.
     * Defaults to a value based on the hue scaled as the damage decreases, but can be overriden.
     *
     * @param stack Stack to get durability from
     * @return A packed RGB value for the durability colour (0x00RRGGBB)
     */
    public int getRGBDurabilityForDisplay(ItemStack stack)
    {
    	return 0x69B3FF;
    }
	
	@Override
	public int getMaxDamage(ItemStack stack)
	{
		return energyCapacity;
	}
	
	@Override
	public boolean showDurabilityBar(ItemStack stack)
    {
		int energy = stack.getOrCreateTag().getInt(NBT_ENERGY);
        return energy < energyCapacity;
    }
	
	/**
     * allows items to add custom lines of information to the mouseover description
     */
	@OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
    	tooltip.add(new TranslationTextComponent("tooltip." + ModSpartanShields.ID + "." + preferredEnergyUnit.getCapacityTranslationKey(), MathHelper.floor(this.getFEStored(stack) * preferredEnergyUnit.getEnergyScaleToFE()), MathHelper.floor(this.getFECapacity(stack)  * preferredEnergyUnit.getEnergyScaleToFE())));
    	tooltip.add(new TranslationTextComponent("tooltip." + ModSpartanShields.ID + "." + preferredEnergyUnit.getEnergyChargeRateTranslationKey(), MathHelper.floor(this.maxEnergyReceive * preferredEnergyUnit.getEnergyScaleToFE())));
    	tooltip.add(new TranslationTextComponent("tooltip." + ModSpartanShields.ID + "." + preferredEnergyUnit.getEnergyPerDamageTranslationKey(), MathHelper.floor(Config.INSTANCE.damageToFEMultiplier.get() * 2 * preferredEnergyUnit.getEnergyScaleToFE())));
    	tooltip.add(new TranslationTextComponent("tooltip." + ModSpartanShields.ID + "." + "fe_shield.desc"));
//    	this.addShieldBashTooltip(stack, worldIn, tooltip, flagIn);
    }
    /**
     * returns the action that specifies what animation to play when the items is being used
     */
    @Override
    public UseAction getUseAnimation(ItemStack stack)
    {
    	if(this.getFEStored(stack) > 0)
    		return UseAction.BLOCK;
    	else
    		return UseAction.NONE;
    }
    
    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand hand)
    {
        playerIn.startUsingItem(hand);
        ItemStack stack = playerIn.getItemInHand(hand);
        if(this.getFEStored(stack) > 0)
        	return ActionResult.consume(stack);
        else
        {
        	if(worldIn.isClientSide)
        		playerIn.sendMessage(new StringTextComponent(TextFormatting.YELLOW
        			+ I18n.get("message." + ModSpartanShields.ID + ".powered_shield_block_fail", stack.getDisplayName().getString())), Util.NIL_UUID);
        	return ActionResult.fail(stack);
        }
    }

	@Override
	public void damageShield(ItemStack shieldStack, PlayerEntity player, Entity attacker, float damage) 
	{
		int energyToUse = MathHelper.floor((float)(damage));
		
		// Remove FE from the shield to absorb the damage.
		int currentEnergy = shieldStack.getOrCreateTag().getInt(NBT_ENERGY);
		int energyRemoved = Math.min(energyToUse, currentEnergy);
		
		currentEnergy -= energyRemoved;
		shieldStack.getTag().putInt(NBT_ENERGY, currentEnergy);
		
		if(currentEnergy == 0)
			player.playSound(SoundEvents.SHIELD_BREAK, 0.8F, 0.8F + player.level.random.nextFloat() * 0.4F);
	}
	
	public FEPoweredShieldItem setCapacity(int capacity) 
	{
		this.energyCapacity = capacity;
		return this;
	}

	public FEPoweredShieldItem  setMaxReceive(int maxReceive) 
	{
		this.maxEnergyReceive = maxReceive;
		return this;
	}

	/* IItemPoweredFE */
	@Override
	public int receiveFE(ItemStack container, int maxReceive, boolean simulate) 
	{
		int energy = container.getOrCreateTag().getInt(NBT_ENERGY);
		int energyReceived = Math.min(energyCapacity - energy, Math.min(this.maxEnergyReceive, maxReceive));

		if (!simulate)
		{
			energy += energyReceived;
			container.getTag().putInt(NBT_ENERGY, energy);
		}
		return energyReceived;
	}

	@Override
	public int extractFE(ItemStack container, int maxExtract, boolean simulate) 
	{
		return 0;
	}

	@Override
	public int getFEStored(ItemStack container) {

		if (!container.getOrCreateTag().contains(NBT_ENERGY)) 
			return 0;
		
		return container.getTag().getInt(NBT_ENERGY);
	}

	@Override
	public int getFECapacity(ItemStack container) 
	{
		return energyCapacity;
	}

	@Override
	public boolean canExtractFE(ItemStack stack)
	{
		return false;
	}

	@Override
	public boolean canReceiveFE(ItemStack stack) 
	{
		return true;
	}
	
	/**
     * Called from ItemStack.setItem, will hold extra data for the life of this ItemStack.
     * Can be retrieved from stack.getCapabilities()
     * The NBT can be null if this is not called from readNBT or if the item the stack is
     * changing FROM is different then this item, or the previous item had no capabilities.
     *
     * This is called BEFORE the stacks item is set so you can use stack.getItem() to see the OLD item.
     * Remember that getItem CAN return null.
     *
     * @param stack The ItemStack
     * @param nbt NBT of this item serialized, or null.
     * @return A holder instance associated with this ItemStack where you can hold capabilities for the life of this item.
     */
	@Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt)
    {
        return new EnergyCapabilityProviderItem(stack, this);
    }
}
