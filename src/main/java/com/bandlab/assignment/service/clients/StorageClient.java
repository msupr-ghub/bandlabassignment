package com.bandlab.assignment.service.clients;

import java.io.File;
import java.io.IOException;

public interface StorageClient {

    String uploadFile(File file) throws IOException;

}
