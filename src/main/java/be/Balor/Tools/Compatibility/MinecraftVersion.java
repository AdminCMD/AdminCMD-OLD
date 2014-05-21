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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Server;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;

/**
 * Determine the current Minecraft version.
 *
 * @author Kristian
 */
public class MinecraftVersion implements Comparable<MinecraftVersion> {

        /**
         * Regular expression used to parse version strings.
         */
        private static final String VERSION_PATTERN = ".*\\(MC:\\s*((?:\\d+\\.)*\\d)\\s*\\)";

        private final int major;
        private final int minor;
        private final int build;

        // The development stage
        private final String development;

        /**
         * Determine the current Minecraft version.
         *
         * @param server - the Bukkit server that will be used to examine the MC
         * version.
         */
        public MinecraftVersion(final Server server) {
                this(extractVersion(server.getVersion()));
        }

        /**
         * Construct a version object from the format major.minor.build.
         *
         * @param versionOnly - the version in text form.
         */
        public MinecraftVersion(final String versionOnly) {
                final String[] section = versionOnly.split("-");
                final int[] numbers = parseVersion(section[0]);

                this.major = numbers[0];
                this.minor = numbers[1];
                this.build = numbers[2];
                this.development = section.length > 1 ? section[1] : null;
        }

        /**
         * Construct a version object directly.
         *
         * @param major - major version number.
         * @param minor - minor version number.
         * @param build - build version number.
         */
        public MinecraftVersion(final int major, final int minor, final int build) {
                this(major, minor, build, null);
        }

        /**
         * Construct a version object directly.
         *
         * @param major - major version number.
         * @param minor - minor version number.
         * @param build - build version number.
         * @param development - development stage.
         */
        public MinecraftVersion(final int major, final int minor, final int build, final String development) {
                this.major = major;
                this.minor = minor;
                this.build = build;
                this.development = development;
        }

        private int[] parseVersion(final String version) {
                final String[] elements = version.split("\\.");
                final int[] numbers = new int[3];

                // Make sure it's even a valid version
                if (elements.length < 1) {
                        throw new IllegalStateException("Corrupt MC version: " + version);
                }

                // The String 1 or 1.2 is interpreted as 1.0.0 and 1.2.0 respectively.
                for (int i = 0; i < Math.min(numbers.length, elements.length); i++) {
                        numbers[i] = Integer.parseInt(elements[i].trim());
                }
                return numbers;
        }

        /**
         * Major version number
         *
         * @return Current major version number.
         */
        public int getMajor() {
                return major;
        }

        /**
         * Minor version number
         *
         * @return Current minor version number.
         */
        public int getMinor() {
                return minor;
        }

        /**
         * Build version number
         *
         * @return Current build version number.
         */
        public int getBuild() {
                return build;
        }

        /**
         * Retrieve the development stage.
         *
         * @return Development stage, or NULL if this is a release.
         */
        public String getDevelopmentStage() {
                return development;
        }

        /**
         * Retrieve the version String (major.minor.build) only.
         *
         * @return A normal version string.
         */
        public String getVersion() {
                if (getDevelopmentStage() == null) {
                        return String.format("%s.%s.%s", getMajor(), getMinor(), getBuild());
                } else {
                        return String.format("%s.%s.%s-%s", getMajor(), getMinor(), getBuild(), getDevelopmentStage());
                }
        }

        @Override
        public int compareTo(final MinecraftVersion o) {
                if (o == null) {
                        return 1;
                }

                return ComparisonChain.start().compare(getMajor(), o.getMajor()).compare(getMinor(), o.getMinor()).compare(getBuild(), o.getBuild()).
                        // No development String means it's a release
                        compare(getDevelopmentStage(), o.getDevelopmentStage(), Ordering.natural().nullsLast()).result();
        }

        @Override
        public boolean equals(final Object obj) {
                if (obj == null) {
                        return false;
                }
                if (obj == this) {
                        return true;
                }

                if (obj instanceof MinecraftVersion) {
                        final MinecraftVersion other = (MinecraftVersion) obj;

                        return getMajor() == other.getMajor() && getMinor() == other.getMinor() && getBuild() == other.getBuild()
                                && Objects.equal(getDevelopmentStage(), other.getDevelopmentStage());
                }

                return false;
        }

        @Override
        public int hashCode() {
                return Objects.hashCode(getMajor(), getMinor(), getBuild());
        }

        @Override
        public String toString() {
                // Convert to a String that we can parse back again
                return String.format("(MC: %s)", getVersion());
        }

        /**
         * Extract the Minecraft version from CraftBukkit itself.
         *
         * @param server - the server object representing CraftBukkit.
         * @return The underlying MC version.
         * @throws IllegalStateException If we could not parse the version
         * string.
         */
        public static String extractVersion(final String text) {
                final Pattern versionPattern = Pattern.compile(VERSION_PATTERN);
                final Matcher version = versionPattern.matcher(text);

                if (version.matches() && version.group(1) != null) {
                        return version.group(1);
                } else {
                        throw new IllegalStateException("Cannot parse version String '" + text + "'");
                }
        }
}
