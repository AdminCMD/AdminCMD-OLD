/************************************************************************
 * ThMobCheck.is file MobCheck.is part of AdminCmd.
 *
 * AdminCmd MobCheck.is free software: you can redMobCheck.istribute it and/or modify
 * it under the terms of the GNU General Public License as publMobCheck.ished by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AdminCmd MobCheck.is dMobCheck.istributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AdminCmd.  If not, see <http://www.gnu.org/licenses/>.
 ************************************************************************/
package be.Balor.Manager.Commands.Mob;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Exceptions.WorldNotLoaded;
import be.Balor.Manager.Permissions.ActionNotPermitedException;
import be.Balor.Tools.Utils;
import be.Balor.Tools.Compatibility.MinecraftReflection;
import be.Balor.Tools.Compatibility.Reflect.MethodHandler;
import be.Balor.bukkit.AdminCmd.ACPluginManager;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class KillMob extends MobCommand {

	/**
	 *
	 */
	public KillMob() {
		permNode = "admincmd.mob.kill";
		cmdName = "bal_killmob";
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
		final HashMap<String, String> replace = new HashMap<String, String>();
		String type = "all";
		Integer range = null;
		if (args.hasFlag('r')) {
			range = Integer.parseInt(args.getValueFlag('r'));
			range *= range;
		}
		if (args.length >= 1) {
			type = args.getString(0);
		}
		replace.put("type", type);
		final String worldString = args.getValueFlag('w');
		final List<World> worldList = new ArrayList<World>();

		if (Utils.isPlayer(sender, false)) {
			World w = ((Player) sender).getWorld();
			if (worldString != null) {
				w = getWorld(worldString);
			}
			worldList.add(w);
			replace.put("worlds", w.getName());
			Utils.sI18n(sender, "killMob", replace);
		} else {
			if (worldString != null) {
				worldList.add(getWorld(worldString));
			} else {
				String worlds = "";
				for (final World world : sender.getServer().getWorlds()) {
					worldList.add(world);
					worlds += world.getName() + ", ";
				}
				if (!worlds.equals("")) {
					if (worlds.endsWith(", ")) {
						worlds = worlds.substring(0, worlds.lastIndexOf(","));
					}
					replace.put("worlds", worlds);
					Utils.sI18n(sender, "killMob", replace);
				}
			}
		}
		final String finalType = type;
		final CommandSender finalSender = sender;
		final Integer finalRange = range;
		ACPluginManager.getScheduler().runTaskAsynchronously(
				ACPluginManager.getCorePlugin(), new Runnable() {
					@Override
					public void run() {
						killMobs(worldList, finalType, finalSender, finalRange);
					}
				});

	}

	private World getWorld(final String name) {
		final World w = Bukkit.getWorld(name);
		if (w == null) {
			throw new WorldNotLoaded("The World " + name + " is not loaded");
		}
		return w;
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

	private void killMobs(final List<World> worlds, final String type,
			final CommandSender sender, final Integer range) {
		int mobKilled = 0;
		Class<? extends Entity> clazz = null;
		if (type.equalsIgnoreCase("monsters")) {
			clazz = Monster.class;

		} else if (type.equalsIgnoreCase("animals")) {
			clazz = Animals.class;
		} else if (!type.equalsIgnoreCase("all")) {
			EntityType ct = null;
			ct = EntityType.fromName(type);
			if (ct == null) {
				final HashMap<String, String> replace = new HashMap<String, String>();
				replace.put("mob", type);
				Utils.sI18n(sender, "errorMob", replace);
				return;
			}
			clazz = ct.getEntityClass();
		}
		if (clazz == null) {
			for (final World w : worlds) {
				for (final Entity m : w.getEntities()) {
					if (m instanceof HumanEntity) {
						continue;
					}
					if (killEntity(m, sender, range)) {
						mobKilled++;
					}

				}
			}
		} else {
			for (final World w : worlds) {
				for (final Entity m : w.getEntitiesByClass(clazz)) {
					if (killEntity(m, sender, range)) {
						mobKilled++;
					}
				}
			}
		}
		Utils.sI18n(sender, "killedMobs", "nbKilled", String.valueOf(mobKilled));
	}

	private boolean killEntity(final Entity e, final CommandSender sender,
			final Integer range) {
		if (!checkKillCondition(e, sender, range)) {
			return false;
		}
		final Object entity = MinecraftReflection.getHandle(e);
		final MethodHandler die = new MethodHandler(entity.getClass(), "die");
		die.invoke(entity);
		return true;
	}

	/**
	 * Check if the entity can be killed with the giving conditions
	 * 
	 * @param toCheck
	 *            entity to be checked
	 * @param sender
	 *            sender of the command
	 * @param range
	 *            distance between the entity and the player accepted for a
	 *            kill.
	 * @return
	 */
	private boolean checkKillCondition(final Entity toCheck,
			final CommandSender sender, final Integer range) {
		boolean result = true;
		if (range != null && Utils.isPlayer(sender, false)) {
			result = toCheck.getLocation().distanceSquared(
					((Player) sender).getLocation()) <= range;
		}
		return result;
	}
}
