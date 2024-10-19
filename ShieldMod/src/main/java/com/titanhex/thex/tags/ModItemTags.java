package com.titanhex.thex.tags;

import com.titanhex.thex.ModThexShields;

import net.minecraft.item.Item;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.tags.ItemTags;

public class ModItemTags 
{
	public static final INamedTag<Item> basicShields = ItemTags.bind(ModThexShields.ID + ":basic_shields");
	public static final INamedTag<Item> towerShields = ItemTags.bind(ModThexShields.ID + ":tower_shields");
	public static final INamedTag<Item> shieldsWithBash = ItemTags.bind(ModThexShields.ID + ":shields_with_bash");
}
