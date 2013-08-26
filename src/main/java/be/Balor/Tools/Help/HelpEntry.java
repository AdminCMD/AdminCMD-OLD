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

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import be.Balor.Manager.Permissions.PermissionManager;
import be.Balor.Tools.CommandUtils.Users;
import be.Balor.Tools.Help.String.ACMinecraftFontWidthCalculator;
import be.Balor.bukkit.AdminCmd.ConfigEnum;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class HelpEntry {
	private final String command;
	private final String description;
	private final String detailedDesc;
	private final List<String> permissions;
	private final String commandName;

	/**
	 * @param command
	 * @param description
	 * @param detailedDesc
	 * @param permissions
	 * @param commandName
	 */
	HelpEntry(final String command, final String description, final String detailedDesc, final List<String> permissions, final String commandName) {
		super();
		this.command = command;
		this.description = description;
		this.detailedDesc = detailedDesc;
		this.permissions = permissions;
		this.commandName = commandName;
	}

	public boolean hasPerm(final CommandSender p) {
		for (final String perm : permissions) {
			if (perm.equals("OP")) {
				if (Users.isPlayer(p, false)) {
					return p.isOp();
				} else {
					return true;
				}
			}
			return PermissionManager.hasPerm(p, perm, false);
		}
		return true;
	}

	@Override
	public String toString() {
		return String.format("%s/%s%s : %s", ChatColor.getByChar(ConfigEnum.H_C_CMD.getString()), addColors(command, false),
				ChatColor.getByChar(ConfigEnum.H_C_NORMAL.getString()), description);
	}

	private String addColors(final String msg, final boolean description) {
		return msg.replace("[", ChatColor.getByChar(ConfigEnum.H_C_REQ_PARAM.getString()) + "[")
				.replace("]", "]" + ChatColor.getByChar(description ? ConfigEnum.H_C_NORMAL.getString() : ConfigEnum.H_C_CMD.getString()))
				.replace("<", ChatColor.getByChar(ConfigEnum.H_C_OPT_PARAM.getString()) + "<")
				.replace(">", ">" + ChatColor.getByChar(description ? ConfigEnum.H_C_NORMAL.getString() : ConfigEnum.H_C_CMD.getString()));
	}

	public String chatString(final boolean detailed) {
		String line = getFormatedCmd();

		final int sizeRemaining = ACMinecraftFontWidthCalculator.chatwidth - ACMinecraftFontWidthCalculator.getStringWidth(line);
		int descriptionSize;
		if (detailed && !detailedDesc.equals("")) {
			descriptionSize = ACMinecraftFontWidthCalculator.strLen(detailedDesc);
			line += ACMinecraftFontWidthCalculator.strPadLeftChat(addColors(detailedDesc, true), sizeRemaining, ' ');
		} else {
			descriptionSize = ACMinecraftFontWidthCalculator.strLen(description);
			line += ACMinecraftFontWidthCalculator.strPadLeftChat(addColors(description, true), sizeRemaining, ' ');
		}

		if (ConfigEnum.H_SHORTE.getBoolean()) {
			return ACMinecraftFontWidthCalculator.strChatTrim(line);
		} else if (sizeRemaining > descriptionSize || !ConfigEnum.H_WRAP.getBoolean()) {
			return line;
		} else if (ConfigEnum.H_RWRAP.getBoolean()) {
			return ACMinecraftFontWidthCalculator.strChatWordWrapRight(line, 10, ' ', ':');
		} else {
			return ACMinecraftFontWidthCalculator.strChatWordWrap(line, 10);
		}
	}

	public String consoleString(final boolean detailed) {
		final int width = System.getProperty("os.name").startsWith("Windows") ? 80 - 17 : 90;
		String line = getFormatedCmd();

		final int sizeRemaining = width - ACMinecraftFontWidthCalculator.strLen(line);
		int descriptionSize;
		if (detailed && !detailedDesc.equals("")) {
			descriptionSize = ACMinecraftFontWidthCalculator.strLen(detailedDesc);
			line += ACMinecraftFontWidthCalculator.unformattedPadLeft(addColors(detailedDesc, true), sizeRemaining, ' ');
		} else {
			descriptionSize = ACMinecraftFontWidthCalculator.strLen(description);
			line += ACMinecraftFontWidthCalculator.unformattedPadLeft(addColors(description, true), sizeRemaining, ' ');
		}

		if (ConfigEnum.H_SHORTE.getBoolean()) {
			return ACMinecraftFontWidthCalculator.strTrim(line, width);
		} else if (sizeRemaining > descriptionSize || !ConfigEnum.H_WRAP.getBoolean()) {
			return line;
		} else if (ConfigEnum.H_RWRAP.getBoolean()) {
			return ACMinecraftFontWidthCalculator.strWordWrapRight(line, width, 10, ' ', ':');
		} else {
			return ACMinecraftFontWidthCalculator.strWordWrap(line, width, 10);
		}

	}

	private String getFormatedCmd() {
		return String.format("%s/%s%s : ", ChatColor.getByChar(ConfigEnum.H_C_CMD.getString()), addColors(command, false),
				ChatColor.getByChar(ConfigEnum.H_C_NORMAL.getString()));
	}

	/**
	 * @return the command
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * @return the commandName
	 */
	public String getCommandName() {
		return commandName;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the detailed description
	 */
	public String getDetailedDesc() {
		return detailedDesc;
	}
}
