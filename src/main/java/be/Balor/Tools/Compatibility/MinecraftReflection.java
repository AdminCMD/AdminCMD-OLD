/*
 *  ProtocolLib - Bukkit server library that allows access to the Minecraft protocol.
 *  Copyright (C) 2012 Kristian S. Stangeland
 *
 *  This program is free software; you can redistribute it and/or modify it under the terms of the 
 *  GNU General Public License as published by the Free Software Foundation; either version 2 of 
 *  the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with this program; 
 *  if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 
 *  02111-1307 USA
 */
package be.Balor.Tools.Compatibility;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.ServerSocket;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;

import be.Balor.Tools.Compatibility.Reflect.Fuzzy.AbstractFuzzyMatcher;
import be.Balor.Tools.Compatibility.Reflect.Fuzzy.FuzzyClassContract;
import be.Balor.Tools.Compatibility.Reflect.Fuzzy.FuzzyFieldContract;
import be.Balor.Tools.Compatibility.Reflect.Fuzzy.FuzzyMatchers;
import be.Balor.Tools.Compatibility.Reflect.Fuzzy.FuzzyMethodContract;
import be.Balor.Tools.Compatibility.Reflect.Fuzzy.FuzzyReflection;

import com.google.common.base.Joiner;

/**
 * Methods and constants specifically used in conjuction with reflecting
 * Minecraft object.
 *
 * @author Kristian
 */
public class MinecraftReflection {

        /**
         * Regular expression that matches a Minecraft object.
         * <p>
         * Replaced by the method {@link #getMinecraftObjectRegex()}.
         */
        @Deprecated
        public static final String MINECRAFT_OBJECT = "net\\.minecraft(\\.\\w+)+";

        /**
         * Regular expression computed dynamically.
         */
        private static String DYNAMIC_PACKAGE_MATCHER = null;

        /**
         * The package name of all the classes that belongs to the native code
         * in Minecraft.
         */
        private static String MINECRAFT_PREFIX_PACKAGE = "net.minecraft.server";

        private static String MINECRAFT_FULL_PACKAGE = null;
        private static String CRAFTBUKKIT_PACKAGE = null;

        private static CachedPackage minecraftPackage;
        private static CachedPackage craftbukkitPackage;

        // org.bukkit.craftbukkit
        private static Constructor<?> craftNMSConstructor;
        private static Constructor<?> craftBukkitConstructor;

        // Matches classes
        private static AbstractFuzzyMatcher<Class<?>> fuzzyMatcher;

        // New in 1.4.5
        private static Method craftNMSMethod;
        private static Method craftBukkitMethod;
        private static boolean craftItemStackFailed;

        // net.minecraft.server
        private static Class<?> itemStackArrayClass;

        /**
         * Whether or not we're currently initializing the reflection handler.
         */
        private static boolean initializing;

        protected MinecraftReflection() {
                // No need to make this constructable.
        }

        /**
         * Retrieve a regular expression that can match Minecraft package
         * objects.
         *
         * @return Minecraft package matcher.
         */
        public static String getMinecraftObjectRegex() {
                if (DYNAMIC_PACKAGE_MATCHER == null) {
                        getMinecraftPackage();
                }
                return DYNAMIC_PACKAGE_MATCHER;
        }

        /**
         * Retrieve a abstract fuzzy class matcher for Minecraft objects.
         *
         * @return A matcher for Minecraft objects.
         */
        public static AbstractFuzzyMatcher<Class<?>> getMinecraftObjectMatcher() {
                if (fuzzyMatcher == null) {
                        fuzzyMatcher = FuzzyMatchers.matchRegex(getMinecraftObjectRegex(), 50);
                }
                return fuzzyMatcher;
        }

        /**
         * Retrieve the name of the Minecraft server package.
         *
         * @return Full canonical name of the Minecraft server package.
         */
        public static String getMinecraftPackage() {
                // Speed things up
                if (MINECRAFT_FULL_PACKAGE != null) {
                        return MINECRAFT_FULL_PACKAGE;
                }
                if (initializing) {
                        throw new IllegalStateException("Already initializing minecraft package!");
                }
                initializing = true;

                final Server craftServer = Bukkit.getServer();

                // This server should have a "getHandle" method that we can use
                if (craftServer != null) {
                        try {
                                // The return type will tell us the full package, regardless of
                                // formating
                                final Class<?> craftClass = craftServer.getClass();
                                CRAFTBUKKIT_PACKAGE = getPackage(craftClass.getCanonicalName());

                                // Libigot patch
                                handleLibigot();

                                // Next, do the same for CraftEntity.getHandle() in order to get
                                // the correct Minecraft package
                                final Class<?> craftEntity = getCraftEntityClass();
                                final Method getHandle = craftEntity.getMethod("getHandle");

                                MINECRAFT_FULL_PACKAGE = getPackage(getHandle.getReturnType().getCanonicalName());

                                // Pretty important invariant
                                if (!MINECRAFT_FULL_PACKAGE.startsWith(MINECRAFT_PREFIX_PACKAGE)) {
                                        // Assume they're the same instead
                                        MINECRAFT_PREFIX_PACKAGE = MINECRAFT_FULL_PACKAGE;

                                        // The package is usualy flat, so go with that assumtion
                                        final String matcher = (MINECRAFT_PREFIX_PACKAGE.length() > 0 ? Pattern.quote(MINECRAFT_PREFIX_PACKAGE + ".") : "") + "\\w+";

                                        // We'll still accept the default location, however
                                        setDynamicPackageMatcher("(" + matcher + ")|(" + MINECRAFT_OBJECT + ")");

                                } else {
                                        // Use the standard matcher
                                        setDynamicPackageMatcher(MINECRAFT_OBJECT);
                                }

                                return MINECRAFT_FULL_PACKAGE;

                        } catch (final SecurityException e) {
                                throw new RuntimeException("Security violation. Cannot get handle method.", e);
                        } catch (final NoSuchMethodException e) {
                                throw new IllegalStateException("Cannot find getHandle() method on server. Is this a modified CraftBukkit version?", e);
                        } finally {
                                initializing = false;
                        }

                } else {
                        initializing = false;
                        throw new IllegalStateException("Could not find Bukkit. Is it running?");
                }
        }

        /**
         * Update the dynamic package matcher.
         *
         * @param regex - the Minecraft package regex.
         */
        private static void setDynamicPackageMatcher(final String regex) {
                DYNAMIC_PACKAGE_MATCHER = regex;

                // Ensure that the matcher is regenerated
                fuzzyMatcher = null;
        }

        // Patch for Libigot
        private static void handleLibigot() {
                try {
                        getCraftEntityClass();
                } catch (final RuntimeException e) {
                        // Try reverting the package to the old format
                        craftbukkitPackage = null;
                        CRAFTBUKKIT_PACKAGE = "org.bukkit.craftbukkit";

                        // This might fail too
                        getCraftEntityClass();
                }
        }

        /**
         * Used during debugging and testing.
         *
         * @param minecraftPackage - the current Minecraft package.
         * @param craftBukkitPackage - the current CraftBukkit package.
         */
        public static void setMinecraftPackage(final String minecraftPackage, final String craftBukkitPackage) {
                MINECRAFT_FULL_PACKAGE = minecraftPackage;
                CRAFTBUKKIT_PACKAGE = craftBukkitPackage;

                // Make sure it exists
                if (getMinecraftServerClass() == null) {
                        throw new IllegalArgumentException("Cannot find MinecraftServer for package " + minecraftPackage);
                }

                // Standard matcher
                setDynamicPackageMatcher(MINECRAFT_OBJECT);
        }

        /**
         * Retrieve the name of the root CraftBukkit package.
         *
         * @return Full canonical name of the root CraftBukkit package.
         */
        public static String getCraftBukkitPackage() {
                // Ensure it has been initialized
                if (CRAFTBUKKIT_PACKAGE == null) {
                        getMinecraftPackage();
                }
                return CRAFTBUKKIT_PACKAGE;
        }

        /**
         * Retrieve the package name from a given canonical Java class name.
         *
         * @param fullName - full Java class name.
         * @return The package name.
         */
        private static String getPackage(final String fullName) {
                final int index = fullName.lastIndexOf(".");

                if (index > 0) {
                        return fullName.substring(0, index);
                } else {
                        return ""; // Default package
                }
        }

        /**
         * Dynamically retrieve the Bukkit entity from a given entity.
         *
         * @param nmsObject - the NMS entity.
         * @return A bukkit entity.
         * @throws RuntimeException If we were unable to retrieve the Bukkit
         * entity.
         */
        public static Object getBukkitEntity(final Object nmsObject) {
                if (nmsObject == null) {
                        return null;
                }

                // We will have to do this dynamically, unfortunately
                try {
                        return nmsObject.getClass().getMethod("getBukkitEntity").invoke(nmsObject);
                } catch (final Exception e) {
                        throw new RuntimeException("Cannot get Bukkit entity from " + nmsObject, e);
                }
        }

        /**
         * Determine if a given object can be found within the package
         * net.minecraft.server.
         *
         * @param obj - the object to test.
         * @return TRUE if it can, FALSE otherwise.
         */
        public static boolean isMinecraftObject(@Nonnull final Object obj) {
                if (obj == null) {
                        throw new IllegalArgumentException("Cannot determine the type of a null object.");
                }

                // Doesn't matter if we don't check for the version here
                return obj.getClass().getName().startsWith(MINECRAFT_PREFIX_PACKAGE);
        }

        /**
         * Determine if the given class is found within the package
         * net.minecraft.server, or any equivalent package.
         *
         * @param clazz - the class to test.
         * @return TRUE if it can, FALSE otherwise.
         */
        public static boolean isMinecraftClass(@Nonnull final Class<?> clazz) {
                if (clazz == null) {
                        throw new IllegalArgumentException("Class cannot be NULL.");
                }

                return getMinecraftObjectMatcher().isMatch(clazz, null);
        }

        /**
         * Determine if a given object is found in net.minecraft.server, and has
         * the given name.
         *
         * @param obj - the object to test.
         * @param className - the class name to test.
         * @return TRUE if it can, FALSE otherwise.
         */
        public static boolean isMinecraftObject(@Nonnull final Object obj, final String className) {
                if (obj == null) {
                        throw new IllegalArgumentException("Cannot determine the type of a null object.");
                }

                final String javaName = obj.getClass().getName();
                return javaName.startsWith(MINECRAFT_PREFIX_PACKAGE) && javaName.endsWith(className);
        }

        /**
         * Determine if a given object is a ChunkPosition.
         *
         * @param obj - the object to test.
         * @return TRUE if it can, FALSE otherwise.
         */
        public static boolean isChunkPosition(final Object obj) {
                return getChunkPositionClass().isAssignableFrom(obj.getClass());
        }

        // /**
        // * Determine if a given object is a ChunkCoordinate.
        // *
        // * @param obj
        // * - the object to test.
        // * @return TRUE if it can, FALSE otherwise.
        // */
        // public static boolean isChunkCoordinates(final Object obj) {
        // return getChunkCoordinatesClass().isAssignableFrom(obj.getClass());
        // }
        /**
         * Determine if the given object is actually a Minecraft packet.
         *
         * @param obj - the given object.
         * @return TRUE if it is, FALSE otherwise.
         */
        public static boolean isPacketClass(final Object obj) {
                return getPacketClass().isAssignableFrom(obj.getClass());
        }

        /**
         * Determine if the given object is a NetLoginHandler
         * (PendingConnection)
         *
         * @param obj - the given object.
         * @return TRUE if it is, FALSE otherwise.
         */
        public static boolean isLoginHandler(final Object obj) {
                return getNetLoginHandlerClass().isAssignableFrom(obj.getClass());
        }

        /**
         * Determine if the given object is assignable to a NetServerHandler
         * (PlayerConnection)
         *
         * @param obj - the given object.
         * @return TRUE if it is, FALSE otherwise.
         */
        public static boolean isServerHandler(final Object obj) {
                return getNetServerHandlerClass().isAssignableFrom(obj.getClass());
        }

        /**
         * Determine if the given object is actually a Minecraft packet.
         *
         * @param obj - the given object.
         * @return TRUE if it is, FALSE otherwise.
         */
        public static boolean isMinecraftEntity(final Object obj) {
                return getEntityClass().isAssignableFrom(obj.getClass());
        }

        /**
         * Determine if the given object is a NMS ItemStack.
         *
         * @param value - the given object.
         * @return TRUE if it is, FALSE otherwise.
         */
        public static boolean isItemStack(final Object value) {
                return getItemStackClass().isAssignableFrom(value.getClass());
        }

        /**
         * Determine if the given object is a CraftPlayer class.
         *
         * @param value - the given object.
         * @return TRUE if it is, FALSE otherwise.
         */
        public static boolean isCraftPlayer(final Object value) {
                return getCraftPlayerClass().isAssignableFrom(value.getClass());
        }

        /**
         * Determine if the given object is a Minecraft player entity.
         *
         * @param obj - the given object.
         * @return TRUE if it is, FALSE otherwise.
         */
        public static boolean isMinecraftPlayer(final Object obj) {
                return getEntityPlayerClass().isAssignableFrom(obj.getClass());
        }

        /**
         * Determine if the given object is a watchable object.
         *
         * @param obj - the given object.
         * @return TRUE if it is, FALSE otherwise.
         */
        public static boolean isWatchableObject(final Object obj) {
                return getWatchableObjectClass().isAssignableFrom(obj.getClass());
        }

        /**
         * Determine if the given object is a data watcher object.
         *
         * @param obj - the given object.
         * @return TRUE if it is, FALSE otherwise.
         */
        public static boolean isDataWatcher(final Object obj) {
                return getDataWatcherClass().isAssignableFrom(obj.getClass());
        }

        /**
         * Determine if the given object is a CraftItemStack instancey.
         *
         * @param obj - the given object.
         * @return TRUE if it is, FALSE otherwise.
         */
        public static boolean isCraftItemStack(final Object obj) {
                return getCraftItemStackClass().isAssignableFrom(obj.getClass());
        }

        /**
         * Retrieve the EntityPlayer (NMS) class.
         *
         * @return The entity class.
         */
        public static Class<?> getEntityPlayerClass() {
                try {
                        return getMinecraftClass("EntityPlayer");
                } catch (final RuntimeException e) {
                        try {
                                // A fairly stable method
                                final Method detect = FuzzyReflection.fromClass(getCraftBukkitClass("CraftServer")).getMethodByName("detectListNameConflict");

                                // EntityPlayer is then the first parameter
                                return detect.getParameterTypes()[0];

                        } catch (final IllegalArgumentException ex) {
                                // Last resort
                                return fallbackMethodReturn("EntityPlayer", "entity.CraftPlayer", "getHandle");
                        }
                }
        }

        /**
         * Retrieve the entity (NMS) class.
         *
         * @return The entity class.
         */
        public static Class<?> getEntityClass() {
                try {
                        return getMinecraftClass("Entity");
                } catch (final RuntimeException e) {
                        return fallbackMethodReturn("Entity", "entity.CraftEntity", "getHandle");
                }
        }

        /**
         * Retrieve the WorldServer (NMS) class.
         *
         * @return The WorldServer class.
         */
        public static Class<?> getWorldServerClass() {
                try {
                        return getMinecraftClass("WorldServer");
                } catch (final RuntimeException e) {
                        return fallbackMethodReturn("WorldServer", "CraftWorld", "getHandle");
                }
        }

        /**
         * Fallback on the return value of a named method in order to get a NMS
         * class.
         *
         * @param nmsClass - the expected name of the Minecraft class.
         * @param craftClass - a CraftBukkit class to look at.
         * @param methodName - the method we will use.
         * @return The return value of this method, which will be saved to the
         * package cache.
         */
        private static Class<?> fallbackMethodReturn(final String nmsClass, final String craftClass, final String methodName) {
                final Class<?> result = FuzzyReflection.fromClass(getCraftBukkitClass(craftClass)).getMethodByName(methodName).getReturnType();

                // Save the result
                return setMinecraftClass(nmsClass, result);
        }

        /**
         * Retrieve the packet class.
         *
         * @return The packet class.
         */
        public static Class<?> getPacketClass() {
                try {
                        return getMinecraftClass("Packet");
                } catch (final RuntimeException e) {
                        // What kind of class we're looking for (sanity check)
                        final FuzzyClassContract paketContract = FuzzyClassContract.newBuilder()
                                .field(FuzzyFieldContract.newBuilder().typeDerivedOf(Map.class).requireModifier(Modifier.STATIC))
                                .field(FuzzyFieldContract.newBuilder().typeDerivedOf(Set.class).requireModifier(Modifier.STATIC))
                                .method(FuzzyMethodContract.newBuilder().parameterSuperOf(DataInputStream.class).returnTypeVoid()).build();

                        // Select a method with one Minecraft object parameter
                        final Method selected = FuzzyReflection.fromClass(getNetHandlerClass()).getMethod(
                                FuzzyMethodContract.newBuilder().parameterMatches(paketContract, 0).parameterCount(1).build());

                        // Save and return
                        final Class<?> clazz = getTopmostClass(selected.getParameterTypes()[0]);
                        return setMinecraftClass("Packet", clazz);
                }
        }

        /**
         * Retrieve the least derived class, except Object.
         *
         * @return Least derived super class.
         */
        private static Class<?> getTopmostClass(Class<?> clazz) {
                while (true) {
                        final Class<?> superClass = clazz.getSuperclass();

                        if (superClass == Object.class || superClass == null) {
                                return clazz;
                        } else {
                                clazz = superClass;
                        }
                }
        }

        /**
         * Retrieve the MinecraftServer class.
         *
         * @return MinecraftServer class.
         */
        public static Class<?> getMinecraftServerClass() {
                try {
                        return getMinecraftClass("MinecraftServer");
                } catch (final RuntimeException e) {
                        useFallbackServer();
                        return getMinecraftClass("MinecraftServer");
                }
        }

        /**
         * Fallback method that can determine the MinecraftServer and the
         * ServerConfigurationManager.
         */
        private static void useFallbackServer() {
                // Get the first constructor that matches CraftServer(MINECRAFT_OBJECT,
                // ANY)
                final Constructor<?> selected = FuzzyReflection.fromClass(getCraftBukkitClass("CraftServer")).getConstructor(
                        FuzzyMethodContract.newBuilder().parameterMatches(getMinecraftObjectMatcher(), 0).parameterCount(2).build());
                final Class<?>[] params = selected.getParameterTypes();

                // Jackpot - two classes at the same time!
                setMinecraftClass("MinecraftServer", params[0]);
                setMinecraftClass("ServerConfigurationManager", params[1]);
        }

        /**
         * Retrieve the player list class (or ServerConfigurationManager),
         *
         * @return The player list class.
         */
        public static Class<?> getPlayerListClass() {
                try {
                        return getMinecraftClass("ServerConfigurationManager", "PlayerList");
                } catch (final RuntimeException e) {
                        // Try again
                        useFallbackServer();
                        return getMinecraftClass("ServerConfigurationManager");
                }
        }

        /**
         * Retrieve the NetLoginHandler class (or PendingConnection)
         *
         * @return The NetLoginHandler class.
         */
        public static Class<?> getNetLoginHandlerClass() {
                try {
                        return getMinecraftClass("NetLoginHandler", "PendingConnection");
                } catch (final RuntimeException e) {
                        final Method selected = FuzzyReflection.fromClass(getPlayerListClass()).getMethod(
                                FuzzyMethodContract.newBuilder().parameterMatches(FuzzyMatchers.matchExact(getEntityPlayerClass()).inverted(), 0)
                                .parameterExactType(String.class, 1).parameterExactType(String.class, 2).build());

                        // Save the pending connection reference
                        return setMinecraftClass("NetLoginHandler", selected.getParameterTypes()[0]);
                }
        }

        /**
         * Retrieve the NetServerHandler class (or PlayerConnection)
         *
         * @return The NetServerHandler class.
         */
        public static Class<?> getNetServerHandlerClass() {
                try {
                        return getMinecraftClass("NetServerHandler", "PlayerConnection");
                } catch (final RuntimeException e) {
                        // Use the player connection field
                        return setMinecraftClass("NetServerHandler",
                                FuzzyReflection.fromClass(getEntityPlayerClass()).getFieldByType("playerConnection", getNetHandlerClass()).getType());
                }
        }

        /**
         * Retrieve the NetworkManager class or its interface.
         *
         * @return The NetworkManager class or its interface.
         */
        public static Class<?> getNetworkManagerClass() {
                try {
                        return getMinecraftClass("INetworkManager", "NetworkManager");
                } catch (final RuntimeException e) {
                        final Constructor<?> selected = FuzzyReflection.fromClass(getNetServerHandlerClass()).getConstructor(
                                FuzzyMethodContract.newBuilder().parameterSuperOf(getMinecraftServerClass(), 0).parameterSuperOf(getEntityPlayerClass(), 2).build());

                        // And we're done
                        return setMinecraftClass("INetworkManager", selected.getParameterTypes()[1]);
                }
        }

        /**
         * Retrieve the NetHandler class (or Connection)
         *
         * @return The NetHandler class.
         */
        public static Class<?> getNetHandlerClass() {
                try {
                        return getMinecraftClass("NetHandler", "Connection");
                } catch (final RuntimeException e) {
                        return setMinecraftClass("NetHandler", getNetLoginHandlerClass().getSuperclass());
                }
        }

        /**
         * Retrieve the NMS ItemStack class.
         *
         * @return The ItemStack class.
         */
        public static Class<?> getItemStackClass() {
                try {
                        return getMinecraftClass("ItemStack");
                } catch (final RuntimeException e) {
                        // Use the handle reference
                        return setMinecraftClass("ItemStack", FuzzyReflection.fromClass(getCraftItemStackClass(), true).getFieldByName("handle").getType());
                }
        }

        /**
         * Retrieve the Block (NMS) class.
         *
         * @return Block (NMS) class.
         */
        public static Class<?> getBlockClass() {
                try {
                        return getMinecraftClass("Block");
                } catch (final RuntimeException e) {
                        final FuzzyReflection reflect = FuzzyReflection.fromClass(getItemStackClass());
                        final Set<Class<?>> candidates = new HashSet<Class<?>>();

                        // Minecraft objects in the constructor
                        for (final Constructor<?> constructor : reflect.getConstructors()) {
                                for (final Class<?> clazz : constructor.getParameterTypes()) {
                                        if (isMinecraftClass(clazz)) {
                                                candidates.add(clazz);
                                        }
                                }
                        }

                        // Useful constructors
                        final Method selected = reflect.getMethod(FuzzyMethodContract.newBuilder().parameterMatches(FuzzyMatchers.matchAnyOf(candidates))
                                .returnTypeExact(float.class).build());
                        return setMinecraftClass("Block", selected.getParameterTypes()[0]);
                }
        }

        /**
         * Retrieve the WorldType class.
         *
         * @return The WorldType class.
         */
        public static Class<?> getWorldTypeClass() {
                try {
                        return getMinecraftClass("WorldType");
                } catch (final RuntimeException e) {
                        // Get the first constructor that matches
                        // CraftServer(MINECRAFT_OBJECT, ANY)
                        final Method selected = FuzzyReflection.fromClass(getMinecraftServerClass(), true).getMethod(
                                FuzzyMethodContract.newBuilder().parameterExactType(String.class, 0).parameterExactType(String.class, 1)
                                .parameterMatches(getMinecraftObjectMatcher()).parameterExactType(String.class, 4).parameterCount(5).build());
                        return setMinecraftClass("WorldType", selected.getParameterTypes()[3]);
                }
        }

        /**
         * Retrieve the DataWatcher class.
         *
         * @return The DataWatcher class.
         */
        public static Class<?> getDataWatcherClass() {
                try {
                        return getMinecraftClass("DataWatcher");
                } catch (final RuntimeException e) {
                        // Describe the DataWatcher
                        final FuzzyClassContract dataWatcherContract = FuzzyClassContract.newBuilder()
                                .field(FuzzyFieldContract.newBuilder().requireModifier(Modifier.STATIC).typeDerivedOf(Map.class))
                                .field(FuzzyFieldContract.newBuilder().banModifier(Modifier.STATIC).typeDerivedOf(Map.class))
                                .method(FuzzyMethodContract.newBuilder().parameterExactType(int.class).parameterExactType(Object.class).returnTypeVoid()).build();
                        final FuzzyFieldContract fieldContract = FuzzyFieldContract.newBuilder().typeMatches(dataWatcherContract).build();

                        // Get such a field and save the result
                        return setMinecraftClass("DataWatcher", FuzzyReflection.fromClass(getEntityClass(), true).getField(fieldContract).getType());
                }
        }

        /**
         * Retrieve the ChunkPosition class.
         *
         * @return The ChunkPosition class.
         */
        public static Class<?> getChunkPositionClass() {
                try {
                        return getMinecraftClass("ChunkPosition");
                } catch (final RuntimeException e) {
                        final Class<?> normalChunkGenerator = getCraftBukkitClass("generator.NormalChunkGenerator");

                        // ChunkPosition a(net.minecraft.server.World world, String string,
                        // int i, int i1, int i2) {
                        final FuzzyMethodContract selected = FuzzyMethodContract.newBuilder().banModifier(Modifier.STATIC).parameterMatches(getMinecraftObjectMatcher(), 0)
                                .parameterExactType(String.class, 1).parameterExactType(int.class, 2).parameterExactType(int.class, 3).parameterExactType(int.class, 4)
                                .build();

                        return setMinecraftClass("ChunkPosition", FuzzyReflection.fromClass(normalChunkGenerator).getMethod(selected).getReturnType());
                }
        }

        // /**
        // * Retrieve the ChunkPosition class.
        // *
        // * @return The ChunkPosition class.
        // */
        // public static Class<?> getChunkCoordinatesClass() {
        // try {
        // return getMinecraftClass("ChunkCoordinates");
        // } catch (final RuntimeException e) {
        // return setMinecraftClass("ChunkCoordinates",
        // WrappedDataWatcher.getTypeClass(6));
        // }
        // }
        /**
         * Retrieve the WatchableObject class.
         *
         * @return The WatchableObject class.
         */
        public static Class<?> getWatchableObjectClass() {
                try {
                        return getMinecraftClass("WatchableObject");
                } catch (final RuntimeException e) {
                        final Method selected = FuzzyReflection.fromClass(getDataWatcherClass(), true).getMethod(
                                FuzzyMethodContract.newBuilder().requireModifier(Modifier.STATIC).parameterDerivedOf(DataOutput.class, 0)
                                .parameterMatches(getMinecraftObjectMatcher(), 1).build());

                        // Use the second parameter
                        return setMinecraftClass("WatchableObject", selected.getParameterTypes()[1]);
                }
        }

        /**
         * Retrieve the ServerConnection abstract class.
         *
         * @return The ServerConnection class.
         */
        public static Class<?> getServerConnectionClass() {
                try {
                        return getMinecraftClass("ServerConnection");
                } catch (final RuntimeException e) {
                        final FuzzyClassContract serverConnectionContract = FuzzyClassContract.newBuilder()
                                .constructor(FuzzyMethodContract.newBuilder().parameterExactType(getMinecraftServerClass()).parameterCount(1))
                                .method(FuzzyMethodContract.newBuilder().parameterExactType(getNetServerHandlerClass())).build();

                        final Method selected = FuzzyReflection.fromClass(getMinecraftServerClass()).getMethod(
                                FuzzyMethodContract.newBuilder().requireModifier(Modifier.ABSTRACT).returnTypeMatches(serverConnectionContract).build());

                        // Use the return type
                        return setMinecraftClass("ServerConnection", selected.getReturnType());
                }
        }

        /**
         * Retrieve the NBT base class.
         *
         * @return The NBT base class.
         */
        public static Class<?> getNBTBaseClass() {
                try {
                        return getMinecraftClass("NBTBase");
                } catch (final RuntimeException e) {
                        final FuzzyClassContract tagCompoundContract = FuzzyClassContract.newBuilder()
                                .constructor(FuzzyMethodContract.newBuilder().parameterExactType(String.class).parameterCount(1))
                                .field(FuzzyFieldContract.newBuilder().typeDerivedOf(Map.class)).build();

                        final Method selected = FuzzyReflection.fromClass(MinecraftReflection.getPacketClass()).getMethod(
                                FuzzyMethodContract.newBuilder().requireModifier(Modifier.STATIC).parameterSuperOf(DataInputStream.class).parameterCount(1)
                                .returnTypeMatches(tagCompoundContract).build());
                        final Class<?> nbtBase = selected.getReturnType().getSuperclass();

                        // That can't be correct
                        if (nbtBase == null || nbtBase.equals(Object.class)) {
                                throw new IllegalStateException("Unable to find NBT base class: " + nbtBase);
                        }

                        // Use the return type here too
                        return setMinecraftClass("NBTBase", nbtBase);
                }
        }

        // /**
        // * Retrieve the NBT Compound class.
        // *
        // * @return The NBT Compond class.
        // */
        // public static Class<?> getNBTCompoundClass() {
        // try {
        // return getMinecraftClass("NBTTagCompound");
        // } catch (final RuntimeException e) {
        // return setMinecraftClass("NBTTagCompound",
        // NbtFactory.ofWrapper(NbtType.TAG_COMPOUND,
        // "Test").getHandle().getClass());
        // }
        // }
        /**
         * Retrieve the EntityTracker (NMS) class.
         *
         * @return EntityTracker class.
         */
        public static Class<?> getEntityTrackerClass() {
                try {
                        return getMinecraftClass("EntityTracker");
                } catch (final RuntimeException e) {
                        final FuzzyClassContract entityTrackerContract = FuzzyClassContract
                                .newBuilder()
                                .field(FuzzyFieldContract.newBuilder().typeDerivedOf(Set.class))
                                .method(FuzzyMethodContract.newBuilder().parameterSuperOf(MinecraftReflection.getEntityClass()).parameterCount(1).returnTypeVoid())
                                .method(FuzzyMethodContract.newBuilder().parameterSuperOf(MinecraftReflection.getEntityClass(), 0).parameterSuperOf(int.class, 1)
                                        .parameterSuperOf(int.class, 2).parameterCount(3).returnTypeVoid()).build();

                        final Field selected = FuzzyReflection.fromClass(MinecraftReflection.getWorldServerClass(), true).getField(
                                FuzzyFieldContract.newBuilder().typeMatches(entityTrackerContract).build());

                        // Go by the defined type of this field
                        return setMinecraftClass("EntityTracker", selected.getType());
                }
        }

        /**
         * Retrieve the NetworkListenThread class (NMS).
         * <p>
         * Note that this class was removed after Minecraft 1.3.1.
         *
         * @return NetworkListenThread class.
         */
        public static Class<?> getNetworkListenThreadClass() {
                try {
                        return getMinecraftClass("NetworkListenThread");
                } catch (final RuntimeException e) {
                        final FuzzyClassContract networkListenContract = FuzzyClassContract.newBuilder()
                                .field(FuzzyFieldContract.newBuilder().typeDerivedOf(ServerSocket.class))
                                .field(FuzzyFieldContract.newBuilder().typeDerivedOf(Thread.class)).field(FuzzyFieldContract.newBuilder().typeDerivedOf(List.class))
                                .method(FuzzyMethodContract.newBuilder().parameterExactType(getNetServerHandlerClass())).build();

                        final Field selected = FuzzyReflection.fromClass(MinecraftReflection.getMinecraftServerClass(), true).getField(
                                FuzzyFieldContract.newBuilder().typeMatches(networkListenContract).build());

                        // Go by the defined type of this field
                        return setMinecraftClass("NetworkListenThread", selected.getType());
                }
        }

        //
        // /**
        // * Retrieve the attribute snapshot class.
        // * <p>
        // * This stores the final value of an attribute, along with all the
        // * associated computational steps.
        // *
        // * @return The attribute snapshot class.
        // */
        // public static Class<?> getAttributeSnapshotClass() {
        // try {
        // return getMinecraftClass("AttributeSnapshot");
        // } catch (final RuntimeException e) {
        // final Class<?> packetUpdateAttributes =
        // PacketRegistry.getPacketClassFromID(44, true);
        // final String packetSignature =
        // packetUpdateAttributes.getCanonicalName().replace('.', '/');
        //
        // // HACK - class is found by inspecting code
        // try {
        // final ClassReader reader = new
        // ClassReader(packetUpdateAttributes.getCanonicalName());
        //
        // reader.accept(new EmptyClassVisitor() {
        // @Override
        // public MethodVisitor visitMethod(final int access, final String name,
        // final String desc, final String signature, final String[] exceptions) {
        // // The read method
        // if (desc.startsWith("(Ljava/io/DataInput")) {
        // return new EmptyMethodVisitor() {
        // public void visitMethodInsn(final int opcode, final String owner, final
        // String name, final String desc) {
        // if (opcode == Opcodes.INVOKESPECIAL && isConstructor(name)) {
        // final String className = owner.replace('/', '.');
        //
        // // Use signature to distinguish between
        // // constructors
        // if (desc.startsWith("(L" + packetSignature)) {
        // setMinecraftClass("AttributeSnapshot",
        // MinecraftReflection.getClass(className));
        // } else if (desc.startsWith("(Ljava/util/UUID;Ljava/lang/String")) {
        // setMinecraftClass("AttributeModifier",
        // MinecraftReflection.getClass(className));
        // }
        // }
        // };
        // };
        // }
        // return null;
        // }
        // }, 0);
        //
        // } catch (final IOException e1) {
        // throw new
        // RuntimeException("Unable to read the content of Packet44UpdateAttributes.",
        // e1);
        // }
        //
        // // If our dirty ASM trick failed, this will throw an exception
        // return getMinecraftClass("AttributeSnapshot");
        // }
        // }
        // /**
        // * Retrieve the attribute modifier class.
        // *
        // * @return Attribute modifier class.
        // */
        // public static Class<?> getAttributeModifierClass() {
        // try {
        // return getMinecraftClass("AttributeModifier");
        // } catch (final RuntimeException e) {
        // // Initialize first
        // getAttributeSnapshotClass();
        // return getMinecraftClass("AttributeModifier");
        // }
        // }
        /**
         * Determine if a given method retrieved by ASM is a constructor.
         *
         * @param name - the name of the method.
         * @return TRUE if it is, FALSE otherwise.
         */
        protected static boolean isConstructor(final String name) {
                return "<init>".equals(name);
        }

        /**
         * Retrieve the ItemStack[] class.
         *
         * @return The ItemStack[] class.
         */
        public static Class<?> getItemStackArrayClass() {
                if (itemStackArrayClass == null) {
                        itemStackArrayClass = getArrayClass(getItemStackClass());
                }
                return itemStackArrayClass;
        }

        /**
         * Retrieve the array class of a given component type.
         *
         * @param componentType - type of each element in the array.
         * @return The class of the array.
         */
        public static Class<?> getArrayClass(final Class<?> componentType) {
                // Bit of a hack, but it works
                return Array.newInstance(componentType, 0).getClass();
        }

        /**
         * Retrieve the CraftItemStack class.
         *
         * @return The CraftItemStack class.
         */
        public static Class<?> getCraftItemStackClass() {
                return getCraftBukkitClass("inventory.CraftItemStack");
        }

        /**
         * Retrieve the CraftPlayer class.
         *
         * @return CraftPlayer class.
         */
        public static Class<?> getCraftPlayerClass() {
                return getCraftBukkitClass("entity.CraftPlayer");
        }

        /**
         * Retrieve the CraftEntity class.
         *
         * @return CraftEntity class.
         */
        public static Class<?> getCraftEntityClass() {
                return getCraftBukkitClass("entity.CraftEntity");
        }

        /**
         * Retrieve a CraftItemStack from a given ItemStack.
         *
         * @param bukkitItemStack - the Bukkit ItemStack to convert.
         * @return A CraftItemStack as an ItemStack.
         */
        public static ItemStack getBukkitItemStack(final ItemStack bukkitItemStack) {
                // Delegate this task to the method that can execute it
                if (craftBukkitMethod != null) {
                        return getBukkitItemByMethod(bukkitItemStack);
                }

                if (craftBukkitConstructor == null) {
                        try {
                                craftBukkitConstructor = getCraftItemStackClass().getConstructor(ItemStack.class);
                        } catch (final Exception e) {
                                // See if this method works
                                if (!craftItemStackFailed) {
                                        return getBukkitItemByMethod(bukkitItemStack);
                                }

                                throw new RuntimeException("Cannot find CraftItemStack(org.bukkit.inventory.ItemStack).", e);
                        }
                }

                // Try to create the CraftItemStack
                try {
                        return (ItemStack) craftBukkitConstructor.newInstance(bukkitItemStack);
                } catch (final Exception e) {
                        throw new RuntimeException("Cannot construct CraftItemStack.", e);
                }
        }

        private static ItemStack getBukkitItemByMethod(final ItemStack bukkitItemStack) {
                if (craftBukkitMethod == null) {
                        try {
                                craftBukkitMethod = getCraftItemStackClass().getMethod("asCraftCopy", ItemStack.class);
                        } catch (final Exception e) {
                                craftItemStackFailed = true;
                                throw new RuntimeException("Cannot find CraftItemStack.asCraftCopy(org.bukkit.inventory.ItemStack).", e);
                        }
                }

                // Next, construct it
                try {
                        return (ItemStack) craftBukkitMethod.invoke(null, bukkitItemStack);
                } catch (final Exception e) {
                        throw new RuntimeException("Cannot construct CraftItemStack.", e);
                }
        }

        /**
         * Retrieve the Bukkit ItemStack from a given net.minecraft.server
         * ItemStack.
         *
         * @param minecraftItemStack - the NMS ItemStack to wrap.
         * @return The wrapped ItemStack.
         */
        public static ItemStack getBukkitItemStack(final Object minecraftItemStack) {
                // Delegate this task to the method that can execute it
                if (craftNMSMethod != null) {
                        return getBukkitItemByMethod(minecraftItemStack);
                }

                if (craftNMSConstructor == null) {
                        try {
                                craftNMSConstructor = getCraftItemStackClass().getConstructor(minecraftItemStack.getClass());
                        } catch (final Exception e) {
                                // Give it a try
                                if (!craftItemStackFailed) {
                                        return getBukkitItemByMethod(minecraftItemStack);
                                }

                                throw new RuntimeException("Cannot find CraftItemStack(net.mineraft.server.ItemStack).", e);
                        }
                }

                // Try to create the CraftItemStack
                try {
                        return (ItemStack) craftNMSConstructor.newInstance(minecraftItemStack);
                } catch (final Exception e) {
                        throw new RuntimeException("Cannot construct CraftItemStack.", e);
                }
        }

        private static ItemStack getBukkitItemByMethod(final Object minecraftItemStack) {
                if (craftNMSMethod == null) {
                        try {
                                craftNMSMethod = getCraftItemStackClass().getMethod("asCraftMirror", minecraftItemStack.getClass());
                        } catch (final Exception e) {
                                craftItemStackFailed = true;
                                throw new RuntimeException("Cannot find CraftItemStack.asCraftMirror(net.mineraft.server.ItemStack).", e);
                        }
                }

                // Next, construct it
                try {
                        return (ItemStack) craftNMSMethod.invoke(null, minecraftItemStack);
                } catch (final Exception e) {
                        throw new RuntimeException("Cannot construct CraftItemStack.", e);
                }
        }

        // /**
        // * Retrieve the net.minecraft.server ItemStack from a Bukkit ItemStack.
        // * <p>
        // * By convention, item stacks that contain air are usually represented as
        // * NULL.
        // *
        // * @param stack
        // * - the Bukkit ItemStack to convert.
        // * @return The NMS ItemStack, or NULL if the stack represents air.
        // */
        // public static Object getMinecraftItemStack(ItemStack stack) {
        // // Make sure this is a CraftItemStack
        // if (!isCraftItemStack(stack)) {
        // stack = getBukkitItemStack(stack);
        // }
        //
        // final BukkitUnwrapper unwrapper = new BukkitUnwrapper();
        // return unwrapper.unwrapItem(stack);
        // }
        /**
         * Retrieve the given class by name.
         *
         * @param className - name of the class.
         * @return The class.
         */
        @SuppressWarnings("rawtypes")
        protected static Class getClass(final String className) {
                try {
                        return MinecraftReflection.class.getClassLoader().loadClass(className);
                } catch (final ClassNotFoundException e) {
                        throw new RuntimeException("Cannot find class " + className, e);
                }
        }

        /**
         * Retrieve the class object of a specific CraftBukkit class.
         *
         * @param className - the specific CraftBukkit class.
         * @return Class object.
         * @throws RuntimeException If we are unable to find the given class.
         */
        @SuppressWarnings("rawtypes")
        public static Class getCraftBukkitClass(final String className) {
                if (craftbukkitPackage == null) {
                        craftbukkitPackage = new CachedPackage(getCraftBukkitPackage());
                }
                return craftbukkitPackage.getPackageClass(className);
        }

        /**
         * Retrieve the class object of a specific Minecraft class.
         *
         * @param className - the specific Minecraft class.
         * @return Class object.
         * @throws RuntimeException If we are unable to find the given class.
         */
        public static Class<?> getMinecraftClass(final String className) {
                if (minecraftPackage == null) {
                        minecraftPackage = new CachedPackage(getMinecraftPackage());
                }
                return minecraftPackage.getPackageClass(className);
        }

        /**
         * Set the class object for the specific Minecraft class.
         *
         * @param className - name of the Minecraft class.
         * @param clazz - the new class object.
         * @return The provided clazz object.
         */
        protected static Class<?> setMinecraftClass(final String className, final Class<?> clazz) {
                if (minecraftPackage == null) {
                        minecraftPackage = new CachedPackage(getMinecraftPackage());
                }
                minecraftPackage.setPackageClass(className, clazz);
                return clazz;
        }

        /**
         * Retrieve the first class that matches a specified Minecraft name.
         *
         * @param className - the specific Minecraft class.
         * @param aliases - alternative names for this Minecraft class.
         * @return Class object.
         * @throws RuntimeException If we are unable to find any of the given
         * classes.
         */
        public static Class<?> getMinecraftClass(final String className, final String... aliases) {
                try {
                        // Try the main class first
                        return getMinecraftClass(className);
                } catch (final RuntimeException e1) {
                        Class<?> success = null;

                        // Try every alias too
                        for (final String alias : aliases) {
                                try {
                                        success = getMinecraftClass(alias);
                                        break;
                                } catch (final RuntimeException e2) {
                                        // Swallov
                                }
                        }

                        if (success != null) {
                                // Save it for later
                                minecraftPackage.setPackageClass(className, success);
                                return success;
                        } else {
                                // Hack failed
                                throw new RuntimeException(String.format("Unable to find %s (%s)", className, Joiner.on(", ").join(aliases)));
                        }
                }
        }

        /**
         * Dynamically retrieve the NetworkManager name.
         *
         * @return Name of the NetworkManager class.
         */
        public static String getNetworkManagerName() {
                return getNetworkManagerClass().getSimpleName();
        }

        /**
         * Dynamically retrieve the name of the current NetLoginHandler.
         *
         * @return Name of the NetLoginHandler class.
         */
        public static String getNetLoginHandlerName() {
                return getNetLoginHandlerClass().getSimpleName();
        }

}
