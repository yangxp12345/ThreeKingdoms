package org.yang.springboot.util;


import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

@Slf4j
public class IOUtil {
    /**
     * 读取文件
     *
     * @param path 文件路径
     * @return 文件内容
     * @throws Exception 异常提示
     */
    public static String readInputStream(String path) throws Exception {
        return readInputStream(new File(path));
    }

    /**
     * 读取文件
     *
     * @param file 文件对象
     * @return 文件内容
     * @throws Exception 异常信息
     */
    public static String readInputStream(File file) throws Exception {
        if (!file.exists()) throw new Exception(String.format("文件[%s]不存在", file.getAbsolutePath()));
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            return readInputStream(fileInputStream);
        } finally {
            close(fileInputStream);
        }
    }

    /**
     * 读取流内容
     *
     * @param resourceAsStream 流对象
     * @return 内容
     * @throws IOException 异常信息
     */
    public static String readInputStream(InputStream resourceAsStream) throws IOException {
        InputStreamReader read = null;
        BufferedReader bufferedReader = null;
        StringBuilder sbr = new StringBuilder();
        try {
            read = new InputStreamReader(resourceAsStream, StandardCharsets.UTF_8);//考虑到编码格式
            bufferedReader = new BufferedReader(read);
            String lineTxt;
            while ((lineTxt = bufferedReader.readLine()) != null) {//读取一行
                sbr.append(lineTxt).append("\n");
            }
            if (sbr.length() != 0) sbr.setLength(sbr.length() - 1);
            return sbr.toString();
        } finally {
            close(read);
            close(bufferedReader);
        }
    }


    /**
     * 读取io流写入新的io流
     *
     * @param inputStream  输入流对象
     * @param outputStream 输出文件名称
     * @throws IOException 异常信息
     */
    public static void readWriter(InputStream inputStream, OutputStream outputStream) throws IOException {
        readWriter(inputStream, outputStream, 2457600);
    }

    /**
     * 读取输入流内容写入输出流对象
     *
     * @param inputStream  输入流
     * @param outputStream 输出流
     * @param cache        缓存大小
     * @throws IOException 异常信息
     */
    public static void readWriter(InputStream inputStream, OutputStream outputStream, int cache) throws IOException {
        byte[] buffer = new byte[cache];
        int bytesRead;
        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;
        try {
            bos = new BufferedOutputStream(outputStream, cache);
            bis = new BufferedInputStream(inputStream, cache);
            while ((bytesRead = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
        } finally {
            close(bos);
            close(bis);
        }
    }

    /**
     * 读取字节文件
     *
     * @param source 文件路径
     * @return 文件字节数组内容
     * @throws IOException 异常提示
     */
    public static byte[] readBytes(String source) throws IOException {
        InputStream fis = null;
        BufferedInputStream bis = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        int length = 512;//读取缓冲大小
        byte[] buffer = new byte[length]; //缓冲区字节数组
        File sourceFile = new File(source);
        try {
            fis = Files.newInputStream(sourceFile.toPath());
            bis = new BufferedInputStream(fis, length);
            byteArrayOutputStream = new ByteArrayOutputStream();//字节缓冲流数组
            int readSize; // 记录每次实际读取字节数
            while ((readSize = bis.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, readSize);
            }
            return byteArrayOutputStream.toByteArray();//结果内容  大文件读取注意内存溢出
        } finally {
            close(byteArrayOutputStream);
            close(bis);
            close(fis);
        }
    }

    /**
     * 读取写入文件
     *
     * @param bytes    字节流
     * @param fileName 保存位置
     * @param append   是否追加内容
     */
    public static void writerFile(byte[] bytes, String fileName, boolean append) {

        if (bytes == null) return;
        log.info("写入文件 {}", fileName);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(fileName, append);
            fileOutputStream.write(bytes);
            fileOutputStream.flush();
        } catch (IOException e) {
            log.error("写入文件异常", e);
        } finally {
            close(fileOutputStream);
        }
    }

    /**
     * 读取写入文件
     *
     * @param source   源文件
     * @param saveFile 写入文件
     */
    public static void writerCsvFile(File source, File saveFile) {
        writerCsvFile(source, saveFile, true);
    }

    public static void writerCsvFile(File source, File saveFile, boolean append) {
        log.info("写入文件 {}", saveFile.getAbsolutePath());
        FileOutputStream fileOutputStream = null;
        InputStream is = null;
        InputStreamReader read = null;
        BufferedReader bufferedReader = null;
        try {
            fileOutputStream = new FileOutputStream(saveFile, append);
            is = Files.newInputStream(source.toPath());
            read = new InputStreamReader(is, StandardCharsets.UTF_8);//考虑到编码格式
            bufferedReader = new BufferedReader(read);
            String lineTxt;
            while ((lineTxt = bufferedReader.readLine()) != null) {//读取一行
                fileOutputStream.write((lineTxt + "\n").getBytes(StandardCharsets.UTF_8));
            }
            fileOutputStream.flush();
        } catch (IOException e) {
            log.error("写入文件异常", e);
        } finally {

            close(bufferedReader);
            close(read);
            close(is);
            close(fileOutputStream);
        }
    }

    /**
     * 将内容写入文件
     *
     * @param data     文本内容
     * @param fileName 文件名称
     */
    public static void writerFile(String data, String fileName) {
        byte[] bytes;
        bytes = data.getBytes(StandardCharsets.UTF_8);
        writerFile(bytes, fileName, false);
    }

    /**
     * 将内容写入文件
     *
     * @param bytes    字节数组内容
     * @param fileName 文件名称
     */
    public static void writerFile(byte[] bytes, String fileName) {
        writerFile(bytes, fileName, false);
    }

    /**
     * 移动文件
     *
     * @param oldLocalFile 旧文件
     * @param newLocalFile 新文件
     * @return 是否移动成功
     */
    public static boolean mvFile(String oldLocalFile, String newLocalFile) {
        return mvFile(new File(oldLocalFile), new File(newLocalFile));
    }

    /**
     * 移动文件
     *
     * @param oldLocalFile 旧文件
     * @param newLocalFile 新文件
     * @return 是否成功
     */
    public static boolean mvFile(File oldLocalFile, String newLocalFile) {
        return mvFile(oldLocalFile, new File(newLocalFile));
    }

    /**
     * 移动文件
     *
     * @param oldLocalFile 旧文件
     * @param newLocalFile 新文件
     * @return
     */
    public static boolean mvFile(String oldLocalFile, File newLocalFile) {
        return mvFile(new File(oldLocalFile), newLocalFile);
    }

    /**
     * 移动文件
     *
     * @param oldLocalFile 旧文件
     * @param newLocalFile 新文件
     * @return 是否成功
     */
    public static boolean mvFile(File oldLocalFile, File newLocalFile) {
        if (oldLocalFile.exists() && !newLocalFile.exists()) return oldLocalFile.renameTo(newLocalFile);
        return false;
    }

    public static void copyDirectory(String sourceDir, String targetDir) throws IOException {
        if ((!new File(sourceDir).exists()) || new File(targetDir).exists())
            throw new IOException("文件不存在或者目标目录已存在");
        Path sourceDirectory = Paths.get(sourceDir);
        Path targetDirectory = Paths.get(targetDir);
        Files.walkFileTree(sourceDirectory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path targetFile = targetDirectory.resolve(sourceDirectory.relativize(file));
                Files.copy(file, targetFile);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path targetDir = targetDirectory.resolve(sourceDirectory.relativize(dir));
                Files.createDirectories(targetDir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * 删除文件
     *
     * @param filePath 文件
     * @return 是否成功
     */
    public static boolean delete(String filePath) {
        if (filePath == null) return false;
        return delete(new File(filePath));
    }

    /**
     * 删除文件
     *
     * @param file 文件
     * @return 是否成功
     */
    public static boolean delete(File file) {
        if (file != null && file.exists()) return file.delete();
        return false;
    }

    /**
     * 删除目录
     *
     * @param directory 文件
     */
    public static void deleteDirectory(File directory) {
        if (!directory.exists()) return;
        if (directory.isFile()) {
            directory.delete();
        } else {
            File[] files = directory.listFiles();//删除目录
            if (files != null) {
                for (File file : files) {
                    deleteDirectory(file);
                }
            }
            directory.delete();
        }
    }

    public static boolean copyFile(String oldFileName, String newFileName) {
        try {
            Path sourcePath = Paths.get(oldFileName);
            Path targetPath = Paths.get(newFileName);
            // 拷贝文件，如果目标文件存在则替换
            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (Exception e) {
            log.error("拷贝文件[{} -> {}]失败", oldFileName, newFileName, e);
            return false;
        }
    }


    private static void close(Closeable obj) {
        if (obj != null) {
            try {
                obj.close();
            } catch (IOException e) {
                log.error("关闭资源[{}]异常", obj.getClass().getName(), e);
            }
        }

    }

    public static void main(String[] args) {
        String filePath = "C:\\Users\\admin\\IdeaProjects\\creativity/data/2024-11-17.zip";
        boolean delete = IOUtil.delete(filePath);
        log.info("删除临时文件路径: [{}], 状态: [{}]", filePath, delete);
    }

}