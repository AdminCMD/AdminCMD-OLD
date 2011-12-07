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
package be.Balor.Tools.Help;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import be.Balor.Tools.Debug.DebugLog;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class HelpLister {
	private static HelpLister instance = null;
	private HashMap<String, HelpList> plugins = new HashMap<String, HelpList>();
	private List<String> noCmds = new ArrayList<String>();

	private HelpLister() {
	}

	/**
	 * @return the instance
	 */
	public static HelpLister getInstance() {
		if (instance == null)
			instance = new HelpLister();
		return instance;
	}

	public static void killInstance() {
		instance = null;
	}

	/**
	 * Add a plugin to the lister
	 * 
	 * @param plugin
	 */
	public void addPlugin(Plugin plugin) {
		String pName = plugin.getDescription().getName();
		if (!plugins.containsKey(pName) && !noCmds.contains(pName))
			try {
				plugins.put(pName, new HelpList(plugin));
			} catch (IllegalArgumentException e) {
				noCmds.add(pName);
			}

	}

	/**
	 * 
	 * @return the list of all registered plugin
	 */
	public Set<String> getPluginList() {
		return plugins.keySet();
	}

	/**
	 * Add a new helpEntry for the wanted plugin. If the plugin is not found,
	 * add it in the database.
	 * 
	 * @param command
	 *            command
	 * @param description
	 *            description of the command
	 * @param plugin
	 *            plugin that is attached to the command
	 * @param permissions
	 *            list of permissions to execute the command.
	 * @param cmdName
	 *            true name of the command
	 */
	public void addHelpEntry(String command, String description, String plugin,
			List<String> permissions, String cmdName) {
		HelpList help = plugins.get(plugin);
		if (help == null) {
			help = new HelpList(plugin);
			plugins.put(plugin, help);
		}
		help.addEntry(new HelpEntry(command, description, permissions, cmdName));
	}

	public boolean removeHelpEntry(String plugin, String commandName) {
		HelpList help = plugins.get(plugin);
		if (help == null)
		{
			DebugLog.INSTANCE.severe("Plugin "+plugin+" not found.");
			return false;
		}
		return help.removeEntry(commandName);
	}

	/**
	 * Send the help for the given plugin.
	 * 
	 * @param plugin
	 *            name of the plugin
	 * @param page
	 *            number of the page
	 * @param sender
	 *            the sender of the command
	 * @return
	 */
	public boolean sendHelpPage(String plugin, int page, CommandSender sender) {
		HelpList help = plugins.get(plugin);
		if (help == null)
			return false;
		List<String> toDisplay = help.getPage(page, sender);
		for (String send : toDisplay)
			for (String l : send.split("\n"))
				sender.sendMessage(l);
		return true;

	}
}
