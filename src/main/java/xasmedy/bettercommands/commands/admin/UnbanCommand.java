package xasmedy.bettercommands.commands.admin;

import arc.util.Log;
import mindustry.gen.Player;
import xasmedy.bettercommands.PlayerInfo;
import xasmedy.bettercommands.Util;

import java.util.ArrayList;
import static mindustry.Vars.netServer;

public class UnbanCommand {

    public static void onUnbanCommand(String[] args, Player admin) {

        if (admin.admin && args.length > 0) {

            ArrayList<PlayerInfo> information = Util.info(args[0]);

            // Check if the info of the player has been found.
            if (information.size() > 0) {

                // Unbanned IPs counter.
                int i = 1;

                // Unban every ip found.
                for (PlayerInfo playerInfo : information) {

                    String[] ips = playerInfo.ips.toString().replace(" ", "")
                            .replace("[", "").replace("]", "").split(",");

                    for (String ip : ips) {

                        netServer.admins.unbanPlayerIP(ip);
                        Log.info(ip + " is unbanned by " + admin.name + " (" + admin.ip() + ")");
                        admin.sendMessage(Util.namePrefix + "[accent]" + ip + " [white]is [green]unbanned. [gold][" + i++ + "]");
                    }
                }
            } else admin.sendMessage(Util.namePrefix + "[scarlet]Nobody could be found, check if you did any mistakes.");
        }
    }
}
