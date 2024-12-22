package innobiz.crm.genompluslab.feature.analysis.domain.errors

class AnalysisNotFoundException(val id: String): RuntimeException("Analysis with id: $id not found! Try for another analysis!")