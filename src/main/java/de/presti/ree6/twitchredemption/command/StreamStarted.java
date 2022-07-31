package de.presti.ree6.twitchredemption.command;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent;
import de.presti.ree6.commands.Category;
import de.presti.ree6.commands.CommandEvent;
import de.presti.ree6.commands.interfaces.Command;
import de.presti.ree6.commands.interfaces.ICommand;
import de.presti.ree6.main.Main;
import de.presti.ree6.twitchredemption.utils.TTSUtil;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
        Path of = Path.of("twitchRedemption/");
        if (!Files.exists(of)) {
            try {
                Files.createDirectory(of);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (commandEvent.getGuild().getIdLong() != 882472860692647947L||
                commandEvent.getMember().getIdLong() != 206433753118146560L) {
            return;
        }


        if (commandEvent.getMember().getVoiceState() == null ||
                commandEvent.getMember().getVoiceState().getChannel() == null ||
                !commandEvent.getMember().getVoiceState().inAudioChannel()) {
            return;
        }

        Main.getInstance().getCommandManager().sendMessage("Understood!\nI will now join the voicechannel and start listing for redeems!",
                commandEvent.getChannel(), commandEvent.getInteractionHook());

        Main.getInstance().getNotifier().getTwitchClient().getEventManager().onEvent(RewardRedeemedEvent.class, channelPointsRedemptionEvent -> {
            switch (channelPointsRedemptionEvent.getRedemption().getReward().getId()) {
                case "amogus" -> Main.getInstance().getMusicWorker().loadAndPlaySilence(commandEvent.getChannel(), commandEvent.getMember().getVoiceState().getChannel(),
                        "https://www.youtube.com/watch?v=dQw4w9WgXcQ", commandEvent.getInteractionHook());
                case "tts" -> {
                    try {
                        Files.write(of.resolve("tts.mp3"), TTSUtil.createTTS(channelPointsRedemptionEvent.getRedemption().getUserInput()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Main.getInstance().getMusicWorker().loadAndPlaySilence(commandEvent.getChannel(), commandEvent.getMember().getVoiceState().getChannel(),
                            of.resolve("tts.mp3").toString(), commandEvent.getInteractionHook());
                }
                default -> Main.getInstance().getLogger().info("Unhandled reward: " + channelPointsRedemptionEvent.getRedemption().getReward().getId());
            }
        });

        OAuth2Credential credential;
        try {
            credential = new OAuth2Credential(
                    "twitch",
                    Files.readString(Path.of("twitchRedemption/","twitch.creds"))
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ///// 47397687
        Main.getInstance().getNotifier().getTwitchClient().getPubSub().listenForChannelPointsRedemptionEvents(credential, credential.getUserId());
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
