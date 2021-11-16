/**
 * PacketWrapper - ProtocolLib wrappers for Minecraft packets
 * Copyright (C) dmulloy2 <http://dmulloy2.net>
 * Copyright (C) Kristian S. Strangeland
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ru.armagidon.poseplugin.bukkit.wrappers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.FieldAccessException;

import java.util.Arrays;

public class WrapperPlayServerEntityDestroy extends AbstractPacket {
	public static final PacketType TYPE = PacketType.Play.Server.ENTITY_DESTROY;

	public WrapperPlayServerEntityDestroy() {
		super(new PacketContainer(TYPE), TYPE);
		handle.getModifier().writeDefaults();
	}

	public WrapperPlayServerEntityDestroy(PacketContainer packet) {
		super(packet, TYPE);
	}

	/**
	 * Retrieve Count.
	 * <p>
	 * Notes: length of following array
	 * 
	 * @return The current Count
	 */
	public int getCount() {
		return handle.getIntegerArrays().read(0).length;
	}

	/**
	 * Retrieve Entity IDs.
	 * <p>
	 * Notes: the list of entities of destroy
	 * 
	 * @return The current Entity IDs
	 */
	public int[] getEntityIDs() {
		return handle.getIntegerArrays().read(0);
	}

	/**
	 * Set Entity IDs.
	 * 
	 * @param value - new value.
	 */
	public WrapperPlayServerEntityDestroy setEntityIds(int... value) {
		try {
			handle.getIntegerArrays().write(0, value);
		} catch (FieldAccessException notArrayExcept) {
			//If this thing happened, probably it needs to use one id
			try {
				handle.getIntegers().write(0, value[0]);
			} catch (FieldAccessException notIntExcept) {
				//Well, if this happens, Mojang's probably hired new JS devs for Java, and then we need to use IntList
				handle.getIntLists().write(0, Arrays.stream(value).boxed().toList());
			}
		}
		return this;
	}

}
