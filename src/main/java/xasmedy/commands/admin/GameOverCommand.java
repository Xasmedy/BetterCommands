// Author: Phinner

package xasmedy.commands.admin;

import arc.Events;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.gen.Player;
import static xasmedy.Util.namePrefix;

public class GameOverCommand {

    public static void onGameOverCommand(Player admin) {

        if (admin.admin) {

            Events.fire(new EventType.GameOverEvent(Team.crux));
            Call.sendMessage(namePrefix + "[scarlet]GameOver by [white]" + admin.name + "! [accent]Nice run people ;)");
        }
    }
}
