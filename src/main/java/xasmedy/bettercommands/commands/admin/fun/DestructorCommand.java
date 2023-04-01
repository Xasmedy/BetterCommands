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
import arc.graphics.Color;
import arc.math.Mathf;
import arc.util.CommandHandler;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.game.EventType;
import mindustry.gen.Building;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.world.blocks.storage.CoreBlock;
import xasmedy.bettercommands.commands.Command;
import java.util.HashSet;
import static xasmedy.bettercommands.Util.NOT_ENOUGH_PERMISSION;

public class DestructorCommand implements Command {

    private final HashSet<Player> activeDestructors = new HashSet<>();
    private final Color colorBuffer = new Color();

    private int ticks = 0;

    private void commandAction(String[] args, Player player) {

        if (!player.admin()) {
            player.sendMessage(NOT_ENOUGH_PERMISSION);
            return;
        }

        if (activeDestructors.contains(player)) activeDestructors.remove(player);
        else activeDestructors.add(player);
    }

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
        if (building instanceof CoreBlock.CoreBuild) return building.health() / 100f;

        // Tells in how many hits it kills the building.
        final float divider = building.maxHealth() / 100f;
        return building.maxHealth() / divider;
    }

    private void dealDamage(Player player, float centerX, float centerY, float rotatedX, float rotatedY, float radius) {

        Building build = Vars.world.buildWorld(rotatedX, rotatedY);
        if (build != null && !build.team.equals(player.team())) build.damage(getBuildDamage(build));

        Groups.unit.forEach(unit -> {
            if (unit.team.equals(player.team())) return;
            if (!unit.within(centerX, centerY, radius)) return;
            unit.damagePierce(unit.maxHealth() / 10);
        });
    }

    public void turboDestructor(float centerX, float centerY, Player player) {

        float radius = Math.max(player.unit().hitSize() * 3f, 100f); // adjust as needed

        float pointCount = 60f; // adjust as needed
        float angleIncrement = 360f / pointCount;
        float rotationAngle = 0f;

        int makeLightningEvery = 5;

        for (float angle = 0; angle < 360f; angle += angleIncrement) {

            float rotatedAngle = angle + rotationAngle;
            float x = centerX + radius * Mathf.cosDeg(rotatedAngle);
            float y = centerY + radius * Mathf.sinDeg(rotatedAngle);

            float cos = Mathf.cosDeg(rotationAngle);
            float sin = Mathf.sinDeg(rotationAngle);
            float rotatedX = centerX + (x - centerX) * cos - (y - centerY) * sin;
            float rotatedY = centerY + (y - centerY) * cos + (x - centerX) * sin;

            dealDamage(player, centerX, centerY, rotatedX, rotatedY, radius);

            // Keep this unreliable, to avoid TCP header and users with bad internet won't suffer as much. (hopefully)
            Call.effect(Fx.shootSmokeSquareBig, rotatedX, rotatedY, rotatedAngle, setColorForAngle(rotatedAngle / 360f));
        }
    }

    private void runTick() {

        if (activeDestructors.isEmpty()) return;

        if ((ticks++ / 3f) != 1) return;
        else ticks = 0;

        for (Player player : activeDestructors) {
            turboDestructor(player.x, player.y, player);
        }
    }

    @Override
    public void init() {
        Core.app.addListener(new ApplicationListener() {
            @Override
            public void update() {
                runTick();
            }
        });
        Events.on(EventType.GameOverEvent.class, gameOverEvent -> activeDestructors.clear());
    }

    @Override
    public void registerClientCommands(CommandHandler handler) {
        handler.register("destructor", "[red]MAX DESTRUCTION!![accent]", this::commandAction);
    }
}
