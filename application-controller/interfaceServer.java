import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

public class interfaceServer {
    public static void main(String[] args) throws Exception {
        // 1. 启动服务器
        HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);
        server.setExecutor(Executors.newFixedThreadPool(10));
        System.out.println("HTTP 服务器启动: http://localhost:8081");

        // 2. 设置路由
        server.createContext("/zhihu/hot", exchange -> handleRequest(exchange, "zhihuHot"));
        server.createContext("/weibo/hot", exchange -> handleRequest(exchange, "weiboHot"));

        // 需要参数的路由
        server.createContext("/zhihu/user", exchange -> handleParamRequest(exchange, "zhihuUser"));
        server.createContext("/weibo/keyword", exchange -> handleParamRequest(exchange, "weiboKeyword"));
        server.createContext("/weibo/user", exchange -> handleParamRequest(exchange, "weiboUser"));

        // Twitter 相关路由 - 需要参数
        server.createContext("/twitter/user", exchange -> handleParamRequest(exchange, "twitterUser"));
        server.createContext("/twitter/keyword", exchange -> handleParamRequest(exchange, "twitterKeyword"));
        server.createContext("/twitter/timeline", exchange -> handleParamRequest(exchange, "twitterTimeline"));

        // 3. 启动
        server.start();
    }

    // 处理不需要参数的路由
    private static void handleRequest(HttpExchange exchange, String method) throws IOException {
        try {
            // 记录请求
            System.out.println("收到请求: " + exchange.getRequestURI());

            String result = "";
            switch (method) {
                case "zhihuHot":
                    result = router.zhihuHot();
                    break;
                case "weiboHot":
                    result = router.weiboHot();
                    break;
            }
            sendResponse(exchange, 200, result);

        } catch (Exception e) {
            System.err.println("处理请求错误: " + e.getMessage());
            e.printStackTrace();
            sendResponse(exchange, 500, "服务器内部错误: " + e.getMessage());
        }
    }

    // 处理需要参数的路由
    private static void handleParamRequest(HttpExchange exchange, String method) throws IOException {
        try {
            // 记录请求
            System.out.println("收到请求: " + exchange.getRequestURI());

            // 解析参数
            String query = exchange.getRequestURI().getQuery();
            String param = "";

            if (query != null) {
                String[] pairs = query.split("&");
                for (String pair : pairs) {
                    int idx = pair.indexOf("=");
                    if (idx > 0) {
                        String key = URLDecoder.decode(pair.substring(0, idx), "UTF-8");
                        String value = URLDecoder.decode(pair.substring(idx + 1), "UTF-8");

                        // 根据方法确定参数名
                        if (method.startsWith("twitter")) {
                            // Twitter 路由的参数名
                            if (method.equals("twitterUser") || method.equals("twitterTimeline")) {
                                if (key.equals("username") || key.equals("userId") || key.equals("id")) {
                                    param = value;
                                    break;
                                }
                            } else if (method.equals("twitterKeyword")) {
                                if (key.equals("keyword") || key.equals("q") || key.equals("search")) {
                                    param = value;
                                    break;
                                }
                            }
                        } else {
                            // 其他路由的参数名
                            if (key.equals("userId") || key.equals("keyword")) {
                                param = value;
                                break;
                            }
                        }
                    }
                }
            }

            // 验证参数
            if (param.isEmpty()) {
                String errorMsg = "";
                if (method.equals("twitterUser") || method.equals("twitterTimeline")) {
                    errorMsg = "需要 username 参数，例如: /twitter/user?username=elonmusk";
                } else if (method.equals("twitterKeyword")) {
                    errorMsg = "需要 keyword 参数，例如: /twitter/keyword?keyword=technology";
                } else {
                    errorMsg = "需要参数，例如: /zhihu/user?userId=diygod";
                }
                sendResponse(exchange, 400, errorMsg);
                return;
            }

            String result = "";
            switch (method) {
                case "zhihuUser":
                    result = router.zhihuUser(param);
                    break;
                case "weiboKeyword":
                    result = router.weiboKeyword(param);
                    break;
                case "weiboUser":
                    result = router.weiboUser(param);
                    break;
                case "twitterUser":
                    result = router.twitterUser(param);
                    break;
                case "twitterKeyword":
                    result = router.twitterKeyword(param);
                    break;
                case "twitterTimeline":
                    result = router.twitterTimeline(param);
                    break;
            }
            sendResponse(exchange, 200, result);

        } catch (Exception e) {
            System.err.println("处理请求错误: " + e.getMessage());
            e.printStackTrace();
            sendResponse(exchange, 500, "服务器内部错误: " + e.getMessage());
        }
    }

    // 发送响应
    private static void sendResponse(HttpExchange exchange, int code, String text) throws IOException {
        // 设置响应头
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");

        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(code, response.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
        }

        exchange.close();
    }
}