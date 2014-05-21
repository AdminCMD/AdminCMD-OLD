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

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * Represents a dynamic package and an arbitrary number of cached classes.
 *
 * @author Kristian
 */
class CachedPackage {

        private final Map<String, Class<?>> cache;
        private final String packageName;

        public CachedPackage(final String packageName) {
                this.packageName = packageName;
                this.cache = Maps.newConcurrentMap();
        }

        /**
         * Associate a given class with a class name.
         *
         * @param className - class name.
         * @param clazz - type of class.
         */
        public void setPackageClass(final String className, final Class<?> clazz) {
                cache.put(className, clazz);
        }

        /**
         * Retrieve the class object of a specific class in the current package.
         *
         * @param className - the specific class.
         * @return Class object.
         * @throws RuntimeException If we are unable to find the given class.
         */
        public Class<?> getPackageClass(final String className) {
                try {
                        Class<?> result = cache.get(className);

                        // Concurrency is not a problem - we don't care if we look up a
                        // class twice
                        if (result == null) {
                                // Look up the class dynamically
                                result = CachedPackage.class.getClassLoader().loadClass(combine(packageName, className));
                                cache.put(className, result);
                        }

                        return result;

                } catch (final ClassNotFoundException e) {
                        throw new RuntimeException("Cannot find class " + className, e);
                }
        }

        /**
         * Correctly combine a package name and the child class we're looking
         * for.
         *
         * @param packageName - name of the package, or an empty string for the
         * default package.
         * @param className - the class name.
         * @return We full class path.
         */
        private String combine(final String packageName, final String className) {
                if (packageName.length() == 0) {
                        return className;
                }
                return packageName + "." + className;
        }
}
