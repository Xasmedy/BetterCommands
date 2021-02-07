package xasmedy;

import arc.struct.Seq;

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
