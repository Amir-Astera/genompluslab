package innobiz.crm.genompluslab.feature.patient.domain.errors

class PatientCreateException(val iin: String): RuntimeException("Patient with iin: $iin not created!")