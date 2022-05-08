package sigil.cache

import sigil.model.Flag

import scala.collection.concurrent.TrieMap

trait Cache {
  def flagById(id: Int): Option[Flag]
  def flagByKey(key: String): Option[Flag]

  def putFlag(flag: Flag): Unit
}

object Cache {
  def of(): Cache = new Cache {
    private val idToFlag = new TrieMap[Int, Flag]()
    private val keyToFlagId = new TrieMap[String, Int]()

    def flagById(id: Int): Option[Flag] = idToFlag.get(id)
    def flagByKey(key: String): Option[Flag] =
      for {
        flagId <- keyToFlagId.get(key)
        flag <- idToFlag.get(flagId)
      } yield flag

    def putFlag(flag: Flag): Unit = {
      idToFlag.put(flag.id, flag)
      keyToFlagId.put(flag.key, flag.id)
    }
  }
}
