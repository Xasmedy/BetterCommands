/*
 * Copyright (c) 2023 - Xasmedy.
 * This file is part of the BetterCommands Project licensed under GNU-GPLv3.
 *
 * The Project source-code can be found at https://github.com/Xasmedy/BetterCommands
 * Contributors of this file may put their name into the copyright notice.
 */

package xasmedy.bettercommands.commands.admin;

import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import xasmedy.bettercommands.PlayerInfo;
import xasmedy.bettercommands.Util;
import java.util.ArrayList;

// TODO Move this inside a menu instead?
public class KickCommand {

    public static void onKickCommand(String[] args, Player admin) {

        if (admin.admin && args.length > 0) {

            ArrayList<PlayerInfo> information = Util.info(args[0]);

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
                                admin.sendMessage(Util.PREFIX + "[accent]" + ip + " [white]is [orange]kicked. [gold][" + i++ + "]");
                                player.con.kick(Util.reason("kicked", args, admin));
                                Call.sendMessage(Util.PREFIX + "[scarlet]" + player.name + " [scarlet]has been kicked by " + admin.name);
                            }
                        }
                    }
                } if (!online) admin.sendMessage(Util.PREFIX + "[scarlet]The player is not online.");
            } else admin.sendMessage(Util.PREFIX + "[scarlet]Nobody could be found, check if you did any mistakes.");
        }
    }
}
