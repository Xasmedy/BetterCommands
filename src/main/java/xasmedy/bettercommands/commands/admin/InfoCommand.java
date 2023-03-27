/*
 * Copyright (c) 2023 - Xasmedy.
 * This file is part of the BetterCommands Project licensed under GNU-GPLv3.
 *
 * The Project source-code can be found at https://github.com/Xasmedy/BetterCommands
 * Contributors of this file may put their name into the copyright notice.
 */

package xasmedy.bettercommands.commands.admin;

import arc.util.Log;
import mindustry.gen.Player;
import xasmedy.bettercommands.PlayerInfo;
import xasmedy.bettercommands.Util;

import java.util.ArrayList;

// TODO Refactor using menus.
public class InfoCommand {

    public static void onInfoCommand(String[] args, Player admin) {

        if (admin.admin && args.length > 0) {

            ArrayList<PlayerInfo> information = Util.info(args[0]);

            // Check if the info of the player has been found.
            if (information.size() > 0) {

                Log.info("Admin: " + admin.name + " required information of the player: " + information.get(0).lastName + "/" + information.get(0).lastIP);

                admin.sendMessage("[white]---------------------------------\n" +
                        Util.PREFIX + "[gold]Player(s) found: [white]" + information.size());

                // Counter of how much info-list found.
                int i = 1;

                for (PlayerInfo playerInfo : information) {

                    admin.sendMessage("[gold][" + i++ + "] [white]Trace info of the player: [accent]'" + playerInfo.lastName + "[accent]'[white] / UUID [accent]'" + playerInfo.id + "[accent]'" +
                            "\n[white] - All names used: [accent]" + playerInfo.names +
                            "\n[white] - IP: [accent]" + playerInfo.lastIP +
                            "\n[white] - All IPs used: [accent]" + playerInfo.ips +
                            "\n[white] - Times joined: [green]" + playerInfo.timesJoined +
                            "\n[white] - Times kicked: [scarlet]" + playerInfo.timesKicked +
                            "\n[white] - Banned: [orange]" + playerInfo.banned +
                            "\n[white]---------------------------------");
                }
            } else admin.sendMessage(Util.PREFIX + "[scarlet]Nobody could be found, check if you did any mistakes.");
        }
    }
}
