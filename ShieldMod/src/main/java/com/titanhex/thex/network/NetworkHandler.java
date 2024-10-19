package com.titanhex.thex.network;

import com.titanhex.thex.ModThexShields;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class NetworkHandler
{
	protected static final String PROTOCOL_VERSION = "1";
	protected static final SimpleChannel instance = NetworkRegistry.ChannelBuilder
			.named(new ResourceLocation(ModThexShields.ID, "network"))
			.clientAcceptedVersions(PROTOCOL_VERSION::equals)
			.serverAcceptedVersions(PROTOCOL_VERSION::equals)
			.networkProtocolVersion(() -> PROTOCOL_VERSION)
			.simpleChannel();

	public static void sendPacketToServer(Object packet)
	{
		instance.sendToServer(packet);
	}

}
