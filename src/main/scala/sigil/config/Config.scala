package sigil.config

final case class Config(dbConfig: DbConfig)

final case class DbConfig(username: String, password: String, host: String)
