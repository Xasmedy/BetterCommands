/*
 * Copyright (c) 2023 - Xasmedy.
 * This file is part of the BetterCommands Project licensed under GNU-GPLv3.
 *
 * The Project source-code can be found at https://github.com/Xasmedy/BetterCommands
 * Contributors of this file may put their name into the copyright notice.
 */

package xasmedy.bettercommands.commands.admin;

import arc.Events;
import arc.util.Strings;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.gen.Player;
import xasmedy.mapie.command.AbstractCommand;
import java.util.Arrays;
import java.util.Optional;
import static xasmedy.bettercommands.Util.NOT_ENOUGH_PERMISSION;
import static xasmedy.bettercommands.Util.PREFIX;

public class GameOverCommand extends AbstractCommand {

    private static final String GAMEOVER_MESSAGE = "%s[red]Game-over by [gold]%s[]!";
    private static final String INVALID_TEAM_MESSAGE = "%s[scarlet]The team [orange]%s[] is not valid.";

    private static Optional<Team> getBaseTeamFromString(String rawTeam) {

        // I return the default.
        if (rawTeam == null) return Optional.of(Team.sharded);

        // You never know what the user will do.
        final String toSearch = Strings.stripColors(rawTeam);
        return Arrays.stream(Team.baseTeams)
                .filter(team -> team.name.equalsIgnoreCase(toSearch))
                .findFirst();
    }

    @Override
    public String name() {
        return "gameover";
    }

    @Override
    public String params() {
        return "[winner-team]";
    }

    @Override
    public String description() {
        return "Forces a game-over.";
    }

    @Override
    public boolean hasRequiredRoles(Player player, String[] args) {
        return player.admin();
    }

    @Override
    public void clientAction(Player player, String[] args) {

        final String message = String.format(GAMEOVER_MESSAGE, PREFIX, player.plainName());
        Call.sendMessage(message);

        getBaseTeamFromString(args.length == 0 ? null : args[0]).ifPresentOrElse(team -> Events.fire(new EventType.GameOverEvent(team)), () -> {
            final String errorMessage = String.format(INVALID_TEAM_MESSAGE, PREFIX, args[0]);
            player.sendMessage(errorMessage);
        });
    }

    @Override
    public void noPermissionsAction(Player player, String[] args) {
        player.sendMessage(NOT_ENOUGH_PERMISSION);
    }
}
