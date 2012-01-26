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
package be.Balor.Listeners;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import be.Balor.Player.ACPlayer;
import be.Balor.Tools.MaterialContainer;
import be.Balor.Tools.Type;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.ConfigEnum;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ACBlockListener implements Listener {
	@EventHandler
	public void onBlockDamage(BlockDamageEvent event) {
		if (event.isCancelled())
			return;
		final ACPlayer player = ACPlayer.getPlayer(event.getPlayer().getName());
		final ItemStack itemInHand = event.getItemInHand();
		if (itemInHand != null && itemInHand.getTypeId() == ConfigEnum.SB_ITEM.getInt()
				&& player.hasPower(Type.SUPER_BREAKER)) {
			event.setInstaBreak(true);
			itemInHand.setDurability((short) 0);
		}
	}

	/**
	 * @author Lathanael (aka Philippe Leipold)
	 * 
	 */
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.isCancelled())
			return;
		final Player player = event.getPlayer();
		final Block block = event.getBlock();
		final MaterialContainer mat = ACHelper.getInstance().checkMaterial(player,
				String.valueOf(block.getTypeId()));
		if (!ACHelper.getInstance().inBlackListBlock(player, mat))
			return;
		event.setCancelled(true);
	}
}
