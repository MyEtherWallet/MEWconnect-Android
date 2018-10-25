package com.myetherwallet.mewconnect.content.data

import org.webrtc.SessionDescription

/**
 * Created by BArtWell on 26.07.2018.
 */
data class Offer(
        val type: String,
        val sdp: String
) : BaseMessage() {
    constructor(sessionDescription: SessionDescription) : this(sessionDescription.type.canonicalForm(), sessionDescription.description)

    fun toSessionDescription() = SessionDescription(SessionDescription.Type.fromCanonicalForm(type), sdp)
}