## Upload data from excel file
### Steps to upload excel file 
- Add dependencies: Need `poi-ooxml` from `org.apache.poi` to extract data from excel file		

```
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.5</version>
</dependency>
```
- Need to handle `non String` type in excel
- Need to handle empty rows
### Links to learn
    - [Short video how to implement](https://www.youtube.com/watch?v=624IUCdmJMg)