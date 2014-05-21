/*This file is part of AdminCmd.

 AdminCmd is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 AdminCmd is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with AdminCmd.  If not, see <http://www.gnu.org/licenses/>.*/
package be.Balor.Player.sql;

import be.Balor.Player.ACPlayer;
import be.Balor.Player.EmptyPlayer;
import be.Balor.Player.IPlayerFactory;
import be.Balor.Tools.Debug.ACLogger;
import be.Balor.Tools.Debug.DebugLog;
import be.Balor.bukkit.AdminCmd.ACPluginManager;
import belgium.Balor.SQL.Database;
import com.google.common.base.Joiner;
import com.google.common.collect.MapMaker;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * @author Balor (aka Antoine Aflalo)
 *
 */
public class SQLPlayerFactory implements IPlayerFactory {

        private final Map<UUID, Long> playersID = new MapMaker()
                .concurrencyLevel(6).makeMap();

        /**
         *
         */
        public SQLPlayerFactory() {
                final ResultSet rs = Database.DATABASE
                        .query("SELECT `uuid`,`id` FROM `ac_players_new`");

                try {
                        while (rs.next()) {
                                playersID.put(UUID.fromString(rs.getString("uuid")), rs.getLong("id"));
                        }
                        rs.close();
                } catch (final SQLException e) {
                        ACLogger.severe("Problem when getting players from the DB", e);
                }
                DebugLog.INSTANCE.info("Players found : "
                        + Joiner.on(", ").join(playersID.keySet()));

        }

        private Long getPlayerID(final UUID uuid) {

                Long id = playersID.get(uuid);
                if (id != null) {
                        return id;
                }
                try {
                        id = doubleCheckPlayer(uuid);
                } catch (final SQLException e) {
                }

                return id;
        }

        /**
         * @param playername
         * @param id
         * @return
         * @throws SQLException
         */
        private Long doubleCheckPlayer(final UUID uuid) throws SQLException {
                ResultSet rs = null;
                Long id;
                final PreparedStatement doubleCheckPlayer = Database.DATABASE
                        .prepare("SELECT `id` FROM `ac_players_new` WHERE `uuid` = ?");
                try {
                        doubleCheckPlayer.clearParameters();
                        doubleCheckPlayer.setString(1, uuid.toString());
                        doubleCheckPlayer.execute();
                        rs = doubleCheckPlayer.getResultSet();
                        if (rs == null) {
                                return null;
                        }
                        if (!rs.next()) {
                                return null;
                        }
                        id = rs.getLong(1);
                        if (id != null) {
                                playersID.put(uuid, id);
                        }
                } finally {
                        try {
                                if (rs != null) {
                                        rs.close();
                                }
                        } catch (final SQLException e) {

                        }
                        Database.DATABASE.closeStatement(doubleCheckPlayer);
                }
                return id;
        }

        /*
         * (Non javadoc)
         * 
         * @see be.Balor.Player.IPlayerFactory#createPlayer(java.lang.String)
         */
        @Override
        public ACPlayer createPlayer(final String playername) {

                final OfflinePlayer op = ACPluginManager.getServer().getOfflinePlayer(playername);

                final Long id = getPlayerID(op.getUniqueId());
                if (id == null) {
                        return new EmptyPlayer(op.getUniqueId());
                } else {
                        return new SQLPlayer(id, op.getUniqueId());
                }

        }

        /*
         * (Non javadoc)
         * 
         * @see
         * be.Balor.Player.IPlayerFactory#createPlayer(org.bukkit.entity.Player)
         */
        @Override
        public ACPlayer createPlayer(final Player player) {
                final Long id = getPlayerID(player.getUniqueId());
                if (id == null) {
                        return new EmptyPlayer(player);
                } else {
                        return new SQLPlayer(player, id);
                }

        }

        /*
         * (Non javadoc)
         * 
         * @see
         * be.Balor.Player.IPlayerFactory#createPlayer(java.lang.UUID)
         */
        @Override
        public ACPlayer createPlayer(final UUID player) {
                final Long id = getPlayerID(player);
                if (id == null) {
                        return new EmptyPlayer(player);
                } else {
                        return new SQLPlayer(id, player);
                }

        }

        /*
         * (Non javadoc)
         * 
         * @see be.Balor.Player.IPlayerFactory#getExistingPlayers()
         */
        @Override
        public Set<UUID> getExistingPlayers() {
                return Collections.unmodifiableSet(playersID.keySet());
        }

        /*
         * (Non javadoc)
         * 
         * @see be.Balor.Player.IPlayerFactory#addExistingPlayer(java.lang.String)
         */
        @Override
        public void addExistingPlayer(final UUID player) {
                if (!playersID.containsKey(player)) {
                        try {
                                insertPlayer(player);
                        } catch (final SQLException e) {
                                ACLogger.severe("Problem when adding player to the DB", e);
                        }
                }

        }

        /**
         * @param player
         * @throws SQLException
         */
        private void insertPlayer(final UUID player) throws SQLException {
                ResultSet rs = null;
                final PreparedStatement insertPlayer = Database.DATABASE
                        .prepare("INSERT INTO `ac_players_new` (`uuid`) VALUES (?);");
                try {

                        insertPlayer.clearParameters();
                        insertPlayer.setString(1, player.toString());
                        insertPlayer.executeUpdate();

                        rs = insertPlayer.getGeneratedKeys();
                        if (rs.next()) {
                                playersID.put(player, rs.getLong(1));
                        }
                        if (rs != null) {
                                rs.close();
                        }
                } finally {
                        Database.DATABASE.closeStatement(insertPlayer);
                }

        }

}
