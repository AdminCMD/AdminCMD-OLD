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
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.ACPluginManager;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class Fireball extends CoreCommand {
	public Fireball() {
		permNode = "admincmd.player.fireball";
		cmdName = "bal_fireball";
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
		Player player = null;
		float power = ACHelper.getInstance().getConfFloat("DefaultFireBallPower");
		String timeOut = args.getValueFlag('t');
		if (args.length >= 1) {
			try {
				player = Utils.getUser(sender, args, permNode, 1, false);
				power = args.getFloat(0);
			} catch (NumberFormatException e) {
				power = ACHelper.getInstance().getConfFloat("DefaultFireBallPower");
				player = Utils.getUser(sender, args, permNode);
			}
			if (args.length >= 2)
				player = Utils.getUser(sender, args, permNode, 1, true);
		} else
			player = Utils.getUser(sender, args, permNode);
		if (player != null) {
			HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("player", Utils.getPlayerName(player));
			final ACPlayer acp = ACPlayer.getPlayer(player);
			if (acp.hasPower(Type.FIREBALL)) {
				acp.removePower(Type.FIREBALL);
				Utils.sI18n(player, "fireballDisabled");
				if (!player.equals(sender))
					Utils.sI18n(sender, "fireballDisabledTarget", replace);
			} else {
				acp.setPower(Type.FIREBALL, power);
				Utils.sI18n(player, "fireballEnabled");
				if (!player.equals(sender))
					Utils.sI18n(sender, "fireballEnabledTarget", replace);
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
						ACPluginManager.getCorePlugin(), new RemovePowerTask(acp, Type.FIREBALL),
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
