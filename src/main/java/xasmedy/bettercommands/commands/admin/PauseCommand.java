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
import xasmedy.bettercommands.AbstractAdminCommand;

import static mindustry.Vars.state;
import static xasmedy.bettercommands.Util.PREFIX;

public class PauseCommand extends AbstractAdminCommand {

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
    public void clientAction(Player player, String[] args) {
        final String message = String.format(PAUSE_MESSAGE, PREFIX, state.isPaused() ? "un-" : "", player.plainName());
        state.set(state.isPaused() ? GameState.State.playing : GameState.State.paused);
        Call.sendMessage(message);
    }
}
