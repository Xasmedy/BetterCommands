/*
 * Copyright (c) 2023 - Xasmedy.
 * This file is part of the BetterCommands Project licensed under GNU-GPLv3.
 *
 * The Project source-code can be found at https://github.com/Xasmedy/BetterCommands
 * Contributors of this file may put their name into the copyright notice.
 */

package xasmedy.bettercommands.commands.admin;

import arc.util.Strings;
import mindustry.gen.Player;
import xasmedy.bettercommands.Util;
import xasmedy.mapie.command.AbstractCommand;
import static xasmedy.bettercommands.Util.NOT_ENOUGH_PERMISSION;
import static xasmedy.bettercommands.Util.PREFIX;

public class TeamCommand extends AbstractCommand {

    private static final String TEAM_CHANGE_MESSAGE = "%s[orange]Your team has been changed to [#%s]%s[orange].";
    private static final String TEAM_NOT_FOUND_ERROR = "%s[scarlet]The team [orange]%s[] does not exist.";

    @Override
    public String name() {
        return "team";
    }

    @Override
    public String params() {
        return "<team>";
    }

    @Override
    public String description() {
        return "Changes the player team.";
    }

    @Override
    public boolean hasRequiredRoles(Player player, String[] args) {
        return player.admin();
    }

    @Override
    public void clientAction(Player player, String[] args) {

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
    public void noPermissionsAction(Player player, String[] args) {
        player.sendMessage(NOT_ENOUGH_PERMISSION);
    }
}
