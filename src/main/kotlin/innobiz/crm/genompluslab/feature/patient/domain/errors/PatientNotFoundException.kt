package innobiz.crm.genompluslab.feature.patient.domain.errors

class PatientNotFoundException(iin: String): RuntimeException("Patient with iin: $iin not found!")