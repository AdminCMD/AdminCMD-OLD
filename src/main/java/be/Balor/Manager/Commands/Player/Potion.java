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
package be.Balor.Manager.Commands.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.google.common.base.Joiner;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Tools.Utils;
import be.Balor.Tools.Help.String.Str;
import be.Balor.bukkit.AdminCmd.LocaleHelper;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class Potion extends PlayerCommand {
	private final static List<String> potions = new ArrayList<String>();
	static {
		for (PotionEffectType type : PotionEffectType.values())
			potions.add(type.getName());
	}

	/**
	 * 
	 */
	public Potion() {
		super("bal_potion", "admincmd.player.potion");
		other = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.Commands.CoreCommand#execute(org.bukkit.command.
	 * CommandSender, be.Balor.Manager.Commands.CommandArgs)
	 */
	@Override
	public void execute(CommandSender sender, CommandArgs args) throws PlayerNotFound {
		Player target = Utils.getUserParam(sender, args, permNode);
		String potion = args.getString(0);
		String potionFound = Str.matchString(potions, potion);
		HashMap<String, String> replace = new HashMap<String, String>();
		if (potionFound == null) {
			replace.put("value", potion);
			replace.put("type", "potion");
			LocaleHelper.DONT_EXISTS.sendLocale(sender, replace);
			sender.sendMessage(ChatColor.GREEN + "Potion list :");
			sender.sendMessage(Joiner.on(", ").skipNulls().join(potions).toLowerCase());
			return;
		}
		String potionDurationString = args.getString(1);
		String potionAmplifierString = args.getString(2);
		int amplifier = 5;
		int duration = 20 * Utils.secInTick;
		if (potionAmplifierString != null)
			try {
				amplifier = Integer.parseInt(potionAmplifierString);
			} catch (NumberFormatException e) {
				Utils.sI18n(sender, "NaN", "number", potionAmplifierString);
				return;
			}
		if (potionDurationString != null)
			try {
				duration = Integer.parseInt(potionDurationString);
			} catch (NumberFormatException e) {
				Utils.sI18n(sender, "NaN", "number", potionDurationString);
				return;
			}
		target.addPotionEffect(new PotionEffect(PotionEffectType.getByName(potionFound), duration,
				amplifier));
		replace.put("player", Utils.getPlayerName(target, sender));
		replace.put("potion", potionFound);
		LocaleHelper.POTION_EFFECT.sendLocale(sender, replace);
		if (!target.equals(sender))
			LocaleHelper.POTION_EFFECT.sendLocale(target, replace);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.Commands.CoreCommand#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(String... args) {
		return args != null && args.length >= 3;
	}

}
