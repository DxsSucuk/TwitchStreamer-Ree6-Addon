package de.presti.ree6.twitchredemption.main;

import com.github.philippheuer.events4j.api.domain.IEventSubscription;
import de.presti.ree6.addons.AddonInterface;
import de.presti.ree6.commands.exceptions.CommandInitializerException;
import de.presti.ree6.twitchredemption.command.StreamStarted;
import de.presti.ree6.main.Main;

import java.util.ArrayList;
import java.util.List;

public class TwitchRedemption implements AddonInterface {

    public static boolean isRunning = false;

    public final static List<IEventSubscription> subscriptionList = new ArrayList<>();

    public static long streamerGuildId = 882472860692647947L;
    public static long streamerUserId = 206433753118146560L;

    @Override
    public void onEnable() {
        Main.getInstance().getLogger().info("Starting Twitch Redemption Addon...");
        try {
            Main.getInstance().getCommandManager().addCommand(new StreamStarted());
        } catch (CommandInitializerException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDisable() {
        Main.getInstance().getCommandManager().removeCommand(Main.getInstance().getCommandManager().getCommandByName("streamstarted"));
        for (IEventSubscription subscription : subscriptionList) {
            subscription.dispose();
        }
    }
}
