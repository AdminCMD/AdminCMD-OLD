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
import be.Balor.Manager.Exceptions.PlayerNotFound;
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
public class NoDrop extends PlayerCommand {
	public NoDrop() {
		super("bal_nodrop", "admincmd.player.nodrop");
		other = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.Commands.CoreCommand#execute(org.bukkit.command.
	 * CommandSender, be.Balor.Manager.Commands.CommandArgs)
	 */
	@Override
	public void execute(final CommandSender sender, final CommandArgs args) throws PlayerNotFound {
		final String timeOut = args.getValueFlag('t');
		final Player player = Utils.getUserParam(sender, args, permNode);
		if (player == null)
			return;
		final HashMap<String, String> replace = new HashMap<String, String>();
		replace.put("player", Utils.getPlayerName(player));
		final ACPlayer acp = ACPlayer.getPlayer(player);
		if (acp.hasPower(Type.NO_DROP)) {
			acp.removePower(Type.NO_DROP);
			Utils.sI18n(player, "noDropDisabled");
			if (!player.equals(sender))
				Utils.sI18n(sender, "noDropDisabledTarget", replace);
		} else {
			acp.setPower(Type.NO_DROP);
			Utils.sI18n(player, "noDropEnabled");
			if (!player.equals(sender))
				Utils.sI18n(sender, "noDropEnabledTarget", replace);
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
					new RemovePowerTask(acp, Type.NO_DROP, sender),
					Utils.secInTick * ConfigEnum.SCALE_TIMEOUT.getInt() * timeOutValue);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.Commands.CoreCommand#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(final String... args) {
		return args != null;
	}

}
