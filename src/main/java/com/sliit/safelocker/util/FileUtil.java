package com.sliit.safelocker.util;

import com.sliit.safelocker.exception.FileStorageException;
import com.sliit.safelocker.model.FileUpload;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.PosixFilePermission;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;



public class FileUtil {
	
	static final boolean isPosix =
		    FileSystems.getDefault().supportedFileAttributeViews().contains("posix");

	  public static FileUpload fileUpload(MultipartFile file) {
	        // Normalize file name
		  	String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()); 
	        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
	        String generatedFileName = timeStamp+"-"+fileName;
	        String fileDownloadUrl  = "http://localhost/safe_locker/file/"+generatedFileName;
	        
	        Set<PosixFilePermission> perms = new HashSet<>();
	        perms.add(PosixFilePermission.OWNER_READ);
	        perms.add(PosixFilePermission.OWNER_WRITE);
	        perms.add(PosixFilePermission.OWNER_EXECUTE);
	        perms.add(PosixFilePermission.GROUP_READ);
	        perms.add(PosixFilePermission.OTHERS_READ);
	     
            createFoldersIfNeeded();

	        try {
	            // Check if the file's name contains invalid characters
	            if(generatedFileName.contains("..")) {
	                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + generatedFileName);
	            }
				Path targetLocation = Paths.get("C:"+File.separator+"xampp"+File.separator+"htdocs"+File.separator+"safe_locker"+File.separator+"file").resolve(generatedFileName);;
	            System.out.println(targetLocation);

	            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
	            if(FileUtil.isPosix) {
	            	 Files.setPosixFilePermissions(targetLocation, perms);
	            }
	             FileUpload fileUpload = new FileUpload();
	             
	             fileUpload.setFileName(generatedFileName);
	             fileUpload.setFileUrl(fileDownloadUrl);
	            
	            return fileUpload;
	        } catch (IOException ex) {
	            throw new FileStorageException("Could not store file " + fileDownloadUrl + ". Please try again!", ex);
	        }
	    }
	  
	  
	  public static void createFoldersIfNeeded() {
		  
		   try {

				     Files.createDirectories( Paths.get("C:"+File.separator+"xampp"+File.separator+"htdocs"+File.separator+"safe_locker"+File.separator+"file"));

	           
	        } catch (Exception ex) {
	            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
	        }
	  }


	public static String getFileChecksum(MessageDigest digest, InputStream file) throws IOException
	{

		//Create byte array to read data in chunks
		byte[] byteArray = new byte[1024];
		int bytesCount = 0;

		//Read file data and update in message digest
		while ((bytesCount = file.read(byteArray)) != -1) {
			digest.update(byteArray, 0, bytesCount);
		};

		//close the stream; We don't need it now.
		file.close();

		//Get the hash's bytes
		byte[] bytes = digest.digest();

		//This bytes[] has bytes in decimal format;
		//Convert it to hexadecimal format
		StringBuilder sb = new StringBuilder();
		for(int i=0; i< bytes.length ;i++)
		{
			sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
		}

		//return complete hash
		return sb.toString();
	}

}
