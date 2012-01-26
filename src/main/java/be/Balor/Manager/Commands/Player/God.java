/************************************************************************
 * This file is part of AdminCmd.
 *
 * AdminCmd is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AdminCmd is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AdminCmd.  If not, see <http://www.gnu.org/licenses/>.
 ************************************************************************/
package be.Balor.Manager.Commands.Player;

import java.util.HashMap;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Commands.CoreCommand;
import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Type;
import be.Balor.Tools.Utils;
import be.Balor.Tools.Threads.RemovePowerTask;
import be.Balor.bukkit.AdminCmd.ACPluginManager;

/**
 * @author Balor (aka Antoine Aflalo)
 *
 */
public class God extends CoreCommand {

	/**
	 *
	 */
	public God() {
		permNode = "admincmd.player.god";
		cmdName = "bal_god";
		other = true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * be.Balor.Manager.ACCommands#execute(org.bukkit.command.CommandSender,
	 * java.lang.String[])
	 */
	@Override
	public void execute(CommandSender sender, CommandArgs args) {
		String timeOut = args.getValueFlag('t');
		Player player = Utils.getUser(sender, args, permNode);
		if (player != null) {
			HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("player", Utils.getPlayerName(player));
			ACPlayer acp = ACPlayer.getPlayer(player);
			if (acp.hasPower(Type.GOD)) {
				acp.removePower(Type.GOD);
				Utils.sI18n(player, "godDisabled");
				if (!player.equals(sender))
					Utils.sI18n(sender, "godDisabledTarget", replace);
			} else {
				acp.setPower(Type.GOD);
				Utils.sI18n(player, "godEnabled");
				if (!player.equals(sender))
					Utils.sI18n(sender, "godEnabledTarget", replace);
				if (timeOut == null)
					return;
				int timeOutValue;
				try {
					timeOutValue = Integer.parseInt(timeOut);
				} catch (Exception e) {
					Utils.sI18n(sender, "NaN", "number", timeOut);
					return;
				}
				ACPluginManager.getScheduler().scheduleAsyncDelayedTask(
						ACPluginManager.getCorePlugin(), new RemovePowerTask(acp, Type.GOD),
						Utils.secInTick * 60 * timeOutValue);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see be.Balor.Manager.ACCommands#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(String... args) {
		return args != null;
	}

}
