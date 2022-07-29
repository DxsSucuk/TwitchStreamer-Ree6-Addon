package de.presti.ree6.derpedcrusader.command;

import com.github.twitch4j.pubsub.events.ChannelPointsRedemptionEvent;
import de.presti.ree6.commands.Category;
import de.presti.ree6.commands.CommandEvent;
import de.presti.ree6.commands.interfaces.Command;
import de.presti.ree6.commands.interfaces.ICommand;
import de.presti.ree6.main.Main;
import de.presti.ree6.utils.others.ThreadUtil;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.time.Duration;

/**
 * @inheritDoc
 */
@Command(name = "streamstarted", description = "HIDDEN USER-SPECIFIC FEATURE!", category = Category.HIDDEN)
public class StreamStarted implements ICommand {

    /**
     * @inheritDoc
     */
    @Override
    public void onPerform(CommandEvent commandEvent) {
        if (commandEvent.getGuild().getIdLong() != 882472860692647947L||
                commandEvent.getMember().getIdLong() != 206433753118146560L) {
            return;
        }

        Main.getInstance().getCommandManager().sendMessage("Understood!\nI will now join the voicechannel and start listing for redeems!",
                commandEvent.getChannel(), commandEvent.getInteractionHook());

        Main.getInstance().getNotifier().getTwitchClient().getEventManager().onEvent(ChannelPointsRedemptionEvent.class, channelPointsRedemptionEvent -> {
            if (channelPointsRedemptionEvent.getRedemption().getReward().getId().equalsIgnoreCase("amongus")) {

            }
        });

        Main.getInstance().getNotifier().getTwitchClient().getClientHelper().enableStreamEventListener("");
    }

    /**
     * @inheritDoc
     */
    @Override
    public CommandData getCommandData() {
        return null;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String[] getAlias() {
        return new String[0];
    }
}
