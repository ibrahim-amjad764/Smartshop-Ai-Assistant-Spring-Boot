package com.example.SmartShop.AI.Assistant.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Cloudinary Service
 *
 * Features:
 * - Upload, delete, validate image files
 * - Handles publicId, secure URLs, transformations
 * - File validation (size/type/extension)
 * - Logging for all operations
 * - getImageInfo(), getAccountInfo(), getAllImages(), getResources()
 */
@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    // ==========================
    // Upload Image
    // ==========================
    public String uploadImage(MultipartFile file, Long productId) throws IOException {
        validateImageFile(file);

        try {
            Map<String, Object> uploadParams = new HashMap<>();
            uploadParams.put("folder", "products/" + productId);
            uploadParams.put("public_id", "product_" + productId + "_" + System.currentTimeMillis());
            uploadParams.put("overwrite", true);
            uploadParams.put("resource_type", "image");
            uploadParams.put("format", "webp");
            uploadParams.put("quality", "auto:good");
            uploadParams.put("fetch_format", "auto");

            Map<String, String> transformation = new HashMap<>();
            transformation.put("crop", "limit");
            transformation.put("width", "800");
            transformation.put("height", "800");
            uploadParams.put("transformation", transformation);

            var uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);
            String secureUrl = (String) uploadResult.get("secure_url");
            String publicId = (String) uploadResult.get("public_id");

            System.out.println("[CloudinaryService]  Upload successful for product " + productId);
            System.out.println("[CloudinaryService]  Secure URL: " + secureUrl);
            System.out.println("[CloudinaryService]  Public ID: " + publicId);

            return secureUrl;

        } catch (Exception e) {
            System.err.println("[CloudinaryService]  Upload failed for product " + productId + ": " + e.getMessage());
            throw new IOException("Failed to upload image to Cloudinary: " + e.getMessage(), e);
        }
    }

    // ==========================
    // Delete Image
    // ==========================
    public boolean deleteImage(String publicId) {
        try {
            System.out.println("[CloudinaryService] 🗑 Deleting image: " + publicId);
            var result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            boolean deleted = "ok".equals(result.get("result"));

            if (deleted) {
                System.out.println("[CloudinaryService]  Image deleted successfully: " + publicId);
            } else {
                System.out.println("[CloudinaryService]  Image deletion failed: " + publicId + ", result: " + result);
            }

            return deleted;
        } catch (Exception e) {
            System.err.println("[CloudinaryService]  Error deleting image " + publicId + ": " + e.getMessage());
            return false;
        }
    }

    // ==========================
    // Validate Image
    // ==========================
    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be null or empty");
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("File size exceeds 10MB limit");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File must be an image. Got: " + contentType);
        }

        String filename = file.getOriginalFilename();
        if (filename != null) {
            String extension = filename.toLowerCase().substring(filename.lastIndexOf('.') + 1);
            if (!isSupportedFormat(extension)) {
                throw new IllegalArgumentException("Unsupported image format: " + extension +
                        ". Supported formats: jpg, jpeg, png, gif, webp, bmp");
            }
        }

        System.out.println("[CloudinaryService]  File validation passed: " + filename);
    }

    private boolean isSupportedFormat(String extension) {
        return extension.equals("jpg") ||
                extension.equals("jpeg") ||
                extension.equals("png") ||
                extension.equals("gif") ||
                extension.equals("webp") ||
                extension.equals("bmp");
    }

    // ==========================
    // Get Image Info
    // ==========================
    public Map<String, Object> getImageInfo(String publicId) {
        try {
            System.out.println("[CloudinaryService]  Getting image info for: " + publicId);
            return cloudinary.api().resource(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            System.err.println("[CloudinaryService]  Error getting image info for " + publicId + ": " + e.getMessage());
            return null;
        }
    }

    // ==========================
    // Cloudinary Resource Methods
    // ==========================
    public Object getAccountInfo() throws Exception {
        return cloudinary.api().resources(ObjectUtils.emptyMap());
    }

    public Object getAllImages() throws Exception {
        return cloudinary.api().resources(ObjectUtils.asMap(
                "type", "upload",
                "resource_type", "image"
        ));
    }

    public Object getResources() throws Exception {
        return cloudinary.api().resources(ObjectUtils.emptyMap());
    }
}