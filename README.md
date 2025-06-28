# 書籍管理システムのバックエンド API

本プロジェクトは、Spring Boot + Kotlin + jOOQ + PostgreSQL を使用した書籍管理システムのバックエンド API です。

## 技術スタック

-   **言語**: Kotlin
-   **フレームワーク**: Spring Boot
-   **データベースアクセス**: jOOQ
-   **マイグレーション**: Flyway
-   **データベース**: PostgreSQL
-   **テスト**: JUnit 5, Mockito

## API 仕様

### 著者管理 API

#### 全著者取得

-   **パス**: `GET /api/authors`
-   **説明**: 登録されているすべての著者情報を取得する
-   **リクエストボディ**: なし
-   **レスポンス**:

```json
[
    {
        "id": "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
        "name": "著者名",
        "dateOfBirth": "1990-01-01"
    }
]
```

#### 著者登録

-   **パス**: `POST /api/authors`
-   **説明**: 新しい著者を登録する
-   **リクエストボディ**:

```json
{
    "name": "著者名",
    "dateOfBirth": "1990-01-01"
}
```

-   **バリデーション**:
    -   `name`: 必須、255 文字以下
    -   `dateOfBirth`: 必須、現在日付より過去
-   **レスポンス**: 201 Created

```json
{
    "id": "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
    "name": "著者名",
    "dateOfBirth": "1990-01-01"
}
```

#### 著者更新

-   **パス**: `PUT /api/authors/{id}`
-   **説明**: 指定した ID の著者情報を更新する
-   **リクエストボディ**:

```json
{
    "name": "更新後の著者名",
    "dateOfBirth": "1985-05-20"
}
```

-   **バリデーション**:
    -   `name`: 必須、255 文字以下
    -   `dateOfBirth`: 必須、現在日付より過去
-   **レスポンス**: 200 OK

```json
{
    "id": "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
    "name": "更新後の著者名",
    "dateOfBirth": "1985-05-20"
}
```

### 書籍管理 API

#### 全書籍取得

-   **パス**: `GET /api/books`
-   **説明**: 登録されているすべての書籍情報を取得する
-   **リクエストボディ**: なし
-   **レスポンス**:

```json
[
    {
        "id": "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
        "title": "書籍タイトル",
        "price": 1500,
        "authorIds": ["xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"],
        "status": "PUBLISHED"
    }
]
```

#### 著者に紐づく書籍取得

-   **パス**: `GET /api/books/author/{authorId}`
-   **説明**: 指定した著者 ID に紐づく書籍情報を取得する
-   **リクエストボディ**: なし
-   **レスポンス**:

```json
[
    {
        "id": "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
        "title": "著者の書籍",
        "price": 2000,
        "authorIds": ["xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"],
        "status": "UNPUBLISHED"
    }
]
```

#### 書籍登録

-   **パス**: `POST /api/books`
-   **説明**: 新しい書籍を登録する
-   **リクエストボディ**:

```json
{
    "title": "書籍タイトル",
    "price": 1000,
    "authorIds": ["xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"],
    "status": "UNPUBLISHED"
}
```

-   **バリデーション**:
    -   `title`: 必須
    -   `price`: 必須、0 以上
    -   `authorIds`: 必須、最低 1 人の著者
    -   `status`: `PUBLISHED` または `UNPUBLISHED`
-   **レスポンス**: 201 Created

```json
{
    "id": "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
    "title": "書籍タイトル",
    "price": 1000,
    "authorIds": ["xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"],
    "status": "UNPUBLISHED"
}
```

#### 書籍更新

-   **パス**: `PUT /api/books/{id}`
-   **説明**: 指定した ID の書籍情報を更新する
-   **リクエストボディ**:

```json
{
    "title": "更新後のタイトル",
    "price": 1200,
    "authorIds": ["xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"],
    "status": "PUBLISHED"
}
```

-   **バリデーション**:
    -   `title`: 必須
    -   `price`: 必須、0 以上
    -   `authorIds`: 必須、最低 1 人の著者
    -   `status`: `PUBLISHED` または `UNPUBLISHED`
    -   **制約**: 出版済み（PUBLISHED）の書籍を未出版（UNPUBLISHED）に変更することはできなし
-   **レスポンス**: 200 OK

```json
{
    "id": "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
    "title": "更新後のタイトル",
    "price": 1200,
    "authorIds": ["xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"],
    "status": "PUBLISHED"
}
```

## エラーレスポンス

### バリデーションエラー（400 Bad Request）

```json
{
    "errors": [
        {
            "field": "name",
            "message": "名前は必須です。"
        },
        {
            "field": "dateOfBirth",
            "message": "生年月日は過去の日付である必要があります。"
        }
    ]
}
```

### 業務ルールエラー（400 Bad Request）

```json
{
    "message": "出版済みの書籍を非公開にすることはできません。"
}
```

### リソース未検出エラー（404 Not Found）

```json
{
    "message": "指定された著者は存在しません。"
}
```

## 起動方法

### 前提条件

-   JDK 21
-   Docker（PostgreSQL 用）

### 手順

1. アプリケーション起動:

```bash
./gradlew bootRun
```

## テスト実行

```bash
# 全テスト実行
./gradlew test

# 特定のテストクラスのみ実行
./gradlew test --tests="AuthorServiceImplTest"
```
