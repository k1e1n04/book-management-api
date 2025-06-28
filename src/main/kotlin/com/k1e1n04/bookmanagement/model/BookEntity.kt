package com.k1e1n04.bookmanagement.model

import com.k1e1n04.bookmanagement.exception.DomainValidationException
import java.util.UUID

/**
 * 書籍エンティティ
 */
data class BookEntity(
    val id: UUID,
    val title: String,
    val price: Int,
    val authorIds: List<UUID>,
    val status: PublicationStatus,
) {
    init {
        require(title.isNotBlank() && title.length <= 255) {
            throw DomainValidationException(
                message = "書籍のタイトルは1文字以上、255文字以下でなければなりません。title: $title",
                userMessage = "書籍のタイトルは1文字以上、255文字以下でなければなりません。",
            )
        }
        require(price >= 0) {
            throw DomainValidationException(
                message = "書籍の価格は0円以上でなければなりません。price: $price",
                userMessage = "書籍の価格は0円以上でなければなりません。",
            )
        }
        require(price <= 1_000_000) {
            throw DomainValidationException(
                message = "書籍の価格は100万円以下でなければなりません。price: $price",
                userMessage = "書籍の価格は100万円以下でなければなりません。",
            )
        }
        require(authorIds.isNotEmpty()) {
            throw DomainValidationException(
                message = "書籍には少なくとも1人の著者が必要です。",
                userMessage = "書籍には少なくとも1人の著者が必要です。",
            )
        }
        require(authorIds.distinct().size == authorIds.size) {
            throw DomainValidationException(
                message = "書籍の著者IDは重複してはいけません。authorIds: ${authorIds.joinToString(", ")}",
                userMessage = "書籍の著者IDは重複してはいけません。",
            )
        }
    }

    companion object {
        /**
         * 書籍エンティティの新規作成するファクトリメソッド
         * @param title 書籍のタイトル
         * @param price 書籍の価格
         * @param authorIds 著者のIDリスト
         * @param status 書籍の公開ステータス
         * @return 書籍エンティティ
         */
        fun new(
            title: String,
            price: Int,
            authorIds: List<String>,
            status: PublicationStatus,
        ): BookEntity {
            val id = UUID.randomUUID()

            val authorUUIDs =
                try {
                    authorIds.map { UUID.fromString(it) }
                } catch (e: IllegalArgumentException) {
                    throw DomainValidationException(
                        message = "著者IDの形式が不正です。authorIds: ${authorIds.joinToString(", ")}",
                        userMessage = "著者IDの形式が不正です。",
                        cause = e,
                    )
                }
            return BookEntity(id, title, price, authorUUIDs, status)
        }
    }

    /**
     * 書籍情報の更新
     *
     * @param title 書籍のタイトル
     * @param price 書籍の価格
     * @param authorIds 著者のIDリスト
     * @param status 書籍の公開ステータス
     * @return 更新後の書籍エンティティ
     */
    fun update(
        title: String,
        price: Int,
        authorIds: List<String>,
        status: PublicationStatus,
    ): BookEntity {
        val authorUUIDs =
            try {
                authorIds.map { UUID.fromString(it) }
            } catch (e: IllegalArgumentException) {
                throw DomainValidationException(
                    message = "著者IDの形式が不正です。authorIds: ${authorIds.joinToString(", ")}",
                    userMessage = "著者IDの形式が不正です。",
                    cause = e,
                )
            }
        if (this.status == PublicationStatus.PUBLISHED && status == PublicationStatus.UNPUBLISHED) {
            throw DomainValidationException(
                message = "出版済みの書籍を非公開にしようとしました。id: $id",
                userMessage = "出版済みの書籍を非公開にすることはできません。",
            )
        }
        return this.copy(
            title = title,
            price = price,
            authorIds = authorUUIDs,
            status = status,
        )
    }
}
