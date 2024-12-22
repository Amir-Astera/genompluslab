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

interface TopicService {
    suspend fun save(topic: Topic): String
    suspend fun get(id: String): Topic
    suspend fun delete(id: String)
    suspend fun getAll(cityId: String): Collection<Topic>
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
        val topIds = analyticsTopicRepository.findTopPopularTopicsByCity(
                cityId,
                1.0,//salesWeight,
                0.2,//viewsWeight,
                0.3,//cartAddsWeight,
                3
        )
        return topIds.map {
            val topic = topicRepository.findById(it.topicId)?.toModel() ?: throw TopicNotFoundException(it.topicId)
            TopicWithScore(
                    topic,
                    it.topicPopularity
            )
        }
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
                           WHEN s.last_sale_date = CURRENT_DATE THEN 1.0
                           WHEN s.last_sale_date >= CURRENT_DATE - INTERVAL '7 days' THEN 0.8
                           WHEN s.last_sale_date >= CURRENT_DATE - INTERVAL '30 days' THEN 0.5
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
        val totalElements = analysisRepository.countPopularAnalysesByTopicAndCity(topic.id, cityId).toLong()
        val totalPages = (totalElements + size - 1) / size // округление вверх

        return mapOf(
                "content" to analyses,
                "totalElements" to totalElements,
                "totalPages" to totalPages.toInt(),
                "currentPage" to page
        )
//        val topics = topicRepository.findByNameContaining(name, page, size).toList()
//        if (topics.isEmpty()) return emptyList()
//
//        val topicId = topics.first().id
//        val analysisIds = topicAnalysisRepository.findAllByTopicId(topicId)?.toList() ?: throw TopicAnalysesNotFoundException()
//        return analysisIds.map { analysisRepository.findById(it.analysisId)!!.toModel() }
    }
    override suspend fun get(id: String): Topic =
        topicRepository.findById(id)?.toModel() ?: throw TopicNotFoundException(id)

    override suspend fun delete(id: String) = topicRepository.deleteById(id)

    override suspend fun getAll(cityId: String): Collection<Topic> {
        return topicRepository.findAll().map { it.toModel() }.toList()
    }

    override suspend fun isTopic(cityId: String, name: String): Long {
        return topicRepository.countTopicsByName(cityId, name)
    }
}