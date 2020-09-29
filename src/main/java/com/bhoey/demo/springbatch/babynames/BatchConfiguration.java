package com.bhoey.demo.springbatch.babynames;

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
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    final static String   BABYNAME_COL_NAME = "name";
    final static String   BABYNAME_COL_SEX = "sex";
    final static String   BABYNAME_COL_NUM = "num";
    final static String[] BABYNAME_COLS = new String[]{ BABYNAME_COL_NAME,  BABYNAME_COL_SEX, BABYNAME_COL_NUM };

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Value("${datafilespec}")
    private Resource[] inputFiles;

    @Bean
    public MultiResourceItemReader<BabyName> multiResourceItemReader(){
        MultiResourceItemReader<BabyName> reader = new MultiResourceItemReader<>();
        reader.setDelegate(reader());
        reader.setResources(inputFiles);
        return reader;
    }

    @Bean
    public FlatFileItemReader<BabyName> reader() {
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames(BABYNAME_COLS);

        DefaultLineMapper<BabyName> mapper = new DefaultLineMapper<>();
        mapper.setLineTokenizer(tokenizer);
        mapper.setFieldSetMapper(new BabyNameFieldSetMapper());
        mapper.afterPropertiesSet();

        FlatFileItemReader<BabyName> reader = new FlatFileItemReader<>();
        reader.setLineMapper(mapper);

        return reader;
    }

    @Bean
    public BabyNameProcessor processor() {
        return new BabyNameProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<BabyName> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<BabyName>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO babynames (name, year, num) VALUES (:name, :year, :num)")
                .dataSource(dataSource)
                .build();
    }

    class BabyNameFieldSetMapper implements FieldSetMapper<BabyName> {
        @Override
        public BabyName mapFieldSet(FieldSet fieldSet) {
            BabyName bn = new BabyName();
            bn.setName(fieldSet.readString(BABYNAME_COL_NAME));
            bn.setNum(fieldSet.readInt(BABYNAME_COL_NUM));
            return bn;
        }
    }

    @Bean
    public Step loadNamesToDB(JdbcBatchItemWriter<BabyName> writer){
        final int chunkSize = 10;

        return stepBuilderFactory.get("loadNamesToDB")
                                .<BabyName, BabyName>chunk(chunkSize)
                                .reader(multiResourceItemReader())
                                .processor(processor())
                                .writer(writer)
                                .build();
    }

    @Bean
    public Job job1(Step step) {
        return jobBuilderFactory.get("job1")
                .incrementer(new RunIdIncrementer())
                .start(step)
                .build();
    }
}
