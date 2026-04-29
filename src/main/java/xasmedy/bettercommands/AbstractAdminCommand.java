package xasmedy.bettercommands;

import arc.Events;
import arc.struct.StringMap;
import mindustry.gen.Player;
import xasmedy.mapie.command.AbstractCommand;

import static xasmedy.bettercommands.Util.NOT_ENOUGH_PERMISSION;

public abstract class AbstractAdminCommand extends AbstractCommand {

    boolean silentNoPerm = false;

    public AbstractAdminCommand() {
        // For BetterHelp command
        Events.on(StringMap.class, map -> {
            if (map == null || !"BetterHelpResponse".equals(map.get("map-type")))
                return;

            if (name().equals(map.get("response")))
                silentNoPerm = true;
        });

        Events.fire(StringMap.class, StringMap.of("map-type", "BetterHelp", "command", name()));
    }

    protected boolean argsLenCheck(Player player, String[] args) {
        return !tooManyArgsCheck(player, args) || !tooFewArgsCheck(player, args);
    }

    protected boolean tooFewArgsCheck(Player player, String[] args) {
        if (args.length != 0)
            return true;

        tooFewArgs(player);
        return false;
    }

    protected boolean tooManyArgsCheck(Player player, String[] args) {
        if (args.length == 0)
            return true;
        int last = args.length - 1;
        String lastArg = args[last];

        var spaceIndex = lastArg.indexOf(' ');
        if (spaceIndex == -1)
            return true;

        args[last] = lastArg.substring(0, spaceIndex);
        tooManyArgs(player);
        return false;
    }

    @Override
    public boolean hasRequiredRoles(Player player, String[] args) {
        return player.admin();
    }

    @Override
    protected void noPermissionsAction(Player player, String[] args) {
        if (!silentNoPerm)
            player.sendMessage(NOT_ENOUGH_PERMISSION);
    }

    protected void tooFewArgs(Player player){
        if (!silentNoPerm || hasRequiredRoles(player, null))
            player.sendMessage("[scarlet]Too few arguments. Usage:[lightgray] " + name() + "[gray] " + params());
    }

    protected void tooManyArgs(Player player){
        if (!silentNoPerm || hasRequiredRoles(player, null))
            player.sendMessage("[scarlet]Too many arguments. Usage:[lightgray] " + name() + "[gray] " + params());
    }
}
