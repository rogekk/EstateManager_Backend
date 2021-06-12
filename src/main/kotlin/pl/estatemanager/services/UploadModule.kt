package pl.estatemanager.services

import life.shank.ShankModule
import life.shank.single
import pl.estatemanager.common.CommonModule.idGenerator

object UploadModule: ShankModule {
    val uploadService = single { -> UploadService(idGenerator()) }
}