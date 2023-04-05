/*
 * Copyright (c) 2023 - Xasmedy.
 * This file is part of the BetterCommands Project licensed under GNU-GPLv3.
 *
 * The Project source-code can be found at https://github.com/Xasmedy/BetterCommands
 * Contributors of this file may put their name into the copyright notice.
 */

package xasmedy.bettercommands;

import arc.util.*;
import xasmedy.bettercommands.commands.admin.*;
import mindustry.mod.Plugin;
import xasmedy.bettercommands.commands.admin.fun.DestructorCommand;
import xasmedy.mapie.command.CommandRepository;
import xasmedy.mapie.icon.ChatIcons;
import xasmedy.mapie.menu.Menu;

public class BetterCommands extends Plugin {

    private static BetterCommands instance;
    public final CommandRepository clientCommands = new CommandRepository();
    public final CommandRepository serverCommands = new CommandRepository();
    private Menu menu;

    public static BetterCommands get() {
        return instance;
    }

    public Menu menu() {
        return menu;
    }

    @Override
    public void init() {
        ChatIcons.get().loadReliable();
        menu = Menu.init();
        instance = this;
    }

    @Override
    public void registerServerCommands(CommandHandler handler) {
        serverCommands.handler(handler);
        // No server commands.
    }

    @Override
    public void registerClientCommands(CommandHandler handler) {
        clientCommands.handler(handler);

        clientCommands.add(new GameOverCommand());
        clientCommands.add(new PauseCommand());
        clientCommands.add(new WaveCommand());
        clientCommands.add(new SpawnUnitCommand());
        clientCommands.add(new TeamCommand());
        clientCommands.add(new ImmortalCommand());

        // Fun
        clientCommands.add(new DestructorCommand());



        //handler.register("info", "<Name/UUID/IP>", "Get all the player information.", PlayerInfoCommand::onInfoCommand);

        //handler.register("ban", "<Name/UUID/IP> [Reasons...]", "Ban all the IPs a player has.", BanCommand::onBanCommand);

        //handler.register("unban", "<Name/UUID/IP>", "Unban all the IPs a player has.", UnbanCommand::onUnbanCommand);

        //handler.register("kick", "<Name/UUID/IP> [Reasons...]", "Kick an IP.", KickCommand::onKickCommand);

        // TODO Add these info inside the help menu of the wave command.
        //handler.register("runwave", "<count...>", "Trigger the next waves.", RunWaveCommand::onRunWaveCommand);
        //handler.register("repeatwave", "<count...>", "Repeat the current wave.", RepeatWaveCommand::onRepeatWave);
        //handler.register("jumpwave", "<Wave...>", "Jump to a specific wave.", JumpWaveCommand::onJumpWave);
    }
}
