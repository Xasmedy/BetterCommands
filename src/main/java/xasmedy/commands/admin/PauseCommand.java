package xasmedy.commands.admin;

import arc.util.Log;
import mindustry.gen.Call;
import mindustry.gen.Player;
import static mindustry.Vars.state;
import static xasmedy.Util.namePrefix;

public class PauseCommand {

    public static void onPauseCommand(String[] args, Player admin) {

        if (admin.admin && args.length > 0) {

            if (!state.serverPaused && args[0].equalsIgnoreCase("on")) {

                Call.sendMessage(namePrefix + "[accent]The game has been paused by " + admin.name);
                Log.info("Game paused by " + admin.name);
                state.serverPaused = true;

            } else if (state.serverPaused && args[0].equalsIgnoreCase("off")) {

                Call.sendMessage(namePrefix + "[accent]The game has been unpaused by " + admin.name);
                Log.info("Game unpaused by " + admin.name);
                state.serverPaused = false;

            } else admin.sendMessage(namePrefix + "[scarlet]You can't pause/unpause if a server is already paused/unpaused.");
        }
    }
}
