/*
 * Copyright (c) 2023 - Xasmedy.
 * This file is part of the BetterCommands Project licensed under GNU-GPLv3.
 *
 * The Project source-code can be found at https://github.com/Xasmedy/BetterCommands
 * Contributors of this file may put their name into the copyright notice.
 */

package xasmedy.bettercommands.commands.admin;

import arc.util.CommandHandler;
import mindustry.gen.Player;
import xasmedy.bettercommands.commands.Command;
import java.util.HashMap;
import static xasmedy.bettercommands.Util.NOT_ENOUGH_PERMISSION;

public class ImmortalCommand implements Command {

    private final HashMap<Player, Float> immortalPlayers = new HashMap<>();

    private void commandAction(String[] args, Player player) {

        if (!player.admin()) {
            player.sendMessage(NOT_ENOUGH_PERMISSION);
            return;
        }

        final Float originalShield = immortalPlayers.remove(player);
        if (originalShield == null) { // The player isn't immortal.
            immortalPlayers.put(player, player.unit().shield());
            player.unit().shield(Float.MAX_VALUE);
        } else player.unit().shield(originalShield);
    }

    @Override
    public void registerClientCommands(CommandHandler handler) {
        handler.register("immortal", "Gives you infinite shield.", this::commandAction);
    }
}
