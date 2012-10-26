package utils

import scala.util.Random

object RandomHelpers {
	/** Returns a pseudorandomly generated String drawing upon
	 *  only ASCII characters between 33 and 126.
	 */
	def nextASCIIString(length: Int) = {
		val (min, max) = (65, 122)
		def nextDigit = Random.nextInt(max - min) + min
			
    	new String(Array.fill(length)(nextDigit.toByte), "ASCII")
	}      
}