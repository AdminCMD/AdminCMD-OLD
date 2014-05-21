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
package be.Balor.Manager.Commands.Warp;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.Balor.Manager.LocaleManager;
import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Commands.Tp.TeleportCommand;
import be.Balor.Manager.Exceptions.ActionNotPermitedException;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Exceptions.WorldNotLoaded;
import be.Balor.Manager.Permissions.PermChild;
import be.Balor.Manager.Permissions.PermissionManager;
import be.Balor.Tools.Warp;
import be.Balor.Tools.CommandUtils.Users;
import be.Balor.Tools.Help.String.ACMinecraftFontWidthCalculator;
import be.Balor.World.ACWorld;
import be.Balor.World.WorldManager;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.ACPluginManager;
import be.Balor.bukkit.AdminCmd.ConfigEnum;
import be.Balor.bukkit.AdminCmd.LocaleHelper;
import java.util.Set;
import org.bukkit.ChatColor;

/**
 * @authors Balor, Lathanael
 *
 */
public class TpToWarp extends WarpCommand {

        private PermChild tpAll;

        /**
         *
         */
        public TpToWarp() {
                permNode = "admincmd.warp.tp";
                cmdName = "bal_tpwarp";
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
                if (args.length == 0) {
                        if (Users.isPlayer(sender)) {
                                final Player p = (Player) sender;
                                String msg = "";
                                Set<String> wp;
                                if (args.hasFlag('a')) {
                                        if (!PermissionManager.hasPerm(sender, tpAll)) {
                                                return;
                                        }
                                        wp = WorldManager.getInstance().getAllWarpList();
                                } else {
                                        wp = ACWorld.getWorld(p.getWorld()).getWarpList();
                                }
                                sender.sendMessage(ChatColor.GOLD + "Warp Point(s) : "
                                        + ChatColor.WHITE + wp.size());
                                for (final String name : wp) {
                                        msg += name + ", ";
                                        if (msg.length() >= ACMinecraftFontWidthCalculator.chatwidth) {
                                                sender.sendMessage(msg);
                                                msg = "";
                                        }
                                }
                                if (!msg.equals("")) {
                                        if (msg.endsWith(", ")) {
                                                msg = msg.substring(0, msg.lastIndexOf(","));
                                        }
                                        sender.sendMessage(msg);
                                }
                                return;
                        }
                }
                final Player target = Users.getUser(sender, args, permNode, 1, true);
                Location loc = null;
                if (target == null) {
                        return;
                }
                final HashMap<String, String> replace = new HashMap<String, String>();

                if (args.getString(0).contains(":")) {
                        if (!PermissionManager.hasPerm(sender, tpAll)) {
                                return;
                        }
                        final String[] split = args.getString(0).split(":");
                        final String world = split[0];
                        final String warp = split[1];
                        replace.put("name", world + ":" + warp);
                        try {
                                final ACWorld acWorld = ACWorld.getWorld(world);
                                final Warp warpPoint = acWorld.getWarp(warp);
                                if (warpPoint == null) {
                                        replace.put("name", args.getString(0));
                                        LocaleManager.sI18n(sender, "errorWarp", replace);
                                        return;
                                }
                                if (warpPoint.permission != null
                                        && !warpPoint.permission.isEmpty()
                                        && !warpPoint.permission.equalsIgnoreCase("")
                                        && !PermissionManager.hasPerm(sender, permNode + "."
                                                + warpPoint.permission, false)) {
                                        replace.put("point", warp);
                                        LocaleHelper.WARP_NO_PERM.sendLocale(sender, replace);
                                        return;
                                }
                                loc = warpPoint.loc;
                                replace.put("name", acWorld.getName() + ":" + warpPoint.name);
                        } catch (final WorldNotLoaded e) {
                                LocaleManager.sI18n(sender, "worldNotFound", "world", world);
                                return;
                        }
                } else {
                        World world;
                        if (Users.isPlayer(sender, false)) {
                                world = ((Player) sender).getWorld();
                        } else if (sender instanceof BlockCommandSender) {
                                world = ((BlockCommandSender) sender).getBlock().getWorld();
                        } else {
                                LocaleHelper.ERROR_EXTERNAL_WARP.sendLocale(sender);
                                return;
                        }
                        replace.put("name", args.getString(0));

                        try {
                                final Warp warpPoint = ACWorld.getWorld(world).getWarp(
                                        args.getString(0));
                                if (warpPoint == null) {
                                        replace.put("name", args.getString(0));
                                        LocaleManager.sI18n(sender, "errorWarp", replace);
                                        return;
                                }
                                if (warpPoint.permission != null
                                        && !warpPoint.permission.isEmpty()
                                        && !warpPoint.permission.equalsIgnoreCase("")
                                        && !PermissionManager.hasPerm(sender, permNode + "."
                                                + warpPoint.permission, false)) {
                                        replace.put("point", args.getString(0));
                                        LocaleHelper.WARP_NO_PERM.sendLocale(sender, replace);
                                        return;
                                }
                                loc = warpPoint.loc;
                                replace.put("name", warpPoint.name);
                        } catch (final WorldNotLoaded e) {
                        }
                }
                if (loc == null) {
                        LocaleManager.sI18n(sender, "errorWarp", replace);
                        return;
                } else {
                        ACPluginManager.getScheduler().scheduleSyncDelayedTask(
                                ACHelper.getInstance().getCoreInstance(),
                                new DelayedTeleport(target.getLocation(), loc, target,
                                        replace, sender), ConfigEnum.TP_DELAY.getLong());
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

        /*
         * (non-Javadoc)
         * 
         * @see be.Balor.Manager.Commands.CoreCommand#registerBukkitPerm()
         */
        @Override
        public void registerBukkitPerm() {
                super.registerBukkitPerm();
                tpAll = new PermChild("admincmd.warp.tp.all", bukkitDefault);
                permParent.addChild(tpAll);
        }

        private class DelayedTeleport implements Runnable {

                protected Location locBefore, teleportToLoc;
                protected Player target;
                protected HashMap<String, String> replace;
                protected CommandSender sender;

                public DelayedTeleport(final Location locBefore,
                        final Location teleportLoc, final Player target,
                        final HashMap<String, String> replace,
                        final CommandSender sender) {
                        this.target = target;
                        this.locBefore = locBefore;
                        this.teleportToLoc = teleportLoc;
                        this.replace = replace;
                        this.sender = sender;
                }

                @Override
                public void run() {
                        if (locBefore.equals(target.getLocation())
                                && ConfigEnum.CHECKTP.getBoolean()) {
                                TeleportCommand.teleportWithChunkCheck(target, teleportToLoc);
                                Users.sendMessage(sender, target, "tpWarp", replace);
                        } else if (!ConfigEnum.CHECKTP.getBoolean()) {
                                TeleportCommand.teleportWithChunkCheck(target, teleportToLoc);
                                Users.sendMessage(sender, target, "tpWarp", replace);
                        } else {
                                replace.clear();
                                replace.put("cmdname", "Warp");
                                Users.sendMessage(sender, target, "errorMoved", replace);
                        }
                }
        }
}
