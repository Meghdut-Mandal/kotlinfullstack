package model

data class Batch(val clazz: Int, val section: String)

data class SubjectTaught(val batch: Batch, val subjectName: String, val subjectSlug: String)