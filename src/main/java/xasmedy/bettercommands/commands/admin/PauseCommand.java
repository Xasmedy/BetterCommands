package xasmedy.bettercommands.commands.admin;

import arc.util.Log;
import mindustry.gen.Call;
import mindustry.gen.Player;
import xasmedy.bettercommands.Util;

import static mindustry.Vars.state;

public class PauseCommand {

    public static void onPauseCommand(String[] args, Player admin) {

        if (admin.admin && args.length > 0) {

            if (!state.serverPaused && args[0].equalsIgnoreCase("on")) {

                Call.sendMessage(Util.namePrefix + "[accent]The game has been paused by " + admin.name);
                Log.info("Game paused by " + admin.name);
                state.serverPaused = true;

            } else if (state.serverPaused && args[0].equalsIgnoreCase("off")) {

                Call.sendMessage(Util.namePrefix + "[accent]The game has been unpaused by " + admin.name);
                Log.info("Game unpaused by " + admin.name);
                state.serverPaused = false;

            } else admin.sendMessage(Util.namePrefix + "[scarlet]You can't pause/unpause if a server is already paused/unpaused.");
        }
    }
}
