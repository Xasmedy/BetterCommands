/*
 * Copyright (c) 2023 - Xasmedy.
 * This file is part of the BetterCommands Project licensed under GNU-GPLv3.
 *
 * The Project source-code can be found at https://github.com/Xasmedy/BetterCommands
 * Contributors of this file may put their name into the copyright notice.
 */

package xasmedy.bettercommands.commands;

import arc.util.CommandHandler;

public interface Command {

    default void init() {}
    default void registerServerCommands(CommandHandler handler) {}
    default void registerClientCommands(CommandHandler handler) {}
}
