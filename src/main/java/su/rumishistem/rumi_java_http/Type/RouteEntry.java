package su.rumishistem.rumi_java_http.Type;

/**
 * ルーティングのエントリーポイント
 */
public interface RouteEntry {
	/**
	 * リクエストが来るとこれが実行されます。
	 * @param r リクエスト
	 * @return 応答(Nullを返すとHTTPRequest経由での応答が可能です)
	 */
	Response run(Request r) throws Exception;
}
