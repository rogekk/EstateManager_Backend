package pl.forums

import com.snitch.Handler
import com.snitch.ok
import pl.propertea.models.ForumResponse
import pl.propertea.models.toResponse
import pl.propertea.repositories.RepositoriesModule.forumsRepository

val getForums: Handler<Nothing, ForumResponse> = {
    val forum = forumsRepository().getForums()
    forum.toResponse().ok
}
