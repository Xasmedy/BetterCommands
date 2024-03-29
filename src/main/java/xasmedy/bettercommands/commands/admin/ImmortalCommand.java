/*
 * Copyright (c) 2023 - Xasmedy.
 * This file is part of the BetterCommands Project licensed under GNU-GPLv3.
 *
 * The Project source-code can be found at https://github.com/Xasmedy/BetterCommands
 * Contributors of this file may put their name into the copyright notice.
 */

package xasmedy.bettercommands.commands.admin;

import mindustry.gen.Player;
import xasmedy.mapie.command.AbstractCommand;
import java.util.HashMap;
import static xasmedy.bettercommands.Util.NOT_ENOUGH_PERMISSION;
import static xasmedy.bettercommands.Util.PREFIX;

public class ImmortalCommand extends AbstractCommand {

    private static final String IMMORTAL_MESSAGE = "%s[orange]You now have infinite shield.";
    private static final String MORTAL_MESSAGE = "%s[orange]You are now mortal.";
    private final HashMap<Player, Float> immortalPlayers = new HashMap<>();

    @Override
    public String name() {
        return "immortal";
    }

    @Override
    public String description() {
        return "Gives you max shield.";
    }

    @Override
    public boolean hasRequiredRoles(Player player, String[] args) {
        return player.admin();
    }

    @Override
    public void clientAction(Player player, String[] args) {

        final Float originalShield = immortalPlayers.remove(player);
        if (originalShield == null) { // The player isn't immortal.
            immortalPlayers.put(player, player.unit().shield());
            player.unit().shield(Float.MAX_VALUE);
            final String message = String.format(IMMORTAL_MESSAGE, PREFIX);
            player.sendMessage(message);
            return;
        }

        player.unit().shield(originalShield);
        final String message = String.format(MORTAL_MESSAGE, PREFIX);
        player.sendMessage(message);
    }

    @Override
    public void noPermissionsAction(Player player, String[] args) {
        player.sendMessage(NOT_ENOUGH_PERMISSION);
    }
}
