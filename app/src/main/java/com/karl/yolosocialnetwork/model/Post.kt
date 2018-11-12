package com.karl.yolosocialnetwork.model

import java.util.*

class Post {
    lateinit var idPost: String
    var title: String? = null
    var content: String? = null
    var userUid: String? = null
    var image: String? = null
    var timeStamp: String? = null
    var video: String? = null

    constructor(idPost: String, title: String,
                content: String?, userUid: String, image: String?, timeStamp: String, video: String?) {
        this.idPost = idPost
        this.image = image
        this.title = title
        this.content = content
        this.userUid = userUid
        this.timeStamp = timeStamp
        this.video = video
    }

    constructor(idPost: String, title: String, userUid: String, image: String, timeStamp: String) {
        this.idPost = idPost
        this.image = image
        this.title = title
        this.userUid = userUid
        this.timeStamp = timeStamp
    }

    constructor() {}
}
