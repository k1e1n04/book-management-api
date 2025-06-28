# コーディングテストについて

クオカードでは、ソフトウェアエンジニアの採用プロセスの一環としてコーディングテストを実施しています。候補者の皆様には貴重な時間を割いて取り組んでいただくことになるため、十分な準備をしていただけるようにテストの内容と評価の観点を事前に公開することにしました。テストの目的を損なうことなく、候補者が必要とするスキルを身につけられる機会を提供したいと考えています。

---

## コーディングテストの内容

現時点でのコーディングテストは、**書籍管理システムのバックエンド API の構築**をお願いしています。具体的には、以下の仕様で実装していただきます。

- 期限: 2 週間
- Web API の作成のみ（フロントエンド実装は不要）
- 可能な範囲で単体テストも作成
- 完成したプロジェクトは GitHub にアップロードし、プロジェクトの URL をご送付ください（リポジトリは「public」で作成）

### 技術要件

- **言語:** Kotlin
- **フレームワーク:** Spring Boot、jOOQ

### 必要な機能

- 書籍と著者の情報を RDB に登録・更新できる機能
- 著者に紐づく本を取得できる機能

#### 書籍の属性

- タイトル
- 価格（0 以上であること）
- 著者（最低 1 人の著者を持つ。複数の著者を持つことが可能）
- 出版状況（未出版、出版済み。出版済みステータスのものを未出版には変更できない）

#### 著者の属性

- 名前
- 生年月日（現在の日付よりも過去であること）
- 著者も複数の書籍を執筆できる

> **補足:** 生成 AI の利用可

---

## 進め方の例

進め方の例を記載します。今回は Spring initializr や追加プラグインを利用していますが、こちらを利用しない形でも上記の技術スタック・仕様通り構築頂ければ問題ありません。

### 環境構築

業務上、新しいツールや技術の導入があり得るため、ドキュメントを参照しながら必要な環境構築が行えるかを確認します。

- 例: [Spring initializr](https://start.spring.io/) を使い以下の設定でプロジェクトを生成
  - Project: Gradle - Groovy
  - 言語: Kotlin
  - 追加プラグイン: JOOQ Access Layer, Flyway Migration, PostgreSQL Driver, Docker Compose Support
  - Java: 21 or 17
- SDKMAN や asdf 等を利用し、上記で指定した JDK をインストール
- IDE は IntelliJ IDEA Community 版（業務では Ultimate 版を使用）
- Spring initializr で生成したプロジェクトを IntelliJ IDEA にインポートし、JDK、Gradle の設定を行い開始

### 実装

- 必要なコントローラーやクラスを追加し、機能を実装
- データベースの構築には Flyway を使用し、その後 jOOQ でコードを自動生成して利用
- jOOQ でコードを自動生成する為に、以下を参照し設定を追加
  - [jOOQ Code Generation (Gradle)](https://www.jooq.org/doc/3.19/manual/code-generation/codegen-gradle/)
  - [jOOQ Code Generation Configuration](https://www.jooq.org/doc/3.19/manual/code-generation/codegen-configuration/)

---

## 評価の観点

評価は主に以下の観点で行います。

- 指定された技術スタックの適用
- フレームワークやライブラリの適切な利用
- 実行可能性
- 仕様に沿った動作
- 変数名やクラス名、関数名が実態を明確に反映しているか
- Null 安全性
- コードフォーマットの整合性
- 変数に再代入しないなど、ベストプラクティスの遵守
- オーバーエンジニアリングしていないか
- 適切な単体テストが作成されているか

---

## ディレクトリ構成

```
src
└── main
    ├── kotlin
    │   └── com
    │       └── k1e1n04
    │           └── bookmanagement
    │               ├── controller
    │               ├── exception
    │               ├── model
    │               ├── repository
    │               ├── service
    │               └── BookManagementApplication.kt
    └── resources
        ├── application.yml
        └── db
            └── migration
```

## 注意

- KotlinやSpring Bootのベストプラクティスに従って実装してください。
- オーバーエンジニアリングは避け、シンプルで明確なコードを心がけてください。


## メモ
- InvalidStateExceptionのテスト
- ロケール
- 前後のスペースのとり扱い
- enumの扱い
