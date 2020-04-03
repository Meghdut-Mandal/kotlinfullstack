package model.notes

import org.dizitart.no2.objects.Id

class Note(@Id val id: String, val name: String, val pages: List<NotePage>, val pageCount: Int)