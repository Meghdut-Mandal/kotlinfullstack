package model.notes

import org.dizitart.no2.objects.Id

class Note(@Id val id: String, val subjectTaughtID: String, val name: String, val pageCount: Int)