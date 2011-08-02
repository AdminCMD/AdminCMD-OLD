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
package be.Balor.Manager.Permissions;

import java.util.LinkedList;
import java.util.logging.Logger;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import be.Balor.Manager.PermParent;
import be.Balor.Manager.Permissions.Plugins.BukkitPermissions;
import be.Balor.Manager.Permissions.Plugins.YetiPermissions;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.AdminCmd;

import com.nijiko.permissions.PermissionHandler;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class PermissionManager {
	private LinkedList<PermParent> permissions = new LinkedList<PermParent>();
	private PermParent majorPerm;
	private static PermissionManager instance = null;
	protected static PermissionHandler permission = null;
	public static final Logger log = Logger.getLogger("Minecraft");
	public static AbstractPermission permissionHandler;

	/**
	 * 
	 */
	private PermissionManager() {
		permissionHandler = new BukkitPermissions();
	}
	/**
	 * @return the instance
	 */
	public static PermissionManager getInstance() {
		if (instance == null)
			instance = new PermissionManager();
		return instance;
	}

	/**
	 * Set major permission, the root.
	 * 
	 * @param major
	 */
	public void setMajorPerm(PermParent major) {
		majorPerm = major;
		for (PermParent pp : permissions)
			majorPerm.addChild(pp.getPermName());
	}

	/**
	 * Add some important node (like myplygin.item)
	 * 
	 * @param toAdd
	 */
	public void addPermParent(PermParent toAdd) {
		permissions.add(toAdd);
	}

	/**
	 * Add permission child (like myplugin.item.add)
	 * 
	 * @param permNode
	 * @param bukkitDefault
	 * @return
	 */
	public Permission addPermChild(String permNode, PermissionDefault bukkitDefault) {
		Permission bukkitPerm = null;
		if ((bukkitPerm = AdminCmd.getBukkitServer().getPluginManager().getPermission(permNode)) == null) {
			bukkitPerm = new Permission(permNode, bukkitDefault);
			AdminCmd.getBukkitServer().getPluginManager().addPermission(bukkitPerm);
			for (PermParent pp : permissions)
				if (permNode.contains(pp.getCompareName()))
					pp.addChild(permNode);
		}
		return bukkitPerm;
	}

	/**
	 * Add permission child (like myplugin.item.add)
	 * 
	 * @param permNode
	 * @return
	 */
	public Permission addPermChild(String permNode) {
		return addPermChild(permNode, PermissionDefault.OP);
	}

	/**
	 * Register all parent node.
	 */
	public void registerAllPermParent() {
		for (PermParent pp : permissions)
			pp.registerBukkitPerm();
		majorPerm.registerBukkitPerm();
		permissions = null;
		majorPerm = null;
	}

	/**
	 * Check the permissions
	 * 
	 * @param player
	 * @param perm
	 * @return boolean
	 */
	public static boolean hasPerm(CommandSender player, String perm) {
		return permissionHandler.hasPerm(player, perm);
	}

	public static boolean hasPerm(CommandSender player, Permission perm) {
		return permissionHandler.hasPerm(player, perm);
	}

	/**
	 * Check the permission with the possibility to disable the error msg
	 * 
	 * @param player
	 * @param perm
	 * @param errorMsg
	 * @return
	 */
	public static boolean hasPerm(CommandSender player, String perm, boolean errorMsg) {
		return permissionHandler.hasPerm(player, perm, errorMsg);

	}

	public static boolean hasPerm(CommandSender player, Permission perm, boolean errorMsg) {
		return permissionHandler.hasPerm(player, perm, errorMsg);

	}

	public static String getPermissionLimit(Player p, String limit) {
		return permissionHandler.getPermissionLimit(p, limit);
	}

	/**
	 * Permission plugin
	 * 
	 * @return
	 */
	public static PermissionHandler getPermission() {
		return permission;
	}

	/**
	 * Set Permission Plugin
	 * 
	 * @param plugin
	 * @return
	 */
	public static boolean setPermission(PermissionHandler plugin) {
		if (permission == null) {
			permission = plugin;
			if(!(Boolean)ACHelper.getInstance().getConfValue("forceOfficialBukkitPerm"))
				permissionHandler = new YetiPermissions(plugin);
		} else {
			return false;
		}
		return true;
	}

}
