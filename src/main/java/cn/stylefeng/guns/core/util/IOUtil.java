package cn.stylefeng.guns.core.util;

import org.apache.commons.codec.binary.Base64;
//import org.apache.commons.io.IOUtils;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.Charset;

public class IOUtil {
    private static final byte[] EMPTY_BYTES = new byte[0];
    private static final Logger LOGGER = LoggerFactory.getLogger(IOUtil.class);

    private IOUtil() {
    }

    public static void close(Closeable... closeableAry) {
        for (Closeable closeable : closeableAry) {
            close(closeable);
        }

    }

    public static void close(Closeable closeable) {
        if (null != closeable) {
            try {
                closeable.close();
            } catch (IOException var2) {
                LOGGER.error("Close closeable faild: " + closeable.getClass(), var2);
            }
        }

    }

    private static byte[] base64StringToBytes(String base64String) {
        return StringUtils.isNotEmpty(base64String) ? Base64.decodeBase64(base64String) : EMPTY_BYTES;
    }

    public static byte[] fileToBytes(File file) {
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(file);
            return streamToBytes(fis);
        } catch (Exception var6) {
            LOGGER.error("read bytes failed", var6);
        } finally {
            close(fis);
        }

        return null;
    }

    private static byte[] streamToBytes(InputStream is) {
        return streamToBytes(is, false);
    }

    public static byte[] streamToBytes(InputStream is, boolean closeInputStream) {
        ByteArrayOutputStream baos = null;

        try {
            baos = new ByteArrayOutputStream();
            byte[] buff = new byte[4096];

            int readedLen;
            while(-1 != (readedLen = is.read(buff))) {
                baos.write(buff, 0, readedLen);
            }

            return baos.toByteArray();
        } catch (Exception var10) {
            LOGGER.error("read bytes failed", var10);
        } finally {
            close(baos);
            if (closeInputStream) {
                close(is);
            }

        }

        return null;
    }

    private static String bytesToBase64String(byte[] bytes) {
        return null != bytes ? Base64.encodeBase64String(bytes) : "";
    }

    public static String fileToBase64String(File file) {
        byte[] bytes = fileToBytes(file);
        return bytesToBase64String(bytes);
    }

    public static String getFileHeader(File file) {
        if (null != file && 0L != file.length() && file.exists()) {
            FileInputStream ips = null;

            String var3;
            try {
                ips = new FileInputStream(file);
                return getFileHeader(ips);
            } catch (FileNotFoundException var7) {
                var7.printStackTrace();
                var3 = "";
            } finally {
                close(ips);
            }

            return var3;
        } else {
            return "";
        }
    }

    private static String getFileHeader(InputStream ips) {
        try {
            byte[] b = new byte[10];
            ips.read(b, 0, b.length);
            return bytesToHexString(b);
        } catch (IOException var2) {
            var2.printStackTrace();
            return null;
        }
    }

    private static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src != null && src.length > 0) {
            for (byte aSrc : src) {
                int v = aSrc & 255;
                String hv = Integer.toHexString(v);
                if (hv.length() < 2) {
                    stringBuilder.append(0);
                }

                stringBuilder.append(hv);
            }

            return stringBuilder.toString();
        } else {
            return null;
        }
    }

//    public static String getFileType(File file) {
//        try {
//            return getFileType((InputStream)(new FileInputStream(file)));
//        } catch (FileNotFoundException var2) {
//            var2.printStackTrace();
//            return "";
//        }
//    }

//    public static String getFileType(InputStream ips) {
//        String fileHeader = getFileHeader(ips);
//        return null == fileHeader ? "" : FileType.getByHeader(fileHeader);
//    }

    public static String streamToBase64String(InputStream is) {
        return streamToBase64String(is, false);
    }

    private static String streamToBase64String(InputStream is, boolean closeInputStream) {
        byte[] bytes = streamToBytes(is, closeInputStream);
        return null != bytes ? Base64.encodeBase64String(bytes) : "";
    }

    private static boolean bytesToFile(byte[] bytes, File file) {
        if (null != bytes && null != file) {
            ByteArrayInputStream bais = null;

            boolean var3;
            try {
                bais = new ByteArrayInputStream(bytes);
                var3 = streamToFile(bais, file);
            } catch (Exception var7) {
                LOGGER.error("write bytes failed", var7);
                return false;
            } finally {
                close(bais);
            }

            return var3;
        } else {
            return false;
        }
    }

    private static boolean streamToFile(InputStream is, File file) {
        return streamToFile(is, file, false);
    }

    public static boolean streamToFile(InputStream is, File file, boolean closeInputStream) {
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(file);
            byte[] buff = new byte[4096];

            int readedLen;
            while(-1 != (readedLen = is.read(buff))) {
                fos.write(buff, 0, readedLen);
            }

            fos.flush();
            return true;
        } catch (Exception var10) {
            LOGGER.error("write bytes failed", var10);
        } finally {
            close(fos);
            if (closeInputStream) {
                close(is);
            }

        }

        return false;
    }

    public static boolean base64StringToFile(String base64String, File file) {
        if (null != base64String) {
            byte[] bytes = base64StringToBytes(base64String);
            return bytesToFile(bytes, file);
        } else {
            return false;
        }
    }

    public static InputStream bytesToStream(byte[] bytes) {
        return new ByteArrayInputStream(bytes);
    }

    public static InputStream base64StringToStream(String base64String) {
        if (null != base64String) {
            byte[] bytes = base64StringToBytes(base64String);
            return new ByteArrayInputStream(bytes);
        } else {
            return null;
        }
    }

    public static InputStream fileToStream(File file) throws FileNotFoundException {
        return new FileInputStream(file);
    }

    public static String fileToString(File file, String charset) {
        return fileToString(file, Charset.forName(charset));
    }

    private static String fileToString(File file, Charset charset) {
        byte[] bytes = fileToBytes(file);
        return null != bytes ? new String(bytes, charset) : "";
    }

//    public static boolean ensureDir(File dir) {
//        if (!dir.exists()) {
//            if (!EnvironmentUtils.isOnWindows()) {
//                LOGGER.error("The file : " + dir.getAbsolutePath() + " is not exists and can't create");
//                return false;
//            }
//
//            dir.mkdir();
//        }
//
//        return true;
//    }
//
//    public static String ensureDir(String path, String outPath, String inPath) {
//        if (path.endsWith(outPath)) {
//            path = path.substring(0, path.length() - outPath.length());
//        }
//
//        path = path + inPath;
//        ensureDir(new File(path));
//        return StringUtil.processPath(path);
//    }

    public static void delete(File file) {
        if (file.exists()) {
            file.delete();
        }

    }

    public static void copy(InputStream ips, OutputStream fos, boolean flushFlag) {
        try {
            IOUtils.copy(ips, fos);
            if (flushFlag) {
                fos.flush();
            }
        } catch (IOException var4) {
            var4.printStackTrace();
        }

    }

    public static Integer streamLength(InputStream ips) {
        if(ips == null){
            return 0;
        }
        byte[] bytes = streamToBytes(ips);
        if(bytes == null){
            return 0;
        }
        return bytes.length;
    }

    private static File[] listFile(File pathFile, FileFilter fileFilter) {
        return pathFile.listFiles(fileFilter);
    }

    public static File findFile(File pathFile, FileFilter fileFilter) {
        File[] fileAry = listFile(pathFile, fileFilter);
        if (fileAry.length == 0) {
            return null;
        } else if (fileAry.length == 1) {
            return fileAry[0];
        } else {
            LOGGER.error("the file in this filter is not unique.get first");
            return fileAry[0];
        }
    }

    private static void flush(Flushable flush) {
        if (null != flush) {
            try {
                flush.flush();
            } catch (IOException var2) {
                LOGGER.error("flush error ", var2);
            }
        }

    }

    public static void flush(Flushable... flushAry) {
        if (null != flushAry && 0 == flushAry.length) {

            for (Flushable flushable : flushAry) {
                flush(flushable);
            }
        }

    }

//    public static InputStream reset(InputStream ips) {
//        try {
//            ips.reset();
//            mark(ips);
//        } catch (Exception var2) {
//            LOGGER.error("reset inputstream error ", var2);
//        }
//
//        return ips;
//    }

//    public static InputStream mark(InputStream ips) {
//        return mark(ips, 2147483647);
//    }

//    public static InputStream mark(InputStream ips, Integer readlimit) {
//        InputStream ips = new BufferedInputStream(ips);
//        ips.mark(readlimit.intValue());
//        return ips;
//    }

    public static BufferedImage bytesToBufferedImage(byte[] b) {
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(b);
            return ImageIO.read(in);
        } catch (Exception var2) {
            LOGGER.error(" bytes to buffered image error");
            return null;
        }
    }
}
