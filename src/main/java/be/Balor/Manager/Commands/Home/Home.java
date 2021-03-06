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
package be.Balor.Manager.Commands.Home;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.Balor.Manager.LocaleManager;
import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Commands.Tp.TeleportCommand;
import be.Balor.Manager.Exceptions.ActionNotPermitedException;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Player.ACPlayer;
import be.Balor.Tools.SimplifiedLocation;
import be.Balor.Tools.Utils;
import be.Balor.Tools.CommandUtils.Users;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.ACPluginManager;
import be.Balor.bukkit.AdminCmd.ConfigEnum;
import be.Balor.bukkit.AdminCmd.LocaleHelper;

/**
 * @authors Balor, Lathanael
 *
 */
public class Home extends HomeCommand {

        public Home() {
                permNode = "admincmd.tp.home";
                cmdName = "bal_home";
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
                if (!Users.isPlayer(sender)) {
                        return;
                }
                final Player player = (Player) sender;
                be.Balor.Tools.Home home = null;
                home = getHome(sender, args.getString(0));
                if (home == null) {
                        return;
                }

                if (sender.getName().equals(home.player)) {
                        this.verifyCanExecute(sender, player);
                }

                Location loc = ACPlayer.getPlayer(home.player).getHome(home.home);
                if (loc == null) {
                        loc = ACPlayer.getPlayer(home.player).getHome(home.home.toLowerCase());
                }
                if (loc == null) {
                        loc = ACPlayer.getPlayer(home.player).getHome(home.home.substring(0, 1).toUpperCase() + home.home.substring(1));
                }
                if (loc == null) {
                        LocaleManager.sI18n(sender, "errorMultiHome", "home", home.home);
                        return;
                } else {
                        ACPluginManager.getScheduler().scheduleSyncDelayedTask(ACHelper.getInstance().getCoreInstance(), new DelayedTeleport(loc, player, home, sender),
                                ConfigEnum.TP_DELAY.getLong());
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

        private class DelayedTeleport implements Runnable {

                protected SimplifiedLocation locBefore;
                protected Location teleportToLoc;
                protected Player target;
                protected be.Balor.Tools.Home home;
                protected CommandSender sender;
                private final boolean checkTp = ConfigEnum.CHECKTP.getBoolean();

                public DelayedTeleport(final Location teleportLoc, final Player target, final be.Balor.Tools.Home home, final CommandSender sender) {
                        this.target = target;
                        this.locBefore = new SimplifiedLocation(target.getLocation());
                        this.teleportToLoc = teleportLoc;
                        this.home = home;
                        this.sender = sender;
                        if (ConfigEnum.TP_DELAY.getLong() > 0) {
                                final Map<String, String> replace = new HashMap<String, String>();
                                replace.put("sec", String.valueOf(ConfigEnum.TP_DELAY.getLong() / Utils.secInTick));
                                LocaleHelper.TELEPORT_SOON.sendLocale(target, replace);
                        }
                }

                @Override
                public void run() {
                        if (!checkTp) {
                                TeleportCommand.teleportWithChunkCheck(target, teleportToLoc);
                                LocaleManager.sI18n(sender, "multiHome", "home", home.home);
                                return;
                        }
                        if (locBefore.equals(target.getLocation())) {
                                TeleportCommand.teleportWithChunkCheck(target, teleportToLoc);
                                LocaleManager.sI18n(sender, "multiHome", "home", home.home);
                        } else {
                                LocaleManager.sI18n(sender, "errorMoved", "cmdname", "Home");
                        }
                }
        }
}
