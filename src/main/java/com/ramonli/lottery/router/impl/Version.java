package com.ramonli.lottery.router.impl;

import java.util.StringTokenizer;

/**
 * Version definition follows below format:
 * 
 * <pre>
 * <code>{Major version}</code>.<code>{Minor version}</code>.<code>{Revision version}</code>
 * </pre>
 * 
 * Version 1.6.8 is older than 2.0.1, and 1 will be parsed as 1.0.0
 * 
 * @author Ramon Li
 */
public class Version {
	private static final String DELIMETER = ".";
	private int _major;
	private int _minor;
	private int _revision;

	public Version(int major, int minor, int revision) {
		this._major = major;
		this._minor = minor;
		this._revision = revision;
	}

	public static Version from(String version) {
		if (version == null)
			throw new IllegalArgumentException("Argument 'version' can't be null.");
		int major = 0, minor = 0, revision = 0;
		StringTokenizer st = new StringTokenizer(version, DELIMETER);
		if (st.hasMoreElements())
			major = Integer.parseInt((String) st.nextElement());
		if (st.hasMoreElements())
			minor = Integer.parseInt((String) st.nextElement());
		if (st.hasMoreElements())
			revision = Integer.parseInt((String) st.nextElement());
		if (st.hasMoreElements())
			throw new IllegalArgumentException(
			        "Unsupported version format, it should follow major.minor.revision");
		return new Version(major, minor, revision);
	}

	@Override
	public String toString() {
		return new StringBuffer().append(this._major).append(".").append(this._minor).append(".")
		        .append(this._revision).toString();
	}

	/**
	 * Compare 2 <code>Version</code>s.
	 * 
	 * @param v The target version.
	 * @return 1 if current version is newer than target version, 0 if they are
	 *         same, -1 if current version is older than target version.
	 */
	public int compare(Version v) {
		int value = -1;
		if (this._major > v.getMajor()) {
			value = 1;
		} else if (this._major == v.getMajor()) {
			if (this._minor > v.getMinor())
				value = 1;
			else if (this._minor == v.getMinor()) {
				if (this._revision > v.getRevision())
					value = 1;
				else if (this._revision == v.getRevision())
					value = 0;
			}
		}

		return value;
	}

	public int getMajor() {
		return _major;
	}

	public int getMinor() {
		return _minor;
	}

	public int getRevision() {
		return _revision;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + _major;
		result = prime * result + _minor;
		result = prime * result + _revision;
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
		Version other = (Version) obj;
		if (_major != other._major)
			return false;
		if (_minor != other._minor)
			return false;
		if (_revision != other._revision)
			return false;
		return true;
	}

}
