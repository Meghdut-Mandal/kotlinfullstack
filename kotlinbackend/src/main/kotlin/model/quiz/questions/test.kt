package model.quiz.questions

import com.google.gson.Gson


fun main() {
    val data = readLine()
    val fg = Gson().fromJson<Questions_List_Response>(data, Questions_List_Response::class.java)

}