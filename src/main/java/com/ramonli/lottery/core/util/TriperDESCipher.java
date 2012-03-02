package com.ramonli.lottery.core.util;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * In cryptography, <b>modes of operation</b> is the procedure of enabling the
 * repeated and secure use of a <b>block cipher</b> under a single key. A block
 * cipher by itself allows encryption only of a single data block of the
 * cipher's block length. When targeting a variable-length message, the data
 * must first be partitioned into separate cipher blocks. Typically, the last
 * block must also be extended to match the cipher's block length using a
 * suitable <b>padding scheme</b>. A <b>mode of operation</b> describes the
 * process of encrypting each of these blocks, and generally uses
 * <b>randomization</b> based on an additional input value, often called an
 * <b>initialization vector</b>, to allow doing so safely.
 * <p>
 * <b>IV</b>
 * </p>
 * <p>
 * An initialization vector (IV) is a block of bits that is used by several
 * modes to randomize the encryption and hence to produce distinct ciphertexts
 * even if the same plaintext is encrypted multiple times, without the need for
 * a slower re-keying process.
 * <p>
 * <b>PADDING</b>
 * </p>
 * A block cipher works on units of a fixed size (known as a block size), but
 * messages come in a variety of lengths. So some modes (namely ECB and CBC)
 * require that the final block be padded before encryption. Several padding
 * schemes exist. The simplest is to add null bytes to the plaintext to bring
 * its length up to a multiple of the block size, but care must be taken that
 * the original length of the plaintext can be recovered;
 * <p>
 * DESede/3DES/TriperDES. There are at least five common conventions of padding
 * scheme:
 * <ul>
 * <li>1. Pad with bytes all of the same value as the number of padding bytes</li>
 * <li>2. Pad with 0x80 followed by zero bytes</li>
 * <li>3. Pad with zeroes except make the last byte equal to the number of
 * padding bytes</li>
 * <li>4. Pad with zero (null) characters</li>
 * <li>5. Pad with space characters Method one is the method described in
 * PKCS#5, PKCS#7 and RFC 3852 Section 6.3 (formerly RFC 3369 and RFC 2630). It
 * is the most commonly used. Refer to: http://www.di-mgt.com.au/cryptopad.html</li>
 * </ul>
 * <p>
 * <b>ECB</b>
 * <p>
 * The simplest of the encryption modes is the electronic codebook (ECB) mode.
 * The message is divided into blocks and each block is encrypted separately.
 * <p>
 * The disadvantage of this method is that identical plaintext blocks are
 * encrypted into identical ciphertext blocks; thus, it does not hide data
 * patterns well. In some senses, it doesn't provide serious message
 * confidentiality, and it is not recommended for use in cryptographic protocols
 * at all.
 * <p>
 * <b>CBC</b>
 * <p>
 * Cipher-block chaining (CBC) mode of operation was invented by IBM in 1976. In
 * CBC mode, each block of plaintext is XORed with the previous ciphertext block
 * before being encrypted. This way, each ciphertext block is dependent on all
 * plaintext blocks processed up to that point. Also, to make each message
 * unique, an initialization vector must be used in the first block.
 * <p>
 * Refer to below resources:
 * <ul>
 * <li>http://en.wikipedia.org/wiki/Block_cipher_modes_of_operation</li>
 * </ul>
 */
public class TriperDESCipher {
	protected static Log logger = LogFactory.getLog(TriperDESCipher.class);
	// define the cipher algorithm
	private static final String algorithm = "DESede";
	private static final String transformation = "DESede/CBC/PKCS5Padding";
	public static final byte[] IV = new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

	/**
	 * Generate a 24-bytes data key, it is encoded by 'Base64'.
	 * 
	 * @return a base64 representation of data key.
	 */
	public static String generateDataKey() throws Exception {
		int keySize = 24;
		MessageDigest md = MessageDigest.getInstance("SHA");
		md.update(random(25).getBytes());
		String tmpPass = md.digest().toString();
		String key = tmpPass + random(keySize - tmpPass.length());

		String dataKey = new String(Base64Coder.encode(key.getBytes()));
		if (logger.isDebugEnabled()) {
			logger.debug("Generated Data Key(base64): " + dataKey);
		}
		return dataKey;
	}

	/**
	 * Encryption...
	 * @param base64Key The base64 representation of secret key
	 * @param input The original input string.
	 * @param hexIv The hex representation of IV.
	 * @return a base64 representation of encrypted output.
	 * @throws Exception when encounter any exception.
	 */
	public static String encrypt(String base64Key, String input, String hexIv) throws Exception {
		byte keyBytes[] = Base64Coder.decode(base64Key);
		byte inputs[] = input.getBytes(); // use default
		                                  // Charset.getDefaultEncoding()
		byte ivBytes[] = HexCoder.hexToBuffer(hexIv);
		byte output[] = encrypt(keyBytes, inputs, ivBytes);
		return new String(Base64Coder.encode(output));
	}

	public static byte[] encrypt(byte[] keyBytes, byte[] src, byte[] ivBytes) throws Exception {
		SecretKey deskey = new SecretKeySpec(keyBytes, algorithm);
		AlgorithmParameterSpec iv = new IvParameterSpec(ivBytes);

		// do encryption
		Cipher c1 = Cipher.getInstance(transformation);
		c1.init(Cipher.ENCRYPT_MODE, deskey, iv, new SecureRandom());
		return c1.doFinal(src);
	}

	/**
	 * Decryption...
	 * @param base64Key The base64 representation of secret key.
	 * @param cipher The base64 representation of encrypted output.
	 * @param hexIv The hex representation of IV.
	 * @return original input string.
	 */
	public static String decrypt(String base64Key, String cipher, String hexIv) throws Exception {
		byte keyBytes[] = Base64Coder.decode(base64Key);
		byte ciphers[] = Base64Coder.decode(cipher);
		byte ivBytes[] = HexCoder.hexToBuffer(hexIv);
		return new String(decrypt(keyBytes, ciphers, ivBytes));
	}

	public static byte[] decrypt(byte[] keyBytes, byte[] cipher, byte[] ivBytes) throws Exception {
		SecretKey deskey = new SecretKeySpec(keyBytes, algorithm);
		// length: 8 bytes
		AlgorithmParameterSpec iv = new IvParameterSpec(ivBytes);

		// do decryption
		Cipher c1 = Cipher.getInstance(transformation);
		c1.init(Cipher.DECRYPT_MODE, deskey, iv, new SecureRandom());
		return c1.doFinal(cipher);
	}

	private static String random(int length) {
		UUID uuid = UUID.randomUUID();
		String myRandom = uuid.toString();
		return myRandom.substring(0, length);
	}
}
