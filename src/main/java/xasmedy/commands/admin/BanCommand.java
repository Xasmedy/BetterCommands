package xasmedy.commands.admin;

import arc.util.Log;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import xasmedy.PlayerInfo;
import java.util.ArrayList;
import static mindustry.Vars.netServer;
import static xasmedy.Util.*;
import static xasmedy.Util.namePrefix;

public class BanCommand {

    public static void onBanCommand(String[] args, Player admin) {

        if (admin.admin && args.length > 0) {

            ArrayList<PlayerInfo> information = info(args[0]);

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
                            admin.sendMessage(namePrefix + "[accent]" + ip + " [white]is [scarlet]banned. [gold][" + i++ + "]");

                        }

                        // Kick banned IPs.
                        for (Player player : Groups.player) {

                            if (player.getInfo().banned) {


                                if (player.getInfo().timesJoined > 30)
                                    
                                player.con.kick(reason("banned", args, admin));
                                Call.sendMessage(namePrefix + "[scarlet]" + player.name + " [scarlet]has been banned by " + admin.name);
                            }
                        }
                    }
                } else admin.sendMessage(namePrefix + "[scarlet]I will not unban you if you ban yourself.");
            } else admin.sendMessage(namePrefix + "[scarlet]Nobody could be found, check if you did any mistakes.");
        }
    }
}
