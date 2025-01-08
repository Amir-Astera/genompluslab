package innobiz.crm.genompluslab.feature.order.domain.errors

class OrderNotFoundException(val id: String): RuntimeException("Order with id: $id not found!")