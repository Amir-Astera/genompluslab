package innobiz.crm.genompluslab.feature.patient.domain.errors

class UserPatientNotFoundException(val id: String): RuntimeException("Cannot found user $id with connected patient!")
