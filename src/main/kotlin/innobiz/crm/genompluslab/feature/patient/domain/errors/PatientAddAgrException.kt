package innobiz.crm.genompluslab.feature.patient.domain.errors

class PatientAddAgrException(val id: String): RuntimeException("Cannot add agr for patient with id: $id")