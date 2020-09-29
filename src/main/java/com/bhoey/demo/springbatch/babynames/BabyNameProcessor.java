package com.bhoey.demo.springbatch.babynames;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BabyNameProcessor implements ItemProcessor<BabyName, BabyName> {
    @Autowired
    MultiResourceItemReader multiResourceItemReader;

    @Override
    public BabyName process(final BabyName bn){

        // Extract the year from filename to assign to babyname instance
        if ( multiResourceItemReader.getCurrentResource() != null ){
            String filename = multiResourceItemReader.getCurrentResource().getFilename();
            // Example filename: yob2019.txt
            Pattern p = Pattern.compile("^yob(\\d{4}).txt$");
            Matcher m = p.matcher(filename);
            if (m.find()) {
                bn.setYear(Integer.valueOf(m.group(1)));
            }
        }

        return bn;
    }
}
