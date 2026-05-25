package com.tcc_vizinhanca.vizinhanca.service.storage;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobHttpHeaders;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import com.azure.storage.blob.BlobContainerClient;

import java.util.UUID;

@Service
public class BlobStorageService {

    @Value("${azure.storage.connection-string}")
    private String connectionString;

    @Value("${azure.storage.container-name}")
    private String containerName;

    private  BlobContainerClient containerClient;

    @PostConstruct
    public void init() {
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();

        this.containerClient = blobServiceClient
                .getBlobContainerClient(containerName);

        if (!containerClient.exists()) {
            containerClient.create();
        }
    }

    public String uploadFile(MultipartFile file, String folder) {
        validateFile(file);

        String extension = getExtension(file.getOriginalFilename());
        String fileName = folder + "/" + UUID.randomUUID() + "." + extension;

        BlobClient blobClient = containerClient.getBlobClient(fileName);

        try {
            BlobHttpHeaders headers = new BlobHttpHeaders()
                    .setContentType(file.getContentType());

            blobClient.upload(file.getInputStream(), file.getSize(), true);
            blobClient.setHttpHeaders(headers);

            return blobClient.getBlobUrl();

        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erro ao fazer upload do arquivo: " + e.getMessage()
            );
        }
    }

    public void deleteFile(String fileUrl) {
        String blobName = fileUrl.replace(
                containerClient.getBlobContainerUrl() + "/", ""
        );

        BlobClient blobClient = containerClient.getBlobClient(blobName);

        if (!blobClient.exists()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Arquivo não encontrado!"
            );
        }

        blobClient.delete();
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Arquivo não pode ser vazio!"
            );
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Apenas imagens são permitidas!"
            );
        }

        long maxSize = 5 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Arquivo excede o limite de 5MB!"
            );
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "jpg";
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
}