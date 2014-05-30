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
package be.Balor.Listeners.Commands;

import be.Balor.Tools.Files.ObjectContainer;
import be.Balor.Tools.Type;
import be.Balor.World.ACWorld;
import org.bukkit.World;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * @author Antoine
 *
 */
public class ACTimePausedListener implements Listener {

        /**
         *
         */
        public ACTimePausedListener() {
        }
        
        @EventHandler(priority = EventPriority.HIGH)
        public void onPlayerJoin(final PlayerJoinEvent event) {
                final Player player = event.getPlayer();
                setFrozenTime(player, player.getWorld());
        }

        /**
         * If the word have the time FROZEN, set the time of the player.
         *
         * @param player
         * @param bWorld
         */
        private void setFrozenTime(final Player player, final World bWorld) {
                final ACWorld world = ACWorld.getWorld(bWorld);
                final ObjectContainer wFreezed = world.getInformation(Type.TIME_FROZEN
                        .toString());
                if (wFreezed.isNull()) {
                        return;
                }
                player.setPlayerTime(wFreezed.getLong(0), false);
        }
        
        @EventHandler(priority = EventPriority.HIGH)
        public void onChangeWorld(final PlayerChangedWorldEvent event) {
                final Player player = event.getPlayer();
                setFrozenTime(player, player.getWorld());
        }
        
        @EventHandler(ignoreCancelled = true)
        public void onCreatureSpawn(final CreatureSpawnEvent e) {
                
                //we disallow mob spawning when the moment of the pause is < 13000 which is daytime
                
                final ACWorld acworld = ACWorld.getWorld(e.getEntity().getWorld());
                final ObjectContainer wFreezed = acworld.getInformation(Type.TIME_FROZEN.toString());
                if (wFreezed.isNull()) {
                        return;
                }
                
                long time = wFreezed.getLong(0);

                //night
                if (time >= 13000) {
                        return;
                }
                
                if(!(e.getEntity() instanceof Monster)) return;

                //since here it's day.
                if (e.getSpawnReason() != CreatureSpawnEvent.SpawnReason.NATURAL) {
                        return;
                }
                
                e.setCancelled(true);
        }
        
}
