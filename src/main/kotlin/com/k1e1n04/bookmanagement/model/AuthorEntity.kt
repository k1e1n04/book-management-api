package com.k1e1n04.bookmanagement.model

import com.k1e1n04.bookmanagement.exception.DomainValidationException
import java.time.LocalDate
import java.util.UUID

/**
 * 著者エンティティ
 *
 * @property id 著者ID
 * @property name 著者名
 * @property dateOfBirth 生年月日
 */
data class AuthorEntity(
    val id: UUID,
    val name: String,
    val dateOfBirth: LocalDate,
) {

    init {
        require(name.isNotBlank() && name.length <= 255) {
            throw DomainValidationException(
                userMessage = "名前は1文字以上、255文字以下でなければなりません。",
                message = "著者の名前は1文字以上、255文字以下でなければなりません。name: $name",
            )
        }
        require(dateOfBirth.isBefore(LocalDate.now())) {
            throw DomainValidationException(
                userMessage = "生年月日は過去の日付である必要があります。",
                message = "著者の生年月日は過去の日付でなければなりません。dateOfBirth: $dateOfBirth",
            )
        }
    }


    companion object {
        /**
         * 著者エンティティの新規作成するファクトリメソッド
         * @param name 著者の名前
         * @param dateOfBirth 著者の生年月日
         * @return 著者エンティティ
         */
        fun new(
            name: String,
            dateOfBirth: LocalDate,
        ): AuthorEntity {
            val id = UUID.randomUUID()
            return AuthorEntity(
                id = id,
                name = name,
                dateOfBirth = dateOfBirth,
            )
        }
    }

    /**
     * 著者情報の更新
     *
     * @param name 著者の名前
     * @param dateOfBirth 著者の生年月日
     * @return 更新後の著者エンティティ
     */
    fun update(
        name: String,
        dateOfBirth: LocalDate,
    ) = copy(
        name = name,
        dateOfBirth = dateOfBirth,
    )
}
