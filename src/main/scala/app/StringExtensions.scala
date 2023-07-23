package app

object StringExtensions {
  extension (str: String) {

    def substringAfterFirst(part: String): String =
      str.indexOf(part) match { 
        case -1 => ""; 
        case i => str.substring(i + part.length)
      }

    def substringAfterLast(part: String): String =
      str.lastIndexOf(part) match { 
        case -1 => ""; 
        case i => str.substring(i + part.length)
      }

    def substringBeforeFirst(part: String): String =
      str.indexOf(part) match { 
        case -1 => str;
        case i => str.substring(0, i)
      }

    def substringBeforeLast(part: String): String =
      str.lastIndexOf(part) match { 
        case -1 => ""; 
        case i => str.substring(0, i)
      }

  }
}