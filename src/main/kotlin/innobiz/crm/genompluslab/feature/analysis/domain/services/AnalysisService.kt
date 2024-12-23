package innobiz.crm.genompluslab.feature.analysis.domain.services

import innobiz.crm.genompluslab.core.extension.toEntity
import innobiz.crm.genompluslab.core.extension.toModel
import innobiz.crm.genompluslab.feature.analysis.data.AnalysisEntity
import innobiz.crm.genompluslab.feature.analysis.domain.errors.AnalysisNotFoundException
import innobiz.crm.genompluslab.feature.analysis.domain.models.Analysis
import innobiz.crm.genompluslab.feature.analytics.data.AnalyticsAnalysisEntity
import innobiz.crm.genompluslab.feature.analytics.domain.models.AnalysisWithScore
import innobiz.crm.genompluslab.feature.analytics.domain.models.AnalysisWithPopularity
import innobiz.crm.genompluslab.feature.city.data.CityAnalysisEntity
import innobiz.crm.genompluslab.feature.city.domain.models.City
import innobiz.crm.genompluslab.feature.repositories.AnalysisRepository
import innobiz.crm.genompluslab.feature.repositories.AnalyticsAnalysisRepository
import innobiz.crm.genompluslab.feature.repositories.CityAnalysisRepository
import innobiz.crm.genompluslab.feature.repositories.TopicAnalysisRepository
import innobiz.crm.genompluslab.feature.topic.data.TopicAnalysisEntity
import innobiz.crm.genompluslab.feature.topic.domain.models.Topic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.asFlux
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.withContext
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Service
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.reactive.TransactionalOperator
import java.time.LocalDateTime
import java.util.UUID
import kotlin.math.ceil

interface AnalysisService {
    suspend fun save(analysis: Analysis, topic: Topic, city: City)
    suspend fun saveAll(analysis: Collection<Analysis>)
    suspend fun get(id: String): Analysis
    suspend fun delete(id: String)
    suspend fun getAll(city: String, text: String, page: Int, size: Int): Collection<Analysis>
    suspend fun getPopular(cityId: String): Collection<AnalysisWithPopularity>
    suspend fun getAllByIds(ids: Collection<String>): Collection<Analysis>
    suspend fun getAllByTopicAndCity(cityId: String, topicId: String, page: Int, size: Int): Map<String, Any>
    suspend fun search(cityId: String, name: String, page: Int, size: Int): Map<String, Any>
}

@Service
internal class AnalysisServiceImpl(
        private val transactionManager: ReactiveTransactionManager,
        private val databaseClient: DatabaseClient,
        private val analysisRepository: AnalysisRepository,
        private val topicAnalysisRepository: TopicAnalysisRepository,
        private val cityAnalysisRepository: CityAnalysisRepository,
        private val analyticsAnalysisRepository: AnalyticsAnalysisRepository
): AnalysisService {
    //TODO edit city analysis
    override suspend fun save(analysis: Analysis, topic: Topic, city: City) {
        val operator = TransactionalOperator.create(transactionManager)
        analysisRepository.saveAll(listOf(analysis.toEntity())).asFlux()
                .thenMany(
                        topicAnalysisRepository.saveAll(
                                listOf(
                                        TopicAnalysisEntity(
                                        "${analysis.id}-${topic.id}",
                                        topic.id,
                                        analysis.id,
                                        null,
                                        LocalDateTime.now()
                                ))
                        ).asFlux()
                ).thenMany(
                    cityAnalysisRepository.saveAll(
                            listOf(
                                    CityAnalysisEntity(
                                            "${analysis.id}-${city.id}",
                                            city.id,
                                            analysis.id,
                                            null,
                                            LocalDateTime.now(),
                                            LocalDateTime.now()
                                    )
                            )
                    ).asFlux()
                ).thenMany(
                     analyticsAnalysisRepository.saveAll(
                             listOf(
                                     AnalyticsAnalysisEntity(
                                             "${analysis.id}-${UUID.randomUUID().toString()}",
                                             analysis.id,
                                             city.id,
                                             0.0,
                                             0.0,
                                             0.0,
                                             null,
                                             null,
                                             LocalDateTime.now(),
                                             LocalDateTime.now()
                                     )
                             )
                     ).asFlux()
                ).`as`(operator::transactional).asFlow().collect {}
    }

    override suspend fun saveAll(analysis: Collection<Analysis>) {
        analysisRepository.saveAll(analysis.map { it.toEntity() })
    }

    override suspend fun get(id: String): Analysis =
        analysisRepository.findById(id)?.toModel() ?: throw AnalysisNotFoundException(id)

    override suspend fun getPopular(cityId: String): Collection<AnalysisWithPopularity> {
        return databaseClient.sql("""
            SELECT a.*,
                   SUM(s.sales_count * :salesWeight +
                       s.views_count * :viewsWeight +
                       s.cart_count * :cartAddsWeight) *
                   CASE
                       WHEN s.last_sale_date = CURRENT_DATE THEN 1.0
                       WHEN s.last_sale_date >= CURRENT_DATE - INTERVAL '7 days' THEN 0.8
                       WHEN s.last_sale_date >= CURRENT_DATE - INTERVAL '30 days' THEN 0.5
                       ELSE 0.2
                       END AS popular_score
            FROM analytics_popular_analysis s
                     JOIN city_analysis ca ON s.analysis_id = ca.analysis_id
                     JOIN analysis a ON s.analysis_id = a.id
            WHERE ca.city_id = :cityId
            GROUP BY a.id, a.code, a.name, a.material, a.deadline, a.price, a.description, a.version, a.created_at, a.updated_at, s.last_sale_date
            ORDER BY popular_score DESC
            LIMIT :limit
        """)
                .bind("cityId", cityId)
                .bind("salesWeight", 1.0)
                .bind("viewsWeight", 0.2)
                .bind("cartAddsWeight", 0.3)
                .bind("limit", 10)
                .map { row, _ ->
                    AnalysisWithPopularity(
                            analysis = Analysis(
                                    id = row.get("id", String::class.java) ?: throw IllegalStateException("Missing analysis id"),
                                    code = row.get("code", String::class.java) ?: "",
                                    name = row.get("name", String::class.java) ?: "",
                                    material = row.get("material", String::class.java) ?: "",
                                    deadline = row.get("deadline", String::class.java) ?: "",
                                    price = row.get("price", String::class.java)?.toDouble() ?: 0.0,
                                    description = row.get("description", String::class.java) ?: "",
                                    version = row.get("version", String::class.java)?.toLong(),
                            ),
                            popularScore = row.get("popular_score", String::class.java)?.toDouble() ?: 0.0
                    )
                }
                .all()
                .collectList()
                .awaitSingle()
    }

    override suspend fun getAll(city: String, text: String, page: Int, size: Int): Collection<Analysis> {
        val offset = page * size
        return emptySet()
    }

    override suspend fun getAllByIds(ids: Collection<String>): Collection<Analysis> {
        return analysisRepository.findAllById(ids).map { it.toModel() }.toList()
    }

    override suspend fun getAllByTopicAndCity(cityId: String, topicId: String, page: Int, size: Int): Map<String, Any> {
        return withContext(Dispatchers.IO) {
            val offset = page * size
            println(offset)
            println("tut 1")
            val analyses = databaseClient.sql("""
    SELECT a.*, 
           CAST(
               SUM(s.sales_count * :salesWeight +
                   s.views_count * :viewsWeight +
                   s.cart_count * :cartAddsWeight) *
                   CASE
                       WHEN s.last_sale_date = CURRENT_DATE THEN 1.0
                       WHEN s.last_sale_date >= CURRENT_DATE - INTERVAL '7 days' THEN 0.8
                       WHEN s.last_sale_date >= CURRENT_DATE - INTERVAL '30 days' THEN 0.5
                       ELSE 0.2
                   END AS double precision
           ) AS popular_score
    FROM analytics_popular_analysis s
    JOIN topic_analysis ta ON s.analysis_id = ta.analysis_id
    JOIN city_analysis ca ON s.analysis_id = ca.analysis_id
    JOIN analysis a ON s.analysis_id = a.id
    WHERE ta.topic_id = :topicId
      AND ca.city_id = :cityId
    GROUP BY a.id, a.code, a.name, a.material, a.deadline, a.price, a.description, a.version, s.last_sale_date
    ORDER BY popular_score DESC
    LIMIT :limit OFFSET :offset
""")
                    .bind("topicId", topicId)
                    .bind("cityId", cityId)
                    .bind("salesWeight", 1.0)
                    .bind("viewsWeight", 0.2)
                    .bind("cartAddsWeight", 0.3)
                    .bind("limit", size)
                    .bind("offset", offset)
                    .map { row, _ ->
                        AnalysisWithPopularity(
                                analysis = Analysis(
                                        id = row.get("id", String::class.java) ?: throw IllegalStateException("Missing analysis id"),
                                        code = row.get("code", String::class.java) ?: "",
                                        name = row.get("name", String::class.java) ?: "",
                                        material = row.get("material", String::class.java) ?: "",
                                        deadline = row.get("deadline", String::class.java) ?: "",
                                        price = row.get("price", String::class.java)?.toDouble() ?: 0.0,
                                        description = row.get("description", String::class.java) ?: "",
                                        version = row.get("version", String::class.java)?.toLong(),
                                ),
                                popularScore = row.get("popular_score", String::class.java)?.toDouble() ?: 0.0
                        )
                    }
                    .all()
                    .collectList()
                    .awaitSingle()
            println(analyses)
            println("tut")
            // Получаем общее количество записей
            val totalElements = databaseClient.sql("""
            SELECT COUNT(DISTINCT s.analysis_id) AS total_count
            FROM analytics_popular_analysis s
            JOIN topic_analysis ta ON s.analysis_id = ta.analysis_id
            JOIN city_analysis ca ON s.analysis_id = ca.analysis_id
            WHERE ta.topic_id = :topicId
              AND ca.city_id = :cityId
        """)
                    .bind("topicId", topicId)
                    .bind("cityId", cityId)
                    .map { row, _ ->
                        row.get("total_count", Integer::class.java)?.toInt()
                                ?: throw IllegalStateException("Count result is null")
                    }
                    .one()
                    .awaitSingle()
                    .toLong()

            // Вычисляем количество страниц
            val totalPages = if (totalElements % size == 0L) {
                totalElements / size
            } else {
                totalElements / size + 1
            }

            // Возвращаем результат в виде карты
            return@withContext mapOf(
                    "content" to analyses,
                    "totalElements" to totalElements,
                    "totalPages" to totalPages.toInt(),
                    "currentPage" to page
            )
        }
    }

    override suspend fun delete(id: String) {
        analysisRepository.deleteById(id)
    }

    override suspend fun search(cityId: String, name: String, page: Int, size: Int): Map<String, Any> {
        val offset = page * size
        val analyses = databaseClient.sql("""
    SELECT a.*, 
           (
               CASE 
                   WHEN LOWER(a.name) LIKE LOWER(:name || '%') THEN 2.0 -- Совпадение с началом строки
                   WHEN LOWER(a.name) LIKE LOWER('%' || :name || '%') THEN 1.0 -- Частичное совпадение
                   ELSE 0.0
               END +
               CASE 
                   WHEN s.sales_count IS NOT NULL THEN 
                       (s.sales_count * :salesWeight + s.views_count * :viewsWeight + s.cart_count * :cartAddsWeight) * 
                       CASE 
                           WHEN s.last_sale_date = CURRENT_DATE THEN 1.0
                           WHEN s.last_sale_date >= CURRENT_DATE - INTERVAL '7 days' THEN 0.8
                           WHEN s.last_sale_date >= CURRENT_DATE - INTERVAL '30 days' THEN 0.5
                           ELSE 0.2
                       END
                   ELSE 0
               END
           ) AS final_score
    FROM analysis a
    JOIN city_analysis c ON a.id = c.analysis_id
    LEFT JOIN analytics_popular_analysis s ON s.analysis_id = a.id
    WHERE c.city_id = :cityId
      AND LOWER(a.name) LIKE LOWER('%' || :name || '%')
    ORDER BY 
        final_score DESC, 
        a.name ASC
    LIMIT :limit OFFSET :offset
""")
                .bind("cityId", cityId)
                .bind("name", name)
                .bind("salesWeight", 1.0)
                .bind("viewsWeight", 0.2)
                .bind("cartAddsWeight", 0.3)
                .bind("limit", size)
                .bind("offset", offset)
                .map { row, _ ->
                    Analysis(
                            id = row.get("id", String::class.java) ?: throw IllegalStateException("Missing analysis id"),
                            code = row.get("code", String::class.java) ?: "",
                            name = row.get("name", String::class.java) ?: "",
                            material = row.get("material", String::class.java) ?: "",
                            deadline = row.get("deadline", String::class.java) ?: "",
                            price = row.get("price", String::class.java)?.toDouble() ?: 0.0,
                            description = row.get("description", String::class.java) ?: "",
                            version = row.get("version", String::class.java)?.toLong(),
                    )
                }
                .all()
                .collectList()
                .awaitSingle()
        val totalElements = databaseClient.sql("""
            SELECT COUNT(a.id) AS total_count
            FROM analysis a
            JOIN city_analysis c ON a.id = c.analysis_id
            LEFT JOIN analytics_popular_analysis s ON s.analysis_id = a.id
            WHERE c.city_id = :cityId
              AND LOWER(a.name) LIKE LOWER(CONCAT('%', :name, '%'))
        """)
                .bind("cityId", cityId)
                .bind("name", name)
                .map { row, _ ->
                    row.get("total_count", java.lang.Long::class.java)?.toLong()
                            ?: throw IllegalStateException("Count result is null")
                }
                .one()
                .awaitSingle()
        val totalPages = ceil(totalElements.toDouble() / size).toInt()

        return mapOf(
                "content" to analyses,
                "totalElements" to totalElements,
                "totalPages" to totalPages,
                "currentPage" to page
        )
    }

}