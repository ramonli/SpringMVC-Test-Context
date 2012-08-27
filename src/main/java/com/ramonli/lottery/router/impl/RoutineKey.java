package com.ramonli.lottery.router.impl;

import com.google.gson.Gson;
import com.ramonli.lottery.router.RequestMap;
import com.ramonli.lottery.router.RoutineStrategy;

/**
 * A routine key uniquely identify a request handling path. The router will
 * route incoming request to appreciate handler.
 * <p>
 * There are some factors to determine a routine key:
 * <ol>
 * <li>Game type - Different game type may require dedicated handler for same
 * transaction type.</li>
 * <li>Transaction type - Each transaction type may require a dedicated handler.
 * </li>
 * <li>Protocol version - A interface may exists multiple different versions.</li>
 * </ol>
 * 
 * @author Ramon Li
 */
public class RoutineKey implements RoutineStrategy {
	public static final int TYPE_UNDEF = -1; // UNdeinfed game type.
	public static final int TYPE_LOTT = 1; // Lotto game type
	public static final int TYPE_SOCCER = 2;
	public static final int TYPE_RACING = 3; // Horse racing type
	
	private int gameType = TYPE_UNDEF;
	private int transType;
	private Version version;

	public RoutineKey() {
	}

	public RoutineKey(int gameType, int transType, Version version) {
		this.gameType = gameType;
		this.transType = transType;
		this.version = version;
	}

	public RoutineKey(int transType) {
		this(TYPE_UNDEF, transType, null);
	}

	/**
	 * Assemble a <code>RoutineKey</code> from a <code>Requestmap</code>. The
	 * format of value of <code>RequestMap</code> must follow a json format:
	 * <p>
	 * {gameType:${gameType},transType:${transType},version:${version}}
	 * <p>
	 * <ol>
	 * <li>gameType(int): the type identifier of a game type, it is optional.</li>
	 * <li>transType(int): the request type of transaction, it is mandatory</li>
	 * <li>version(string): the version of required interface, it is optional.</li>
	 * </ol>
	 * For example:
	 * <ul>
	 * <li>{gameType:1,transType:200,version:1.0}, it will be converted to
	 * RoutineKey(1_200_1.0.0)</li>
	 * <li>{version:1.0,transType:200}, it will be converted to
	 * RoutineKey(-1_200_1.0.0)</li>
	 * <li>{version:"1.0.1",transType:200}, it will be converted to
	 * RoutineKey(-1_200_1.0.1).</li>
	 * <li>{transType:200}, it will be converted to RoutineKey(-1_200_)</li>
	 * </ul>
	 * the default game type is -1(undefined), and default protocol version is
	 * null.
	 */
	@Override
	public void from(RequestMap requestMap) {
		String value = requestMap.value(); 
		RoutineKeyJson json = new Gson().fromJson(value, RoutineKeyJson.class);
		this.setGameType(json.getGameType() == null ? TYPE_UNDEF : json.getGameType());
		this.setTransType(json.getTransType());
		if (json.getVersion() != null)
			this.setVersion(Version.from(json.getVersion()));
	}

	private class RoutineKeyJson {
		private Integer gameType = TYPE_UNDEF;
		private int transType;
		private String version;

		public Integer getGameType() {
			return gameType;
		}

		public void setGameType(Integer gameType) {
			this.gameType = gameType;
		}

		public int getTransType() {
			return transType;
		}

		public void setTransType(int transType) {
			this.transType = transType;
		}

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}
	}

	public int getGameType() {
		return gameType;
	}

	public int getTransType() {
		return transType;
	}

	public Version getVersion() {
		return version;
	}

	private void setGameType(int gameType) {
		this.gameType = gameType;
	}

	private void setTransType(int transType) {
		this.transType = transType;
	}

	private void setVersion(Version version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return new StringBuffer("").append(this.getGameType()).append("_")
		        .append(this.getTransType()).append("_")
		        .append(this.getVersion() == null ? "" : this.getVersion().toString()).toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + gameType;
		result = prime * result + transType;
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RoutineKey other = (RoutineKey) obj;
		if (gameType != other.gameType)
			return false;
		if (transType != other.transType)
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}
}
