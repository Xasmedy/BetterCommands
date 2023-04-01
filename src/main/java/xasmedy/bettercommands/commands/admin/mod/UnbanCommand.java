/*
 * Copyright (c) 2023 - Xasmedy.
 * This file is part of the BetterCommands Project licensed under GNU-GPLv3.
 *
 * The Project source-code can be found at https://github.com/Xasmedy/BetterCommands
 * Contributors of this file may put their name into the copyright notice.
 */

package xasmedy.bettercommands.commands.admin.mod;

import arc.util.Log;
import mindustry.gen.Player;
import xasmedy.bettercommands.PlayerInfo;
import xasmedy.bettercommands.Util;
import java.util.ArrayList;
import static mindustry.Vars.netServer;

// TODO Move this inside a menu instead?
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
                        admin.sendMessage(Util.PREFIX + "[accent]" + ip + " [white]is [green]unbanned. [gold][" + i++ + "]");
                    }
                }
            } else admin.sendMessage(Util.PREFIX + "[scarlet]Nobody could be found, check if you did any mistakes.");
        }
    }
}
