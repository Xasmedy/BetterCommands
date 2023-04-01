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
import xasmedy.bettercommands.commands.Command;
import java.util.HashSet;

public class DestructorCommand implements Command {

    private final HashSet<Player> turboDestructor = new HashSet<>();

    private int ticks = 0;

    @Override
    public void init() {
        Core.app.addListener(new ApplicationListener() {
            @Override
            public void update() {
                runTick();
            }
        });
        Events.on(EventType.GameOverEvent.class, gameOverEvent -> turboDestructor.clear());
    }

    @Override
    public void registerClientCommands(CommandHandler handler) {
        handler.register("destructor", "[red]MAX DESTRUCTION!![accent]", (String[] args, Player p) -> {
            if (turboDestructor.contains(p)) turboDestructor.remove(p);
            else turboDestructor.add(p);
        });
    }
    public void runTick() {

        if (ticks % 30 != 0) {
            ticks++;
            return;
        }

        ticks = 0;

        for (Player player : turboDestructor) {
            turboDestructor(player.x, player.y, player);
        }
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

            Building build = Vars.world.buildWorld(rotatedX, rotatedY);
            if (build != null && !build.team.equals(player.team())) {
                build.damage(100f);
            }

            Groups.unit.forEach(unit -> {
                if (unit.team.equals(player.team())) return;
                if (!unit.within(centerX, centerY, radius)) return;
                unit.damagePierce(unit.maxHealth / 10);
            });

            Call.effect(Fx.shootSmokeSquareBig, rotatedX, rotatedY, rotatedAngle, getColorAtTime(rotatedAngle / 360f));
        }
    }

    private static Color getColorAtTime(float t) {
        float r, g, b;

        if (t < 0.16f) {
            r = 1f;
            g = t / 0.16f;
            b = 0f;
        } else if (t < 0.33f) {
            r = 1f - (t - 0.16f) / 0.17f;
            g = 1f;
            b = 0f;
        } else if (t < 0.5f) {
            r = 0f;
            g = 1f;
            b = (t - 0.33f) / 0.17f;
        } else if (t < 0.66f) {
            r = 0f;
            g = 1f - (t - 0.5f) / 0.17f;
            b = 1f;
        } else if (t < 0.83f) {
            r = (t - 0.66f) / 0.17f;
            g = 0f;
            b = 1f;
        } else {
            r = 1f;
            g = 0f;
            b = 1f - (t - 0.83f) / 0.17f;
        }

        return new Color(r, g, b, 1f);
    }
}
