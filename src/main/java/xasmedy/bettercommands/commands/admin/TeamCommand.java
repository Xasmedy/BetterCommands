/*
 * Copyright (c) 2023 - Xasmedy.
 * This file is part of the BetterCommands Project licensed under GNU-GPLv3.
 *
 * The Project source-code can be found at https://github.com/Xasmedy/BetterCommands
 * Contributors of this file may put their name into the copyright notice.
 */

package xasmedy.bettercommands.commands.admin;

import arc.util.CommandHandler;
import arc.util.Strings;
import mindustry.gen.Player;
import xasmedy.bettercommands.Util;
import xasmedy.bettercommands.commands.Command;
import static xasmedy.bettercommands.Util.NOT_ENOUGH_PERMISSION;
import static xasmedy.bettercommands.Util.PREFIX;

public class TeamCommand implements Command {

    private static final String TEAM_CHANGE_MESSAGE = "%s[orange]Your team has been changed to [#%s]%s[orange].";
    private static final String TEAM_NOT_FOUND_ERROR = "%s[scarlet]The team [orange]%s[] does not exist.";

    private void commandAction(String[] args, Player player) {

        if (!player.admin()) {
            player.sendMessage(NOT_ENOUGH_PERMISSION);
            return;
        }

        args[0] = Strings.stripColors(args[0]);

        Util.findTeam(args[0]).ifPresentOrElse(team -> {

            player.team(team);

            final String message = String.format(TEAM_CHANGE_MESSAGE, PREFIX, team.color, team.name.toLowerCase());
            player.sendMessage(message);

        }, () -> {
            final String message = String.format(TEAM_NOT_FOUND_ERROR, PREFIX, args[0]);
            player.sendMessage(message);
        });
    }

    @Override
    public void registerClientCommands(CommandHandler handler) {
        // TODO Being able to change other players team.
        // TODO Change other player teams with a timer.
        handler.register("team", "<team>", "Changes the current controlled unit team.", this::commandAction);
    }
}
