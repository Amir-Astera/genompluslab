package innobiz.crm.genompluslab.feature.topic.domain.services

import innobiz.crm.genompluslab.core.extension.toEntity
import innobiz.crm.genompluslab.core.extension.toModel
import innobiz.crm.genompluslab.feature.analysis.domain.models.Analysis
import innobiz.crm.genompluslab.feature.analytics.domain.models.AnalysisWithPopularity
import innobiz.crm.genompluslab.feature.analytics.domain.models.TopicWithScore
import innobiz.crm.genompluslab.feature.repositories.AnalysisRepository
import innobiz.crm.genompluslab.feature.repositories.AnalyticsTopicRepository
import innobiz.crm.genompluslab.feature.repositories.TopicAnalysisRepository
import innobiz.crm.genompluslab.feature.repositories.TopicRepository
import innobiz.crm.genompluslab.feature.topic.data.TopicEntity
import innobiz.crm.genompluslab.feature.topic.domain.errors.TopicAnalysesNotFoundException
import innobiz.crm.genompluslab.feature.topic.domain.errors.TopicNotFoundException
import innobiz.crm.genompluslab.feature.topic.domain.models.Topic
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toCollection
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Service
import java.time.LocalDateTime

interface TopicService {
    suspend fun save(topic: Topic): String
    suspend fun get(id: String): Topic
    suspend fun delete(id: String)
    suspend fun getAll(cityId: String, page: Int, size: Int): Map<String, Any>
    suspend fun getTopTopics(cityId: String): Collection<TopicWithScore>
    suspend fun getByName(cityId: String, name: String, page: Int, size: Int): Map<String, Any>
    suspend fun isTopic(cityId: String, name: String): Long
}

@Service
internal class TopicServiceImpl(
        private val topicRepository: TopicRepository,
        private val databaseClient: DatabaseClient,
        private val analyticsTopicRepository: AnalyticsTopicRepository,
        private val topicAnalysisRepository: TopicAnalysisRepository,
        private val analysisRepository: AnalysisRepository
): TopicService {
    override suspend fun save(topic: Topic): String = topicRepository.save(topic.toEntity()).id

    override suspend fun getTopTopics(cityId: String): Collection<TopicWithScore> {
        return databaseClient.sql("""
    SELECT t.*, 
           SUM(
               s.sales_count * :salesWeight +
               s.views_count * :viewsWeight +
               s.cart_count * :cartAddsWeight
           ) *
           CASE
               WHEN MAX(s.last_sale_date) = CURRENT_DATE THEN 1.0
               WHEN MAX(s.last_sale_date) >= CURRENT_DATE - INTERVAL '7 days' THEN 0.8
               WHEN MAX(s.last_sale_date) >= CURRENT_DATE - INTERVAL '30 days' THEN 0.5
               ELSE 0.2
           END AS total_popularity,
           MIN(a.price) AS min_price,
           COUNT(ta.analysis_id) AS total_analyses
    FROM topic t
    JOIN topic_analysis ta ON t.id = ta.topic_id
    JOIN analytics_popular_analysis s ON ta.analysis_id = s.analysis_id
    JOIN analysis a ON a.id = ta.analysis_id
    JOIN city_analysis ca ON a.id = ca.analysis_id
    WHERE ca.city_id = :cityId
    GROUP BY t.id, t.name, t.version, t.created_at, t.updated_at
    ORDER BY total_popularity DESC
    LIMIT 3
""")
                .bind("salesWeight", 1.0)
                .bind("viewsWeight", 0.2)
                .bind("cartAddsWeight", 0.3)
                .bind("cityId", cityId)
                .map { row, _ ->
                    TopicWithScore(
                            topic = Topic(
                                    id = row.get("id", String::class.java) ?: throw IllegalStateException("Missing topic id"),
                                    name = row.get("name", String::class.java) ?: "",
                                    version = row.get("version", String::class.java)?.toLong()
                            ),
                            totalPopularity = row.get("total_popularity", String::class.java)?.toDouble() ?: 0.0,
                            minPrice = row.get("min_price", String::class.java)?.toDouble() ?: 0.0,
                            totalAnalyses = row.get("total_analyses", String::class.java)?.toInt() ?: 0
                    )
                }
                .all()
                .collectList()
                .awaitSingle()
    }

    override suspend fun getByName(cityId: String, name: String, page: Int, size: Int): Map<String, Any> {
        // Найти первый подходящий топик по названию в указанном городе
        val topic = topicRepository.findFirstTopicByNameInCity(cityId, name)
                ?: return emptyMap()

        // Найти анализы, связанные с этим топиком и городом
        val offset = page * size
        val analyses = databaseClient.sql("""
            SELECT a.*, 
                   SUM(s.sales_count * :salesWeight +
                       s.views_count * :viewsWeight +
                       s.cart_count * :cartAddsWeight) *
                       CASE
                           WHEN MAX(s.last_sale_date) = CURRENT_DATE THEN 1.0
                           WHEN MAX(s.last_sale_date) >= CURRENT_DATE - INTERVAL '7 days' THEN 0.8
                           WHEN MAX(s.last_sale_date) >= CURRENT_DATE - INTERVAL '30 days' THEN 0.5
                           ELSE 0.2
                       END AS popular_score
            FROM analytics_popular_analysis s
            JOIN topic_analysis ta ON s.analysis_id = ta.analysis_id
            JOIN city_analysis ca ON s.analysis_id = ca.analysis_id
            JOIN analysis a ON s.analysis_id = a.id
            WHERE ta.topic_id = :topicId
              AND ca.city_id = :cityId
            GROUP BY a.id, a.code, a.name, a.material, a.deadline, a.price, a.description, a.version, a.created_at, a.updated_at
            ORDER BY popular_score DESC
            LIMIT :limit OFFSET :offset
        """)
                .bind("topicId", topic.id)
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
                                    materialKeyId = row.get("material_key_id", String::class.java) ?: "",
                                    materialId = row.get("material_id", String::class.java) ?: "",
                                    deadline = row.get("deadline", String::class.java) ?: "",
                                    price = row.get("price", Double::class.java) ?: 0.0,
                                    description = row.get("description", String::class.java) ?: "",
                                    version = row.get("version", Long::class.java),
                            ),
                            popularScore = row.get("popular_score", Double::class.java) ?: 0.0
                    )
                }
                .all()
                .collectList()
                .awaitSingle()

        val totalElements = databaseClient.sql("""
            SELECT COUNT(DISTINCT s.analysis_id) AS total_count
            FROM analytics_popular_analysis s
            JOIN topic_analysis ta ON s.analysis_id = ta.analysis_id
            JOIN city_analysis ca ON s.analysis_id = ca.analysis_id
            WHERE ta.topic_id = :topicId
              AND ca.city_id = :cityId
        """)
                .bind("topicId", topic.id)
                .bind("cityId", cityId)
                .map { row, _ ->
                    row.get("total_count", Integer::class.java)?.toInt()
                            ?: throw IllegalStateException("Count result is null")
                }
                .one()
                .awaitSingle()

        val totalPages = (totalElements + size - 1) / size // округление вверх

        return mapOf(
                "content" to analyses,
                "totalElements" to totalElements,
                "totalPages" to totalPages,
                "currentPage" to page
        )
    }
    override suspend fun get(id: String): Topic =
        topicRepository.findById(id)?.toModel() ?: throw TopicNotFoundException(id)

    override suspend fun delete(id: String) = topicRepository.deleteById(id)

    override suspend fun getAll(cityId: String, page: Int, size: Int): Map<String, Any> {
        val offset = page * size
        //TODO реализовать гет алл по городу, нужно показать топики по городам
        val topics = databaseClient.sql("""
            SELECT t.*
            FROM topic t
            WHERE EXISTS (
                SELECT 1
                FROM topic_analysis ta
                JOIN city_analysis ca ON ta.analysis_id = ca.analysis_id
                WHERE ta.topic_id = t.id
                  AND ca.city_id = :cityId
            )
            LIMIT :limit OFFSET :offset
        """)
                .bind("cityId", cityId)
                .bind("limit", size)
                .bind("offset", offset)
                .map { row, _ ->
                    Topic(
                            id = row.get("id", String::class.java) ?: throw IllegalStateException("Missing topic id"),
                            name = row.get("name", String::class.java) ?: "",
                            version = row.get("version", String::class.java)?.toLong()
                    )
                }
                .all()
                .collectList()
                .awaitSingle()

        val totalElements = databaseClient.sql("""
            SELECT COUNT(*)
            FROM topic t
            WHERE EXISTS (
                SELECT 1
                FROM topic_analysis ta
                JOIN city_analysis ca ON ta.analysis_id = ca.analysis_id
                WHERE ta.topic_id = t.id
                  AND ca.city_id = :cityId
            )
        """)
                .bind("cityId", cityId)
                .map { row, _ ->
                    row.get(0, String::class.java)?.toLong() ?: 0L
                }
                .one()
                .awaitSingle()
        val totalPages = if (totalElements % size == 0L) totalElements / size else totalElements / size + 1

        return mapOf(
                "content" to topics,
                "totalElements" to totalElements,
                "totalPages" to totalPages,
                "currentPage" to page
        )
    }

    override suspend fun isTopic(cityId: String, name: String): Long {
        return topicRepository.countTopicsByName(cityId, name)
    }
}