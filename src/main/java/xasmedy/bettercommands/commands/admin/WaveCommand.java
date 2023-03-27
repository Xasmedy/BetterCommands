/*
 * Copyright (c) 2023 - Xasmedy.
 * This file is part of the BetterCommands Project licensed under GNU-GPLv3.
 *
 * The Project source-code can be found at https://github.com/Xasmedy/BetterCommands
 * Contributors of this file may put their name into the copyright notice.
 */

package xasmedy.bettercommands.commands.admin;

import arc.Events;
import arc.util.CommandHandler;
import arc.util.Strings;
import mindustry.game.EventType;
import mindustry.gen.Call;
import mindustry.gen.Player;
import xasmedy.bettercommands.commands.Command;
import java.util.HashMap;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.function.BiConsumer;
import static mindustry.Vars.*;
import static xasmedy.bettercommands.Util.PREFIX;

public class WaveCommand implements Command {

    private static final int MAX_WAVES = 20;
    private static final String INVALID_NUMBER_ERROR = "%s[scarlet]The inserted value [sky]%s[] is not a valid number.";
    private static final String MAX_WAVES_OVERFLOW_ERROR = "%s[scarlet]You can not spawn more than [orange]" + MAX_WAVES + "[] waves cause lag. [gray]([accent]%d[])";
    private static final String NO_VALUE_JUMP_WAVE_ERROR = "%s[scarlet]Please provide the wave to jump to.";
    private static final String RUN_WAVE_MESSAGE = "%s[red]WARNING! [sky]%d[orange] wave%s been spawned by [gold]%s[].";
    private static final String JUMP_WAVE_MESSAGE = "%s[red]WARNING! [orange]Jumped to wave [sky]%d[] by [gold]%s[orange].";
    private static final String REPEAT_WAVE_MESSAGE = "%s[red]WARNING! [orange]The wave [sky]%d[] has been repeated [sky]%d time%s by [gold]%s[orange].";
    private final HashMap<String, BiConsumer<String[], Player>> actions = new HashMap<>();

    public WaveCommand() {
        // TODO Help action.
        actions.put("run", (String[] args, Player admin) -> {

            if (args.length == 1) {
                runWaves(player, 1); // Default.
                return;
            }
            getIntFromInput(admin, args[2]).ifPresent(waves -> runWaves(player, waves));
        });
        actions.put("jump", (String[] args, Player admin) -> {

            if (args.length == 1) {
                final String message = String.format(NO_VALUE_JUMP_WAVE_ERROR, PREFIX);
                admin.sendMessage(message);
                return;
            }
            getIntFromInput(admin, args[2]).ifPresent(wave -> jumpToWave(player, wave));
        });
        actions.put("repeat", (String[] args, Player admin) -> {

            if (args.length == 1) {
                repeatWave(player, 1); // Default.
                return;
            }
            getIntFromInput(admin, args[2]).ifPresent(repetitions -> repeatWave(player, repetitions));
        });
        actions.put("skip", (String[] args, Player admin) -> {

            if (args.length == 1) {
                jumpToWave(admin, state.wave + 1); // Default.
                return;
            }
            getIntFromInput(admin, args[2]).ifPresent(wave -> jumpToWave(player, state.wave + wave));
        });
    }

    private OptionalInt getIntFromInput(Player admin, String rawInt) {

        Objects.requireNonNull(rawInt);
        rawInt = Strings.stripColors(rawInt);

        try {
            return OptionalInt.of(Integer.parseInt(rawInt));
        } catch (NumberFormatException ignored) {
            final String message = String.format(INVALID_NUMBER_ERROR, PREFIX, rawInt);
            admin.sendMessage(message);
            return OptionalInt.empty();
        }
    }

    private void runWaves(Player admin, int waves) {

        if (MAX_WAVES < waves) {
            final String message = String.format(MAX_WAVES_OVERFLOW_ERROR, PREFIX, waves);
            admin.sendMessage(message);
            return;
        }

        for (int i = 0; i < waves; i++) logic.runWave();
        final String message = String.format(RUN_WAVE_MESSAGE, PREFIX, waves, waves == 1 ? " has" : "s have", admin.plainName());
        Call.sendMessage(message);
    }

    private void jumpToWave(Player admin, int newWave) {

        state.wave = newWave;
        state.wavetime = state.rules.waveSpacing;

        final String message = String.format(JUMP_WAVE_MESSAGE, PREFIX, newWave, admin.plainName());
        Call.sendMessage(message);
    }

    private void repeatWave(Player admin, int repetitions) {

        if (MAX_WAVES < repetitions) {
            final String message = String.format(MAX_WAVES_OVERFLOW_ERROR, PREFIX, repetitions);
            admin.sendMessage(message);
            return;
        }

        for (int i = 0; i < repetitions; i++) {
            spawner.spawnEnemies();
            Events.fire(new EventType.WaveEvent());
        }

        final String message = String.format(REPEAT_WAVE_MESSAGE, PREFIX, state.wave, repetitions, repetitions == 1 ? "" : "s", admin.plainName());
        Call.sendMessage(message);
    }

    private void commandAction(String[] args, Player player) {

        if (!player.admin) {
            // TODO not enough permissions.
            return;
        }

        final BiConsumer<String[], Player> action = actions.get(args[0].toLowerCase());
        if (action == null) {
            // TODO Help menu.
            return;
        }
        action.accept(args, player);
    }

    @Override
    public void registerClientCommands(CommandHandler handler) {
        handler.register("wave", "<run/jump/skip/repeat> [args]", "Manages waves.", this::commandAction);
    }
}
