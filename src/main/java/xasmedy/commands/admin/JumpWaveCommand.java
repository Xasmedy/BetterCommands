package xasmedy.commands.admin;

import mindustry.gen.Call;
import mindustry.gen.Player;
import static mindustry.Vars.state;
import static xasmedy.Util.namePrefix;

public class JumpWaveCommand {

    public static void onJumpWave(String[] args, Player admin) {

        if (admin.admin && args.length > 0) {

            state.wave = Integer.parseInt(args[0]);
            state.wavetime = state.rules.waveSpacing;
            Call.sendMessage(namePrefix + "[scarlet]WARNING! [accent]Jumped to wave " + args[0] + ", by " + admin.name);
        }
    }
}
