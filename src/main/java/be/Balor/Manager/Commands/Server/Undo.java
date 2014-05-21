/**
 * **********************************************************************
 * This file is part of AdminCmd.
 *
 * AdminCmd is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * AdminCmd is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * AdminCmd. If not, see <http://www.gnu.org/licenses/>.
 * **********************************************************************
 */
package be.Balor.Manager.Commands.Server;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.Balor.Manager.LocaleManager;
import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.ActionNotPermitedException;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Tools.CommandUtils.Users;
import be.Balor.bukkit.AdminCmd.ACHelper;

/**
 * @author Balor (aka Antoine Aflalo)
 *
 */
public class Undo extends ServerCommand {

        /**
         *
         */
        public Undo() {
                permNode = "admincmd.server.replace";
                cmdName = "bal_undo";
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
                if (Users.isPlayer(sender)) {
                        int count = 0;
                        try {
                                count = ACHelper.getInstance().undoLastModification(
                                        ((Player) sender).getName());
                                LocaleManager.sI18n(sender, "undo", "nb", String.valueOf(count));
                        } catch (final Exception e) {
                                LocaleManager.sI18n(sender, "nothingToUndo");
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
                return true;
        }

}
