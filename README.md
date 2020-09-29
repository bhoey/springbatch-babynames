# Baby Name Spring Batch Demo

## Purpose
This is a demo to showcase the following technologies working together:

* Spring Batch
* Spring Boot
* CSV Parsing and Enriching
* H2 Database

## Project Description
The US Social Security Administration provides
[a dataset](https://catalog.data.gov/dataset/baby-names-from-social-security-card-applications-national-level-data)
of first names from social security applications submitted for babies born in the US. The data is grouped into yearly
CSV files with the following fields: first name, sex, and a count of how many times the name occurred that year.

This program parses these CSV files and inserts them into an in-memory H2 database. Some points to note:
   * The multiple files are read using the MultiResourceItemReader combined with filenames injected via
   @Value annotation (pathspec being externalized to the application.properties file)

   * Since the year in not included in the CSV records themselves, a processor extracts the year from the CSV filename
   and assigns it to each BabyName object from its respective file.

## Running Program Locally
```
git clone https://github.com/bhoey/springbatch-babynames.git

cd springbatch-babynames

mvn spring-boot:run
```
