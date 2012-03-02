package com.ramonli.lottery.core.util;

import java.security.SecureRandom;
import java.util.UUID;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A helper class for hash digesting.
 */
public class HMacMd5Cipher {
	private static Log logger = LogFactory.getLog(HMacMd5Cipher.class);
	private static final String algorithm = "HmacMD5";
	private static final int defaultKeySize = 24*8;
	
	/**
	 * generate a MAC key with default key size(bit).
	 */
	public static String generateMACKey() throws Exception{
		return generateMACKey(defaultKeySize);
	}

	/**
	 * Generate a secret key for MAC.
	 * @param 	keySize		The size(bit) of secret key.
	 * @return a base64 representation of MAC key.
	 * @throws Exception when encounter any exception.
	 */
	public static String generateMACKey(int keySize) throws Exception{
		KeyGenerator kg = KeyGenerator.getInstance(algorithm);
		SecureRandom secureRandom = new SecureRandom();
		String uuid = UUID.randomUUID().toString();
		secureRandom.setSeed(uuid.getBytes());
		kg.init(keySize, secureRandom);
		// The length of MAC key is 64 bytes.
		SecretKey macKey = kg.generateKey();
		if (logger.isDebugEnabled()){
			logger.debug("Key format of " + algorithm + ": " + macKey.getFormat());
		}		
		String macKeyStr =  new String(Base64Coder.encode(macKey.getEncoded()));
		
		if (logger.isDebugEnabled()){
			logger.debug("Generate MAC key(base64): " + macKeyStr);
		}
		return macKeyStr;
	}
	
	/**
	 * Digest a hex string input by HMacMd5.
	 * @param src		The input string .
	 * @param base64Key	The base64 representation of secret key.
	 * @return a hex string of digested output.
	 * @throws Exception when no suitable algorithm is found.
	 */
	public static String doDigest(String src, String base64Key) throws Exception{
		byte[] key = Base64Coder.decode(base64Key);
		byte[] output = doDigest(src.getBytes("UTF-8"), key);
		return HexCoder.bufferToHex(output);
	}
	
	/**
	 * Digest a input byte array by HMacMD5.
	 * @param src		The input byte array.
	 * @param keyBytes	The secret key byte array.
	 * @return a digested byte array.
	 * @throws Exception when no suitable algorithm is found.
	 */
	public static byte[] doDigest(byte[] src, byte[] keyBytes) throws Exception{
		SecretKey remacKey = new SecretKeySpec(keyBytes, algorithm);

		// Get instance of Mac object implementing HMAC-SHA1, and
		// initialize it with the above secret key
		Mac mac = Mac.getInstance(algorithm);
		mac.init(remacKey);
		byte[] result = mac.doFinal(src);

		return result;		
	}
}
