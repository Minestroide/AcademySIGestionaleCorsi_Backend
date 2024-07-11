package it.marco.digrigoli.services.interfaces;

import java.io.InputStream;

import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.web.multipart.MultipartFile;

public interface IDBFileService {
	
	public String uploadFile(MultipartFile multipartFile);
	
	public String uploadFile(InputStream inputStream, FormDataContentDisposition detail);
	
	public String uploadFile(InputStream inputStream, ContentDisposition detail);
	
	public void deleteFile(String dbFileId);

	GridFsResource getFile(String dbFileId);

}
