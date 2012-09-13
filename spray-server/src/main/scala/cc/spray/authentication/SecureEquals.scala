package cc.spray
package authentication

import java.nio.charset.Charset

private[authentication] object SecureEquals {
  private val utf8 = Charset.forName("UTF-8")

  /**Tests two strings for value equality avoiding timing attacks.
   *
   * @note This function leaks information about the length of each string as well as whether the two strings have the
   *       same length.
   */
  def secureEquals(a: String, b: String): Boolean = {
    require(a != null && b != null, "a == null || b == null")

    secureEquals(a.getBytes(utf8), b.getBytes(utf8))
  }

  /**Tests two byte arrays for value equality avoiding timing attacks.
   *
   * @note This function leaks information about the length of each byte array as well as whether the two byte arrays
   *       have the same length.
   * @see [[http://codahale.com/a-lesson-in-timing-attacks/]]
   * @see [[http://rdist.root.org/2009/05/28/timing-attack-in-google-keyczar-library/]]
   * @see [[http://emerose.com/timing-attacks-explained]]
   */
  def secureEquals(a: Array[Byte], b: Array[Byte]): Boolean = {
    require(a != null && b != null, "a == null || b == null")

    if (a.length == b.length) {
      var result = 0

      for (i <- 0 until a.length) {
        result |= a(i) ^ b(i)
      }

      result == 0
    } else
      false
  }

}
