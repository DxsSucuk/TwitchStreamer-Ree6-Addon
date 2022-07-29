package de.presti.ree6.derpedcrusader.main;

import de.presti.ree6.addons.AddonInterface;
import de.presti.ree6.commands.exceptions.CommandInitializerException;
import de.presti.ree6.derpedcrusader.command.StreamStarted;
import de.presti.ree6.main.Main;

public class DerpedCrusader implements AddonInterface {

    @Override
    public void onEnable() {
        try {
            Main.getInstance().getCommandManager().addCommand(new StreamStarted());
        } catch (CommandInitializerException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDisable() {

    }
}
