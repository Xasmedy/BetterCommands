// Author: Xasmedy
// The code can be copied and used, but if you want use the plugin you need to give credits to mindustry.ddns.net

import arc.struct.ObjectSet;
import arc.util.CommandHandler;
import arc.util.Log;

import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.mod.Plugin;
import mindustry.net.Administration;
import mindustry.net.Packets;

// WaveManager Depedencies
import arc.Events;
import mindustry.game.Team;
import mindustry.game.EventType.*;
import static mindustry.Vars.netServer;
import static mindustry.Vars.state;
import static mindustry.Vars.logic;
import static mindustry.Vars.spawner;

public class BetterCommands extends Plugin {

    public void registerClientCommands(CommandHandler handler) {

        handler.<Player>register("info", "[UUID|IP]", "Get all the player information.", (args, admin) -> {

            if (admin.admin) {

                ObjectSet<Administration.PlayerInfo> infos = netServer.admins.findByName(args[0]);

                if (infos.size > 0 && args.length > 1) {

                    int i = 1;
                    admin.sendMessage("------------------------------------------");
                    admin.sendMessage("[gold]Players found: [white]" + infos.size);

                    for (Administration.PlayerInfo info : infos) {
                        admin.sendMessage("[gold][" + i++ + "] [white]Trace info for admin [accent]'" + info.lastName + "[accent]'[white] / UUID [accent]'" + info.id + "'");
                        admin.sendMessage("- All names used: [accent]" + info.names);
                        admin.sendMessage("- IP: [accent]" + info.lastIP);
                        admin.sendMessage("- All IPs used: [accent]" + info.ips);
                        admin.sendMessage("- Times joined: [green]" + info.timesJoined);
                        admin.sendMessage("- Times kicked: [scarlet]" + info.timesKicked);
                        admin.sendMessage("------------------------------------------");
                    }
                } else admin.sendMessage("[accent]Nobody could be found, check if you did any mistake.");
            }
        });

        handler.<Player>register("ban", "[IP]", "Ban all the IPs a player has.", (args, admin) -> {

            if (admin.admin) {

                if (args.length > 0 && args[0].contains(".")) {

                    ObjectSet<Administration.PlayerInfo> infos = netServer.admins.findByName(args[0]);

                    boolean present = false;

                    // I check if the admin ip is one of the ip that will get banned.
                    for (Administration.PlayerInfo info : infos) {

                        String clearIps = info.ips.toString().replace(" ", "").replace("[", "").replace("]", "");
                        String[] ips = clearIps.split(",");

                        for (String ip : ips) {
                            if (ip.contains(admin.ip())) present = true;
                        }
                    }

                    if (!present) {

                        int i = 1;

                        // Ban every ip found.
                        for (Administration.PlayerInfo info : infos) {

                            String clearIps = info.ips.toString().replace(" ", "").replace("[", "").replace("]", "");
                            String[] ips = clearIps.split(",");

                            for (String ip : ips) {
                                netServer.admins.banPlayerIP(ip);
                                Log.info(ip + " is banned by " + admin.name + " (" + admin.ip() + ")");
                                admin.sendMessage("[gold][" + i++ + "] [accent]" + ip + " [white]is [scarlet]banned.");
                            }

                            // Kick every uuid online.
                            for (Player player : Groups.player) {

                                if (netServer.admins.isIDBanned(player.uuid())) {
                                    Call.sendMessage(player.name + " [scarlet]has been banned by " + admin.name);
                                    player.con.kick(Packets.KickReason.banned);
                                }
                            }
                        }
                    } else admin.sendMessage("[scarlet]I will not unban you if you ban yourself.");
                } else admin.sendMessage("[scarlet]The value is not valid, please use an IP.");
            }
        });

        handler.<Player>register("unban", "[IP]", "Unban all the IPs a player has.", (args, admin) -> {

            if (admin.admin) {

                if (args.length > 0 && args[0].contains(".")) {

                    ObjectSet<Administration.PlayerInfo> infos = netServer.admins.findByName(args[0]);
                    int i = 1;

                    // Unban every ip found.
                    for (Administration.PlayerInfo info : infos) {

                        String clearIps = info.ips.toString().replace(" ", "").replace("[", "").replace("]", "");
                        String[] ips = clearIps.split(",");

                        for (String ip : ips) {
                            netServer.admins.unbanPlayerIP(ip);
                            Log.info(ip + " is unbanned by " + admin.name + " (" + admin.ip() + ")");
                            admin.sendMessage("[gold][" + i++ + "] [accent]" + ip + " [white]is [green]unbanned.");
                        }
                    }
                } else admin.sendMessage("[scarlet]The value is not valid, please use an IP.");
            }
        });

        // WaveManager Commands
        handler.<Player>register("runwave", "<count...>", "Trigger the next waves.", (args, player) -> {
            if (player.admin) {
                int w = Integer.parseInt(args[0]);
                if (0 < w && w <= 10) {
                    for (int i = 0; i < w; i++) logic.runWave();
                    Call.sendMessage("[scarlet]WARNING: [accent]" + args[0] + " wave" + ((w == 1) ? "" : "s") + " spawned.");
                } else player.sendMessage("[scarlet]The wave count have to be between 1 and 10. Going higher may crash the server.");
            } else player.sendMessage("[scarlet]Sorry, this command is for admins only.");
        });

        handler.<Player>register("repeatwave", "<count...>", "Repeat the current wave.", (args, player) -> {
            if (player.admin) {
                int w = Integer.parseInt(args[0]);
                if (0 < w && w <= 10) {
                    for (int i = 0; i < w; i++) {
                    spawner.spawnEnemies();
                    Events.fire(new WaveEvent());
                    } Call.sendMessage("[scarlet]WARNING: [accent]The wave " + state.wave + " have been triggered " + args[0] + " time" + ((w == 1) ? "" : "s") + ".");
                } else player.sendMessage("[scarlet]The wave count have to be between 1 and 10. Going higher may crash the server.");
            } else player.sendMessage("[scarlet]Sorry, this command is for admins only.");
        });

        handler.<Player>register("jumpwave", "<wave...>", "Jump to a specific wave.", (args, player) -> {
            if (player.admin) {
                int w = Integer.parseInt(args[0]);
                state.wave = w;
                state.wavetime = state.rules.waveSpacing;
                Call.sendMessage("[scarlet]WARNING: [accent]Jumped to wave " + args[0] + ".");
            } else player.sendMessage("[scarlet]Sorry, this command is for admins only.");
        });

    	handler.<Player>register("gameover", "Force a gameover.", (args, player) -> {
            if (player.admin) {
                Events.fire(new GameOverEvent(Team.crux));
                Call.sendMessage("[scarlet]GAMEOVER: [accent]nice run people.");
            } else player.sendMessage("[scarlet]Sorry, this command is for admins only.");
        });
    }
}
