package com.k1e1n04.bookmanagement.response

/**
 * 書籍情報のレスポンスを表すクラス
 *
 * @property id 書籍のID
 * @property title 書籍のタイトル
 * @property price 書籍の価格
 * @property authorIds 著者のIDリスト
 * @property status 書籍の出版状況
 */
data class BookResponse(
    val id: String,
    val title: String,
    val price: Int,
    val authorIds: List<String>,
    val status: String,
)
