// router.java
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class router {

    /**
     * 执行进程并处理超时的通用函数
     *
     * @param command 要执行的命令和参数数组
     * @param timeoutSeconds 超时时间（秒）
     * @return 进程的输出字符串
     * @throws Exception 当进程执行失败或超时时抛出异常
     */
    private static String executeProcessWithTimeout(String[] command, int timeoutSeconds) throws Exception {
        // 1. 构建并执行命令
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        // 2. 读取输出
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        // 使用AtomicReference保证线程安全
        AtomicReference<StringBuilder> outputRef = new AtomicReference<>(new StringBuilder());

        Thread readThread = new Thread(() -> {
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    outputRef.get().append(line).append("\n");
                }
            } catch (Exception e) {
                // 忽略读取异常
            } finally {
                try {
                    reader.close();
                } catch (Exception e) {
                    // 忽略关闭异常
                }
            }
        });
        readThread.start();

        // 3. 设置超时并等待进程结束
        boolean finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);

        if (!finished) {
            process.destroy();
            readThread.interrupt();
            // 等待读取线程结束
            readThread.join(1000);
            throw new Exception("脚本执行超时（" + timeoutSeconds + "秒）");
        }

        // 4. 等待读取线程完成
        readThread.join(5000); // 最多等待5秒

        return outputRef.get().toString();
    }

    /**
     * 获取指定知乎用户的动态数据。
     * 调用外部脚本 `zhihu-user.mjs` 执行抓取。
     *
     * @param userId 知乎ID
     * @return String 脚本的完整原始输出
     * @throws Exception 脚本执行失败、超时30秒或路径错误时抛出
     */
    public static String zhihuUser(String userId) throws Exception {
        String[] command = {"node", "../data-scraper/mjs-new/zhihu-user.mjs", userId};
        return executeProcessWithTimeout(command, 30);
    }

    /**
     * 获取知乎全站热榜数据。
     * 调用外部脚本 `zhihu-hot.mjs` 执行抓取。
     *
     * @return String 脚本的完整原始输出
     * @throws Exception 脚本执行失败、超时30秒或路径错误时抛出
     */
    public static String zhihuHot() throws Exception {
        String[] command = {"node", "../data-scraper/mjs-new/zhihu-hot.mjs"};
        return executeProcessWithTimeout(command, 120);
    }

    /**
     * 获取微博热搜榜数据。
     * 调用外部脚本 `weibo-hot.mjs` 执行抓取。
     *
     * @return String 脚本的完整原始输出
     * @throws Exception 脚本执行失败、超时30秒或路径错误时抛出
     */
    public static String weiboHot() throws Exception {
        String[] command = {"node", "../data-scraper/mjs-new/weibo-hot.mjs"};
        return executeProcessWithTimeout(command, 120);
    }

    /**
     * 根据关键词搜索微博相关内容。
     * 调用外部脚本 `weibo-keyword.mjs` 执行抓取。
     *
     * @param keyword 要搜索的关键词
     * @return String 脚本的完整原始输出
     * @throws Exception 脚本执行失败、超时30秒或路径错误时抛出
     */
    public static String weiboKeyword(String keyword) throws Exception {
        String[] command = {"node", "../data-scraper/mjs-new/weibo-keyword.mjs", keyword};
        return executeProcessWithTimeout(command, 120);
    }

    /**
     * 获取指定微博用户的个人主页数据。
     * 调用外部脚本 `weibo-user.mjs` 执行抓取。
     *
     * @param userId 微博用户ID
     * @return String 脚本的完整原始输出
     * @throws Exception 脚本执行失败、超时30秒或路径错误时抛出
     */
    public static String weiboUser(String userId) throws Exception {
        String[] command = {"node", "../data-scraper/mjs-new/weibo-user.mjs", userId};
        return executeProcessWithTimeout(command, 120);
    }

    /**
     * 获取Twitter用户的时间线数据。
     * 调用外部脚本 `twitter-user.mjs` 执行抓取。
     *
     * @param username Twitter用户名
     * @return String 脚本的完整原始输出
     * @throws Exception 脚本执行失败、超时30秒或路径错误时抛出
     */
    public static String twitterUser(String username) throws Exception {
        String[] command = {"node", "../data-scraper/mjs-new/twitter-user.mjs", username};
        return executeProcessWithTimeout(command, 30);
    }

    /**
     * 根据关键词搜索Twitter相关内容。
     * 调用外部脚本 `twitter-keyword.mjs` 执行抓取。
     *
     * @param keyword 要搜索的关键词
     * @return String 脚本的完整原始输出
     * @throws Exception 脚本执行失败、超时30秒或路径错误时抛出
     */
    public static String twitterKeyword(String keyword) throws Exception {
        String[] command = {"node", "../data-scraper/mjs-new/twitter-keyword.mjs", keyword};
        return executeProcessWithTimeout(command, 30);
    }

    /**
     * 获取Twitter用户的时间线推文。
     * 调用外部脚本 `twitter-timeline.mjs` 执行抓取。
     *
     * @param username Twitter用户名
     * @return String 脚本的完整原始输出
     * @throws Exception 脚本执行失败、超时30秒或路径错误时抛出
     */
    public static String twitterTimeline(String username) throws Exception {
        String[] command = {"node", "../data-scraper/mjs-new/twitter-timeline.mjs", username};
        return executeProcessWithTimeout(command, 30);
    }

    /**
     * 测试函数
     */
    public static void main(String[] args) {
        try {
//            //测试知乎热榜
//            System.out.println("开始获取知乎热榜数据...");
//            String rawOutput = zhihuHot();
//            System.out.println("获取成功！");
//            System.out.println("原始输出:");
//            System.out.println(rawOutput);

//            //测试知乎用户
//            System.out.println("\n开始获取用户数据...");
//            String userOutput = zhihuUser("diygod");
//            System.out.println("获取成功！");
//            System.out.println(userOutput);

//            //测试微博热榜
//            System.out.println("\n开始获取微博热搜数据...");
//            String weiboHotOutput = weiboHot();
//            System.out.println("获取成功！");
//            System.out.println(weiboHotOutput);

//            //测试微博关键词搜索
//            System.out.println("\n开始搜索微博关键词...");
//            String keywordOutput = weiboKeyword("科技");
//            System.out.println("获取成功！");
//            System.out.println(keywordOutput);

//            //测试微博用户
//            System.out.println("\n开始获取微博用户数据...");
//            String weiboUserOutput = weiboUser("1195230310");
//            System.out.println("获取成功！");
//            System.out.println(weiboUserOutput);

//            // 测试Twitter用户
//            System.out.println("\n开始获取Twitter用户数据...");
//            String twitterUserOutput = twitterUser("_RSSHub");
//            System.out.println("获取成功！");
//            System.out.println(twitterUserOutput);

//            // 测试Twitter关键词搜索
//            System.out.println("\n开始搜索Twitter关键词...");
//            String twitterKeywordOutput = twitterKeyword("RSSHub");
//            System.out.println("获取成功！");
//            System.out.println(twitterKeywordOutput);

//            // 测试Twitter时间线
//            System.out.println("\n开始获取Twitter时间线...");
//            String twitterTimelineOutput = twitterTimeline("1650844643997646852");
//            System.out.println("获取成功！");
//            System.out.println(twitterTimelineOutput);

        } catch (Exception e) {
            System.err.println("错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
}