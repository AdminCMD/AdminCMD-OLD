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
import be.Balor.Manager.Exceptions.ActionNotPermitedException;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Type;
import be.Balor.Tools.CommandUtils.Immunity;
import be.Balor.Tools.CommandUtils.Users;
import be.Balor.Tools.Threads.KickTask;
import be.Balor.bukkit.AdminCmd.LocaleHelper;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class KickPlayer extends PlayerCommand {

	/**
	 *
	 */
	public KickPlayer() {
		permNode = "admincmd.player.kick";
		cmdName = "bal_kick";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Manager.ACCommands#execute(org.bukkit.command.CommandSender,
	 * java.lang.String[])
	 */
	@Override
	public void execute(final CommandSender sender, final CommandArgs args)
			throws ActionNotPermitedException, PlayerNotFound {
		final HashMap<String, String> replace = new HashMap<String, String>();
		final Player toKick = sender.getServer().getPlayer(args.getString(0));
		if (toKick == null) {
			replace.put("player", args.getString(0));
			LocaleManager.sI18n(sender, "playerNotFound", replace);
			return;
		}
		if (!Immunity.checkImmunity(sender, toKick)) {
			LocaleManager.sI18n(sender, "insufficientLvl");
			return;
		}
		String message = "";
		if (args.hasFlag('m')) {
			message = LocaleManager.getInstance().get("kickMessages",
					args.getValueFlag('m'), "player", toKick.getName());
		} else if (args.length >= 2) {
			for (int i = 1; i < args.length; i++) {
				message += args.getString(i) + " ";
			}
		}
		if (message == null || (message != null && message.isEmpty())) {
			message = "You have been kicked by ";
			if (!Users.isPlayer(sender, false)) {
				message += "Server Admin";
			} else {
				message += Users.getPlayerName((Player) sender);
			}
		}

		ACPlayer.getPlayer(toKick).setPower(Type.KICKED);
		replace.put("player", Users.getPlayerName(toKick));
		new KickTask(toKick, message.trim()).scheduleSync();
		replace.put("reason", message.trim());
		Users.broadcastMessage(LocaleHelper.PLAYER_KICKED.getLocale(replace));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.ACCommands#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(final String... args) {
		return args != null && args.length >= 1;
	}

}
