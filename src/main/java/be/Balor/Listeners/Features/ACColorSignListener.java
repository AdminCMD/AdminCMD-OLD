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
package be.Balor.Listeners.Features;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import be.Balor.Manager.LocaleManager;
import be.Balor.Manager.Permissions.PermissionManager;
import be.Balor.Tools.Utils;
import be.Balor.Tools.CommandUtils.Materials;

/**
 * @author Balor (aka Antoine Aflalo)
 *
 */
public class ACColorSignListener implements Listener {

        @EventHandler(ignoreCancelled = true)
        public void onSignChange(final SignChangeEvent event) {
                final boolean havePerm = PermissionManager.hasPerm(event.getPlayer(),
                        "admincmd.coloredsign.create", false);

                String parsed = null;
                String line;
                if (Utils.signExtention && (line = event.getLine(0)) != null
                        && line.endsWith("Sign]")) {
                        return;
                }
                for (int i = 0; i < 4; i++) {
                        line = event.getLine(i);
                        if (line == null || (line != null && line.isEmpty())) {
                                continue;
                        }
                        if (!Materials.REGEX_COLOR_PERSER.matcher(line).find()) {
                                continue;
                        }
                        if (!havePerm) {
                                LocaleManager.sI18n(event.getPlayer(), "errorNotPerm", "p",
                                        "admincmd.coloredsign.create");
                                return;
                        }
                        parsed = Materials.colorParser(line);
                        if (parsed != null) {
                                event.setLine(i, parsed);
                        }

                }
        }
}
