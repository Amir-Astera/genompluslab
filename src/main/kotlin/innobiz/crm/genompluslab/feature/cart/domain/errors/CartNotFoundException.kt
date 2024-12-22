package innobiz.crm.genompluslab.feature.cart.domain.errors

class CartNotFoundException(val userId: String): RuntimeException("Cart with userId: $userId not found!")