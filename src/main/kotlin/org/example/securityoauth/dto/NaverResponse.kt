package org.example.securityoauth.dto

data class NaverResponse(private val attribute: MutableMap<String, Any>): OAuth2Response {

    val responseAttribute: Map<String, Any?> = attribute["response"] as? Map<String, Any?>
        ?: throw IllegalArgumentException("No response attribute found")

    override fun getProvider(): String {
        return "naver"
    }

    override fun getProviderId(): String {
        return attribute["id"].toString()
    }

    override fun getEmail(): String {
        return attribute["email"].toString()
    }

    override fun getName(): String {
        return attribute["name"].toString()
    }
}
