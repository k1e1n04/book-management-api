package com.k1e1n04.bookmanagement.response

/**
 * 著者情報のレスポンスデータクラス
 *
 * @param id 著者のID
 * @param name 著者の名前
 * @param dateOfBirth 著者の生年月日
 */
data class AuthorResponse(
    val id: String,
    val name: String,
    val dateOfBirth: String,
)
