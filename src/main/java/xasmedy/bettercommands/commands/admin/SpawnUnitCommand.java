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
import mindustry.Vars;
import mindustry.content.UnitTypes;
import mindustry.game.EventType;
import mindustry.gen.Call;
import mindustry.gen.Player;
import mindustry.gen.Unit;
import mindustry.type.UnitType;
import mindustry.ui.Menus;
import xasmedy.bettercommands.Util;
import xasmedy.bettercommands.commands.Command;
import java.util.HashMap;
import java.util.stream.StreamSupport;
import static xasmedy.bettercommands.Util.PREFIX;

public class SpawnUnitCommand implements Command {

    private static final String UNIT_NOT_FOUND_ERROR = "%s[scarlet]Could not find the unit [orange]%s[].";
    private static final String INVALID_SPAWN_ERROR = "%s[scarlet]The unit [orange]%s[] cannot be spawned at this location.";
    private static final String UNIT_SPAWNED_MESSAGE = "%s[orange]The unit [sky]%s[] has been spawned. [gray]([sky]%s[accent]/[sky]%d[gray])";
    private static final String UNIT_CAP_REACHED_MESSAGE = "%s[red]The cap of [sky]%d[] has been reached for the unit [sky]%s[red].";
    private final HashMap<Player, ListMenu> activeMenus = new HashMap<>();
    private UnitType[] availableUnits = null;
    private int menuId;

    @Override
    public void init() {

        // I throw an exception because there's no reason to call this twice.
        if (availableUnits != null) throw new IllegalStateException("Already instanced.");
        this.availableUnits = StreamSupport.stream(Vars.content.units().spliterator(), false)
                // Crashes the game.
                .filter(unitType -> !UnitTypes.block.equals(unitType))
                // Crashes the game + not present inside UnitTypes.
                .filter(unitType -> !unitType.name.equalsIgnoreCase("turret-unit-build-tower"))
                // This one will spawn nothing.
                .filter(unitType -> !UnitTypes.assemblyDrone.equals(unitType))
                // I sort by alphabetical order. (Kind of)
                .sorted((u1, u2) -> u1.name.compareToIgnoreCase(u2.name))
                .toArray(UnitType[]::new);

        // The player is always found inside the map since added the moment the command is issued.
        this.menuId = Menus.registerMenu((player, option) -> activeMenus.get(player).updateMenu(option));
        // I do this to avoid memory leaks.
        Events.on(EventType.PlayerJoin.class, e -> activeMenus.remove(e.player));
    }

    private void spawnUnit(UnitType type, Player admin) {

        // I try to spawn the unit.
        final Unit unit = type.spawn(admin.x, admin.y);

        // If the unit is dead, it means it could not spawn because of the cap.
        if (type.useUnitCap && unit.count() == unit.cap() && unit.dead()) {
            final String message = String.format(UNIT_CAP_REACHED_MESSAGE, PREFIX, unit.cap(), type.name);
            Call.sendMessage(message);
            return;
        }

        // I wait the next game-tick, so I can see if the unit has been killed because of a bad spawn tile.
        Util.executeAtNextTick(() -> {

            if (unit.dead()) {
                final String message = String.format(INVALID_SPAWN_ERROR, PREFIX, type.name);
                admin.sendMessage(message);
                return;
            }

            // The unit was spawned correctly.
            final String message = String.format(UNIT_SPAWNED_MESSAGE, PREFIX, type.name, unit.count(), unit.cap());
            admin.sendMessage(message);
        });
    }

    private void commandAction(String[] args, Player player) {

        if (!player.admin) {
            // TODO No permissions
            return;
        }

        // I do this to avoid having problems with colors later on.
        args[0] = Strings.stripColors(args[0]);

        // I don't think there will ever be a unit called "list".
        if (args[0].equalsIgnoreCase("list")) {
            activeMenus.put(player, new ListMenu(player));
            return;
        }

        // I search the wanted unit.
        for (UnitType type : availableUnits) {
            if (!type.name.equalsIgnoreCase(args[0])) continue;
            spawnUnit(type, player);
            return;
        }

        final String message = String.format(UNIT_NOT_FOUND_ERROR, PREFIX, args[0]);
        player.sendMessage(message);
    }

    @Override
    public void registerClientCommands(CommandHandler handler) {
        handler.register("spawn", "<list/unit>", "Spawns a unit.", this::commandAction);
    }

    /**
     * A non-hardcoded menu that won't break in case of future mindustry updates or when the {@link ListMenu#UNITS_PER_PAGE} value is changed.
     */
    private final class ListMenu {

        private static final int UNITS_PER_PAGE = 15;
        public final Player player;
        private int currentPage = 0;

        public ListMenu(Player player) {
            this.player = player;
            displayMenu();
        }

        private int getMaxPages() {
            return (int) Math.ceil((float) availableUnits.length / UNITS_PER_PAGE);
        }

        private boolean isAtLastPage() {
            // currentPage + 1 because it starts from 0.
            return currentPage + 1 == getMaxPages();
        }

        private boolean isAtFirstPage() {
            return currentPage == 0;
        }

        private String getTitle() {
            return "Units List [" + (currentPage + 1) + "/" + getMaxPages() + "]";
        }

        private String[][] getOptions() {
            /* Unicodes
            Left arrow : '\ue803'
            Exit       : '\ue85f'
            Right arrow: '\ue803'
             */
            final String prev = (isAtFirstPage() ? "[#bababa]" : "[sky]") + '\ue802';
            final String exit = "[red]" + '\ue85f';
            final String next = (isAtLastPage() ? "[#bababa]" : "[sky]") + '\ue803';
            return new String[][] {new String[] {prev, exit, next}};
        }

        private String getMenuMessage() {

            final StringBuilder builder = new StringBuilder();
            final int currentIndex = currentPage * UNITS_PER_PAGE;

            for (int i = 0; i < UNITS_PER_PAGE; i++) {

                // I append empty values to keep the menu height the same for each page.
                if (availableUnits.length <= (i + currentIndex)) {
                    if (i == UNITS_PER_PAGE - 1) builder.append("[gray]( End )\n");
                    else builder.append("\n");
                    continue;
                }

                final String unitName = availableUnits[i + currentIndex].name;

                // I alternate the colors for visibility purposes.
                final String color = i % 2 == 0 ? "[orange]" : "[sky]";
                builder.append("[gray] - ").append(color).append(unitName).append("\n");
            }
            return builder.toString();
        }

        private void displayMenu() {
            Call.menu(player.con(), menuId, getTitle(), getMenuMessage(), getOptions());
        }

        public void updateMenu(int option) {

            switch (option) {

                // This player menu is no longer active.
                case -1, 1 -> activeMenus.remove(player);

                case 0 -> {
                    if (!isAtFirstPage()) currentPage--;
                    displayMenu();
                }

                case 2 -> {
                    if (!isAtLastPage()) currentPage++;
                    displayMenu();
                }

                default -> throw new IllegalStateException("Unhandled option: " + option);
            }
        }
    }
}
