package com.oblivioussp.spartanshields.network;

import com.oblivioussp.spartanshields.ModSpartanShields;

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
			.named(new ResourceLocation(ModSpartanShields.ID, "network"))
			.clientAcceptedVersions(PROTOCOL_VERSION::equals)
			.serverAcceptedVersions(PROTOCOL_VERSION::equals)
			.networkProtocolVersion(() -> PROTOCOL_VERSION)
			.simpleChannel();
	protected static int currentId = 0;

	public static void init()
	{
		instance.registerMessage(getNextPacketId(), ShieldBashPacket.class, ShieldBashPacket::encode, ShieldBashPacket::decode, ShieldBashPacket.Handler::handle);
	}
	
	public static int getNextPacketId()
	{
		int id = currentId;
		currentId++;
		return id;
	}

	public static void sendPacketTo(Object packet, ServerPlayerEntity player)
	{
		if(!(player instanceof FakePlayer))
			instance.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
	}

	public static void sendPacketToServer(Object packet)
	{
		instance.sendToServer(packet);
	}

}
