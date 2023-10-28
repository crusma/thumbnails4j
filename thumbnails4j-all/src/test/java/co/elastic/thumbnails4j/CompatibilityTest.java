package co.elastic.thumbnails4j;

import co.elastic.thumbnails4j.core.Dimensions;
import co.elastic.thumbnails4j.core.Thumbnailer;
import co.elastic.thumbnails4j.core.ThumbnailingException;
import co.elastic.thumbnails4j.doc.DOCThumbnailer;
import co.elastic.thumbnails4j.docx.DOCXThumbnailer;
import co.elastic.thumbnails4j.image.ImageThumbnailer;
import co.elastic.thumbnails4j.pdf.PDFThumbnailer;
import co.elastic.thumbnails4j.pptx.PPTXThumbnailer;
import co.elastic.thumbnails4j.xls.XLSThumbnailer;
import co.elastic.thumbnails4j.xlsx.XLSXThumbnailer;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

class CompatibilityTest {
    private static final String OUTPUT_TYPE = "png";
    private static final String TEST_FILENAME = "test-input";
    private static final String[] FILE_TYPES = {"docx", "doc", "pdf", "png", "pptx", "xls", "xlsx"};
    private static final List<String> FILE_TYPE_SET = Arrays.asList(FILE_TYPES);

    @Test
    void shouldGenerateThumbnailsUsingAllThumbnailers() throws IOException, ThumbnailingException {
        for (String fileType : FILE_TYPE_SET) {
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream inputFile = classLoader.getResource(String.format("%s/%s.%s", fileType, TEST_FILENAME, fileType)).openStream();
            byte[] bytes = generateThumbnail(inputFile, fileType);
            saveOutput(String.format("shouldGenerateThumbnailsUsingAllThumbnailers-%s.png", fileType), bytes);
        }
    }

    private void saveOutput(String filename, byte[] bytes) throws IOException {
        File file = new File(filename);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        if (!file.exists()) {
            file.createNewFile();
        }
        fileOutputStream.write(bytes);
        fileOutputStream.flush();
        fileOutputStream.close();
    }

    private byte[] generateThumbnail(InputStream inputStream, String type) throws IOException, ThumbnailingException {
        Dimensions outputDimensions = new Dimensions(150, 150);
        Thumbnailer thumbnailer = getThumbnailerFor(type);
        BufferedImage bufferedImage;
        bufferedImage = thumbnailFor(inputStream, thumbnailer, outputDimensions);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, OUTPUT_TYPE, outputStream);
        return outputStream.toByteArray();
    }

    private Thumbnailer getThumbnailerFor(String type) {
        switch (type) {
            case "doc":
                return new DOCThumbnailer();
            case "docx":
                return new DOCXThumbnailer();
            case "png":
                return new ImageThumbnailer(OUTPUT_TYPE);
            case "pdf":
                return new PDFThumbnailer();
            case "pptx":
                return new PPTXThumbnailer();
            case "xls":
                return new XLSThumbnailer();
            case "xlsx":
                return new XLSXThumbnailer();
            default:
                return null;
        }
    }

    private BufferedImage thumbnailFor(InputStream inputStream, Thumbnailer thumbnailer, Dimensions dimensions) throws ThumbnailingException {
        List<Dimensions> dimensionList = new ArrayList<>();
        dimensionList.add(dimensions);
        return thumbnailer.getThumbnails(inputStream, dimensionList).get(0);
    }

}
