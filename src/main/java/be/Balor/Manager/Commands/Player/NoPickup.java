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

import be.Balor.Manager.LocaleManager;
import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Permissions.ActionNotPermitedException;
import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Type;
import be.Balor.Tools.Utils;
import be.Balor.Tools.CommandUtils.Users;
import be.Balor.Tools.Threads.RemovePowerTask;
import be.Balor.bukkit.AdminCmd.ACPluginManager;
import be.Balor.bukkit.AdminCmd.ConfigEnum;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class NoPickup extends PlayerCommand {

	/**
	 *
	 */
	public NoPickup() {
		permNode = Type.NO_PICKUP.getPermission();
		cmdName = "bal_np";
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
	public void execute(final CommandSender sender, final CommandArgs args) throws ActionNotPermitedException, PlayerNotFound {
		final String timeOut = args.getValueFlag('t');
		final Player player = Users.getUser(sender, args, permNode);
		if (player != null) {
			final HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("player", Users.getPlayerName(player));
			final ACPlayer acp = ACPlayer.getPlayer(player);
			if (acp.hasPower(Type.NO_PICKUP)) {
				acp.removePower(Type.NO_PICKUP);
				LocaleManager.sI18n(player, "npDisabled");
				if (!player.equals(sender)) {
					LocaleManager.sI18n(sender, "npDisabledTarget", replace);
				}
			} else {
				acp.setPower(Type.NO_PICKUP);
				LocaleManager.sI18n(player, "npEnabled");
				if (!player.equals(sender)) {
					LocaleManager.sI18n(sender, "npEnabledTarget", replace);
				}
				if (timeOut == null) {
					return;
				}
				int timeOutValue;
				try {
					timeOutValue = Integer.parseInt(timeOut);
				} catch (final Exception e) {
					LocaleManager.sI18n(sender, "NaN", "number", timeOut);
					return;
				}
				ACPluginManager.getScheduler().runTaskLaterAsynchronously(ACPluginManager.getCorePlugin(), new RemovePowerTask(acp, Type.NO_PICKUP, sender),
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
