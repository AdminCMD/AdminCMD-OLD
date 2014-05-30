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
package be.Balor.Manager.Commands.Mob;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.ActionNotPermitedException;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.LocaleManager;
import be.Balor.Tools.CommandUtils.Users;
import be.Balor.bukkit.AdminCmd.ACPluginManager;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * @author Balor (aka Antoine Aflalo)
 *
 */
public class SpawnMob extends MobCommand {

        /**
         *
         */
        public SpawnMob() {
                permNode = "admincmd.mob.spawn";
                cmdName = "bal_mob";
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
        public void execute(final CommandSender sender, final CommandArgs args)
                throws ActionNotPermitedException, PlayerNotFound {
                if (Users.isPlayer(sender)) {
                        final HashMap<String, String> replace = new HashMap<String, String>();
                        final String name = args.getString(0);
                        replace.put("mob", name);
                        int nbTaped;
                        int distance = 0;
                        try {
                                nbTaped = args.getInt(1);
                        } catch (final Exception e) {
                                nbTaped = 1;
                        }
                        try {
                                distance = args.getInt(2);
                        } catch (final Exception e) {
                                distance = 0;
                        }

                        Player temp = Users.getUser(sender, args, permNode, 3, false);
                        if (temp == null) {
                                temp = ((Player) sender);
                        }

                        final Player player = temp;
                        Location loc;
                        if (distance == 0 && player.equals(sender)) {
                                loc = player.getTargetBlock(null, 100).getLocation()
                                        .add(0, 1, 0);
                        } else if (distance == 0) {
                                final Location playerLoc = player.getLocation();
                                loc = playerLoc.add(
                                        playerLoc
                                        .getDirection()
                                        .normalize()
                                        .multiply(2)
                                        .toLocation(player.getWorld(),
                                                playerLoc.getYaw(),
                                                playerLoc.getPitch())).add(0, 1D, 0);
                        } else {
                                final Location playerLoc = player.getLocation();
                                loc = playerLoc.add(
                                        playerLoc
                                        .getDirection()
                                        .normalize()
                                        .multiply(distance)
                                        .toLocation(player.getWorld(),
                                                playerLoc.getYaw(),
                                                playerLoc.getPitch())).add(0, 1D, 0);
                        }
                        EntityType ct = null;

                        if (name.contains(":")) {
                                final String[] creatures = name.split(":");
                                ct = EntityType.fromName(creatures[0]);
                                EntityType ct2 = EntityType.fromName(creatures[1]);

                                if (ct == null) {
                                        try {
                                                ct = EntityType.valueOf(creatures[0].toUpperCase());
                                        } catch (IllegalArgumentException ex) {
                                                ct = null;
                                        }
                                }

                                if (ct2 == null) {
                                        try {
                                                ct2 = EntityType.valueOf(creatures[1].toUpperCase());
                                        } catch (IllegalArgumentException ex) {
                                                ct2 = null;
                                        }
                                }

                                if (ct == null) {
                                        replace.put("mob", creatures[0]);
                                        LocaleManager.sI18n(sender, "errorMob", replace);
                                }
                                if (ct2 == null) {
                                        replace.put("mob", creatures[1]);
                                        LocaleManager.sI18n(sender, "errorMob", replace);
                                }
                                if (ct == null || ct2 == null) {
                                        return;
                                }
                                ACPluginManager.scheduleSyncTask(new PassengerMob(loc, nbTaped,
                                        ct, ct2, player, sender));
                        } else {
                                ct = EntityType.fromName(name);

                                if (ct == null) {
                                        try {
                                                ct = EntityType.valueOf(name.toUpperCase());
                                        } catch (IllegalArgumentException e) {
                                                ct = null;
                                        }
                                }

                                if (ct == null) {
                                        LocaleManager.sI18n(sender, "errorMob", replace);
                                        return;
                                }

                                ACPluginManager.scheduleSyncTask(new NormalMob(loc, nbTaped,
                                        ct, player, sender));
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
                return args != null && args.length >= 1;
        }

        protected class NormalMob implements Runnable {

                protected Location loc;
                protected int nb;
                protected EntityType ct;
                protected Player player;
                protected CommandSender sender;

                /**
                 *
                 */
                public NormalMob(final Location loc, final int nb, final EntityType ct,
                        final Player player, final CommandSender sender) {
                        this.loc = loc;
                        this.nb = nb;
                        this.ct = ct;
                        this.player = player;
                        this.sender = sender;
                }

                @Override
                public void run() {
                        final HashMap<String, String> replace = new HashMap<String, String>();
                        replace.put("mob", ct.getName());
                        for (int i = 0; i < nb; i++) {
                                loc.getWorld().spawnEntity(loc, ct);
                        }

                        replace.put("nb", String.valueOf(nb));
                        if (player.equals(sender)) {
                                LocaleManager.sI18n(player, "spawnMob", replace);
                        } else {
                                replace.put("player", Users.getPlayerName((Player) sender));
                                LocaleManager.sI18n(player, "spawnMobOther", replace);
                        }
                }
        }

        protected class PassengerMob extends NormalMob {

                protected EntityType passenger;

                /**
                 *
                 * @param loc
                 * @param nb
                 * @param mount
                 * @param passenger
                 * @param player
                 * @param sender
                 */
                public PassengerMob(final Location loc, final int nb,
                        final EntityType mount, final EntityType passenger,
                        final Player player, final CommandSender sender) {
                        super(loc, nb, mount, player, sender);
                        this.passenger = passenger;
                }

                @Override
                public void run() {
                        final HashMap<String, String> replace = new HashMap<String, String>();
                        replace.put("mob", ct.getName() + "-" + passenger.getName());
                        for (int i = 0; i < nb; i++) {
                                loc.getWorld()
                                        .spawnEntity(loc, ct)
                                        .setPassenger(
                                                loc.getWorld().spawnEntity(loc, passenger));
                        }

                        replace.put("nb", String.valueOf(nb));
                        if (player.equals(sender)) {
                                LocaleManager.sI18n(player, "spawnMob", replace);
                        } else {
                                replace.put("player", Users.getPlayerName((Player) sender));
                                LocaleManager.sI18n(player, "spawnMobOther", replace);
                        }
                }

        }

}
