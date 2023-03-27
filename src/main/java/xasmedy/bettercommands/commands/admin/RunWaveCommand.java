// Author: Phinner

package xasmedy.bettercommands.commands.admin;

import mindustry.gen.Call;
import mindustry.gen.Player;
import xasmedy.bettercommands.Util;

import static mindustry.Vars.logic;

public class RunWaveCommand {

    public static void onRunWaveCommand(String[] args, Player admin) {

        if (admin.admin && args.length > 0) {

            int waves = Integer.parseInt(args[0]);

            // Run multiple waves.
            if (waves <= 10) {

                for (int i = 0; i < waves; i++) logic.runWave();

                Call.sendMessage(Util.namePrefix + "[scarlet]WARNING! [accent]" + args[0] + " Wave" + ((waves == 1) ? "" : "s") + " spawned by " + admin.name);

            } else admin.sendMessage(Util.namePrefix + "[scarlet]You can't put values greater than 10 for lag purpose.");
        }
    }
}