/*
 * Copyright (c) 2023 - Xasmedy.
 * This file is part of the BetterCommands Project licensed under GNU-GPLv3.
 *
 * The Project source-code can be found at https://github.com/Xasmedy/BetterCommands
 * Contributors of this file may put their name into the copyright notice.
 */

package xasmedy.bettercommands.commands.admin;

import arc.util.CommandHandler;
import mindustry.core.GameState;
import mindustry.gen.Call;
import mindustry.gen.Player;
import xasmedy.bettercommands.commands.Command;
import static mindustry.Vars.state;
import static xasmedy.bettercommands.Util.PREFIX;

public class PauseCommand implements Command {

    private static final String PAUSE_MESSAGE = "%s[red]The game has been %spaused by [gold]%s";

    private void commandAction(String[] lazy, Player player) {

        if (!player.admin) {
            // TODO No permissions.
            return;
        }

        final String message = String.format(PAUSE_MESSAGE, PREFIX, state.isPaused() ? "un-" : "", player.plainName());
        state.set(state.isPaused() ? GameState.State.playing : GameState.State.paused);
        Call.sendMessage(message);
    }

    @Override
    public void registerClientCommands(CommandHandler handler) {
        handler.register("pause", "Pauses/Unpauses the game.", this::commandAction);
    }
}
