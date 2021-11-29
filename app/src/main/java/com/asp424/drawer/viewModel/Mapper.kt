package com.asp424.drawer.viewModel

import com.asp424.drawer.database.*
import com.asp424.drawer.models.CommonModel
import com.asp424.drawer.utilites.TYPE_CHAT
import com.asp424.drawer.utilites.TYPE_GROUP
import com.asp424.drawer.utilites.cipherDecrypt

open class Mapper(ViewModel: MainViewModel) {
    private val mMainListViewModel = ViewModel
    fun getDataForChat(
        id: String,
        newModel: CommonModel,
        newModel1: CommonModel,
        tempList: List<CommonModel>,
        statusModel: CommonModel,
    ) {
        newModel.timeStamp =
            statusModel.timeExitStamp
        if (tempList.isNotEmpty()) {
            newModel.who = tempList[0].from
            newModel.lastMessageTime =
                tempList[0].timeStamp.toString()
            val hui: String = tempList[0].id
            newModel.from = hui
            newModel.counter =
                tempList[0].counter
        }
        if (tempList.isEmpty()) newModel.lastMessage =
            "Сообщений пока нет"
        else {
            if (tempList[0].type == CHILD_TEXT) {
                if (tempList[0].text.startsWith(
                        "Я создал ключ, давай общаться!") ||
                    tempList[0].text.startsWith(
                        "Я ввёл ключ, можем общаться!")
                ) {
                    newModel.lastMessage =
                        tempList[0].text
                } else {

                    if (existFile(id)) {
                        val f =
                            tempList[0].text.cipherDecrypt(
                                readTXT(id)
                            )
                        if (f == null) newModel.lastMessage =
                            "*****"
                        else newModel.lastMessage =
                            f.toString()
                    }
                }
            } else {
                if (tempList[0].type == CHILD_IMAGE) newModel.lastMessage =
                    "Фотография"
                else {
                    if (tempList[0].type == CHILD_VOICE) newModel.lastMessage =
                        "Голосовое сообщение"
                    else newModel.lastMessage =
                        "Файл"
                }
            }
        }
        if (newModel.fullname.isEmpty()) newModel.fullname =
            newModel1.fullname
        if (newModel.fullname.isEmpty()) newModel.fullname =
            newModel.phone
        newModel.type = TYPE_CHAT
        mMainListViewModel._modelItem.value =
            newModel
    }

    fun getDataForGroup(
        newModel: CommonModel,
        tempList: List<CommonModel>,
        statusModel: CommonModel,
    ) {
        newModel.timeStamp = statusModel.timeExitStamp
        if (tempList.isEmpty()) newModel.lastMessage =
            "Сообщений пока нет"
        else {
            if (tempList[0].type == CHILD_TEXT) {
                newModel.lastMessage =
                    tempList[0].username + ": " + tempList[0].text
            } else {
                if (tempList[0].type == CHILD_IMAGE) newModel.lastMessage =
                    tempList[0].username + ": " + "Фотография"
                else {
                    if (tempList[0].type == CHILD_VOICE) newModel.lastMessage =
                        tempList[0].username + ": " + "Голосовое сообщение"
                    else newModel.lastMessage =
                        tempList[0].username + ": " + "Файл"
                    if (tempList[0].type == "many") newModel.lastMessage =
                        tempList[0].username + ": " + "Фотография"
                }
            }
        }
        newModel.type = TYPE_GROUP
        mMainListViewModel._modelItem.value =
            newModel
    }

}