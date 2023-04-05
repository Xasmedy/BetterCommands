/*
 * Copyright (c) 2023 - Semetrix, Xasmedy.
 * This file is part of the BetterCommands Project licensed under GNU-GPLv3.
 *
 * The Project source-code can be found at https://github.com/Xasmedy/BetterCommands
 * Contributors of this file may put their name into the copyright notice.
 */

package xasmedy.bettercommands.commands.admin.fun;

import arc.ApplicationListener;
import arc.Core;
import arc.Events;
import arc.func.Cons;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Circle;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.world.blocks.storage.CoreBlock;
import xasmedy.mapie.command.AbstractCommand;
import java.util.HashSet;
import static xasmedy.bettercommands.Util.NOT_ENOUGH_PERMISSION;
import static xasmedy.bettercommands.Util.newUpdateListener;

public class DestructorCommand extends AbstractCommand {

    private final HashSet<Player> activeDestructors = new HashSet<>();
    private final Color colorBuffer = new Color();
    private final Circle circle = new Circle();
    private final ApplicationListener listener = newUpdateListener(this::runTick);
    private final Cons<EventType.PlayerLeave> playerLeaveListener = event -> activeDestructors.remove(event.player);
    private int ticks = 0;

    private Color setColorForAngle(float t) {

        // a by default is 1.
        if (t < 0.16f) colorBuffer.set(1, t / 0.16f, 0);
        else if (t < 0.33f) colorBuffer.set(1 - ((t - 0.16f) / 0.17f), 1, 0);
        else if (t < 0.5f) colorBuffer.set(0, 1, (t - 0.33f) / 0.17f);
        else if (t < 0.66f) colorBuffer.set(0, 1 - ((t - 0.5f) / 0.17f), 1);
        else if (t < 0.83f) colorBuffer.set((t - 0.66f) / 0.17f, 0, 1);
        else colorBuffer.set(1, 0, 1 - ((t - 0.83f) / 0.17f));
        return colorBuffer;
    }

    private float getBuildDamage(Building building) {

        // I avoid cores getting killed too fast.
        if (building instanceof CoreBlock.CoreBuild) return building.maxHealth() / 1000f;
        // I one-shot every build.
        return building.maxHealth();
    }

    private void dealDamage(Team playerTeam) {

        circle.set(circle.x, circle.y, circle.radius + (3 * 8));

        Groups.unit.forEach(unit -> {
            if (unit.team.equals(playerTeam)) return;
            if (!circle.contains(unit.x(), unit.y())) return;
            unit.damagePierce(unit.maxHealth() / 10);
        });

        Vars.world.tiles.forEach(tile -> {
            final Building build = tile.build;
            if (build == null) return;
            if (build.team.equals(playerTeam)) return;
            if (!circle.contains(build.x(), build.y())) return;
            build.damage(getBuildDamage(build));
        });
    }

    public void turboDestructor(Player player) {

        circle.set(player.x(), player.y(), Math.max(player.unit().hitSize() * 3f, 100f)); // adjust as needed

        float pointCount = 50f; // adjust as needed
        float angleIncrement = 360f / pointCount;

        for (float angle = 0; angle < 360f; angle += angleIncrement) {

            float x = player.x() + circle.radius * Mathf.cosDeg(angle);
            float y = player.y() + circle.radius * Mathf.sinDeg(angle);

            float rotatedX = player.x() + (x - player.x());
            float rotatedY = player.y() + (y - player.y());

            // Keep this unreliable, to avoid TCP header and users with bad internet won't suffer as much. (hopefully)
            Call.effect(Fx.shootSmokeSquareBig, rotatedX, rotatedY, angle, setColorForAngle(angle / 360f));
        }
        dealDamage(player.team());
    }

    private void runTick() {

        if (activeDestructors.isEmpty()) return;

        if ((ticks++ / 3f) != 1) return;
        else ticks = 0;

        activeDestructors.forEach(this::turboDestructor);
    }

    @Override
    public String name() {
        return "destructor";
    }

    @Override
    public String description() {
        return "[red]MAX DESTRUCTION!![accent]";
    }

    @Override
    public boolean hasRequiredRoles(Player player, String[] args) {
        return player.admin();
    }

    @Override
    public void clientAction(Player player, String[] args) {
        if (activeDestructors.contains(player)) activeDestructors.remove(player);
        else activeDestructors.add(player);
    }

    @Override
    public void noPermissionsAction(Player player, String[] args) {
        player.sendMessage(NOT_ENOUGH_PERMISSION);
    }

    @Override
    protected void init(boolean isServer) {
        Events.on(EventType.PlayerLeave.class, playerLeaveListener);
        Core.app.addListener(listener);
    }

    @Override
    protected void clientDispose(Player player, String[] args) {
        if (!activeDestructors.isEmpty()) return;
        Events.remove(EventType.PlayerLeave.class, playerLeaveListener);
        Core.app.removeListener(listener);
        isInit.set(false); // I tell the command manager to run init().
    }
}
