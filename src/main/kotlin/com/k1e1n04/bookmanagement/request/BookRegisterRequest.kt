package com.k1e1n04.bookmanagement.request

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

/**
 * 書籍登録リクエスト
 *
 * @property title 書籍のタイトル
 * @property price 書籍の価格
 * @property status 出版状況
 * @property authorIds 著者のIDリスト
 */
data class BookRegisterRequest(
    @field:NotBlank(message = "タイトルは必須です。")
    val title: String,
    @field:NotNull(message = "価格は必須です。")
    @field:Min(value = 0, message = "価格は0以上である必要があります。")
    val price: Int,
    val status: PublicationStatusRequest,
    @field:NotEmpty(message = "著者は最低1人必要です。")
    val authorIds: List<String>,
)
