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

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.AbstractStructure;
import com.comphenix.protocol.events.InternalStructure;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.FieldAccessException;
import com.comphenix.protocol.reflect.IntEnum;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

import org.bukkit.ChatColor;

public class WrapperPlayServerScoreboardTeam extends AbstractPacket {
	public static final PacketType TYPE =
			PacketType.Play.Server.SCOREBOARD_TEAM;

	public WrapperPlayServerScoreboardTeam() {
		super(new PacketContainer(TYPE), TYPE);
		handle.getModifier().writeDefaults();
	}

	public WrapperPlayServerScoreboardTeam(PacketContainer packet) {
		super(packet, TYPE);
	}

	/**
	 * Enum containing all known modes.
	 * 
	 * @author dmulloy2
	 */
	public static class Mode extends IntEnum {
		public static final int TEAM_CREATED = 0;
		public static final int TEAM_REMOVED = 1;
		public static final int TEAM_UPDATED = 2;
		public static final int PLAYERS_ADDED = 3;
		public static final int PLAYERS_REMOVED = 4;

		private static final Mode INSTANCE = new Mode();

		public static Mode getInstance() {
			return INSTANCE;
		}
	}

	/**
	 * Retrieve Team Name.
	 * <p>
	 * Notes: a unique name for the team. (Shared with scoreboard).
	 * 
	 * @return The current Team Name
	 */
	public String getName() {
		return handle.getStrings().read(0);
	}

	/**
	 * Set Team Name.
	 * 
	 * @param value - new value.
	 */
	public void setName(String value) {
		handle.getStrings().write(0, value);
	}

	/**
	 * Retrieve Team Display Name.
	 * <p>
	 * Notes: only if Mode = 0 or 2.
	 * 
	 * @return The current Team Display Name
	 */
	public WrappedChatComponent getDisplayName() {
		AbstractStructure s = getModifier(handle);

		if (s instanceof PacketContainer) {
			return s.getChatComponents().readSafely(0);
		} else if (s instanceof InternalStructure){
			return s.getChatComponents().readSafely(0);
		}
		return handle.getChatComponents().read(0);
	}

	/**
	 * Set Team Display Name.
	 * 
	 * @param value - new value.
	 */
	public void setDisplayName(WrappedChatComponent value) {
		AbstractStructure s = getModifier(handle);

		if (s instanceof PacketContainer) {
			s.getChatComponents().writeSafely(0, value);
		} else if (s instanceof InternalStructure){
			s.getChatComponents().writeSafely(0, value);
		}
	}

	/**
	 * Retrieve Team Prefix.
	 * <p>
	 * Notes: only if Mode = 0 or 2. Displayed before the players' name that are
	 * part of this team.
	 * 
	 * @return The current Team Prefix
	 */
	public WrappedChatComponent getPrefix() {
		AbstractStructure s = getModifier(handle);

		if (s instanceof PacketContainer) {
			return s.getChatComponents().readSafely(1);
		} else if (s instanceof InternalStructure){
			return s.getChatComponents().readSafely(1);
		}
		return handle.getChatComponents().read(1);
	}

	/**
	 * Set Team Prefix.
	 * 
	 * @param value - new value.
	 */
	public void setPrefix(WrappedChatComponent value) {
		AbstractStructure s = getModifier(handle);

		if (s instanceof PacketContainer) {
			s.getChatComponents().writeSafely(1, value);
		} else if (s instanceof InternalStructure){
			s.getChatComponents().writeSafely(1, value);
		}
	}

	/**
	 * Retrieve Team Suffix.
	 * <p>
	 * Notes: only if Mode = 0 or 2. Displayed after the players' name that are
	 * part of this team.
	 * 
	 * @return The current Team Suffix
	 */
	public WrappedChatComponent getSuffix() {
		AbstractStructure s = getModifier(handle);

		if (s instanceof PacketContainer) {
			return s.getChatComponents().readSafely(2);
		} else if (s instanceof InternalStructure){
			return s.getChatComponents().readSafely(2);
		}
		return handle.getChatComponents().read(2);
	}

	/**
	 * Set Team Suffix.
	 * 
	 * @param value - new value.
	 */
	public void setSuffix(WrappedChatComponent value) {
		AbstractStructure s = getModifier(handle);

		if (s instanceof PacketContainer) {
			s.getChatComponents().writeSafely(2, value);
		} else if (s instanceof InternalStructure){
			s.getChatComponents().writeSafely(2, value);
		}
	}

	/**
	 * Retrieve Name Tag Visibility.
	 * <p>
	 * Notes: only if Mode = 0 or 2. always, hideForOtherTeams, hideForOwnTeam,
	 * never.
	 * 
	 * @return The current Name Tag Visibility
	 */
	public String getNameTagVisibility() {
		AbstractStructure s = getModifier(handle);

		if (s instanceof PacketContainer) {
			return s.getStrings().readSafely(1);
		} else if (s instanceof InternalStructure){
			return s.getStrings().readSafely(0);
		}
		return "always";
	}

	/**
	 * Set Name Tag Visibility.
	 * 
	 * @param value - new value.
	 */
	public void setNameTagVisibility(String value) {
		AbstractStructure s = getModifier(handle);

		if (s instanceof PacketContainer) {
			s.getStrings().writeSafely(1, value);
		} else if (s instanceof InternalStructure){
			s.getStrings().writeSafely(0, value);
		}
	}

	/**
	 * Retrieve Color.
	 * <p>
	 * Notes: only if Mode = 0 or 2. Same as Chat colors.
	 * 
	 * @return The current Color
	 */
	public ChatColor getColor() {
		try {
			return getModifier(handle).getEnumModifier(ChatColor.class, MinecraftReflection.getMinecraftClass("EnumChatFormat")).read(0);
		} catch (NullPointerException e) {
			return ChatColor.WHITE;
		}
	}

	/**
	 * Set Color.
	 * 
	 * @param value - new value.
	 */
	public void setColor(ChatColor value) {
		Optional.ofNullable(getModifier(handle)).ifPresent(m -> m.getEnumModifier(ChatColor.class, MinecraftReflection.getMinecraftClass("EnumChatFormat")).write(0, value));
	}

	/**
	 * Get the collision rule.
	 * Notes: only if Mode = 0 or 2. always, pushOtherTeams, pushOwnTeam, never.
	 * @return The current collision rule
	 */
	public String getCollisionRule() {

		AbstractStructure s = getModifier(handle);

		if (s instanceof PacketContainer) {
			return s.getStrings().readSafely(2);
		} else if (s instanceof InternalStructure){
			return s.getStrings().readSafely(1);
		}
		return "always";
	}

	/**
	 * Sets the collision rule.
	 * @param value - new value.
	 */
	public void setCollisionRule(String value) {

		AbstractStructure s = getModifier(handle);

		if (s instanceof PacketContainer) {
			s.getStrings().writeSafely(2, value);
		} else if (s instanceof InternalStructure){
			s.getStrings().writeSafely(1, value);
		}
	}

	/**
	 * Retrieve Players.
	 * <p>
	 * Notes: only if Mode = 0 or 3 or 4. Players to be added/remove from the
	 * team. Max 40 characters so may be uuid's later
	 * 
	 * @return The current Players
	 */
	@SuppressWarnings("unchecked")
	public List<String> getPlayers() {
		return (List<String>) handle.getSpecificModifier(Collection.class)
				.read(0);
	}

	/**
	 * Set Players.
	 * 
	 * @param value - new value.
	 */
	public void setPlayers(List<String> value) {
		handle.getSpecificModifier(Collection.class).write(0, value);
	}

	/**
	 * Retrieve Mode.
	 * <p>
	 * Notes: if 0 then the team is created. If 1 then the team is removed. If 2
	 * the team team information is updated. If 3 then new players are added to
	 * the team. If 4 then players are removed from the team.
	 * 
	 * @return The current Mode
	 */
	public int getMode() {
		return handle.getIntegers().read(0);
	}

	/**
	 * Set Mode.
	 * 
	 * @param value - new value.
	 */
	public void setMode(int value) {
		handle.getIntegers().write(0, value);
	}

	/**
	 * Retrieve pack option data. Pack data is calculated as follows:
	 * 
	 * <pre>
	 * <code>
	 * int data = 0;
	 * if (team.allowFriendlyFire()) {
	 *     data |= 1;
	 * }
	 * if (team.canSeeFriendlyInvisibles()) {
	 *     data |= 2;
	 * }
	 * </code>
	 * </pre>
	 * 
	 * @return The current pack option data
	 */
	public int getPackOptionData() {
		AbstractStructure s = getModifier(handle);

		if (s instanceof PacketContainer) {
			return s.getIntegers().readSafely(1);
		} else if (s instanceof InternalStructure){
			return s.getIntegers().readSafely(0);
		}
		return 0;
	}

	/**
	 * Set pack option data.
	 * 
	 * @param value - new value
	 * @see #getPackOptionData()
	 */
	public void setPackOptionData(int value) {
		AbstractStructure s = getModifier(handle);

		if (s instanceof PacketContainer) {
			s.getIntegers().writeSafely(1, value);
		} else if (s instanceof InternalStructure){
			s.getIntegers().writeSafely(0, value);
		}
	}

	//Used to get data from Optional<Parameters>
	private AbstractStructure getModifier(PacketContainer handle) {
		//If there is this "internal structure" of team parameters, return it
		try {
			Optional<InternalStructure> structure = handle.getOptionalStructures().readSafely(0);
			if (structure == null || structure.isEmpty()) return null; //Yes, i check if it's null, cuz as readSafely states, it can return null
			return structure.get();
		} catch (FieldAccessException e) {
			//If there isn't any "internal structures", that probably means that you use version before that JS dev was hired
			return handle;
		}
	}
}
