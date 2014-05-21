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

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.ActionNotPermitedException;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.bukkit.AdminCmd.LocaleHelper;
import be.Balor.bukkit.AdminCmd.TextLocale;

/**
 * @author Balor (aka Antoine Aflalo)
 *
 */
public class ReloadTxt extends ServerCommand {

        /**
         *
         */
        public ReloadTxt() {
                super("bal_rtxt", "admincmd.server.reloadtxt");
        }

        /*
         * (non-Javadoc)
         * 
         * @see be.Balor.Manager.Commands.CoreCommand#execute(org.bukkit.command.
         * CommandSender, be.Balor.Manager.Commands.CommandArgs)
         */
        @Override
        public void execute(final CommandSender sender, final CommandArgs args)
                throws PlayerNotFound, ActionNotPermitedException {
                for (final TextLocale txt : TextLocale.values()) {
                        txt.reloadContent();
                }
                LocaleHelper.TXT_RELOADED.sendLocale(sender);

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
