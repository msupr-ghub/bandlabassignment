package com.bandlab.assignment.service.clients.impl;

import com.bandlab.assignment.service.clients.StorageClient;

import java.io.File;
import java.io.IOException;

public class LocalStorageClient implements StorageClient {
    @Override
    public String uploadFile(File file) throws IOException {
        return file.getAbsolutePath();
    }
}
