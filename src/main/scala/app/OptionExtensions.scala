package app

object OptionExtensions {
  extension [T] (opt: Option[T]) {
    def getOrThrow(exception: () => Exception): T = {
      opt match
        case Some(value) =>
          value
        case None => 
          throw exception()
    }
  }
}