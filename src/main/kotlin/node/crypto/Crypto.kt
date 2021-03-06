package node.crypto

import java.security.MessageDigest
import java.math.BigInteger
import org.apache.commons.codec.binary.Base64
import javax.crypto.spec.SecretKeySpec
import javax.crypto.Mac

/**
 * Encrypt string data. Supports the following encodings:
 *  * bcrypt
 *  * md5
 *  * sha256
 */
fun String.encrypt(algorithm: String, encoding: String? = null): String {
  if (algorithm == "bcrypt") {
    return BCrypt.hashpw(this, BCrypt.gensalt())!!
  }

  var md = MessageDigest.getInstance(algorithm)
  md.update(this.toByteArray())
  var bytes = md.digest()!!
  if (encoding != null) {
    return bytes.encode(encoding)
  } else {
    return String(bytes)
  }
}

/**
 * Encode a string to an alternate encoding (hex or base64 is currently supported)
 */
fun String.encode(encoding: String): String {
  return this.toByteArray().encode(encoding)
}

/**
 * Create an hmac hash of a string
 */
fun String.hmac(algorithm: String, salt: String, encoding: String): String {
  val secretKeySpec = SecretKeySpec(salt.toByteArray(), algorithm)
  val mac = Mac.getInstance(algorithm)!!
  mac.init(secretKeySpec)
  val bytes = mac.doFinal(this.toByteArray())!!
  return bytes.encode(encoding)
}

/**
 * Encode a byte array as a string
 */
fun ByteArray.encode(encoding: String): String {
  var result: String
  if (encoding == "hex") {
    var big = BigInteger(1, this)
    var length = this.size().shl(1)
    result = java.lang.String.format("%0" + length + "X", big)
  } else if (encoding == "base64") {
    result = Base64.encodeBase64String(this)!!
  } else {
    throw IllegalArgumentException()
  }
  return result
}

/**
 * Decode a string with a given encoding into another string.
 */
fun String.decode(encoding: String): String {
  if (encoding == "base64") {
    return String(Base64.decodeBase64(this)!!)
  } else {
    throw IllegalArgumentException()
  }
}

/**
 * Decode a string with a given encoding into a byte array. Only
 * "base64" is currently supported.
 */
public
fun String.decodeToBytes(encoding: String): ByteArray {
  if (encoding == "base64") {
    return Base64.decodeBase64(this)!!
  } else {
    throw IllegalArgumentException()
  }
}