/*
 * Copyright (c) 2023 - Xasmedy.
 * This file is part of the BetterCommands Project licensed under GNU-GPLv3.
 *
 * The Project source-code can be found at https://github.com/Xasmedy/BetterCommands
 * Contributors of this file may put their name into the copyright notice.
 */

package xasmedy.bettercommands;

import arc.ApplicationListener;
import arc.struct.ObjectSet;
import arc.util.Timer;
import mindustry.game.Team;
import mindustry.gen.Player;
import mindustry.net.Administration;
import java.util.ArrayList;
import java.util.Optional;
import static mindustry.Vars.netServer;

public class Util {

    // Be careful to leave a space at the end.
    // TODO Move this into a configuration file.
    public static final String PREFIX = "[#A72608]Better[orange]C [gray]>> ";
    public static final String NOT_ENOUGH_PERMISSION = PREFIX + "[scarlet]You do not have enough permissions.";

    public static void executeAtNextTick(Runnable runnable) {
        // I execute the action at the next game tick.
        Timer.schedule(runnable, 1 / 60f);
    }

    public static Optional<Team> findTeam(String rawTeam) {
        // I allow all teams.
        for (Team team : Team.all) {
            if (!team.name.equalsIgnoreCase(rawTeam)) continue;
            return Optional.of(team);
        }
        return Optional.empty();
    }

    public static ApplicationListener newUpdateListener(Runnable runnable) {
        return new ApplicationListener() {
            @Override
            public void update() {
                runnable.run();
            }
        };
    }

    // Make the reason for a kick.
    public static String reason(String kickType, String[] args, Player admin) {

        StringBuilder reason = new StringBuilder();

        // Put together all the args after the IP one, with a space for make the reason.
        for (int i = 1; args.length > 1 && i < args.length; i++) { reason.append(" ").append(args[i]); }

        // Make the reason displayed.
        String completedReason;

        completedReason = "              [scarlet]You have been " + kickType + "!" +
                "\n\n[white] - [#ff5a00]Admin[white]: " + admin.name;


        if (args.length > 1)

            completedReason = "              [scarlet]You have been " + kickType + "!" +
                "\n\n[white] - Reason: [gray]" + reason +
                "\n[white] - [#ff5a00]Admin[white]: " + admin.name;

        return completedReason;
    }

    // Get the info of a player IP and returns an ArrayList.
    public static ArrayList<PlayerInfo> info(String arg) {

        ObjectSet<Administration.PlayerInfo> infos = netServer.admins.findByName(arg);

        ArrayList<PlayerInfo> completedInfo = new ArrayList<>();

        if (infos.size > 0) {

            int i = 0;
            for (Administration.PlayerInfo info : infos) {

                PlayerInfo getInfo = new PlayerInfo(info.lastName, info.names, info.id, info.lastIP, info.ips, info.timesJoined, info.timesKicked, info.banned);
                completedInfo.add(i, getInfo);
                i++;
            }
        }
        return completedInfo;
    }
}
