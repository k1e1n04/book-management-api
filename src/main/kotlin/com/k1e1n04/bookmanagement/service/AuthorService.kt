package com.k1e1n04.bookmanagement.service

import com.k1e1n04.bookmanagement.request.AuthorRegisterRequest
import com.k1e1n04.bookmanagement.request.AuthorUpdateRequest
import com.k1e1n04.bookmanagement.response.AuthorResponse

/**
 * 著者に関するサービスインターフェース
 */
interface AuthorService {
    /**
     * すべての著者を取得する
     *
     * @return 著者のリスト
     */
    fun getAllAuthors(): List<AuthorResponse>

    /**
     * 著者を登録する
     *
     * @param author 登録する著者の情報
     * @return 登録された著者の情報
     */
    fun registerAuthor(author: AuthorRegisterRequest): AuthorResponse

    /**
     * 著者を更新する
     *
     * @param id 更新する著者のID
     * @param author 更新する著者の情報
     * @return 更新された著者の情報
     */
    fun updateAuthor(id: String, author: AuthorUpdateRequest): AuthorResponse
}
