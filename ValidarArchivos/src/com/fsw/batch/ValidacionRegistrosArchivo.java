package com.fsw.batch;


import org.slf4j.Logger;


import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import com.fsw.batch.modelo.ArchivoFomatoRBM;


public class ValidacionRegistrosArchivo implements ItemProcessor<ArchivoFomatoRBM, ArchivoFomatoRBM> {
	
	 private static final Logger log = LoggerFactory.getLogger(ValidacionRegistrosArchivo.class);
	
	@Override
	  public ArchivoFomatoRBM process(final ArchivoFomatoRBM person) throws Exception {
	    final String firstName = person.getFirstName().toUpperCase();
	    final String lastName = person.getLastName().toUpperCase();

	    final ArchivoFomatoRBM transformedPerson = new ArchivoFomatoRBM(firstName, lastName);

	    log.info("Converting (" + person + ") into (" + transformedPerson + ")");

	    return transformedPerson;
	  }

}
