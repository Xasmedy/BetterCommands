/*
 * Copyright (c) 2023 - Xasmedy.
 * This file is part of the BetterCommands Project licensed under GNU-GPLv3.
 *
 * The Project source-code can be found at https://github.com/Xasmedy/BetterCommands
 * Contributors of this file may put their name into the copyright notice.
 */

package xasmedy.bettercommands.commands.admin;

import arc.Events;
import arc.util.Strings;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.gen.Call;
import mindustry.gen.Player;
import xasmedy.bettercommands.BetterCommands;
import xasmedy.mapie.command.AbstractCommand;
import xasmedy.mapie.menu.buttons.UnmodifiableButton;
import xasmedy.mapie.menu.panels.FollowUpPanel;
import xasmedy.mapie.menu.parsers.ButtonsLayout;
import xasmedy.mapie.menu.templates.UnmodifiableTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.function.BiConsumer;
import static xasmedy.bettercommands.Util.NOT_ENOUGH_PERMISSION;
import static xasmedy.bettercommands.Util.PREFIX;

public class WaveCommand extends AbstractCommand {

    private static final int MAX_WAVES = 20;
    private static final String INVALID_OPTION = "%s[scarlet]The option [red]%s[] is not valid.";
    private static final String INVALID_NUMBER_ERROR = "%s[scarlet]The inserted value [sky]%s[] is not a valid number.";
    private static final String MAX_WAVES_OVERFLOW_ERROR = "%s[scarlet]You can not spawn more than [orange]" + MAX_WAVES + "[] waves. [gray]([accent]%d[])";
    private static final String NO_VALUE_JUMP_WAVE_ERROR = "%s[scarlet]Please provide the wave to jump to.";
    private static final String RUN_WAVE_MESSAGE = "%s[sky]%d[red] wave%s been spawned by [gold]%s[].";
    private static final String JUMP_WAVE_MESSAGE = "%s[red]Jumped to wave [sky]%d[] by [gold]%s[red].";
    private static final String REPEAT_WAVE_MESSAGE = "%s[red]The wave [sky]%d[] has been repeated [sky]%d[red] time%s by [gold]%s[].";
    private final HashMap<String, BiConsumer<String[], Player>> actions = new HashMap<>();

    public WaveCommand() {

        actions.put("help", (String[] args, Player admin) -> new HelpMenu(admin));

        actions.put("run", (String[] args, Player admin) -> {

            if (args.length == 1) {
                runWaves(admin, 1); // Default.
                return;
            }
            getIntFromInput(admin, args[1]).ifPresent(waves -> runWaves(admin, waves));
        });

        actions.put("jump", (String[] args, Player admin) -> {

            if (args.length == 1) {
                final String message = String.format(NO_VALUE_JUMP_WAVE_ERROR, PREFIX);
                admin.sendMessage(message);
                return;
            }
            getIntFromInput(admin, args[1]).ifPresent(wave -> jumpToWave(admin, wave));
        });

        actions.put("repeat", (String[] args, Player admin) -> {

            if (args.length == 1) {
                repeatWave(admin, 1); // Default.
                return;
            }
            getIntFromInput(admin, args[1]).ifPresent(repetitions -> repeatWave(admin, repetitions));
        });

        actions.put("skip", (String[] args, Player admin) -> {

            if (args.length == 1) {
                jumpToWave(admin, Vars.state.wave + 1); // Default.
                return;
            }
            getIntFromInput(admin, args[1]).ifPresent(wave -> jumpToWave(admin, Vars.state.wave + wave));
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

        for (int i = 0; i < waves; i++) Vars.logic.runWave();
        final String message = String.format(RUN_WAVE_MESSAGE, PREFIX, waves, waves == 1 ? " has" : "s have", admin.plainName());
        Call.sendMessage(message);
    }

    private void jumpToWave(Player admin, int newWave) {

        Vars.state.wave = newWave;
        Vars.state.wavetime = Vars.state.rules.waveSpacing;

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
            Vars.spawner.spawnEnemies();
            Events.fire(new EventType.WaveEvent());
        }

        final String message = String.format(REPEAT_WAVE_MESSAGE, PREFIX, Vars.state.wave, repetitions, repetitions == 1 ? "" : "s", admin.plainName());
        Call.sendMessage(message);
    }

    @Override
    public String name() {
        return "wave";
    }

    @Override
    public String params() {
        return "<[accent]help[gray]/[red]run[gray]/[pink]jump[gray]/[sky]skip[gray]/[orange]repeat[gray]> [[[#bababa]args[]]";
    }

    @Override
    public String description() {
        return "Manages waves.";
    }

    @Override
    public boolean hasRequiredRoles(Player player, String[] args) {
        return player.admin();
    }

    @Override
    public void clientAction(Player player, String[] args) {
        final BiConsumer<String[], Player> action = actions.get(args[0].toLowerCase());
        if (action == null) {
            final String message = String.format(INVALID_OPTION, PREFIX, args[0]);
            player.sendMessage(message);
            return;
        }
        action.accept(args, player);
    }

    @Override
    public void noPermissionsAction(Player player, String[] args) {
        player.sendMessage(NOT_ENOUGH_PERMISSION);
    }

    private static final class HelpMenu {

        private HelpMenu(Player player) {

            final ButtonsLayout<UnmodifiableButton> layout = new ButtonsLayout<>();

            final UnmodifiableTemplate mainTemplate = new UnmodifiableTemplate("[#A72608]Wave[gray]/[blue]Main [gray]- [accent]Help Menu", """
                    [blue]Main Wave help page.
                    
                    [accent]There are [green]4[] different types of [#A72608]wave control [gray]([red]run[gray], [pink]jump[gray], [sky]skip[gray], [orange]repeat[gray])
                    [accent]Each one of them has its own perks and can be specified in the first argument.
                    [gray]/wave <[green]type[]>
                    
                    [#bababa]Click the buttons bellow for specific info.
                    """, layout);
            final UnmodifiableTemplate runTemplate = new UnmodifiableTemplate("[#A72608]Wave[gray]/[red]Run [gray]- [accent]Help Menu", """
                    [sky]Runs the wanted number of waves with a maximum of 20.
                    [orange]If no wave is specified, the current wave will be ran.
                    """, layout);
            final UnmodifiableTemplate jumpTemplate = new UnmodifiableTemplate("[#A72608]Wave[gray]/[pink]Jump [gray]- [accent]Help Menu", """
                    [sky]Jumps to the wanted wave without starting it.
                    [red]A value is always needed.
                    """, layout);
            final UnmodifiableTemplate skipTemplate = new UnmodifiableTemplate("[#A72608]Wave[gray]/[sky]Skip [gray]- [accent]Help Menu", """
                    [sky]Skips [accent]from[] the current wave by a specified amount.
                    [orange]If no value is specified, it will skip to the next wave.
                    """, layout);
            final UnmodifiableTemplate repeatTemplate = new UnmodifiableTemplate("[#A72608]Wave[gray]/[orange]Repeat [gray]- [accent]Help Menu", """
                    [sky]Repeats the current wave a wanted number of times with a maximum of 20.
                    [orange]If no value is specified, it will run the current wave one time.
                    """, layout);

            final FollowUpPanel<UnmodifiableTemplate> panel = new FollowUpPanel<>(BetterCommands.get().menu(), player, mainTemplate);
            layout.addColumn(0, List.of(
                    new UnmodifiableButton("[red]Run", () -> panel.display(runTemplate)),
                    new UnmodifiableButton("[blue]Main", () -> panel.display(mainTemplate)),
                    new UnmodifiableButton("[pink]Jump", () -> panel.display(jumpTemplate))));

            layout.addColumn(1, List.of(
                    new UnmodifiableButton("[sky]Skip", () -> panel.display(skipTemplate)),
                    new UnmodifiableButton("[red]" + '\ue85f', panel::close),
                    new UnmodifiableButton("[orange]Repeat", () -> panel.display(repeatTemplate))));

            panel.update();
        }
    }
}
