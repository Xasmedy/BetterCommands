/*
 * Copyright (c) 2023 - Xasmedy.
 * This file is part of the BetterCommands Project licensed under GNU-GPLv3.
 *
 * The Project source-code can be found at https://github.com/Xasmedy/BetterCommands
 * Contributors of this file may put their name into the copyright notice.
 */

package xasmedy.bettercommands;
import xasmedy.bettercommands.commands.admin.*;
import arc.util.CommandHandler;
import mindustry.mod.Plugin;

public class BetterCommands extends Plugin {

    public void registerClientCommands(CommandHandler handler) {

        handler.register("info", "<Name/UUID/IP>", "Get all the player information.", InfoCommand::onInfoCommand);

        handler.register("ban", "<Name/UUID/IP> [Reasons...]", "Ban all the IPs a player has.", BanCommand::onBanCommand);

        handler.register("unban", "<Name/UUID/IP>", "Unban all the IPs a player has.", UnbanCommand::onUnbanCommand);

        handler.register("kick", "<Name/UUID/IP> [Reasons...]", "Kick an IP.", KickCommand::onKickCommand);

        // TODO Add these info inside the help menu of the wave command.
        //handler.register("runwave", "<count...>", "Trigger the next waves.", RunWaveCommand::onRunWaveCommand);
        //handler.register("repeatwave", "<count...>", "Repeat the current wave.", RepeatWaveCommand::onRepeatWave);
        //handler.register("jumpwave", "<Wave...>", "Jump to a specific wave.", JumpWaveCommand::onJumpWave);
    }
}
