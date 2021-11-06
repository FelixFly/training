package top.felixfly.training.jvm01;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;

/**
 * hello.xlass 加载器
 *
 * @author FelixFly <chenglinxu@yeah.com>
 * @date 2021/11/5
 */
public class HelloxClassLoader extends ClassLoader {

    public static void main(String[] args) throws Exception {
        // 加载类文件
        Class<?> hello = new HelloxClassLoader().loadClass("Hello");
        // 方法
        Method method = hello.getMethod("hello");
        method.invoke(hello.newInstance());
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        // 从这里面获取Hello.xlass文件内容
        try {
            // 获取资源文件
            Enumeration<URL> resources = this.getResources(name + ".xlass");
            // 没有获取到文件报错
            if(!resources.hasMoreElements()){
                throw new RuntimeException("未获取到对应的文件" + name);
            }
            // 只会拿到一个文件
            URL url = resources.nextElement();
            // java 9 有一定的加强
            try (FileInputStream fileInputStream = new FileInputStream(url.getFile());
                 BufferedInputStream inputStream = new BufferedInputStream(fileInputStream)) {
                byte[] bytes = new byte[128];
                int read = 0;
                // 所有的字节
                byte[] all = new byte[0];
                while ((read = inputStream.read(bytes)) != -1){
                    int length = all.length;
                    // 拷贝
                    all = Arrays.copyOf(all, length + read);
                    System.arraycopy(bytes, 0, all, length, read);
                }

                for (int i = 0; i < all.length; i++) {
                    all[i] = (byte) (255 - all[i]);
                }
                return defineClass(name, all, 0, all.length);
            }
        } catch (IOException e) {
            throw new RuntimeException("未获取到对应的文件" + name);
        }
    }

}
