package net.skylinemc.skylinemc.client;

import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.activity.Activity;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.skylinemc.skylinemc.client.packets.client.SkylineClientInfoPacket;
import net.skylinemc.skylinemc.client.packets.server.SkylineActivityUpdatePacket;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

public class SkylineStatsClient implements ClientModInitializer {
    private Core core;
    private final Activity discordActivity = new Activity();

    @Override
    public void onInitializeClient() {
        System.out.println(FabricLoader.getInstance().getModContainer("skylinestats").get().getMetadata().getVersion().getFriendlyString());

        try (CreateParams params = new CreateParams()) {
            params.setClientID(1271149887882264599L);
            params.setFlags(CreateParams.getDefaultFlags());

            core = new Core(params);
        }

        PayloadTypeRegistry.playC2S().register(SkylineClientInfoPacket.ID, SkylineClientInfoPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(SkylineActivityUpdatePacket.ID, SkylineActivityUpdatePacket.CODEC);

        ClientPlayNetworking.registerGlobalReceiver(SkylineActivityUpdatePacket.ID, (payload, context) -> {
            System.out.println("Receiving activity payload " + (payload.message()));
            setDiscordActivity(payload.message(), false);
        });

        ClientPlayConnectionEvents.JOIN.register(((handler, packetSender, client) -> {
            ClientPlayNetworking.send(new SkylineClientInfoPacket(FabricLoader.getInstance().getModContainer("skylinestats").get().getMetadata().getVersion().getFriendlyString()));
            setDiscordActivity("Playing SkylineMC", true);
        }));

        ClientPlayConnectionEvents.DISCONNECT.register(((handler, client) -> {
            core.activityManager().clearActivity();
        }));
    }

    public void setDiscordActivity(String message, Boolean resetTime) {
        ServerInfo serverInfo = MinecraftClient.getInstance().getNetworkHandler().getServerInfo();
        int onlinePlayers = serverInfo.players != null ? serverInfo.players.online() : 0;
        int maxPlayers = serverInfo.players != null ? serverInfo.players.max() : 0;

        discordActivity.assets().setLargeImage("logo");
        discordActivity.setState("play.skylinemc.net");
        discordActivity.setDetails(message);
        discordActivity.party().size().setCurrentSize(onlinePlayers);
        discordActivity.party().size().setMaxSize(maxPlayers);

        if (resetTime) {
            discordActivity.timestamps().setStart(Instant.now());
        }

        core.activityManager().updateActivity(discordActivity);
    }
}
