package innobiz.crm.genompluslab.feature.topic.domain.errors

class TopicNotFoundException(val id: String): RuntimeException("Topic with id $id not found exception. Try for another topic!")