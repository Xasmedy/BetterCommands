/*
 * Copyright (c) 2023 - Xasmedy.
 * This file is part of the BetterCommands Project licensed under GNU-GPLv3.
 *
 * The Project source-code can be found at https://github.com/Xasmedy/BetterCommands
 * Contributors of this file may put their name into the copyright notice.
 */

package xasmedy.bettercommands.commands.admin;

import arc.util.Log;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import xasmedy.bettercommands.PlayerInfo;
import xasmedy.bettercommands.Util;

import java.util.ArrayList;
import static mindustry.Vars.netServer;

// TODO Move this inside a menu instead?
public class BanCommand {

    public static void onBanCommand(String[] args, Player admin) {

        if (admin.admin && args.length > 0) {

            ArrayList<PlayerInfo> information = Util.info(args[0]);

            // Check if the info of the player has been found.
            if (information.size() > 0) {

                boolean present = false;

                // Check if the admin IP is one of the IPs that wants to be banned.
                for (PlayerInfo playerInfo : information) {

                    String[] ips = playerInfo.ips.toString().replace(" ", "")
                            .replace("[", "").replace("]", "").split(",");

                    // Compare all the IPs.
                    for (String ip : ips) {
                        if (ip.contains(admin.ip())) present = true;
                    }
                }

                if (!present) {

                    // Banned IPs counter.
                    int i = 1;

                    // Get IPs.
                    // Get IPs.
                    for (PlayerInfo playerInfo : information) {

                        String[] ips = playerInfo.ips.toString().replace(" ", "")
                                .replace("[", "").replace("]", "").split(",");


                        // Ban IPs found.
                        for (String ip : ips) {

                            netServer.admins.banPlayerIP(ip);
                            Log.info(ip + " is banned by " + admin.name + " (" + admin.ip() + ")");
                            admin.sendMessage(Util.PREFIX + "[accent]" + ip + " [white]is [scarlet]banned. [gold][" + i++ + "]");

                        }

                        // Kick banned IPs.
                        for (Player player : Groups.player) {

                            if (player.getInfo().banned) {


                                if (player.getInfo().timesJoined > 30)
                                    
                                player.con.kick(Util.reason("banned", args, admin));
                                Call.sendMessage(Util.PREFIX + "[scarlet]" + player.name + " [scarlet]has been banned by " + admin.name);
                            }
                        }
                    }
                } else admin.sendMessage(Util.PREFIX + "[scarlet]I will not unban you if you ban yourself.");
            } else admin.sendMessage(Util.PREFIX + "[scarlet]Nobody could be found, check if you did any mistakes.");
        }
    }
}
