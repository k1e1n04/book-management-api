package com.k1e1n04.bookmanagement.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Past
import java.time.LocalDate

/**
 * 著者の更新リクエスト
 *
 * @param name 著者の名前
 * @param dateOfBirth 著者の生年月日
 */
data class AuthorUpdateRequest(
    @field:NotBlank(message = "名前は必須です。")
    val name: String,
    @field:Past(message = "生年月日は過去の日付である必要があります。")
    val dateOfBirth: LocalDate,
)
