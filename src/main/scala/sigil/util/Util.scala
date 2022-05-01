package sigil.util

import scala.util.hashing.MurmurHash3

object Util {
  def hash(str: String): Int = MurmurHash3.stringHash(str)
  def toPositive(n: Int): Int = Math.abs(n)
}
