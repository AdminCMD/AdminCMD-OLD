/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package be.Balor.Player;

import java.util.UUID;
import org.bukkit.OfflinePlayer;

/**
* @author TheJeterLP
*/
public class InformationContainer {
        
        private final UUID uuid;
        private final String lastName;
        
        protected InformationContainer(final UUID uuid, String lastName) {
                this.uuid = uuid;
                this.lastName = lastName;
        }
        
        protected InformationContainer(OfflinePlayer p) {
                this.uuid = p.getUniqueId();
                this.lastName = p.getName();
        }
        
        public UUID getUUID() {
                return uuid;
        }
        
        public String getLastKnownName() {
                return lastName;
        }

}
