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
import java.util.HashMap;

import be.Balor.Tools.Utils;
import be.Balor.bukkit.AdminCmd.LocaleHelper;

/**
 * @author Balor (aka Antoine Aflalo)
 *
 */
public class TempBannedPlayer extends BannedPlayer implements ITempBan {

        private Date endBan;

        /**
         *
         */
        public TempBannedPlayer() {
        }

        /**
         * @param player
         * @param reason
         * @param time in second.
         */
        public TempBannedPlayer(final String player, final String reason,
                final long time) {
                super(player, reason);
                endBan = new Date(System.currentTimeMillis() + time
                        * Utils.secondInMillis);
        }

        /*
         * (non-Javadoc)
         * 
         * @see be.Balor.Player.TempBan#getEndBan()
         */
        @Override
        public Date getEndBan() {
                return endBan;
        }

        /*
         * (non-Javadoc)
         * 
         * @see be.Balor.Player.TempBan#setEndBan(java.sql.Date)
         */
        @Override
        public void setEndBan(final Date endBan) {
                this.endBan = endBan;
        }

        /*
         * (non-Javadoc)
         * 
         * @see be.Balor.Player.TempBan#timeLeft()
         */
        @Override
        public long timeLeft() {
                return endBan.getTime() - System.currentTimeMillis();
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
                final int prime = 31;
                int result = super.hashCode();
                result = prime * result + ((endBan == null) ? 0 : endBan.hashCode());
                return result;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(final Object obj) {
                if (this == obj) {
                        return true;
                }
                if (!super.equals(obj)) {
                        return false;
                }
                if (!(obj instanceof TempBannedPlayer)) {
                        return false;
                }
                final TempBannedPlayer other = (TempBannedPlayer) obj;
                if (endBan == null) {
                        if (other.endBan != null) {
                                return false;
                        }
                } else if (!endBan.equals(other.endBan)) {
                        return false;
                }
                return true;
        }

        /*
         * (non-Javadoc)
         * 
         * @see be.Balor.Player.ITempBan#getReadableTimeLeft()
         */
        @Override
        public String getReadableTimeLeft() {
                final Long[] timeleft = Utils.transformToElapsedTime(this.timeLeft());
                final HashMap<String, String> replace = new HashMap<String, String>();
                replace.put("d", String.valueOf(timeleft[0]));
                replace.put("h", String.valueOf(timeleft[1]));
                replace.put("m", String.valueOf(timeleft[2]));
                replace.put("s", String.valueOf(timeleft[3]));
                return LocaleHelper.TIME_LEFT.getLocale(replace);
        }

}
