package xyz.tclx;

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

import java.awt.Color;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;


public class PageBackground {
    static final String PATH = "/Users/chenli/Documents/syncthing/collections";  // 注意不能用 ~，File 不会认为是绝对路径
    static final String INEYE_DIR = "ineye"; // 原文件复制到 ineye 目录
//    static final Color EYE_COLOR = new Color(0xc7edcc); // 豆沙绿
    static final Color EYE_COLOR = new Color(0xdfedd3); // readpaper 绿
//    static final Color EYE_COLOR = new Color(0xfaf9de); // 杏仁黄

    public static void main(String[] args) {
        System.out.println("--- PDF 背景颜色转换启动 ---");
        PageBackground pbClass = new PageBackground();

        File file = new File(PATH);
        if (file.isFile()) {
            pbClass.handleFile(file);
        } else {
            pbClass.handleDir(file);
        }
        System.out.println("--- PDF 背景颜色转换完成 ---");
    }

    private void handleDir(File dir) {
        File[] files = dir.listFiles();

        for (File file : files) {
            // 判断是文件还是目录
            if (file.isFile()) {
                handleFile(file);
            } else {
                if (file.getName().equals(INEYE_DIR)) {
                    continue;
                }
                handleDir(file);
            }
        }
    }

    private void handleFile(File file) {
        // 判断是否是 pdf 文件
        boolean isPDF = isPDFByExtension(file.toString());
        if (!isPDF) {
            return;
        }

        // 判断是否在 ineye 目录下存在文件，若存在跳过
        String fileName = file.getName();
        File ineyeDir = new File(file.getParent(), INEYE_DIR);
        File ineyeFile = new File(ineyeDir, fileName);
        if (ineyeFile.exists()) {
            return;
        }

        try {
            // 移动文件到 ineye 目录
            if (!ineyeDir.exists()) {
                ineyeDir.mkdir();
            }
            Files.move(file.toPath(), ineyeFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // 处理，重命名后 File 对象的路径并不会自动更新
            PdfDocument srcDoc = new PdfDocument(new PdfReader(ineyeFile.toString()).setUnethicalReading(true));
            PdfDocument destDoc = new PdfDocument(new PdfWriter(file.toString()));
            destDoc.addEventHandler(PdfDocumentEvent.END_PAGE, new PageBackgroundsEventHandler());
            srcDoc.copyPagesTo(1, srcDoc.getNumberOfPages(), destDoc);
            destDoc.close();
            // 移除背景
//            RemoveBackground.removeLowestContent(ineyeFile.toString(), file.toString());
            System.out.println(fileName + " done!");
        } catch (java.lang.Exception e) {
            e.printStackTrace();
        }
    }

    // 判断一个文件是否是pdf文件
    public static boolean isPDFByExtension(String filePath) {
        File file = new File(filePath);
        String extension = getFileExtension(file);
        return extension != null && extension.equalsIgnoreCase("pdf");
    }

    public static String getFileExtension(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? null : fileName.substring(dotIndex + 1);
    }

    private static class PageBackgroundsEventHandler implements IEventHandler {
        @Override
        public void handleEvent(Event currentEvent) {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) currentEvent;
            PdfDocument pdfDoc = docEvent.getDocument();
            PdfPage page = docEvent.getPage();

            PdfCanvas canvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdfDoc); // 必须这个构造函数才能保留文本，因为 newContentStreamBefore，所以这次操作的内容会在原来内容下方
            Rectangle rect = page.getPageSize();

            canvas
                    .saveState()
                    .setFillColor(new DeviceRgb(EYE_COLOR))
                    .rectangle(rect) // 相当于加了一层绿色的矩型
                    .fill()
                    .restoreState();

            canvas.release();
        }
    }
}
