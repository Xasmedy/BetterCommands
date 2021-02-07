// Author: Xasmedy
// The code can be copied and used, but if you want use the plugin you need to give credits to mindustry.ddns.net

package xasmedy;
import mindustry.gen.Player;
import xasmedy.commands.admin.*;
import arc.util.CommandHandler;
import mindustry.mod.Plugin;

public class BetterCommands extends Plugin {

    public void registerClientCommands(CommandHandler handler) {

        handler.register("info", "<Name/UUID/IP>", "Get all the player information.", InfoCommand::onInfoCommand);

        handler.register("ban", "<Name/UUID/IP> [Reasons...]", "Ban all the IPs a player has.", BanCommand::onBanCommand);

        handler.register("unban", "<Name/UUID/IP>", "Unban all the IPs a player has.", UnbanCommand::onUnbanCommand);

        handler.register("kick", "<Name/UUID/IP> [Reasons...]", "Kick an IP.", KickCommand::onKickCommand);

        handler.register("pause", "<On/Off>", "Pause the server.", PauseCommand::onPauseCommand);

        handler.register("runwave", "<count...>", "Trigger the next waves.", RunWaveCommand::onRunWaveCommand);

        handler.register("repeatwave", "<count...>", "Repeat the current wave.", RepeatWaveCommand::onRepeatWave);

        handler.register("jumpwave", "<Wave...>", "Jump to a specific wave.", JumpWaveCommand::onJumpWave);

        handler.register("gameover", "Force a GameOver.", (String[] args, Player admin) -> GameOverCommand.onGameOverCommand(admin));
    }
}
