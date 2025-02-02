package innobiz.crm.genompluslab.feature.admin.domain.services

import innobiz.crm.genompluslab.feature.admin.domain.errors.*
import innobiz.crm.genompluslab.feature.admin.domain.models.Material
import innobiz.crm.genompluslab.feature.admin.presentation.dto.GetMaterialDto
import innobiz.crm.genompluslab.feature.admin.presentation.dto.ResponseOrderDto
import innobiz.crm.genompluslab.feature.order.presentation.dto.LISResponseDto
import kotlinx.coroutines.reactive.awaitSingle
import org.slf4j.Logger
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

interface RegistryService {
    suspend fun getMaterials(serviceCode: String, materialId: String): Material
    suspend fun addMaterialWithIds(internalId: String, specimenId: String, ids: String): String
    suspend fun addServiceToMaterial(internalIdWithMaterial: String, materialId: String)
    suspend fun processOrder(internalIdWithMaterial: String, ids: String)
    suspend fun createRecordForOrder(internalId: String)
    suspend fun postProcess(internalId: String)
}

@Service
internal class RegistryServiceImpl(
        private val webClient: WebClient,
        private val logger: Logger
): RegistryService {
    override suspend fun getMaterials(serviceCode: String, materialId: String): Material {
        try {
            val response = webClient
                    .get()
                    .uri { uriBuilder ->
                        uriBuilder
                                .scheme("https")
                                .host("lcn.bregis.kz")
                                .port(10002)
                                .path("/backend-weblab/api/weblab/GET_MATERIALS_FOR_SERVICE")
                                .queryParam("SERVICE_CODE", serviceCode)
                                .build()
                    }
                    .header("Authorization", "eyJhbGciOiJIUzI1NiJ9.eyJuYmYiOjE3MzgwOTIzMzcsImRhdGEiOnsiaWQiOjc4NTAyOCwidXVpZCI6IjVmYTQ2MTcwLTE3NWItNGZhMi1hYzIwLTA5ODBmYjU3NGM5ZCJ9LCJleHAiOjE3MzgwOTU5MzcsImlhdCI6MTczODA5MjMzN30.XIZE0OHq-s0aQI0-dvVQwG8vVThEzsMJzLRMjg1WBs0")
                    .retrieve()
                    .awaitBody<List<GetMaterialDto>>()
            if (response.isEmpty()) throw MaterialGetException()
            val foundMaterial = response.find { it.materialId == materialId } ?: throw MaterialNotFoundException()
            return Material(
                    code = foundMaterial.code,
                    text = foundMaterial.text,
                    keyId = foundMaterial.keyid,
                    materialId = foundMaterial.materialId
            )
        } catch (ex: Exception) {
            logger.info("$serviceCode + $materialId")
            throw MaterialGetException()
        }
    }

    override suspend fun addMaterialWithIds(internalId: String, specimenId: String, ids: String): String {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy'+'HH:mm")
        val currentDateTime = LocalDateTime.now().format(formatter)
        try {
            val response = webClient
                    .get()
                    .uri { uriBuilder ->
                        uriBuilder
                                .scheme("https")
                                .host("lcn.bregis.kz")
                                .port(10002)
                                .path("/backend-weblab/api/weblab/CREATE_RESEARCH_ADD_MATERIAL_WITH_IDS_DEPID_COLLECTDATA")
                                .queryParam("ROOT_RESEARCH_ID", internalId)
                                .queryParam("SPECIMEN_ID", specimenId)
                                .queryParam("IDS", ids)
                                .queryParam("DEPID", "")
                                .queryParam("COLLECT_DATE", currentDateTime)
                                .queryParam("COLLECT_PLACE_ID", "")
                                .build()
                    }
                    .header("Authorization", "eyJhbGciOiJIUzI1NiJ9.eyJuYmYiOjE3MzgwOTIzMzcsImRhdGEiOnsiaWQiOjc4NTAyOCwidXVpZCI6IjVmYTQ2MTcwLTE3NWItNGZhMi1hYzIwLTA5ODBmYjU3NGM5ZCJ9LCJleHAiOjE3MzgwOTU5MzcsImlhdCI6MTczODA5MjMzN30.XIZE0OHq-s0aQI0-dvVQwG8vVThEzsMJzLRMjg1WBs0")
                    .retrieve()
                    .awaitBody<List<LISResponseDto>>()
            if (response.isEmpty()) throw MaterialAddWithIdsException()
            if (response.first().researchId.isNullOrBlank()) throw MaterialAddWithIdsException()
            return response.first().researchId!!
        } catch (ex: Exception) {
            logger.info("$internalId + $specimenId + $ids")
            throw RuntimeException("Add material with Ids error!")
        }
    }

    override suspend fun addServiceToMaterial(internalIdWithMaterial: String, materialId: String) {
        try {
            val response = webClient
                    .get()
                    .uri { uriBuilder ->
                        uriBuilder
                                .scheme("https")
                                .host("lcn.bregis.kz")
                                .port(10002)
                                .path("/backend-weblab/api/weblab/CREATE_RESEARCH_ADD_SERVICE")
                                .queryParam("RESEARCH_ID", internalIdWithMaterial)
                                .queryParam("SRVDEP_ID", materialId)
                                .queryParam("CITO", "0")
                                .queryParam("MAN_ID", "785006")
                                .build()
                    }
                    .header("Authorization", "eyJhbGciOiJIUzI1NiJ9.eyJuYmYiOjE3MzgwOTIzMzcsImRhdGEiOnsiaWQiOjc4NTAyOCwidXVpZCI6IjVmYTQ2MTcwLTE3NWItNGZhMi1hYzIwLTA5ODBmYjU3NGM5ZCJ9LCJleHAiOjE3MzgwOTU5MzcsImlhdCI6MTczODA5MjMzN30.XIZE0OHq-s0aQI0-dvVQwG8vVThEzsMJzLRMjg1WBs0")
                    .retrieve()
                    .toBodilessEntity()
                    .awaitSingle()
            if (response.statusCode != HttpStatus.OK) throw MaterialAddServiceException()
        } catch (ex: Exception) {
            logger.info("$internalIdWithMaterial + $materialId")
            throw RuntimeException("Add service to material error!")
        }
    }

    override suspend fun processOrder(internalIdWithMaterial: String, ids: String) {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy'+'HH:mm")
        val currentDateTime = LocalDateTime.now().format(formatter)
        try {
            val response = webClient
                    .get()
                    .uri { uriBuilder ->
                        uriBuilder
                                .scheme("https")
                                .host("lcn.bregis.kz")
                                .port(10002)
                                .path("/backend-weblab/api/weblab/SET_RESEARCH_IDS")
                                .queryParam("RESEARCH_ID", internalIdWithMaterial)
                                .queryParam("IDS", ids)
                                .queryParam("COLLECTDATE", currentDateTime)
                                .build()
                    }
                    .header("Authorization", "eyJhbGciOiJIUzI1NiJ9.eyJuYmYiOjE3MzgwOTIzMzcsImRhdGEiOnsiaWQiOjc4NTAyOCwidXVpZCI6IjVmYTQ2MTcwLTE3NWItNGZhMi1hYzIwLTA5ODBmYjU3NGM5ZCJ9LCJleHAiOjE3MzgwOTU5MzcsImlhdCI6MTczODA5MjMzN30.XIZE0OHq-s0aQI0-dvVQwG8vVThEzsMJzLRMjg1WBs0")
                    .retrieve()
                    .awaitBody<List<ResponseOrderDto>>()
            if (response.isEmpty()) throw OrderProcessException()
            if (response.first().success != "1") throw OrderProcessException()
        } catch (ex: Exception) {
            logger.info("$internalIdWithMaterial + $ids")
            throw RuntimeException("Cannot process order!")
        }
    }

    override suspend fun createRecordForOrder(internalId: String) {
        try {
            val response = webClient
                    .get()
                    .uri { uriBuilder ->
                        uriBuilder
                                .scheme("https")
                                .host("lcn.bregis.kz")
                                .port(10002)
                                .path("/backend-weblab/api/weblab/CREATE_PATSERV")
                                .queryParam("RESEARCHID", internalId)
                                .queryParam("MAN_ID", "785006")
                                .build()
                    }
                    .header("Authorization", "eyJhbGciOiJIUzI1NiJ9.eyJuYmYiOjE3MzgwOTIzMzcsImRhdGEiOnsiaWQiOjc4NTAyOCwidXVpZCI6IjVmYTQ2MTcwLTE3NWItNGZhMi1hYzIwLTA5ODBmYjU3NGM5ZCJ9LCJleHAiOjE3MzgwOTU5MzcsImlhdCI6MTczODA5MjMzN30.XIZE0OHq-s0aQI0-dvVQwG8vVThEzsMJzLRMjg1WBs0")
                    .retrieve()
                    .awaitBody<List<ResponseOrderDto>>()
            if (response.isEmpty()) throw OrderProcessException()
            if (response.first().success != "1") throw OrderProcessException()
        } catch (ex: Exception) {
            logger.info(internalId)
            throw RuntimeException("Cannot create record for orders!")
        }
    }

    override suspend fun postProcess(internalId: String) {
        try {
            val response = webClient
                    .get()
                    .uri { uriBuilder ->
                        uriBuilder
                                .scheme("https")
                                .host("lcn.bregis.kz")
                                .port(10002)
                                .path("/backend-weblab/api/weblab/CREATE_RESEARCH_POSTPROCESSING")
                                .queryParam("ROOT_RESEARCH_ID", internalId)
                                .build()
                    }
                    .header("Authorization", "eyJhbGciOiJIUzI1NiJ9.eyJuYmYiOjE3MzgwOTIzMzcsImRhdGEiOnsiaWQiOjc4NTAyOCwidXVpZCI6IjVmYTQ2MTcwLTE3NWItNGZhMi1hYzIwLTA5ODBmYjU3NGM5ZCJ9LCJleHAiOjE3MzgwOTU5MzcsImlhdCI6MTczODA5MjMzN30.XIZE0OHq-s0aQI0-dvVQwG8vVThEzsMJzLRMjg1WBs0")
                    .retrieve()
                    .awaitBody<List<ResponseOrderDto>>()
            if (response.isEmpty()) throw OrderProcessException()
            if (response.first().success != "1") throw OrderProcessException()
        } catch (ex: Exception) {
            logger.info(internalId)
            throw RuntimeException("Cannot run postprocessing for orders!")
        }
    }
}