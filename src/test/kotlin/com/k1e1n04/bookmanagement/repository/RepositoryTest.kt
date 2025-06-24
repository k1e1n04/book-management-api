package com.k1e1n04.bookmanagement.repository

import com.k1e1n04.bookmanagement.config.TestcontainersConfiguration
import org.springframework.boot.test.autoconfigure.jooq.JooqTest
import org.springframework.context.annotation.Import
import org.springframework.transaction.annotation.Transactional

/**
 * リポジトリテスト用のアノテーション
 *
 *  - DSLContextなどのJOOQ関連のBeanが自動で設定される
 *  - Testcontainersを使用してPostgreSQLのテスト環境が構築される
 *  - トランザクション管理が有効になり、テスト後にロールバックされる
 */
@JooqTest
@Import(TestcontainersConfiguration::class)
@Transactional
annotation class RepositoryTest
