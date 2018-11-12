package com.karl.yolosocialnetwork.model

class Message {
    var dateTime: String? = null
    var from: String? = null
    var contents: String? = null
    var type: String? = null

    constructor()
    constructor(dateTime: String, from: String, contents: String, type: String) {
        this.dateTime = dateTime
        this.from = from
        this.contents = contents
        this.type = type
    }
}