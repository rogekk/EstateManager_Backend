package pl.estatemanager.repositories

import com.snitch.extensions.json

fun main() {
    val key = ""

  print(khttp.post("https://api.sendgrid.com/v3/mail/send",
      headers = mapOf("Authorization" to "Bearer $key",
      "Content-Type" to "application/json",
      ),
      data = Request(
          listOf(Personalization(listOf(Contact("foobaar@gmail.com")))),
          from = Contact("foobar@gmail.com"),
          subject = "Hello world",
          content = listOf(Content("text/plain", "Hey this is just a test"))
      ).json).text)

}

data class Request(
    val personalizations: List<Personalization>,
    val from: Contact,
    val subject: String,
    val content: List<Content>
)

data class Personalization(
    val to: List<Contact>
)


data class Contact(val email: String)
data class Content(val type: String, val value: String)

