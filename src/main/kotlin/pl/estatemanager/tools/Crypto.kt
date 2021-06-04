package pl.estatemanager.tools

import com.lambdaworks.crypto.SCryptUtil

fun hash(plainPassword: String): String = SCryptUtil.scrypt(plainPassword, 16384, 8, 1)
fun verify(plainPassword: String, hashedPassword: String) = SCryptUtil.check(plainPassword, hashedPassword)
