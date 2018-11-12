package com.karl.yolosocialnetwork.model

class Comment {
    var content: String? = null
    var timeStamp: String? = null
    var userName: String? = null
    var userUid: String? = null
    var avatar: String? = null

    constructor(content: String, timeStamp: String, userName: String, userUid: String, avatar: String) {
        this.content = content
        this.timeStamp = timeStamp
        this.userName = userName
        this.userUid = userUid
        this.avatar = avatar

    }

    constructor() {}
}
