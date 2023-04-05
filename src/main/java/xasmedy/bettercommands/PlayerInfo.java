/*
 * Copyright (c) 2023 - Xasmedy.
 * This file is part of the BetterCommands Project licensed under GNU-GPLv3.
 *
 * The Project source-code can be found at https://github.com/Xasmedy/BetterCommands
 * Contributors of this file may put their name into the copyright notice.
 */

package xasmedy.bettercommands;

import arc.struct.Seq;

// TODO Is this class really needed?
public class PlayerInfo {

    public String lastName;
    public Seq<String> names;
    public String id;
    public String lastIP;
    public Seq<String> ips;
    public int timesJoined;
    public int timesKicked;
    public boolean banned;

    PlayerInfo(String lastName, Seq<String> names, String id, String lastIP, Seq<String> ips, int timesJoined, int timesKicked, boolean banned) {
        this.lastName = lastName;
        this.names = names;
        this.id = id;
        this.lastIP = lastIP;
        this.ips = ips;
        this.timesJoined = timesJoined;
        this.timesKicked = timesKicked;
        this.banned = banned;
    }
}
