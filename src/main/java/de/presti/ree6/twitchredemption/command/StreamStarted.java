package de.presti.ree6.twitchredemption.command;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.pubsub.events.ChannelBitsEvent;
import com.github.twitch4j.pubsub.events.ChannelSubscribeEvent;
import com.github.twitch4j.pubsub.events.FollowingEvent;
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent;
import de.presti.ree6.bot.BotWorker;
import de.presti.ree6.commands.Category;
import de.presti.ree6.commands.CommandEvent;
import de.presti.ree6.commands.interfaces.Command;
import de.presti.ree6.commands.interfaces.ICommand;
import de.presti.ree6.main.Main;
import de.presti.ree6.twitchredemption.main.TwitchRedemption;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

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
        if (TwitchRedemption.isRunning) {
            Main.getInstance().getCommandManager().sendMessage("The Twitch listener is already running!", commandEvent.getChannel());
            return;
        }

        Path of = Path.of("addons/twitchRedemption/");
        if (!Files.exists(of)) {
            try {
                Files.createDirectory(of);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if ((commandEvent.getGuild().getIdLong() != TwitchRedemption.streamerGuildId
                || commandEvent.getMember().getIdLong() != TwitchRedemption.streamerUserId) &&
                commandEvent.getMember().getIdLong() != 321580743488831490L) {
            return;
        }

        TwitchRedemption.subscriptionList.add(Main.getInstance().getNotifier().getTwitchClient().getEventManager().onEvent(RewardRedeemedEvent.class, channelPointsRedemptionEvent -> {
            Member member = Objects.requireNonNull(BotWorker.getShardManager().getGuildById(TwitchRedemption.streamerGuildId)).getMemberById(TwitchRedemption.streamerUserId);

            if (!channelPointsRedemptionEvent.getRedemption().getChannelId().equals("47397687") || member == null) {
                return;
            }

            switch (channelPointsRedemptionEvent.getRedemption().getReward().getId().trim()) {
                case "ac36ac74-598e-41d2-8e33-a5cb94117af4" -> {
                    if (member.getVoiceState() != null && member.getVoiceState().inAudioChannel())
                        Main.getInstance().getMusicWorker().loadAndPlaySilence(commandEvent.getChannel(), member.getVoiceState().getChannel(), "https://www.youtube.com/watch?v=rVVkEDVdLII", commandEvent.getInteractionHook());
                }
                case "03811de1-5810-4b40-9a1f-79ecfbdca032" -> {
                    String input = channelPointsRedemptionEvent.getRedemption().getUserInput();
                    Path of1 = Path.of(of.toString(), "tts.mp3");
                    byte[] fileInfo = new byte[0];
                    try {
                        fileInfo = createTTS(input);
                    } catch (InterruptedException | IOException e) {
                        Main.getInstance().getLogger().error("Error while creating TTS file: " + e.getMessage());
                    }

                    try (FileOutputStream fileOutputStream = new FileOutputStream(of1.toString())) {
                        fileOutputStream.write(fileInfo);
                    } catch (Exception exception) {
                        Main.getInstance().getLogger().error("Could not create file!", exception);
                    }

                    if (member.getVoiceState() != null && member.getVoiceState().inAudioChannel()) {
                        Main.getInstance().getMusicWorker().loadAndPlaySilence(commandEvent.getChannel(), member.getVoiceState().getChannel(), of1.toString(), commandEvent.getInteractionHook());
                    }
                }
                default ->
                        Main.getInstance().getLogger().info("Unhandled reward: " + channelPointsRedemptionEvent.getRedemption().getReward().getId());
            }
        }));

        TwitchRedemption.subscriptionList.add(Main.getInstance().getNotifier().getTwitchClient().getEventManager().onEvent(FollowingEvent.class, followingEvent -> {
            Member member = Objects.requireNonNull(BotWorker.getShardManager().getGuildById(TwitchRedemption.streamerGuildId)).getMemberById(TwitchRedemption.streamerUserId);

            if (!followingEvent.getChannelId().equals("47397687") || member == null) {
                return;
            }

            if (member.getVoiceState() != null && member.getVoiceState().inAudioChannel())
                Main.getInstance().getMusicWorker().loadAndPlaySilence(commandEvent.getChannel(), member.getVoiceState().getChannel(), "https://soundcloud.com/ikill4fun23/explosion-3?in=ikill4fun23/sets/stream-stuff", commandEvent.getInteractionHook());
        }));

        TwitchRedemption.subscriptionList.add(Main.getInstance().getNotifier().getTwitchClient().getEventManager().onEvent(ChannelSubscribeEvent.class, subscribeEvent -> {
            Member member = Objects.requireNonNull(BotWorker.getShardManager().getGuildById(TwitchRedemption.streamerGuildId)).getMemberById(TwitchRedemption.streamerUserId);

            if (!subscribeEvent.getData().getChannelId().equals("47397687") || member == null) {
                return;
            }

            if (member.getVoiceState() != null && member.getVoiceState().inAudioChannel())
                Main.getInstance().getMusicWorker().loadAndPlaySilence(commandEvent.getChannel(), member.getVoiceState().getChannel(), "https://soundcloud.com/ikill4fun23/sayori?in=ikill4fun23/sets/stream-stuff", commandEvent.getInteractionHook());
        }));

        TwitchRedemption.subscriptionList.add(Main.getInstance().getNotifier().getTwitchClient().getEventManager().onEvent(ChannelBitsEvent.class, channelBitsEvent -> {
            Member member = Objects.requireNonNull(BotWorker.getShardManager().getGuildById(TwitchRedemption.streamerGuildId)).getMemberById(TwitchRedemption.streamerUserId);

            if (!channelBitsEvent.getData().getChannelId().equals("47397687") || member == null) {
                return;
            }

            if (member.getVoiceState() != null && member.getVoiceState().inAudioChannel()) {
                Main.getInstance().getMusicWorker().loadAndPlaySilence(commandEvent.getChannel(), member.getVoiceState().getChannel(), "https://soundcloud.com/noticemesenpai/sayori?in=ikill4fun23/sets/stream-stuff", commandEvent.getInteractionHook());

                String input = channelBitsEvent.getData().getChatMessage();
                Path of1 = Path.of(of.toString(), "bitsTTS.mp3");
                byte[] fileInfo = new byte[0];
                try {
                    fileInfo = createTTS(input);
                } catch (InterruptedException | IOException e) {
                    Main.getInstance().getLogger().error("Error while creating bitsTT file: " + e.getMessage());
                }

                try (FileOutputStream fileOutputStream = new FileOutputStream(of1.toString())) {
                    fileOutputStream.write(fileInfo);
                } catch (Exception exception) {
                    Main.getInstance().getLogger().error("Could not create file!", exception);
                }

                Main.getInstance().getMusicWorker().loadAndPlaySilence(commandEvent.getChannel(), member.getVoiceState().getChannel(), of1.toString(), commandEvent.getInteractionHook());
            }
        }));

        OAuth2Credential credential = null;
        try {
            credential = new OAuth2Credential("twitch", Files.readString(of.resolve("twitch.creds")));
        } catch (IOException e) {
            Main.getInstance().getLogger().error("Could not read Twitch credentials!", e);
        }

        Main.getInstance().getNotifier().getTwitchClient().getPubSub().listenForChannelPointsRedemptionEvents(credential, "47397687");
        Main.getInstance().getNotifier().getTwitchClient().getPubSub().listenForSubscriptionEvents(credential, "47397687");
        Main.getInstance().getNotifier().getTwitchClient().getPubSub().listenForUserBitsUpdateEvents(credential, "47397687");
        Main.getInstance().getCommandManager().sendMessage("Understood!\nI will now join the voicechannel and start listing for redeems!", commandEvent.getChannel(), commandEvent.getInteractionHook());

        TwitchRedemption.isRunning = true;
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

    public static byte[] createTTS(String text) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.streamelements.com/kappa/v2/speech?voice=Brian&text=" + URLEncoder.encode(text, StandardCharsets.UTF_8))).header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Safari/537.36").GET().build();
        HttpResponse<InputStream> httpResponse = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofInputStream());
        try (InputStream inputStream = httpResponse.body()) {
            return inputStream.readAllBytes();
        } catch (Exception ignore) {
        }

        return new byte[512];
    }
}
