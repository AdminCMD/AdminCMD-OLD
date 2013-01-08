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
package be.Balor.Tools.Converter;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;

import be.Balor.Tools.Debug.ACLogger;
import be.Balor.World.IWorldFactory;
import be.Balor.World.WorldConvertTask;

/**
 * @author Antoine
 * 
 */
public class WorldConverter {
	private final IWorldFactory oldFactory, newFactory;

	/**
	 * @param oldFactory
	 * @param newFactory
	 */
	public WorldConverter(final IWorldFactory oldFactory,
			final IWorldFactory newFactory) {
		super();
		this.oldFactory = oldFactory;
		this.newFactory = newFactory;
	}

	public synchronized void convert() {
		final List<World> worlds = Bukkit.getServer().getWorlds();
		ACLogger.info("Begin Conversion of " + worlds.size() + " worlds");
		for (final World world : worlds) {
			new WorldConvertTask(oldFactory, newFactory, world).run();
		}
	}

}
