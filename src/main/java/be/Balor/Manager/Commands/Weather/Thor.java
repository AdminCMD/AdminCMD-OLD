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
package be.Balor.Manager.Commands.Weather;

import java.util.HashMap;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Type;
import be.Balor.Tools.Utils;
import be.Balor.Tools.Threads.RemovePowerTask;
import be.Balor.bukkit.AdminCmd.ACPluginManager;
import be.Balor.bukkit.AdminCmd.ConfigEnum;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class Thor extends WeatherCommand {

	/**
	 *
	 */
	public Thor() {
		permNode = "admincmd.weather.thor";
		cmdName = "bal_thor";
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
	public void execute(final CommandSender sender, final CommandArgs args) {
		final Player player = Utils.getUser(sender, args, permNode);
		final String timeOut = args.getValueFlag('t');
		if (player != null) {
			final HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("player", Utils.getPlayerName(player));
			final ACPlayer acp = ACPlayer.getPlayer(player.getName());
			if (acp.hasPower(Type.THOR)) {
				acp.removePower(Type.THOR);
				Utils.sI18n(player, "thorDisabled");
				if (!player.equals(sender))
					Utils.sI18n(sender, "thorDisabledTarget", replace);
			} else {
				acp.setPower(Type.THOR);
				Utils.sI18n(player, "thorEnabled");
				if (!player.equals(sender))
					Utils.sI18n(sender, "thorEnabledTarget", replace);
				if (timeOut == null)
					return;
				int timeOutValue;
				try {
					timeOutValue = Integer.parseInt(timeOut);
				} catch (final Exception e) {
					Utils.sI18n(sender, "NaN", "number", timeOut);
					return;
				}
				ACPluginManager.getScheduler().scheduleAsyncDelayedTask(
						ACPluginManager.getCorePlugin(),
						new RemovePowerTask(acp, Type.THOR, sender),
						Utils.secInTick * ConfigEnum.SCALE_TIMEOUT.getInt() * timeOutValue);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.ACCommands#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(final String... args) {
		return args != null;
	}

}
