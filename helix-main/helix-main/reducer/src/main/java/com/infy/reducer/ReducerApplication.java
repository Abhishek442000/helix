package com.infy.reducer;

import java.io.InputStream;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.infy.reducer.datacompressor.DataCompressor;
import com.infy.reducer.datacompressor.DataCompressorImpl;
import com.infy.reducer.dataconverter.DataConverter;
import com.infy.reducer.dataconverter.DataConverterImpl;
import com.infy.reducer.entity.Person;
import com.infy.reducer.file.CompressedFile;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
public class ReducerApplication {
	private static ObjectMapper objectMapper = new ObjectMapper();
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SpringApplication.run(ReducerApplication.class, args);
		
		try {
				Properties properties = new Properties() ;
				try(InputStream inputStream = DataConverterImpl.class.getClassLoader().getResourceAsStream("application.properties")){
					properties.load(inputStream);
				}
				catch(Exception e)
				{
					e.printStackTrace(); 
				}
				
			String ENTITY_NAME = properties.getProperty("entity_name");
		
			String path = "src/main/resources/Person.json";
//			String json = readFileAsString(path);
			byte[] jsonData = readFileAsBytes(path);
			 // Convert the byte array to a string
	        String jsonString = new String(jsonData);
	     // Parse the JSON array as a List of Person objects
            List<Person> personList = objectMapper.readValue(jsonString, new TypeReference<List<Person>>() {});
            String FILEPATH = "src/main/resources/file.txt" ;
			Class<?> entityClass = Class.forName(ENTITY_NAME);

			 DataConverter<?> dataConverter = new DataConverterImpl<>(entityClass);
			 DataCompressor<?> dataCompressor = new DataCompressorImpl<>(dataConverter);
			 
			 for (Person person : personList) {
				    String json = dataConverter.javaObjectToJson(person);
				    Object convertedObject = dataConverter.jsonToJavaObject(json);
				    byte[] compressedData = dataCompressor.compress(convertedObject);
				    System.out.println(compressedData);
				    CompressedFile.bytetoFile(compressedData,FILEPATH);

				    Object decompressedData = dataCompressor.decompress(compressedData);

				    String result = dataConverter.javaObjectToJson(decompressedData);
				    System.out.println(result);
				}
			
//			<-------------------Json to Java Object------------------------->
//			 for(var it : personList) {
//				 Object convertedObject = dataConverter.jsonToJavaObject(it.toString());
//
//					byte[] compressedData = dataCompressor.compress(convertedObject);
//
////					<---------------------Compressed File----------------------------------->
//					
//					CompressedFile.bytetoFile(compressedData);
//					Object decompressedData = dataCompressor.decompress(compressedData);
//					
//					String result = dataConverter.javaObjectToJson(decompressedData);
//					System.out.println(result) ;
//			 }
			
//          <-------------------------JavaObject to Json ---------------------------->
			
			
	

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public static byte[] readFileAsBytes(String path) throws Exception {
	    return Files.readAllBytes(Paths.get(path));
	}


	public static String readFileAsString(String path) throws Exception {
		return new String(Files.readAllBytes(Paths.get(path)));
	}

}
