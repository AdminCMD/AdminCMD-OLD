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
package be.Balor.Tools.Exceptions;

/**
 * @author Balor (aka Antoine Aflalo)
 *
 */
public class InvalidInputException extends Exception {

        /**
         *
         */
        private static final long serialVersionUID = -2704740483298449995L;

        /**
         *
         */
        public InvalidInputException() {
        }

        /**
         * @param message
         */
        public InvalidInputException(final String message) {
                super(message);
        }

        /**
         * @param cause
         */
        public InvalidInputException(final Throwable cause) {
                super(cause);
        }

        /**
         * @param message
         * @param cause
         */
        public InvalidInputException(final String message, final Throwable cause) {
                super(message, cause);
        }

}
