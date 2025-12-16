# Release APK Signing Setup

GitHub Actions で署名済み Release APK をビルドするための設定手順。

## 概要

| 用語 | 説明 |
|------|------|
| **Keystore** | 複数の秘密鍵を格納するコンテナファイル（.keystore / .jks） |
| **KEYSTORE_PASSWORD** | Keystore ファイル自体を開くためのパスワード |
| **KEY_ALIAS** | Keystore 内の特定のキーを識別する名前 |
| **KEY_PASSWORD** | 特定のキー（alias）を使用するためのパスワード |

> 実務では KEYSTORE_PASSWORD と KEY_PASSWORD に同じ値を設定することが多い。

## 手順

### 1. Keystore の生成

```bash
keytool -genkey -v \
  -keystore release.keystore \
  -alias release \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000
```

プロンプトに従って情報を入力：
- Keystore password（KEYSTORE_PASSWORD になる）
- 名前、組織、国コードなど
- Key password（KEYSTORE_PASSWORD と同じでよい場合は Enter）

### 2. Keystore を Base64 エンコード

```bash
base64 -i release.keystore | pbcopy  # macOS: クリップボードにコピー
# または
base64 -i release.keystore > release.keystore.b64
```

### 3. GitHub Secrets の設定

リポジトリの **Settings** → **Secrets and variables** → **Actions** → **New repository secret** で以下を設定：

| Secret 名 | 値 |
|-----------|-----|
| `KEYSTORE_BASE64` | Base64 エンコードした Keystore の内容 |
| `KEYSTORE_PASSWORD` | Keystore のパスワード |
| `KEY_ALIAS` | キーのエイリアス（例: `release`） |
| `KEY_PASSWORD` | キーのパスワード |

### 4. 確認

push または workflow_dispatch で GitHub Actions を実行すると、署名済みの `app-release.apk` が生成される。

## 注意事項

- **Keystore ファイルは絶対にリポジトリにコミットしない**
- Keystore を紛失するとアプリのアップデートができなくなるため、安全な場所にバックアップを保管
- Secrets が未設定の場合は未署名の APK（`app-release-unsigned.apk`）が生成される

## トラブルシューティング

### 署名されていない APK が生成される

GitHub Secrets が正しく設定されているか確認：
1. Secret 名のスペルミス
2. Base64 エンコード時の改行の混入
3. パスワードの特殊文字のエスケープ問題

### Keystore の内容を確認したい

```bash
keytool -list -v -keystore release.keystore
```
