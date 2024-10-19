package com.oblivioussp.spartanshields.tags;

import com.oblivioussp.spartanshields.ModSpartanShields;

import net.minecraft.item.Item;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.tags.ItemTags;

public class ModItemTags 
{
	public static final INamedTag<Item> basicShields = ItemTags.bind(ModSpartanShields.ID + ":basic_shields");
	public static final INamedTag<Item> towerShields = ItemTags.bind(ModSpartanShields.ID + ":tower_shields");
	public static final INamedTag<Item> shieldsWithBash = ItemTags.bind(ModSpartanShields.ID + ":shields_with_bash");
}
