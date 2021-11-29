package com.asp424.drawer.models

data class CommonModel
    (
    var id: String = "",
    var username: String = "",
    var fullname: String = "",
    var state: String = "",
    var photoUrl: String = "empty",
    var phone: String = "",
    var text: String = "",
    var type: String = "",
    var from: String = "",
    var who: String = "",
    var timeStamp:Any = "",
    var timeExitStamp:Any = "",
    var timeRegistrationStamp:Any = "",
    var fileUrl:String = "empty",
    var lastMessage:String = "",
    var lastMessageTime:String = "",
    var inPhoneName:String ="",
    var choose:Boolean = false,
    var read_status:String = "",
    var pathStorage:String = "",
    var counter:Int = 0,
    var writing:Int = 0



    ) {
    override fun equals(other: Any?): Boolean {
        return (other as CommonModel).id == id
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + username.hashCode()
        result = 31 * result + fullname.hashCode()
        result = 31 * result + state.hashCode()
        result = 31 * result + photoUrl.hashCode()
        result = 31 * result + phone.hashCode()
        result = 31 * result + text.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + from.hashCode()
        result = 31 * result + who.hashCode()
        result = 31 * result + timeStamp.hashCode()
        result = 31 * result + timeExitStamp.hashCode()
        result = 31 * result + timeRegistrationStamp.hashCode()
        result = 31 * result + fileUrl.hashCode()
        result = 31 * result + lastMessage.hashCode()
        result = 31 * result + lastMessageTime.hashCode()
        result = 31 * result + inPhoneName.hashCode()
        result = 31 * result + choose.hashCode()
        result = 31 * result + read_status.hashCode()
        result = 31 * result + pathStorage.hashCode()
        result = 31 * result + counter
        result = 31 * result + writing
        return result
    }

}
