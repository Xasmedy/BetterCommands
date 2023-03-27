// Author: Phinner

package xasmedy.bettercommands.commands.admin;

import arc.Events;
import mindustry.game.EventType;
import mindustry.gen.Call;
import mindustry.gen.Player;
import xasmedy.bettercommands.Util;

import static mindustry.Vars.spawner;
import static mindustry.Vars.state;

public class RepeatWaveCommand {

    public static void onRepeatWave(String[] args, Player admin) {

        if (admin.admin && args.length > 0) {

            int waves = Integer.parseInt(args[0]);

            // Run multiple waves.
            if (waves <= 10) {

                for (int i = 0; i < waves; i++) {

                    spawner.spawnEnemies();
                    Events.fire(new EventType.WaveEvent());
                }
                Call.sendMessage(Util.namePrefix + "[scarlet]WARNING! [accent]The wave " + state.wave + " have been triggered "
                        + args[0] + " time" + ((waves == 1) ? "" : "s") + " by " + admin.name);

            } else admin.sendMessage(Util.namePrefix + "[scarlet]You can't put values greater than 10 for lag purpose.");
        }
    }
}
