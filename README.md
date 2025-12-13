# App Open Confirmation

特定のアプリを開く前に確認ダイアログを表示するAndroidアプリ。

## 免責事項

このアプリはほぼ全て Claude Code（AIコーディングエージェント）によって作成されています。
動作の保証はなく、使用によって生じたいかなる損害についても責任を負いません。
自己責任でご使用ください。

## 機能

- インストール済みアプリの一覧から監視対象を選択
- 監視対象アプリの起動時に確認ダイアログを表示
- 「開く」で起動、「キャンセル」で中止
- 設定はローカルに保存

## 動作要件

- Android 14 (API 36) 以上

## セットアップ

1. APKをインストール
2. アプリを起動
3. 「Enable Service in Settings」から設定画面へ移動
4. 「App Open Confirmation」のアクセシビリティサービスを有効化
5. 「Select Apps to Monitor」から監視対象アプリを選択

## 技術仕様

- AccessibilityService による起動検知
- DataStore による設定永続化
- Jetpack Compose による UI

## ビルド

```
./gradlew assembleDebug
```

APK出力先: `app/build/outputs/apk/debug/app-debug.apk`

## ライセンス

MIT
