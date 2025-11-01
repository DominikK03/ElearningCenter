package pl.dominik.elearningcenter.infrastructure.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );

    private static final List<String> ALLOWED_DOCUMENT_TYPES = Arrays.asList(
        "application/pdf"
    );

    private static final List<String> ALL_ALLOWED_TYPES = Arrays.asList(
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp",
        "application/pdf"
    );

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    public FileStorageService(@Value("${file.upload-dir:uploads}") String uploadDir) throws IOException {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(this.fileStorageLocation);
    }

    public String storeFile(MultipartFile file) throws IOException, FileStorageException {
        if (file.isEmpty()) {
            throw new FileStorageException("Failed to store empty file");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileStorageException("File size exceeds maximum allowed size of 10MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALL_ALLOWED_TYPES.contains(contentType.toLowerCase())) {
            throw new FileStorageException("File type not allowed. Allowed types: PDF, JPG, JPEG, PNG, GIF, WEBP");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.contains("..")) {
            throw new FileStorageException("Invalid file name");
        }

        String fileExtension = getFileExtension(originalFilename);
        if (!isValidExtension(fileExtension, contentType)) {
            throw new FileStorageException("File extension does not match content type");
        }

        if (!isValidFileContent(file, contentType)) {
            throw new FileStorageException("File content validation failed. Possible file type spoofing detected.");
        }

        String newFileName = UUID.randomUUID().toString() + "." + fileExtension;
        Path targetLocation = this.fileStorageLocation.resolve(newFileName);

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
        }

        return newFileName;
    }

    public void deleteFile(String filename) throws IOException {
        Path filePath = this.fileStorageLocation.resolve(filename).normalize();
        Files.deleteIfExists(filePath);
    }

    public Path getFilePath(String filename) {
        return this.fileStorageLocation.resolve(filename).normalize();
    }

    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1).toLowerCase();
    }

    private boolean isValidExtension(String extension, String contentType) {
        switch (contentType.toLowerCase()) {
            case "image/jpeg":
            case "image/jpg":
                return extension.equals("jpg") || extension.equals("jpeg");
            case "image/png":
                return extension.equals("png");
            case "image/gif":
                return extension.equals("gif");
            case "image/webp":
                return extension.equals("webp");
            case "application/pdf":
                return extension.equals("pdf");
            default:
                return false;
        }
    }

    /**
     * Validates file content by checking magic bytes (file signatures)
     * This prevents attackers from uploading malicious files with fake extensions
     */
    private boolean isValidFileContent(MultipartFile file, String contentType) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            byte[] fileHeader = new byte[8];
            int bytesRead = inputStream.read(fileHeader);

            if (bytesRead < 4) {
                return false;
            }

            switch (contentType.toLowerCase()) {
                case "image/jpeg":
                case "image/jpg":
                    return fileHeader[0] == (byte) 0xFF &&
                           fileHeader[1] == (byte) 0xD8 &&
                           fileHeader[2] == (byte) 0xFF;

                case "image/png":
                    return fileHeader[0] == (byte) 0x89 &&
                           fileHeader[1] == (byte) 0x50 &&
                           fileHeader[2] == (byte) 0x4E &&
                           fileHeader[3] == (byte) 0x47;

                case "image/gif":
                    return fileHeader[0] == (byte) 0x47 &&
                           fileHeader[1] == (byte) 0x49 &&
                           fileHeader[2] == (byte) 0x46 &&
                           fileHeader[3] == (byte) 0x38;

                case "image/webp":
                    return fileHeader[0] == (byte) 0x52 &&
                           fileHeader[1] == (byte) 0x49 &&
                           fileHeader[2] == (byte) 0x46 &&
                           fileHeader[3] == (byte) 0x46;

                case "application/pdf":
                    return fileHeader[0] == (byte) 0x25 &&
                           fileHeader[1] == (byte) 0x50 &&
                           fileHeader[2] == (byte) 0x44 &&
                           fileHeader[3] == (byte) 0x46;

                default:
                    return false;
            }
        }
    }
}
