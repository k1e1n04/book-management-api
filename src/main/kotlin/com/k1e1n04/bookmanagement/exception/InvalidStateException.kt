package com.k1e1n04.bookmanagement.exception

/**
 * 不正な状態である場合にスローされる例外
 *
 * @param message エラーメッセージ
 * @param cause 原因となる例外
 */
class InvalidStateException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
