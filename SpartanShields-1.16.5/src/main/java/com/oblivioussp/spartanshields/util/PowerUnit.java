package com.oblivioussp.spartanshields.util;

public enum PowerUnit 
{
	RedstoneFlux("rf_capacity", "rf_per_damage", "rf_charge_rate", 1.0f),
	ForgeEnergy("fe_capacity", "fe_per_damage", "fe_charge_rate", 1.0f),
	MicroInfinity("ui_capacity", "ui_per_damage", "ui_charge_rate", 1.0f);
	
	
	private String capUnloc;
	private String enPerDamUnloc;
	private String enChargeRate;
	private float scale;
	
	private PowerUnit(String capacityUnloc, String energyPerDamageUnloc, String energyChargeRate, float powerScale)
	{
		capUnloc = capacityUnloc;
		enPerDamUnloc = energyPerDamageUnloc;
		enChargeRate = energyChargeRate;
		scale = powerScale;
	}

	public String getCapacityTranslationKey()
	{
		return capUnloc;
	}
	
	public String getEnergyPerDamageTranslationKey()
	{
		return enPerDamUnloc;
	}
	
	public String getEnergyChargeRateTranslationKey()
	{
		return enChargeRate;
	}
	
	public float getEnergyScaleToFE()
	{
		return scale;
	}
}
