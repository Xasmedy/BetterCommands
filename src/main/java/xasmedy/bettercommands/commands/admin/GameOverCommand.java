/*
 * Copyright (c) 2023 - Xasmedy.
 * This file is part of the BetterCommands Project licensed under GNU-GPLv3.
 *
 * The Project source-code can be found at https://github.com/Xasmedy/BetterCommands
 * Contributors of this file may put their name into the copyright notice.
 */

package xasmedy.bettercommands.commands.admin;

import arc.Events;
import arc.util.CommandHandler;
import arc.util.Strings;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.gen.Player;
import xasmedy.bettercommands.commands.Command;
import java.util.Arrays;
import java.util.Optional;
import static xasmedy.bettercommands.Util.PREFIX;

public class GameOverCommand implements Command {

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

    private void commandAction(String[] args, Player player) {

        if (!player.admin) {
            // TODO Not enough permissions message.
            return;
        }

        final String message = String.format(GAMEOVER_MESSAGE, PREFIX, player.plainName());
        Call.sendMessage(message);

        getBaseTeamFromString(args.length == 0 ? null : args[0]).ifPresentOrElse(team -> Events.fire(new EventType.GameOverEvent(team)), () -> {
            final String errorMessage = String.format(INVALID_TEAM_MESSAGE, PREFIX, args[0]);
            player.sendMessage(errorMessage);
        });
    }

    @Override
    public void registerClientCommands(CommandHandler handler) {
        handler.register("gameover", "[winner-team]", "Forces a game-over.", this::commandAction);
    }
}
