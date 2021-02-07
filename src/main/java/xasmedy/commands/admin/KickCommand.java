package xasmedy.commands.admin;

import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import xasmedy.PlayerInfo;
import java.util.ArrayList;
import static xasmedy.Util.*;
import static xasmedy.Util.namePrefix;

public class KickCommand {

    public static void onKickCommand(String[] args, Player admin) {

        if (admin.admin && args.length > 0) {

            ArrayList<PlayerInfo> information = info(args[0]);

            // Check if the info of the player has been found.
            if (information.size() > 0) {

                boolean online = false;

                // Get the IPs.
                for (PlayerInfo playerInfo : information) {

                    String[] ips = playerInfo.ips.toString().replace(" ", "")
                            .replace("[", "").replace("]", "").split(",");

                    // Kicked IPs counter.
                    int i = 1;

                    // Kick the IPs.
                    for (String ip : ips) {

                        for (Player player : Groups.player) {

                            if (player.ip().equals(ip)) {

                                online = true;
                                admin.sendMessage(namePrefix + "[accent]" + ip + " [white]is [orange]kicked. [gold][" + i++ + "]");
                                player.con.kick(reason("kicked", args, admin));
                                Call.sendMessage(namePrefix + "[scarlet]" + player.name + " [scarlet]has been kicked by " + admin.name);
                            }
                        }
                    }
                } if (!online) admin.sendMessage(namePrefix + "[scarlet]The player is not online.");
            } else admin.sendMessage(namePrefix + "[scarlet]Nobody could be found, check if you did any mistakes.");
        }
    }
}
