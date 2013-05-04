package networklib.server.game;

import java.util.LinkedList;
import java.util.UUID;

import networklib.NetworkConstants;
import networklib.channel.Packet;
import networklib.server.packets.MapInfoPacket;
import networklib.server.packets.PlayerInfoPacket;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class Match {

	private final String id = UUID.randomUUID().toString();
	private final LinkedList<Player> players = new LinkedList<Player>();
	private final LinkedList<Player> leftPlayers = new LinkedList<Player>();
	private final byte maxPlayers;
	private final MapInfoPacket map;
	private final String name;

	private EMatchState state = EMatchState.OPENED;

	public Match(String name, byte maxPlayers, MapInfoPacket map) {
		this.maxPlayers = maxPlayers;
		this.map = map;
		this.name = name;
	}

	public EMatchState getState() {
		return state;
	}

	public boolean canJoin() {
		return state == EMatchState.OPENED && players.size() < maxPlayers;
	}

	public String getId() {
		return id;
	}

	public MapInfoPacket getMap() {
		return map;
	}

	public String getName() {
		return name;
	}

	public PlayerInfoPacket[] getPlayerInfos() {
		synchronized (players) {
			PlayerInfoPacket[] result = new PlayerInfoPacket[players.size()];
			int i = 0;
			for (Player curr : players) {
				result[i] = curr.getPlayerInfo();
				i++;
			}

			return result;
		}
	}

	public byte getMaxPlayers() {
		return maxPlayers;
	}

	public boolean isRunning() {
		return state == EMatchState.RUNNING;
	}

	public boolean hasPlayer(Player player) {
		return getPlayer(player.getId()) != null;
	}

	public boolean hasLeftPlayer(String playerId) {
		synchronized (leftPlayers) {
			for (Player curr : players) {
				if (curr.getId().equals(playerId)) {
					return true;
				}
			}

			return false;
		}
	}

	public Player getPlayer(String playerId) {
		synchronized (players) {
			for (Player curr : players) {
				if (curr.getId().equals(playerId)) {
					return curr;
				}
			}

			return null;
		}
	}

	public void notifyPlayerLeft(Player player) {
		Player matchPlayer = getPlayer(player.getId());
		assert matchPlayer != null : "Given player not found in this match! " + this + "  Player: " + player;

		sendAsyncMessage(new PlayerInfoPacket(NetworkConstants.Keys.PLAYER_DISCONNECTED, matchPlayer.getPlayerInfo()));
	}

	private void sendAsyncMessage(Packet packet) {
		synchronized (players) {
			for (Player curr : players) {
				curr.sendPacket(packet);
			}
		}
	}

	public void join(Player player) {
		synchronized (players) {
			sendAsyncMessage(new PlayerInfoPacket(NetworkConstants.Keys.PLAYER_JOINED, player.getPlayerInfo())); // inform the others

			players.add(player);
		}
	}

	public void playerLeft(Player player) {
		synchronized (players) {
			players.remove(player);

			if (isRunning()) {
				synchronized (leftPlayers) {
					leftPlayers.add(player);
				}
			}

			sendAsyncMessage(new PlayerInfoPacket(NetworkConstants.Keys.PLAYER_LEFT, player.getPlayerInfo())); // inform the others
		}
	}

}
