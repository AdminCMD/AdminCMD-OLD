package be.Balor.Listeners.Features;

import be.Balor.Tools.Compatibility.Reflect.FieldUtils;
import be.Balor.Tools.Debug.ACLogger;
import be.Balor.Tools.Utils;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.ConfigEnum;
import be.Balor.bukkit.AdminCmd.LocaleHelper;
import de.JeterLP.MakeYourOwnCommands.Command.CommandManager;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class ACUnknownCommandListener implements Listener {

               
        @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
        public void onUnknownCommand(final PlayerCommandPreprocessEvent event) {
                String cmd = event.getMessage().replaceFirst("/", "").split(" ")[0];

                final Player player = event.getPlayer();
                if (!ACHelper.getInstance().isCommandRegistered(cmd)) {
                        LocaleHelper.UNKNOWN_COMMAND.sendLocale(player);
                        System.out.println(player.getName() + " issued server command: "
                                + event.getMessage());
                        event.setCancelled(true);
                }
        }        
}
