/*
 * Copyright (c) 2023 - Xasmedy.
 * This file is part of the BetterCommands Project licensed under GNU-GPLv3.
 *
 * The Project source-code can be found at https://github.com/Xasmedy/BetterCommands
 * Contributors of this file may put their name into the copyright notice.
 */

package xasmedy.bettercommands.commands.admin;

import mindustry.core.GameState;
import mindustry.gen.Call;
import mindustry.gen.Player;
import xasmedy.mapie.command.AbstractCommand;
import static mindustry.Vars.state;
import static xasmedy.bettercommands.Util.NOT_ENOUGH_PERMISSION;
import static xasmedy.bettercommands.Util.PREFIX;

public class PauseCommand extends AbstractCommand {

    private static final String PAUSE_MESSAGE = "%s[red]The game has been %spaused by [gold]%s";

    @Override
    public String name() {
        return "pause";
    }

    @Override
    public String description() {
        return "Pauses/Unpauses the game.";
    }

    @Override
    public boolean hasRequiredRoles(Player player, String[] args) {
        return player.admin();
    }

    @Override
    public void clientAction(Player player, String[] args) {
        final String message = String.format(PAUSE_MESSAGE, PREFIX, state.isPaused() ? "un-" : "", player.plainName());
        state.set(state.isPaused() ? GameState.State.playing : GameState.State.paused);
        Call.sendMessage(message);
    }

    @Override
    public void noPermissionsAction(Player player, String[] args) {
        player.sendMessage(NOT_ENOUGH_PERMISSION);
    }
}
