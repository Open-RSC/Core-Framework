/**
 * rscminus
 *
 * This file was part of rscminus.
 *
 * rscminus is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * rscminus is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with rscminus. If not,
 * see <http://www.gnu.org/licenses/>.
 *
 * Authors: see <https://github.com/OrN/rscminus>
 */
package com.openrsc.server.util.rsc;

public class CipheredMessage {
	/**
	 * Holds a specific players' chat message until it is cleared
	 * in a player update type 1
	 * Max length is the authentic client's network buffer size - 3
	 * -3 is to account for 1 byte opcode, 2 byte message size
	 */
	public byte[] messageBuffer = new byte[5000 - 3];

	/**
	 * Length of the raw message
	 */
	public int decipheredLength;

	/**
	 * Length of the enciphered message
	 */
	public int encipheredLength;
}
