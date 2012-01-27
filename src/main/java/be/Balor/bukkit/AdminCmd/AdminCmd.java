package be.Balor.bukkit.AdminCmd;

import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;

import be.Balor.Listeners.ACBlockListener;
import be.Balor.Listeners.ACColorSignListener;
import be.Balor.Listeners.ACEntityListener;
import be.Balor.Listeners.ACPlayerListener;
import be.Balor.Listeners.ACPluginListener;
import be.Balor.Listeners.ACWeatherListener;
import be.Balor.Listeners.Commands.ACCreatureSpawnListener;
import be.Balor.Listeners.Commands.ACFlyListener;
import be.Balor.Listeners.Commands.ACFoodListener;
import be.Balor.Listeners.Commands.ACGodListener;
import be.Balor.Listeners.Commands.ACLockedServerListener;
import be.Balor.Manager.CommandManager;
import be.Balor.Manager.LocaleManager;
import be.Balor.Manager.Commands.Home.DeleteHome;
import be.Balor.Manager.Commands.Home.Home;
import be.Balor.Manager.Commands.Home.ListHomes;
import be.Balor.Manager.Commands.Home.SetHome;
import be.Balor.Manager.Commands.Items.AddAlias;
import be.Balor.Manager.Commands.Items.AddBlackList;
import be.Balor.Manager.Commands.Items.Coloring;
import be.Balor.Manager.Commands.Items.Drop;
import be.Balor.Manager.Commands.Items.Give;
import be.Balor.Manager.Commands.Items.Kit;
import be.Balor.Manager.Commands.Items.More;
import be.Balor.Manager.Commands.Items.MoreAll;
import be.Balor.Manager.Commands.Items.RemoveAlias;
import be.Balor.Manager.Commands.Items.RemoveBlackList;
import be.Balor.Manager.Commands.Items.Repair;
import be.Balor.Manager.Commands.Items.RepairAll;
import be.Balor.Manager.Commands.Mob.ChangeMobSpawner;
import be.Balor.Manager.Commands.Mob.KillMob;
import be.Balor.Manager.Commands.Mob.MobLimit;
import be.Balor.Manager.Commands.Mob.SpawnMob;
import be.Balor.Manager.Commands.Player.Afk;
import be.Balor.Manager.Commands.Player.BanPlayer;
import be.Balor.Manager.Commands.Player.ClearInventory;
import be.Balor.Manager.Commands.Player.Eternal;
import be.Balor.Manager.Commands.Player.Experience;
import be.Balor.Manager.Commands.Player.FakeQuit;
import be.Balor.Manager.Commands.Player.Feed;
import be.Balor.Manager.Commands.Player.Fireball;
import be.Balor.Manager.Commands.Player.Fly;
import be.Balor.Manager.Commands.Player.Freeze;
import be.Balor.Manager.Commands.Player.GameModeSwitch;
import be.Balor.Manager.Commands.Player.God;
import be.Balor.Manager.Commands.Player.Heal;
import be.Balor.Manager.Commands.Player.Invisible;
import be.Balor.Manager.Commands.Player.Ip;
import be.Balor.Manager.Commands.Player.KickAllPlayers;
import be.Balor.Manager.Commands.Player.KickPlayer;
import be.Balor.Manager.Commands.Player.Kill;
import be.Balor.Manager.Commands.Player.Mute;
import be.Balor.Manager.Commands.Player.NoPickup;
import be.Balor.Manager.Commands.Player.Played;
import be.Balor.Manager.Commands.Player.PlayerList;
import be.Balor.Manager.Commands.Player.PlayerLocation;
import be.Balor.Manager.Commands.Player.Presentation;
import be.Balor.Manager.Commands.Player.PrivateMessage;
import be.Balor.Manager.Commands.Player.Reply;
import be.Balor.Manager.Commands.Player.Roll;
import be.Balor.Manager.Commands.Player.SpyMsg;
import be.Balor.Manager.Commands.Player.SuperBreaker;
import be.Balor.Manager.Commands.Player.UnBan;
import be.Balor.Manager.Commands.Player.UnMute;
import be.Balor.Manager.Commands.Player.Vulcan;
import be.Balor.Manager.Commands.Player.Whois;
import be.Balor.Manager.Commands.Server.BanConvert;
import be.Balor.Manager.Commands.Server.Execution;
import be.Balor.Manager.Commands.Server.Extinguish;
import be.Balor.Manager.Commands.Server.Help;
import be.Balor.Manager.Commands.Server.ListValues;
import be.Balor.Manager.Commands.Server.LockServer;
import be.Balor.Manager.Commands.Server.MOTD;
import be.Balor.Manager.Commands.Server.Memory;
import be.Balor.Manager.Commands.Server.News;
import be.Balor.Manager.Commands.Server.Reload;
import be.Balor.Manager.Commands.Server.ReloadAll;
import be.Balor.Manager.Commands.Server.RepeatCmd;
import be.Balor.Manager.Commands.Server.ReplaceBlock;
import be.Balor.Manager.Commands.Server.Rules;
import be.Balor.Manager.Commands.Server.Set;
import be.Balor.Manager.Commands.Server.Undo;
import be.Balor.Manager.Commands.Server.Uptime;
import be.Balor.Manager.Commands.Server.Version;
import be.Balor.Manager.Commands.Server.WorldDifficulty;
import be.Balor.Manager.Commands.Spawn.SetSpawn;
import be.Balor.Manager.Commands.Spawn.Spawn;
import be.Balor.Manager.Commands.Time.Day;
import be.Balor.Manager.Commands.Time.SetTime;
import be.Balor.Manager.Commands.Tp.LastLocation;
import be.Balor.Manager.Commands.Tp.TpAtSee;
import be.Balor.Manager.Commands.Tp.TpHere;
import be.Balor.Manager.Commands.Tp.TpLoc;
import be.Balor.Manager.Commands.Tp.TpPlayerToPlayer;
import be.Balor.Manager.Commands.Tp.TpTo;
import be.Balor.Manager.Commands.Tp.TpToggle;
import be.Balor.Manager.Commands.Warp.AddWarp;
import be.Balor.Manager.Commands.Warp.RemoveWarp;
import be.Balor.Manager.Commands.Warp.TpToWarp;
import be.Balor.Manager.Commands.Warp.WarpList;
import be.Balor.Manager.Commands.Weather.ClearSky;
import be.Balor.Manager.Commands.Weather.FreezeWeather;
import be.Balor.Manager.Commands.Weather.Rain;
import be.Balor.Manager.Commands.Weather.Storm;
import be.Balor.Manager.Commands.Weather.Strike;
import be.Balor.Manager.Commands.Weather.Thor;
import be.Balor.Manager.Permissions.PermParent;
import be.Balor.Manager.Terminal.TerminalCommandManager;
import be.Balor.Player.ACPlayer;
import be.Balor.Player.FilePlayer;
import be.Balor.Player.PlayerManager;
import be.Balor.Tools.Metrics;
import be.Balor.Tools.Utils;
import be.Balor.Tools.Debug.ACLogger;
import be.Balor.Tools.Debug.DebugLog;
import be.Balor.Tools.Help.HelpLister;
import belgium.Balor.Workers.AFKWorker;
import belgium.Balor.Workers.InvisibleWorker;

/**
 * AdminCmd for Bukkit (fork of PlgEssentials)
 * 
 * @authors Plague, Balor, Lathanael
 */
public final class AdminCmd extends AbstractAdminCmdPlugin {
	private ACHelper worker;

	/**
	 * @param name
	 */
	public AdminCmd() {
		super("Core");
	}

	@Override
	public void onDisable() {
		final PluginDescriptionFile pdfFile = this.getDescription();
		getServer().getScheduler().cancelTasks(this);
		FilePlayer.forceSaveList();
		for (final ACPlayer p : PlayerManager.getInstance().getOnlineACPlayers()) {
			PlayerManager.getInstance().setOffline(p);
		}
		ACPluginManager.getInstance().stopChildrenPlugins();
		CommandManager.getInstance().stopAllExecutorThreads();
		worker = null;
		ACHelper.killInstance();
		InvisibleWorker.killInstance();
		AFKWorker.killInstance();
		CommandManager.killInstance();
		HelpLister.killInstance();
		DebugLog.stopLogging();
		System.gc();
		ACLogger.info("Plugin Disabled. (version " + pdfFile.getVersion() + ")");
	}

	@Override
	public void onEnable() {
		ACPluginManager.setServer(getServer());
		DebugLog.setFile(getDataFolder().getPath());
		final PluginDescriptionFile pdfFile = this.getDescription();
		DebugLog.INSTANCE.info("Plugin Version : " + pdfFile.getVersion());
		final PluginManager pm = getServer().getPluginManager();
		ACLogger.info("Plugin Enabled. (version " + pdfFile.getVersion() + ")");
		pm.registerEvents(new ACPluginListener(), this);
		worker = ACHelper.getInstance();
		worker.setCoreInstance(this);
		super.onEnable();
		TerminalCommandManager.getInstance().setPerm(this);
		worker.loadInfos();
		permissionLinker.registerAllPermParent();
		pm.registerEvents(new ACBlockListener(), this);
		pm.registerEvents(new ACEntityListener(), this);
		pm.registerEvents(new ACPlayerListener(), this);
		pm.registerEvents(new ACWeatherListener(), this);
		if (ConfigEnum.COLSIGN.getBoolean())
			pm.registerEvents(new ACColorSignListener(), this);
		try {
			// create a new metrics object
			final Metrics metrics = new Metrics();
			ACPluginManager.setMetrics(metrics);

			metrics.addCustomData(this, new Metrics.Plotter() {
				@Override
				public String getColumnName() {
					return "Total Banned Players";
				}

				@Override
				public int getValue() {
					return worker.countBannedPlayers();
				}
			});
			metrics.addCustomData(this, new Metrics.Plotter() {
				@Override
				public String getColumnName() {
					return "Total Kits";
				}

				@Override
				public int getValue() {
					return worker.getNbKit();
				}
			});
			metrics.addCustomData(this, new Metrics.Plotter() {
				@Override
				public String getColumnName() {
					return "Total Invisible Players";
				}

				@Override
				public int getValue() {
					return InvisibleWorker.getInstance().nbInvisibles();
				}
			});
			metrics.addCustomData(this, new Metrics.Plotter() {
				@Override
				public String getColumnName() {
					return "Total Afk Players";
				}

				@Override
				public int getValue() {
					return AFKWorker.getInstance().nbAfk();
				}
			});
			getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {

				@Override
				public void run() {
					try {
						metrics.beginMeasuringPlugin(AdminCmd.this);
						DebugLog.INSTANCE.info("Stats started");
					} catch (IOException e) {
						DebugLog.INSTANCE.log(Level.SEVERE, "Stats loggin problem", e);
					}
				}
			}, 30 * Utils.secInTick);
		} catch (final IOException e) {
			DebugLog.INSTANCE.log(Level.SEVERE, "Stats loggin problem", e);
		}
	}

	@Override
	public void registerCmds() {
		final PluginManager pm = getServer().getPluginManager();
		CommandManager.getInstance().registerCommand(Day.class);
		CommandManager.getInstance().registerCommand(Repair.class);
		CommandManager.getInstance().registerCommand(RepairAll.class);
		CommandManager.getInstance().registerCommand(More.class);
		CommandManager.getInstance().registerCommand(PlayerList.class);
		CommandManager.getInstance().registerCommand(PlayerLocation.class);
		if (CommandManager.getInstance().registerCommand(God.class))
			pm.registerEvents(new ACGodListener(), this);
		CommandManager.getInstance().registerCommand(Thor.class);
		CommandManager.getInstance().registerCommand(Kill.class);
		CommandManager.getInstance().registerCommand(Heal.class);
		CommandManager.getInstance().registerCommand(ClearSky.class);
		CommandManager.getInstance().registerCommand(Storm.class);
		CommandManager.getInstance().registerCommand(SetSpawn.class);
		CommandManager.getInstance().registerCommand(Spawn.class);
		CommandManager.getInstance().registerCommand(Memory.class);
		CommandManager.getInstance().registerCommand(SetTime.class);
		CommandManager.getInstance().registerCommand(ClearInventory.class);
		CommandManager.getInstance().registerCommand(Give.class);
		CommandManager.getInstance().registerCommand(AddBlackList.class);
		CommandManager.getInstance().registerCommand(RemoveBlackList.class);
		CommandManager.getInstance().registerCommand(TpHere.class);
		CommandManager.getInstance().registerCommand(TpTo.class);
		CommandManager.getInstance().registerCommand(Coloring.class);
		CommandManager.getInstance().registerCommand(Strike.class);
		CommandManager.getInstance().registerCommand(RemoveAlias.class);
		CommandManager.getInstance().registerCommand(SpawnMob.class);

		CommandManager.getInstance().registerCommand(KickPlayer.class);
		CommandManager.getInstance().registerCommand(PrivateMessage.class);
		CommandManager.getInstance().registerCommand(AddAlias.class);
		CommandManager.getInstance().registerCommand(TpPlayerToPlayer.class);
		CommandManager.getInstance().registerCommand(TpLoc.class);
		CommandManager.getInstance().registerCommand(KickAllPlayers.class);
		CommandManager.getInstance().registerCommand(Vulcan.class);
		CommandManager.getInstance().registerCommand(Drop.class);
		CommandManager.getInstance().registerCommand(Invisible.class);
		CommandManager.getInstance().registerCommand(SpyMsg.class);
		CommandManager.getInstance().registerCommand(Fireball.class);
		CommandManager.getInstance().registerCommand(Home.class);
		CommandManager.getInstance().registerCommand(SetHome.class);
		CommandManager.getInstance().registerCommand(AddWarp.class);
		CommandManager.getInstance().registerCommand(RemoveWarp.class);
		CommandManager.getInstance().registerCommand(TpToWarp.class);
		CommandManager.getInstance().registerCommand(WarpList.class);
		CommandManager.getInstance().registerCommand(Ip.class);
		if (CommandManager.getInstance().registerCommand(BanPlayer.class))
			pm.registerEvents(new ACBlockListener(), this);
		CommandManager.getInstance().registerCommand(UnBan.class);
		CommandManager.getInstance().registerCommand(KillMob.class);
		if (CommandManager.getInstance().registerCommand(Fly.class))
			pm.registerEvents(new ACFlyListener(), this);
		CommandManager.getInstance().registerCommand(DeleteHome.class);
		CommandManager.getInstance().registerCommand(ListHomes.class);
		CommandManager.getInstance().registerCommand(Freeze.class);
		CommandManager.getInstance().registerCommand(Mute.class);
		CommandManager.getInstance().registerCommand(UnMute.class);
		if (CommandManager.getInstance().registerCommand(MobLimit.class))
			pm.registerEvents(new ACCreatureSpawnListener(), this);
		CommandManager.getInstance().registerCommand(NoPickup.class);
		CommandManager.getInstance().registerCommand(FreezeWeather.class);
		CommandManager.getInstance().registerCommand(MOTD.class);
		CommandManager.getInstance().registerCommand(Execution.class);
		CommandManager.getInstance().registerCommand(News.class);
		CommandManager.getInstance().registerCommand(Rain.class);
		CommandManager.getInstance().registerCommand(Roll.class);
		CommandManager.getInstance().registerCommand(Extinguish.class);
		CommandManager.getInstance().registerCommand(Reload.class);
		CommandManager.getInstance().registerCommand(ReplaceBlock.class);
		CommandManager.getInstance().registerCommand(Undo.class);
		CommandManager.getInstance().registerCommand(ReloadAll.class);
		CommandManager.getInstance().registerCommand(RepeatCmd.class);
		CommandManager.getInstance().registerCommand(Afk.class);
		CommandManager.getInstance().registerCommand(MoreAll.class);
		CommandManager.getInstance().registerCommand(TpToggle.class);
		CommandManager.getInstance().registerCommand(TpAtSee.class);
		CommandManager.getInstance().registerCommand(Uptime.class);
		CommandManager.getInstance().registerCommand(Kit.class);
		CommandManager.getInstance().registerCommand(Version.class);
		CommandManager.getInstance().registerCommand(ListValues.class);
		CommandManager.getInstance().registerCommand(LastLocation.class);
		CommandManager.getInstance().registerCommand(SuperBreaker.class);
		CommandManager.getInstance().registerCommand(Help.class);
		CommandManager.getInstance().registerCommand(Played.class);
		CommandManager.getInstance().registerCommand(BanConvert.class);
		if (CommandManager.getInstance().registerCommand(LockServer.class))
			pm.registerEvents(new ACLockedServerListener(), this);
		CommandManager.getInstance().registerCommand(Set.class);
		CommandManager.getInstance().registerCommand(Rules.class);
		if(CommandManager.getInstance().registerCommand(Eternal.class))
			pm.registerEvents(new ACFoodListener(), this);
		CommandManager.getInstance().registerCommand(FakeQuit.class);
		CommandManager.getInstance().registerCommand(Feed.class);
		CommandManager.getInstance().registerCommand(GameModeSwitch.class);
		CommandManager.getInstance().registerCommand(Whois.class);
		CommandManager.getInstance().registerCommand(ChangeMobSpawner.class);
		CommandManager.getInstance().registerCommand(Reply.class);
		CommandManager.getInstance().registerCommand(WorldDifficulty.class);
		CommandManager.getInstance().registerCommand(Presentation.class);
		CommandManager.getInstance().registerCommand(Experience.class);
	}

	@Override
	protected void registerPermParents() {
		permissionLinker.addPermParent(new PermParent("admincmd.item.*"));
		permissionLinker.addPermParent(new PermParent("admincmd.player.*"));
		permissionLinker.addPermParent(new PermParent("admincmd.mob.*"));
		permissionLinker.addPermParent(new PermParent("admincmd.spawn.*"));
		permissionLinker.addPermParent(new PermParent("admincmd.time.*"));
		permissionLinker.addPermParent(new PermParent("admincmd.tp.*"));
		permissionLinker.addPermParent(new PermParent("admincmd.tp.toggle.*"));
		permissionLinker.addPermParent(new PermParent("admincmd.weather.*"));
		permissionLinker.addPermParent(new PermParent("admincmd.warp.*"));
		permissionLinker.addPermParent(new PermParent("admincmd.invisible.*"));
		permissionLinker.addPermParent(new PermParent("admincmd.server.*"));
		permissionLinker.addPermParent(new PermParent("admincmd.server.exec.*"));
		permissionLinker.addPermParent(new PermParent("admincmd.server.set.*"));
		permissionLinker.addPermParent(new PermParent("admincmd.admin.*"));
		permissionLinker.addPermParent(new PermParent("admincmd.kit.*"));
		permissionLinker.setMajorPerm(new PermParent("admincmd.*"));
		permissionLinker.addPermChild("admincmd.player.bypass");
		permissionLinker.addPermChild("admincmd.item.noblacklist");
		permissionLinker.addPermChild("admincmd.player.noreset");
		permissionLinker.addPermChild("admincmd.spec.notprequest");
		permissionLinker.addPermChild("admincmd.player.noafkkick");
		permissionLinker.addPermChild("admincmd.admin.home");
		permissionLinker.addPermChild("admincmd.immunityLvl.samelvl");
		permissionLinker.addPermChild("admincmd.item.infinity");
		permissionLinker.addPermChild("admincmd.player.fly.allowed");
		for (int i = 0; i < 150; i++) {
			permissionLinker.addPermChild("admincmd.maxHomeByUser." + i, PermissionDefault.FALSE);
			permissionLinker.addPermChild("admincmd.immunityLvl." + i, PermissionDefault.FALSE);
			permissionLinker.addPermChild("admincmd.maxItemAmount." + i, PermissionDefault.FALSE);
		}

	}

	@Override
	protected void setDefaultLocale() {
		Utils.addLocale("playerNotFound", ChatColor.RED + "No such player: " + ChatColor.WHITE
				+ "%player");
		Utils.addLocale("kitNotFound", ChatColor.RED + "No such kit: " + ChatColor.WHITE + "%kit");
		Utils.addLocale("pluginNotFound", ChatColor.RED + "No such Plugin: " + ChatColor.WHITE
				+ "%plugin");
		Utils.addLocale("worldNotFound", ChatColor.RED + "No such world: " + ChatColor.WHITE
				+ "%world");
		Utils.addLocale("unknownMat", ChatColor.RED + "Unknown Material : " + ChatColor.WHITE
				+ "%material");
		Utils.addLocale("onlinePlayers", ChatColor.RED + "Online players: ");
		Utils.addLocale("serverReload", ChatColor.YELLOW + "Server Reloaded.");
		Utils.addLocale(
				"changedWorld",
				ChatColor.DARK_RED
						+ "All your powers have been deactivated because you teleported to an another world");
		Utils.addLocale("stillInv", ChatColor.RED + "You are still Invisible");
		Utils.addLocale("errorNotPerm", ChatColor.RED
				+ "You don't have the Permissions to do that " + ChatColor.BLUE + "(%p)");
		Utils.addLocale("dropItemOtherPlayer", ChatColor.RED + "[%sender]" + ChatColor.WHITE
				+ " dropped at your feet " + ChatColor.GOLD + "%amount %material");
		Utils.addLocale("dropItemCommandSender", ChatColor.RED + "Dropped " + ChatColor.GOLD
				+ "%amount %material to " + ChatColor.WHITE + "%target");
		Utils.addLocale("dropItemYourself", ChatColor.RED + "Dropped " + ChatColor.GOLD
				+ "%amount %material");
		Utils.addLocale("giveItemOtherPlayer", ChatColor.RED + "[%sender]" + ChatColor.WHITE
				+ " send you " + ChatColor.GOLD + "%amount %material");
		Utils.addLocale("giveItemCommandSender", ChatColor.RED + "Added " + ChatColor.GOLD
				+ "%amount %material to " + ChatColor.WHITE + "%target's inventory");
		Utils.addLocale("giveItemYourself", ChatColor.RED + "Added " + ChatColor.GOLD
				+ "%amount %material" + ChatColor.WHITE + " to your inventory");
		Utils.addLocale("errorHolding", ChatColor.RED + "You have to be holding something!");
		Utils.addLocale("moreTooMuch", "Excedent(s) item(s) (" + ChatColor.BLUE + "%amount"
				+ ChatColor.WHITE + ") have been stored in your inventory");
		Utils.addLocale("repair", "Your item " + ChatColor.RED + "%type" + ChatColor.WHITE
				+ " have been successfully repaired.");
		Utils.addLocale("errorRepair", "You can't repair this item : " + ChatColor.RED + "%type");
		Utils.addLocale("repairAll", "All %player's items have been repaired.");
		Utils.addLocale("repairAllTarget", "All your items have been repaired.");
		Utils.addLocale("errorMob", ChatColor.RED + "No such creature: " + ChatColor.WHITE + "%mob");
		Utils.addLocale("spawnMob", ChatColor.BLUE + "Spawned " + ChatColor.WHITE + "%nb %mob");
		Utils.addLocale("spawnMobOther", ChatColor.GOLD + "%player " + ChatColor.BLUE + "spawned "
				+ ChatColor.WHITE + "%nb %mob (s)" + ChatColor.BLUE + " at your location.");
		Utils.addLocale("clear", ChatColor.RED + "Your inventory has been cleared");
		Utils.addLocale("clearTarget", ChatColor.RED + "Inventory of " + ChatColor.WHITE
				+ "%player" + ChatColor.RED + " cleared");
		Utils.addLocale("fireballDisabled", ChatColor.DARK_RED + "Fireball mode disabled.");
		Utils.addLocale("fireballDisabledTarget", ChatColor.DARK_RED
				+ "Fireball mode disabled for %player");
		Utils.addLocale("fireballEnabled", ChatColor.DARK_RED + "Fireball mode enabled.");
		Utils.addLocale("fireballEnabledTarget", ChatColor.DARK_RED
				+ "Fireball mode enabled for %player");
		Utils.addLocale("godDisabled", ChatColor.DARK_AQUA + "GOD mode disabled.");
		Utils.addLocale("godDisabledTarget", ChatColor.DARK_AQUA + "GOD mode disabled for %player");
		Utils.addLocale("godEnabled", ChatColor.DARK_AQUA + "GOD mode enabled.");
		Utils.addLocale("godEnabledTarget", ChatColor.DARK_AQUA + "GOD mode enabled for %player");
		Utils.addLocale("thorDisabled", ChatColor.DARK_AQUA + "THOR mode disabled.");
		Utils.addLocale("thorDisabledTarget", ChatColor.DARK_AQUA
				+ "THOR mode disabled for %player");
		Utils.addLocale("thorEnabled", ChatColor.DARK_AQUA + "THOR mode enabled.");
		Utils.addLocale("thorEnabledTarget", ChatColor.DARK_AQUA + "THOR mode enabled for %player");
		Utils.addLocale("vulcanDisabled", ChatColor.DARK_RED + "VULCAN mode disabled.");
		Utils.addLocale("vulcanDisabledTarget", ChatColor.DARK_RED
				+ "VULCAN mode disabled for %player");
		Utils.addLocale("vulcanEnabled", ChatColor.DARK_RED + "VULCAN mode enabled.");
		Utils.addLocale("vulcanEnabledTarget", ChatColor.DARK_RED
				+ "VULCAN mode enabled for %player");
		Utils.addLocale("spymsgDisabled", ChatColor.DARK_AQUA + "SPYMSG mode disabled.");
		Utils.addLocale("spymsgEnabled", ChatColor.DARK_AQUA + "SPYMSG mode enabled.");
		Utils.addLocale("invisibleEnabled", ChatColor.RED + "You are now Invisible");
		Utils.addLocale("invisibleEnabledTarget", ChatColor.DARK_AQUA
				+ "INVISIBLE mode enabled for %player");
		Utils.addLocale("invisibleDisabled", ChatColor.GREEN + "You are now Visible");
		Utils.addLocale("invisibleDisabledTarget", ChatColor.DARK_AQUA
				+ "INVISIBLE mode disabled for %player");
		Utils.addLocale("errorMultiHome", ChatColor.DARK_GREEN + "Home " + ChatColor.RED + "%home"
				+ ChatColor.WHITE + " not set.");
		Utils.addLocale("multiHome", ChatColor.DARK_GREEN + "Teleported" + ChatColor.WHITE
				+ " to your home " + ChatColor.DARK_AQUA + "%home.");
		Utils.addLocale("setMultiHome", ChatColor.DARK_GREEN + "Home " + ChatColor.DARK_AQUA
				+ "%home" + ChatColor.WHITE + " set.");
		Utils.addLocale("rmHome", ChatColor.RED + "Home " + ChatColor.DARK_AQUA + "%home"
				+ ChatColor.WHITE + " removed.");
		Utils.addLocale("homeLimit", ChatColor.RED + "You have reached your "
				+ ChatColor.DARK_GREEN + "home limit");
		Utils.addLocale("itemLimit", ChatColor.RED + "You have exceeded your "
				+ ChatColor.DARK_GREEN + "item limit" + ChatColor.RED
				+ " of %limit items per command.");
		Utils.addLocale("errorLocation", ChatColor.RED + "Location has to be formed by numbers");
		Utils.addLocale("addWarp", ChatColor.GREEN + "WarpPoint %name" + ChatColor.WHITE
				+ " added.");
		Utils.addLocale("rmWarp", ChatColor.RED + "WarpPoint %name" + ChatColor.WHITE + " removed.");
		Utils.addLocale("errorWarp", ChatColor.DARK_RED + "WarpPoint %name not found");
		Utils.addLocale("tpWarp", ChatColor.GREEN + "Teleported to " + ChatColor.WHITE + "%name");
		Utils.addLocale("strike", "%player was striked by Thor");
		Utils.addLocale("tp", "Successfully teleported " + ChatColor.BLUE + "%fromPlayer"
				+ ChatColor.WHITE + " to " + ChatColor.GREEN + "%toPlayer");
		Utils.addLocale("addBlacklistItem", ChatColor.GREEN + "Item (" + ChatColor.WHITE
				+ "%material" + ChatColor.GREEN
				+ ") added to the Command Black List for i, give and drop.");
		Utils.addLocale("addBlacklistBlock", ChatColor.GREEN + "Block (" + ChatColor.WHITE
				+ "%material" + ChatColor.GREEN + ") added to the BlockPlace Black List.");
		Utils.addLocale("rmBlacklist", ChatColor.GREEN + "Item (" + ChatColor.WHITE + "%material"
				+ ChatColor.GREEN + ") removed from the Black List.");
		Utils.addLocale("inBlacklistItem", ChatColor.DARK_RED + "This item (" + ChatColor.WHITE
				+ "%material" + ChatColor.DARK_RED + ") is black listed.");
		Utils.addLocale("inBlacklistBlock", ChatColor.DARK_RED + "This block (" + ChatColor.WHITE
				+ "%material" + ChatColor.DARK_RED + ") is black listed.");
		Utils.addLocale("errorSpawn", ChatColor.DARK_GREEN + "spawn" + ChatColor.WHITE
				+ " not set for this world.");
		Utils.addLocale("spawn", ChatColor.DARK_GREEN + "Teleported" + ChatColor.WHITE
				+ " to your spawn.");
		Utils.addLocale("setSpawn", ChatColor.DARK_GREEN + "spawn" + ChatColor.WHITE + " set.");
		Utils.addLocale("sClear", "Sky cleared in world :");
		Utils.addLocale("sStorm", "Storm set for %duration mins in world : ");
		Utils.addLocale("sRain", "Rain set for %duration mins in world : ");
		Utils.addLocale("afk", "%player " + ChatColor.RED + "is AFK");
		Utils.addLocale("online", "%player " + ChatColor.GREEN + "is Online");
		Utils.addLocale("afkTitle", ChatColor.BLUE + "[AFK]" + ChatColor.WHITE);
		Utils.addLocale("ip", ChatColor.YELLOW + "IP adress of " + ChatColor.WHITE
				+ "%player - %ip");
		Utils.addLocale("ban", ChatColor.YELLOW + "%player has been banned.");
		Utils.addLocale("unban", ChatColor.YELLOW + "%player is now unbanned.");
		Utils.addLocale("killMob", ChatColor.RED + "Killing mobs (" + ChatColor.WHITE + "%type"
				+ ChatColor.RED + ") of worlds : " + ChatColor.DARK_PURPLE + "%worlds");
		Utils.addLocale("killedMobs", "%nbKilled" + ChatColor.DARK_RED + " mobs have been killed.");
		Utils.addLocale("flyDisabled", ChatColor.GOLD + "FLY mode disabled.");
		Utils.addLocale("flyDisabledTarget", ChatColor.GOLD + "FLY mode disabled for %player");
		Utils.addLocale("flyEnabled", ChatColor.GOLD + "FLY mode enabled.");
		Utils.addLocale("flyEnabledTarget", ChatColor.GOLD + "FLY mode enabled for %player");
		Utils.addLocale("npDisabled", ChatColor.GOLD + "No Pickup mode disabled.");
		Utils.addLocale("npDisabledTarget", ChatColor.GOLD + "No Pickup mode disabled for %player");
		Utils.addLocale("npEnabled", ChatColor.GOLD + "No Pickup mode enabled.");
		Utils.addLocale("npEnabledTarget", ChatColor.GOLD + "No Pickup mode enabled for %player");
		Utils.addLocale("afkKick", "You have been kick because you were AFK");
		Utils.addLocale("freezeDisabled", ChatColor.DARK_GREEN + "You can now move again.");
		Utils.addLocale("freezeDisabledTarget", ChatColor.DARK_GREEN
				+ "Freeze mode disabled for %player");
		Utils.addLocale("freezeEnabled", ChatColor.DARK_RED
				+ "You can't move until you are defrozen.");
		Utils.addLocale("freezeEnabledTarget", ChatColor.DARK_RED
				+ "Freeze mode enabled for %player");
		Utils.addLocale("muteDisabled", ChatColor.DARK_GREEN + "You can chat again.");
		Utils.addLocale("muteDisabledTarget", ChatColor.DARK_GREEN + "%player is unmuted.");
		Utils.addLocale("muteEnabled", ChatColor.DARK_RED + "You can't chat anymore.");
		Utils.addLocale("tmpMuteEnabled", ChatColor.DARK_RED
				+ "You can't chat anymore for %minutes minutes.");
		Utils.addLocale("muteEnabledTarget", ChatColor.DARK_RED + "%player is muted.");
		Utils.addLocale("alreadyMuted", ChatColor.DARK_AQUA
				+ "This player is already muted. To unmute him it's the unmute command.");
		Utils.addLocale("notMuted", ChatColor.DARK_AQUA + "This player is not muted.");
		Utils.addLocale("NaN", "%number " + ChatColor.DARK_RED + "is not a number.");
		Utils.addLocale("mobLimit", ChatColor.GOLD + "Mob limit (%number) set for world : %world");
		Utils.addLocale("mobLimitPerMob", "#mobLimit# " + ChatColor.RED + "for mob %mob");
		LocaleManager.getInstance().save();
		Utils.addLocale("mobLimitRemoved", ChatColor.GREEN
				+ "Mob limit is removed for world : %world");
		Utils.addLocale("mobLimitRemovedPerMob", "#mobLimitRemoved# " + ChatColor.AQUA
				+ " for mob %mob");
		Utils.addLocale("wFrozen", "Weather is frozen in world :");
		Utils.addLocale("wUnFrozen", "Weather can change in world :");
		Utils.addLocale("invTitle", "[INV]");
		Utils.addLocale("roll", ChatColor.DARK_GREEN + "[%player] " + ChatColor.WHITE + "rolled a "
				+ ChatColor.GOLD + "%face dice : " + ChatColor.YELLOW + "%result");
		Utils.addLocale("extinguish", ChatColor.AQUA + "%nb blocks" + ChatColor.DARK_AQUA
				+ " have been extinguished.");
		Utils.addLocale("pluginReloaded", ChatColor.YELLOW + "This plugin has been reloaded : "
				+ ChatColor.WHITE + "%plugin");
		Utils.addLocale("replaced", ChatColor.RED + "%nb blocks of " + ChatColor.DARK_PURPLE
				+ "%mat" + ChatColor.DARK_AQUA + " are now AIR.");
		Utils.addLocale("undo", ChatColor.GREEN + "%nb blocks " + ChatColor.DARK_GREEN
				+ "have been replaced");
		Utils.addLocale("nothingToUndo", ChatColor.DARK_PURPLE + "Nothing to undo.");
		Utils.addLocale("noRepeat", ChatColor.DARK_RED + "No command to repeat.");
		Utils.addLocale("reExec", ChatColor.YELLOW + "Repeating the last command.");
		Utils.addLocale("timeSet", ChatColor.GOLD + "Time set to %type in world : "
				+ ChatColor.WHITE + "%world");
		Utils.addLocale("timeNotSet", ChatColor.RED + "%type doesn't exist.");
		Utils.addLocale("timePaused", ChatColor.DARK_RED + "Time is paused in " + ChatColor.WHITE
				+ "%world. " + ChatColor.DARK_GREEN + "To unpause : /time unpause .");
		Utils.addLocale("moreAll", ChatColor.AQUA
				+ "All your items are now at their max stack size.");
		Utils.addLocale("tpRequestTo", ChatColor.BLUE + "%player " + ChatColor.GOLD
				+ "wants to teleport to you. " + ChatColor.DARK_GREEN + "Type " + ChatColor.GREEN
				+ "/tpt yes " + ChatColor.DARK_GREEN + "to accept.");
		Utils.addLocale("tpRequestSend", ChatColor.DARK_PURPLE + "You send a Teleport request to "
				+ ChatColor.WHITE + "%player" + ChatColor.DARK_PURPLE + " for a teleport "
				+ ChatColor.AQUA + "%tp_type");
		Utils.addLocale("tpRequestFrom", ChatColor.BLUE + "%player " + ChatColor.DARK_AQUA
				+ "wants to teleport you to their location. " + ChatColor.DARK_GREEN + "Type "
				+ ChatColor.GREEN + "/tpt yes " + ChatColor.DARK_GREEN + "to accept.");
		Utils.addLocale("tpRequestOff", ChatColor.DARK_GREEN + "Tp Request system Disabled.");
		Utils.addLocale("tpRequestOn", ChatColor.DARK_RED + "Tp Request system Enabled.");
		Utils.addLocale("tpSeeEnabled", ChatColor.DARK_GREEN + "You Tp at see when you left click.");
		Utils.addLocale("tpSeeDisabled", ChatColor.DARK_RED + "TP AT SEE mode disabled.");
		Utils.addLocale("elapsedTime", "Uptime : " + ChatColor.YELLOW + "%d day(s) %h:%m:%s");
		Utils.addLocale("kitList", ChatColor.GOLD + "Available Kits : " + ChatColor.AQUA + "%list");
		Utils.addLocale("kitOtherPlayer", ChatColor.RED + "[%sender]" + ChatColor.WHITE
				+ " send you the kit : " + ChatColor.GOLD + "%kit");
		Utils.addLocale("kitCommandSender", ChatColor.RED + "Added " + ChatColor.GOLD + "%kit to "
				+ ChatColor.WHITE + "%target's inventory");
		Utils.addLocale("kitYourself", ChatColor.RED + "Added " + ChatColor.GOLD + "%kit"
				+ ChatColor.WHITE + " to your inventory");
		Utils.addLocale("tpRequestTimeOut", ChatColor.RED
				+ "This tp request has timed out and will not be executed.");
		Utils.addLocale("noTpRequest", ChatColor.GREEN + "There is no tp request to execute");
		Utils.addLocale("noteAfk", ChatColor.DARK_RED + "Note: " + ChatColor.WHITE
				+ "%player is AFK at the moment:");
		Utils.addLocale("idleTime", ChatColor.DARK_AQUA + "Idle for %mins minute(s)");
		Utils.addLocale("pluginVersion", ChatColor.YELLOW + "Version of " + ChatColor.WHITE
				+ "%plugin: " + ChatColor.GREEN + "%version");
		Utils.addLocale("emptyList", ChatColor.RED
				+ "Empty list or the selected type don't exists.");
		Utils.addLocale("telportSuccess", ChatColor.DARK_GREEN
				+ "You have been successfully teleported.");
		Utils.addLocale("noLastLocation", ChatColor.RED
				+ "You don't have a last location to tp back");
		Utils.addLocale("super_breakerDisabled", ChatColor.GOLD + "Super Breaker mode disabled.");
		Utils.addLocale("super_breakerDisabledTarget", ChatColor.GOLD
				+ "Super Breaker mode disabled for %player");
		Utils.addLocale("super_breakerEnabled", ChatColor.GOLD + "Super Breaker mode enabled.");
		Utils.addLocale("super_breakerEnabledTarget", ChatColor.GOLD
				+ "Super Breaker mode enabled for %player");
		Utils.addLocale("airForbidden", ChatColor.DARK_RED + "You can't give AIR item.");
		Utils.addLocale("playedTime", ChatColor.DARK_AQUA + "%player " + ChatColor.WHITE
				+ "played " + ChatColor.AQUA + "#elapsedTotalTime#");
		Utils.addLocale("serverUnlock", ChatColor.GREEN + "Server is now UnLocked.");
		Utils.addLocale("serverLock", ChatColor.RED + "Server will be lock in 5 seconds,"
				+ " you'll be kicked if you don't have the Permission to stay.");
		Utils.addLocale("eternalDisabled", ChatColor.DARK_RED + "ETERNAL mode disabled.");
		Utils.addLocale("eternalDisabledTarget", ChatColor.DARK_RED
				+ "ETERNAL mode disabled for %player");
		Utils.addLocale("eternalEnabled", ChatColor.DARK_RED + "ETERNAL mode enabled.");
		Utils.addLocale("eternalEnabledTarget", ChatColor.DARK_RED
				+ "ETERNAL mode enabled for %player");
		Utils.addLocale("fakeQuitDisabled", ChatColor.DARK_AQUA
				+ "FakeQuit mode disabled, you are now listed online again.");
		Utils.addLocale("fakeQuitDisabledTarget", ChatColor.DARK_AQUA
				+ "FakeQuit mode disabled for %player");
		Utils.addLocale("fakeQuitEnabled", ChatColor.DARK_AQUA
				+ "FakeQuit mode enabled, you are now not listed online anymore.");
		Utils.addLocale("fakeQuitEnabledTarget", ChatColor.DARK_AQUA
				+ "FakeQuit mode enabled for %player");
		Utils.addLocale("noLoginInformation", "No login information available");
		Utils.addLocale("insufficientLvl", ChatColor.DARK_RED
				+ "You don't have the sufficient lvl to do that.");
		Utils.addLocale("gmSwitch", ChatColor.GREEN + "GameMode for " + ChatColor.GOLD + "%player "
				+ ChatColor.GREEN + "switched to : " + ChatColor.WHITE + "%gamemode");
		Utils.addLocale("kitDelayNotUp", ChatColor.RED + "You cannot use that kit for another "
				+ ChatColor.WHITE + "%delay");
		Utils.addLocale("days", "%d day(s)");
		Utils.addLocale("elapsedTotalTime", "#days# %h:%m:%s");
		Utils.addLocale("spawnerSetDelay", ChatColor.GREEN + "Delay set to: " + ChatColor.GOLD
				+ "%delay");
		Utils.addLocale("spawnerSetType", ChatColor.GREEN
				+ "CreatureType of the Mob Spawner changed to: " + ChatColor.GOLD + "%type");
		Utils.addLocale("spawnerGetData", ChatColor.DARK_AQUA + "This Mob Spawner spawns "
				+ ChatColor.GOLD + "%mob" + "s" + ChatColor.DARK_AQUA + " with a delay of "
				+ ChatColor.GOLD + "%delay" + ChatColor.DARK_AQUA + ".");
		Utils.addLocale("spawnerNaN", ChatColor.RED + "Your input is not a number!");
		Utils.addLocale("addSpawnWarp", ChatColor.GREEN + "Spawnpoint for the group "
				+ ChatColor.DARK_AQUA + " %name" + ChatColor.GREEN + " added.");
		Utils.addLocale("tpTO", "to them.");
		Utils.addLocale("tpHERE", "to you.");
		Utils.addLocale("tpPLAYERSTO", "of %target to you.");
		Utils.addLocale("tpPLAYERSFROM", "you to %target.");
		Utils.addLocale("offline", "%player " + ChatColor.RED + "is Offline");
		Utils.addLocale("noPlayerToReply", ChatColor.RED
				+ "You can't reply to a message if noone did send you a private message.");
		Utils.addLocale("mustBePlayer", "[AdminCmd] You must be a player to use this command.");
		Utils.addLocale("errorInsufficientArguments",
				"You have to specify a %argument to use this command from the command line.");
		Utils.addLocale("setDifficutly", ChatColor.DARK_AQUA + "The Difficulty of "
				+ ChatColor.GOLD + "%world" + ChatColor.DARK_AQUA + " has been set to: "
				+ ChatColor.GOLD + "%difficulty");
		Utils.addLocale("getDifficulty", ChatColor.DARK_AQUA + "The Difficulty of "
				+ ChatColor.GOLD + "%world" + ChatColor.DARK_AQUA + " is set to: " + ChatColor.GOLD
				+ "%difficulty");
		Utils.addLocale("serverLockMessage", "The server is locked!");
		Utils.addLocale("errorMoved", ChatColor.RED
				+ "You have moved since you issued the %cmdname command, teleportation aborted!");
		Utils.addLocale("privateTitle", ChatColor.RED + "[Private]" + ChatColor.WHITE);
		Utils.addLocale("joinMessage", "%name" + ChatColor.YELLOW + " joined the game!");
		Utils.addLocale("quitMessage", "%name" + ChatColor.YELLOW + " left the game!");
		Utils.addLocale("presSet", ChatColor.YELLOW + "Presentation for" + ChatColor.WHITE
				+ " %player" + ChatColor.YELLOW + " set to : " + ChatColor.GOLD + "%pres");
		Utils.addLocale("expAdded", ChatColor.GOLD + "%amount " + ChatColor.DARK_AQUA
				+ "has been added to your experience.");
		Utils.addLocale("expLevelSet", ChatColor.DARK_AQUA + "Your current level has been set to "
				+ ChatColor.GOLD + "%amount");
		Utils.addLocale("expProgressionSet", ChatColor.DARK_AQUA
				+ "Your current level progression has been set to " + ChatColor.GOLD + "%amount");
		Utils.addLocale("expDropped", ChatColor.DARK_AQUA
				+ "An experience orb has been dropped near your location!");
		Utils.addLocale("expTotal", ChatColor.DARK_AQUA + "Your total experience is: "
				+ ChatColor.GOLD + "%exp");
		Utils.addLocale("expAddedTarget", ChatColor.GREEN + "You have added" + ChatColor.GOLD
				+ " %amount " + ChatColor.GREEN + "to %target" + "s total experience.");
		Utils.addLocale("expLevelSetTarget", ChatColor.GREEN + "%target" + "s level now is:"
				+ ChatColor.GOLD + " %amount");
		Utils.addLocale("expProgressionSetTarget", ChatColor.GREEN + "You have set %target"
				+ "s current " + "progression to " + ChatColor.GOLD + "%amount");
		Utils.addLocale("expDroppedTarget", ChatColor.GREEN + "You have dropped an experience"
				+ " orb at %target" + "s location.");
		Utils.addLocale("expTotalTarget", ChatColor.DARK_AQUA + "%target"
				+ "s total experience is: " + ChatColor.GOLD + "%exp");
		Utils.addLocale("kitOnce", ChatColor.RED + "The kit " + ChatColor.GOLD + "%kit"
				+ ChatColor.RED + " can be only used once.");
		Utils.addLocale("MOTDset", ChatColor.YELLOW + "The new Message Of The Day is : %motd");
		Utils.addLocale("NEWSset", ChatColor.YELLOW + "The News is : %news");
		Utils.addLocale("RulesSet", "The new rules are://n" + "%rules");
		Utils.addLocale("timeOutPower", ChatColor.GOLD + "Time Out of the power %power. "
				+ ChatColor.DARK_RED + "You lost it.");
		LocaleManager.getInstance().save();
	}
}
