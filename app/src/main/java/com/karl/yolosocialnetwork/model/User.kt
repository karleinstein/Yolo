package com.karl.yolosocialnetwork.model

class User {
    var description: String? = null
    var nameUser: String? = null
    var gender: String? = null
    var background: String? = null
    var avatar: String? = null
    var uid: String? = null
    var online: Long? = null

    constructor(description: String?,
                nameUser: String,
                gender: String?, uid: String) {
        this.description = description
        this.nameUser = nameUser
        this.gender = gender
        this.uid = uid

    }

    constructor(nameUser: String, gender: String?, uid: String, avatar: String, background: String) {
        this.nameUser = nameUser
        this.gender = gender
        this.uid = uid
        this.avatar = avatar
        this.background = background
    }

    constructor(backgroundImage: String?) {
        this.background = backgroundImage
    }

    constructor()

}