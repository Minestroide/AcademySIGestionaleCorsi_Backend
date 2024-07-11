package it.marco.digrigoli.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsCriteria;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mongodb.client.model.Filters;

import it.marco.digrigoli.services.interfaces.IDBFileService;

@Service
public class DBFileServiceImpl implements IDBFileService {
	
	private GridFsOperations gridFs;
	
	public DBFileServiceImpl(GridFsOperations gridFs, GridFsTemplate gridFsTemplate) {
		this.gridFs = gridFs;
	}
	
	private Logger logger = LogManager.getLogger(this.getClass());

	@Override
	public String uploadFile(MultipartFile multipartFile) {
		try {
			return this.gridFs.store(multipartFile.getInputStream(), multipartFile.getOriginalFilename()).toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("An error occurred: ", e);
		}
		return null;
	}

	@Override
	public String uploadFile(InputStream inputStream, FormDataContentDisposition detail) {
		return this.gridFs.store(inputStream, detail.getFileName()).toString();
	}

	@Override
	public String uploadFile(InputStream inputStream, ContentDisposition detail) {
		return this.gridFs.store(inputStream, detail.getFileName()).toString();
	}

	@Override
	public void deleteFile(String dbFileId) {
		this.gridFs.delete(new Query(Criteria.where("_id").is(dbFileId)));
	}
	
	@Override
	public GridFsResource getFile(String dbFileId) {
		return gridFs.getResource(gridFs.findOne(new Query(Criteria.where("_id").is(dbFileId))));
	}

}
