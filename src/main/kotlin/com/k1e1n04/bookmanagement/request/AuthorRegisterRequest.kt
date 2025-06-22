package com.k1e1n04.bookmanagement.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Past
import jakarta.validation.constraints.Size
import java.time.LocalDate

/**
 * 著者登録リクエスト
 *
 * @property name 著者の名前
 * @property dateOfBirth 著者の生年月日
 */
data class AuthorRegisterRequest(
    @field:NotBlank(message = "名前は必須です。")
    @Size(max = 255, message = "名前は255文字以下でなければなりません。")
    val name: String,
    @field:Past(message = "生年月日は過去の日付である必要があります。")
    val dateOfBirth: LocalDate,
)
