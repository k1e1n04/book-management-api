package com.k1e1n04.bookmanagement.request

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty

/**
 * 書籍更新リクエスト
 *
 * @param title 書籍タイトル（必須）
 * @param price 価格（0以上）
 * @param authorIds 著者IDリスト（1人以上必須）
 * @param status 出版状況（必須）
 */
data class BookUpdateRequest(
    @field:NotBlank(message = "タイトルは必須です。")
    val title: String,
    @field:Min(value = 0, message = "価格は0以上で入力してください。")
    val price: Int,
    @field:NotEmpty(message = "著者は1人以上指定してください。")
    val authorIds: List<String>,
    val status: PublicationStatusRequest,
)
