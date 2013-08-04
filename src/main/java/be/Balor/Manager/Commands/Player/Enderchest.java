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
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with AdminCmd. If not, see <http://www.gnu.org/licenses/>.
************************************************************************/

package be.Balor.Manager.Commands.Player;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Permissions.ActionNotPermitedException;
import be.Balor.Tools.CommandUtils.Users;

/**
* @author JeterLP
*
*/

public class Enderchest extends PlayerCommand {

    /**
    *
    */
    public Enderchest() {
        permNode = "admincmd.player.enderchest";
        cmdName = "jet_enderchest";
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
        if (!Users.isPlayer(sender) {
  	return;
	} 
	final Player player = (Player) sender;
	final Player target = Users.getUser(sender, args, permNode);
        if (target == null) {
		return;  
        }
	player.openInventory(target.getEnderchest());
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
