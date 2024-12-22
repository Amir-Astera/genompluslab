package innobiz.crm.genompluslab.feature.files.domain.services

import innobiz.crm.genompluslab.core.extension.toModel
import innobiz.crm.genompluslab.feature.files.data.FileEntity
import innobiz.crm.genompluslab.feature.files.domain.models.File
import innobiz.crm.genompluslab.feature.files.domain.models.FileDirectory
import innobiz.crm.genompluslab.feature.repositories.FileRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.withContext
import org.apache.tika.mime.MimeTypes
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.io.path.exists
import kotlin.io.path.isDirectory

interface FileService {
    suspend fun storeFile(directory: FileDirectory, part: FilePart): File
    suspend fun getFile(directory: FileDirectory, id: String, format: String): Resource
    suspend fun   updateFile(id: String, directory: FileDirectory?, part: FilePart?): File
    suspend fun deleteFile(id: String, directory: FileDirectory, format: String) : Unit
}

@Service
internal class FileServiceImpl(
        private val location: Path,
        private val dispatcher: CoroutineDispatcher,
        private val fileRepository: FileRepository
): FileService {
    override suspend fun storeFile(directory: FileDirectory, part: FilePart): File {
        val allTypes = MimeTypes.getDefaultMimeTypes()
        val fileType = allTypes.forName(part.headers().contentType.toString())
        val format = fileType.extension.replace(".", "")

        val id = "${UUID.randomUUID()}"
        val fileName = "$id.$format"
        val cleanPath = StringUtils.cleanPath("${directory.name.lowercase()}/$fileName")
        val targetLocation = location.resolve(cleanPath)
        withContext(Dispatchers.IO) {
            Files.createDirectories(targetLocation.parent)
            part.transferTo(targetLocation).awaitSingleOrNull()
        }
        return withContext(dispatcher) {
            val fileEntity = fileRepository.save(
                    FileEntity(
                            id = id,
                            format = format,
                            directory = directory,
                            url = "/api/files/${directory.name.lowercase()}/$fileName",
                            version = null
                    )
            )
            return@withContext fileEntity.toModel()
        }
    }

    override suspend fun getFile(directory: FileDirectory, id: String, format: String): Resource {
        val cleanPath = StringUtils.cleanPath("${directory.name.lowercase()}/$id.$format")
        val targetLocation = location.resolve(cleanPath)
        return if (targetLocation.exists() && !targetLocation.isDirectory()) FileSystemResource(targetLocation) else throw FileNotFoundException()
    }

    override suspend fun updateFile(id: String, directory: FileDirectory?, part: FilePart?): File {
        if (directory == null && part == null) throw FileNotFoundException()
        return withContext(dispatcher) {
            val oldEntity = fileRepository.findById(id) ?: throw FileNotFoundException()
            val newEntity = oldEntity.copy(
                    directory = directory ?: oldEntity.directory
            )
            fileRepository.save(newEntity)
            return@withContext newEntity.toModel()
        }
    }

    override suspend fun deleteFile(id: String, directory: FileDirectory, format: String) {
        val cleanPath: String = StringUtils.cleanPath("${directory.name.lowercase()}/$id.$format")
        val targetLocation = location.resolve(cleanPath)
        return withContext(dispatcher) {
            if (targetLocation.exists() && !targetLocation.isDirectory()) {
                Files.deleteIfExists(targetLocation)
                fileRepository.deleteById(id)
            } else throw FileNotFoundException()
        }
    }
}