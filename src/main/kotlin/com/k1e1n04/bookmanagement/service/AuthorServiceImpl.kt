package com.k1e1n04.bookmanagement.service

import com.k1e1n04.bookmanagement.exception.NotFoundException
import com.k1e1n04.bookmanagement.model.AuthorEntity
import com.k1e1n04.bookmanagement.repository.AuthorRepository
import com.k1e1n04.bookmanagement.request.AuthorRegisterRequest
import com.k1e1n04.bookmanagement.request.AuthorUpdateRequest
import com.k1e1n04.bookmanagement.response.AuthorResponse
import java.time.format.DateTimeFormatter
import java.util.UUID
import org.springframework.stereotype.Service

/**
 * 著者に関するサービスの実装クラス
 */
@Service
class AuthorServiceImpl(
    private val authorRepository: AuthorRepository,
) : AuthorService {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    override fun getAllAuthors() = authorRepository.findAll().map(::toResponse)

    override fun registerAuthor(author: AuthorRegisterRequest) =
        AuthorEntity
            .new(
                name = author.name,
                dateOfBirth = author.dateOfBirth,
            ).let(authorRepository::save)
            .let(::toResponse)

    override fun    updateAuthor(id: String, author: AuthorUpdateRequest): AuthorResponse {
        val authorId =
            try {
                UUID.fromString(id)
            } catch (e: IllegalArgumentException) {
                throw NotFoundException(
                    userMessage = "指定された著者は存在しません",
                    message = "著者IDの形式が不正です: $id",
                    cause = e,
                )
            }

        val existingAuthor =
            authorRepository.findById(authorId)
                ?: throw NotFoundException(
                    userMessage = "指定された著者は存在しません",
                    message = "著者ID: $authorId は存在しません",
                )

        val updatedAuthor =
            existingAuthor.copy(
                name = author.name,
                dateOfBirth = author.dateOfBirth,
            )

        return authorRepository
            .update(updatedAuthor)
            .let(::toResponse)
    }

    /**
     * 著者エンティティをレスポンスオブジェクトに変換する
     *
     * @param author 著者エンティティ
     * @return 著者レスポンスオブジェクト
     */
    private fun toResponse(author: AuthorEntity): AuthorResponse =
        AuthorResponse(
            id = author.id.toString(),
            name = author.name,
            dateOfBirth = author.dateOfBirth.format(dateFormatter),
        )
}
