/*************************************************************************
 * This file is part of AdminCmd.
 *
 * AdminCmd is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AdminCmd is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AdminCmd. If not, see <http://www.gnu.org/licenses/>.
 *
 **************************************************************************/

package be.Balor.Manager.Commands.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.TreeSet;

import org.bukkit.command.CommandSender;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Player.IBan;
import be.Balor.Tools.Utils;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.LocaleHelper;

/**
 * @author Lathanael (aka Philippe Leipold)
 * 
 */
public class BanList extends PlayerCommand {

	public BanList() {
		cmdName = "bal_banlist";
		permNode = "admincmd.player.banlist";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.Commands.CoreCommand#execute(org.bukkit.command.
	 * CommandSender, be.Balor.Manager.Commands.CommandArgs)
	 */
	@Override
	public void execute(final CommandSender sender, final CommandArgs args) throws PlayerNotFound {
		final Collection<IBan> banned = ACHelper.getInstance().getBannedPlayers();
		final HashMap<String, String> replace = new HashMap<String, String>();
		final TreeSet<String> toSend = new TreeSet<String>();
		for (final IBan p : banned) {
			replace.clear();
			replace.put("player", p.getPlayer());
			replace.put("reason", p.getReason());
			replace.put("date", Utils.replaceDateAndTimeFormat(p.getDate()));
			toSend.add(LocaleHelper.BANLIST.getLocale(replace));
		}
		for (final String s : toSend)
			sender.sendMessage(s);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.Commands.CoreCommand#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(final String... args) {
		return true;
	}

}
