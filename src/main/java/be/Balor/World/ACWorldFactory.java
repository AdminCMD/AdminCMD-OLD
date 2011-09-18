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
package be.Balor.World;


import org.bukkit.World;

import be.Balor.Manager.Exceptions.WorldNotLoaded;
import be.Balor.bukkit.AdminCmd.ACPluginManager;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ACWorldFactory {
	final String directory;

	/**
	 * 
	 */
	public ACWorldFactory(String directory) {
		this.directory = directory;
	}


	ACWorld createWorld(String worldName) throws WorldNotLoaded {
		World w = ACPluginManager.getServer().getWorld(worldName);
		if (w == null)
			throw new WorldNotLoaded(worldName);
		else if (directory != null)
			return new FileWorld(w, directory);
		else
			return null;
	}

}
