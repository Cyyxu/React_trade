package com.xyes.springboot.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * EasyExcel 工具类
 * 提供 Excel 导入导出的通用方法
 *
 * * @author xujun
 * @version 1.0
 * @since 2025/11/3
 */
@Slf4j
public class EasyExcelUtils {

    /**
     * 导出 Excel 文件(自动列宽)
     *
     * @param response  HTTP 响应对象
     * @param data      数据列表
     * @param clazz     数据类型
     * @param fileName  文件名(不含扩展名)
     * @param <T>       数据类型泛型
     */
    public static <T> void exportExcelWithAutoWidth(HttpServletResponse response,
                                                    List<T> data,
                                                    Class<T> clazz,
                                                    String fileName) {
        try {
            setExcelResponseHeader(response, fileName);
            EasyExcel.write(response.getOutputStream(), clazz)
                    .autoCloseStream(Boolean.FALSE)
                    .sheet(fileName)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .doWrite(data);
            response.flushBuffer();
            log.info("Excel 导出成功(自动列宽): {}.xlsx, 数据量: {}", fileName, data.size());
        } catch (IOException e) {
            log.error("Excel 导出失败: {}", fileName, e);
            throw new RuntimeException("Excel 导出失败: " + e.getMessage(), e);
        }
    }

    /**
     * 导入 Excel 文件(同步读取)
     *
     * @param file  上传的文件
     * @param clazz 数据类型
     * @param <T>   数据类型泛型
     * @return 数据列表
     */
    public static <T> List<T> importExcel(MultipartFile file, Class<T> clazz) {
        try (InputStream inputStream = file.getInputStream()) {
            List<T> data = EasyExcel.read(inputStream)
                    .head(clazz)
                    .sheet()
                    .doReadSync();
            log.info("Excel 导入成功: {}, 数据量: {}", file.getOriginalFilename(), data.size());
            return data;
        } catch (IOException e) {
            log.error("Excel 导入失败: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("Excel 导入失败: " + e.getMessage(), e);
        }
    }

    /**
     * 验证上传的文件是否为 Excel 文件
     *
     * @param file 上传的文件
     * @return true-是 Excel 文件, false-不是
     */
    public static boolean isExcelFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }
        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.isBlank()) {
            return false;
        }
        return fileName.endsWith(".xls") || fileName.endsWith(".xlsx");
    }

    /**
     * 设置 Excel 响应头
     *
     * @param response HTTP 响应对象
     * @param fileName 文件名(不含扩展名)
     * @throws IOException IO 异常
     */
    private static void setExcelResponseHeader(HttpServletResponse response, String fileName) throws IOException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
        response.setHeader("Content-Disposition", "attachment; filename=" + encodedFileName + ".xlsx");
    }

}
