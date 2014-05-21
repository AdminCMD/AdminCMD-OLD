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
package be.Balor.Player;

import java.sql.Date;

/**
 * @author Balor (aka Antoine Aflalo)
 *
 */
public interface IBan {

        /**
         * @return the reason
         */
        public abstract String getReason();

        /**
         * @return the date
         */
        public abstract Date getDate();

        /**
         * @return the player
         */
        public abstract String getPlayer();

        /**
         * get the name of the player that issued the ban.
         *
         * @param banner name of the player
         */
        public abstract String getBanner();

}
