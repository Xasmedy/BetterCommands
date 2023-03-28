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
import mindustry.entities.Units;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.gen.Player;
import mindustry.gen.Unit;
import mindustry.type.UnitType;
import mindustry.ui.Menus;
import xasmedy.bettercommands.Util;
import xasmedy.bettercommands.commands.Command;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.StreamSupport;
import static xasmedy.bettercommands.Util.PREFIX;

public class SpawnUnitCommand implements Command {

    private static final String UNIT_NOT_FOUND_ERROR = "%s[scarlet]Could not find the unit [orange]%s[].";
    private static final String INVALID_SPAWN_ERROR = "%s[scarlet]The unit [orange]%s[] cannot be spawned at this location.";
    private static final String INVALID_AMOUNT_ERROR = "%s[scarlet]The amount [orange]%s[] is not valid.";
    private static final String INVALID_TEAM_ERROR = "%s[scarlet]The team [orange]%s[] does not exist.";
    private static final String INVALID_SHIELD_ERROR = "%s[scarlet]The shield value [orange]%s[] is not valid.";
    private static final String UNIT_SPAWNED_MESSAGE = "%s[orange]The unit [#%s]%s[] has been spawned [sky]%d[] time%s. [gray]([sky]%s[accent]/[sky]%d[gray])";
    private static final String UNIT_NOT_ALL_SPAWNED_MESSAGE = "%s[orange]The unit [#%s]%s[] has been spawned [sky]%d[accent]/[sky]%d[orange] times. [gray]([sky]%s[accent]/[sky]%d[gray])";
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

    private float getShieldFromInput(String rawShield, Player admin) {

        // Special exception.
        if (rawShield.equalsIgnoreCase("max")) return Float.MAX_VALUE;

        try {
            final float shield = Float.parseFloat(rawShield);
            if (shield < 0) throw new NumberFormatException("Negative number.");
            return shield;
        } catch (NumberFormatException ignored) {
            final String message = String.format(INVALID_SHIELD_ERROR, PREFIX, rawShield);
            admin.sendMessage(message);
            return -1;
        }
    }

    private Optional<Team> findTeam(String rawTeam) {
        // I allow all teams.
        for (Team team : Team.all) {
            if (!team.name.equalsIgnoreCase(rawTeam)) continue;
            return Optional.of(team);
        }
        return Optional.empty();
    }

    /**
     * -1 if not a valid amount.
     */
    private int getAmountFromInput(String rawAmount, Player admin) {
        try {
            final int number = Integer.parseInt(rawAmount);
            if (number < 1) throw new NumberFormatException("Negative number.");
            return number;
        } catch (NumberFormatException ignored) {
            final String message = String.format(INVALID_AMOUNT_ERROR, PREFIX, rawAmount);
            admin.sendMessage(message);
            return -1;
        }
    }

    private void sendSpawnMessage(ArrayList<Unit> spawned, Player admin, UnitType type, Team team, int amount, int unitCap) {

        final long amountSpawned = spawned.stream()
                .filter(unit -> !unit.dead())
                .count();

        if (amountSpawned == 0) {
            final String message = String.format(INVALID_SPAWN_ERROR, PREFIX, type.name);
            admin.sendMessage(message);
            return;
        }

        final int unitTotalCount = team.data().countType(type);

        // The unit was spawned correctly. (even if not all)
        final String message;
        if (amountSpawned != amount) {
            message = String.format(UNIT_NOT_ALL_SPAWNED_MESSAGE, PREFIX, team.color, type.name, amountSpawned, amount, unitTotalCount, unitCap);
        } else {
            final String plural = amount == 1 ? "" : "s";
            message = String.format(UNIT_SPAWNED_MESSAGE, PREFIX, team.color, type.name, amount, plural, unitTotalCount, unitCap);
        }
        admin.sendMessage(message);
    }

    private void spawnUnit(UnitType type, Player admin, int amount, Team team, float shield) {

        final int unitCap = Units.getCap(team);
        final int unitsAlive = team.data().countType(type);
        // I spawn the maximum possible units if the cap limit is exceeded.
        if (amount > unitCap && !Vars.state.isPaused()) amount = unitCap - unitsAlive;

        // I check the pause state since it allows to spawn units over the cap. (Mindustry Bug)
        if (type.useUnitCap && unitCap <= unitsAlive && !Vars.state.isPaused()) {
            final String message = String.format(UNIT_CAP_REACHED_MESSAGE, PREFIX, unitCap, type.name);
            Call.sendMessage(message);
            return;
        }

        // I try to spawn the units.
        final ArrayList<Unit> spawned = new ArrayList<>();
        for (int i = 0; i < amount; i++) {

            final Unit unit;
            // To spawn the unit even in case there are no cores. (The unit cap might be a little messed up though)
            // An exception is the crux team.
            if (!team.equals(Team.crux) && team.data().noCores()) {
                unit = type.spawn(admin.team(), admin.x(), admin.y());
                unit.team(team);
            } else unit = type.spawn(team, admin.x(), admin.y());

            unit.shield(shield == 0 ? unit.shield() : shield);
            spawned.add(unit);
        }
        // I copy the value for the lambda.
        final int finalAmount = amount;
        // I wait the next game-tick, so I can see if the unit has been killed because of a bad spawn tile.
        Util.executeAtNextTick(() -> sendSpawnMessage(spawned, admin, type, team, finalAmount, unitCap));
    }

    private Optional<UnitType> findUnitType(String unitName) {
        // I search the wanted unit.
        for (UnitType type : availableUnits) {
            if (!type.name.equalsIgnoreCase(unitName)) continue;
            return Optional.of(type);
        }
        return Optional.empty();
    }

    private void commandAction(String[] args, Player player) {

        if (!player.admin) {
            // TODO No permissions
            return;
        }

        // I do this to avoid having problems with colors later on.
        for (int i = 0; i < args.length; i++) {
            args[i] = Strings.stripColors(args[i]);
        }

        // I don't think there will ever be a unit called "list".
        if (args[0].equalsIgnoreCase("list")) {
            activeMenus.put(player, new ListMenu(player));
            return;
        }

        // If not provided 1, else the provided amount.
        final int amount = 1 < args.length ? getAmountFromInput(args[1], player) : 1;
        if (amount == -1) return; // Not a valid amount.

        // If not provided admin team, else the provided team.
        final Optional<Team> team = 2 < args.length ? findTeam(args[2]) : Optional.of(player.team());
        if (team.isEmpty()) {
            final String message = String.format(INVALID_TEAM_ERROR, PREFIX, args[2]);
            player.sendMessage(message);
            return;
        }

        // If not provided 0, else the provided shield.
        final float shield = 3 < args.length ? getShieldFromInput(args[3], player) : 0;
        if (shield == -1) return; // Not a valid shield value.

        findUnitType(args[0]).ifPresentOrElse(type -> spawnUnit(type, player, amount, team.orElseThrow(), shield), () -> {
            final String message = String.format(UNIT_NOT_FOUND_ERROR, PREFIX, args[0]);
            player.sendMessage(message);
        });
    }

    @Override
    public void registerClientCommands(CommandHandler handler) {
        handler.register("spawn", "<list/unit> [amount] [team] [shield]", "Spawns a unit.", this::commandAction);
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
