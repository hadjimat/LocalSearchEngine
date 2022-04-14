
# Local search engine

This project called search engine is a diploma project of the SkillBox platform JAVA-course.
I created search engine, that can indexing sites and search for query in sites content.
It has web interface based on spring framework, and the project need to connect to the SQL base data for
storage indexing data of sites.

During this project I used Spring framework, JSOUP, MYSQL connector, project Lombok, Jetbrain, Hibernate, lucene.morphology library.

Before to use this project you need to create the new scheme with 8utfmb4 coder in SQL database.
Next add url to DB, password, username into the application.yaml. So there you can add the list of site which you want to indexing. 
   
## Usage/Examples

```javascript
import Component from 'application.yaml'

spring.datasource.url: jdbc:mysql://localhost:3306/search_engine? //add url to your DB
spring.datasource.username: root // add username
spring.datasource.password: root // add password
spring.batch.jdbc.table-prefix: search_engine // add scheme name
spring.jpa.hibernate.ddl-auto: update
spring.jpa.properties.hibernate.jdbc.batch_size: 5
spring.jpa.properties.hibernate.order_inserts: true

siteconfig:
  sites:
    - url: https://lenta.ru // add your sitelist
      name: lenta

```

