package com.fsw.batch;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.fsw.batch.modelo.ArchivoFomatoRBM;


@Configuration
public class BatchConfiguration {

	
	 @Autowired
	  public JobBuilderFactory jobBuilderFactory;

	  @Autowired
	  public StepBuilderFactory stepBuilderFactory;
	  
	  
	  
	  @Bean
	  public FlatFileItemReader<ArchivoFomatoRBM> reader() {
	    return new FlatFileItemReaderBuilder<ArchivoFomatoRBM>()
	      .name("personItemReader")
	      .resource(new ClassPathResource("sample-data.csv"))
	      .delimited()
	      .names(new String[]{"firstName", "lastName"})
	      .fieldSetMapper(new BeanWrapperFieldSetMapper<ArchivoFomatoRBM>() {{
	        setTargetType(ArchivoFomatoRBM.class);
	      }})
	      .build();
	  }

	  @Bean
	  public ValidacionRegistrosArchivo processor() {
	    return new ValidacionRegistrosArchivo();
	  }

	  @Bean
	  public JdbcBatchItemWriter<ArchivoFomatoRBM> writer(DataSource dataSource) {
	    return new JdbcBatchItemWriterBuilder<ArchivoFomatoRBM>()
	      .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
	      .sql("INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)")
	      .dataSource(dataSource)
	      .build();
	  }
	  
	  @Bean
		public Job importUserJob(JobCompletionNotificationListener listener, Step step1) {
			return jobBuilderFactory.get("importUserJob")
				.incrementer(new RunIdIncrementer())
				.listener(listener)
				.flow(step1)
				.end()
				.build();
		}

		@Bean
		public Step step1(JdbcBatchItemWriter<ArchivoFomatoRBM> writer) {
			return stepBuilderFactory.get("step1")
				.<ArchivoFomatoRBM, ArchivoFomatoRBM> chunk(10)
				.reader(reader())
				.processor(processor())
				.writer(writer)
				.build();
		}
}
