package innobiz.crm.genompluslab.feature.patient.domain.errors

class PatientAlreadyExcitingException(iin: String): RuntimeException("Patient with iin: $iin already exciting!")