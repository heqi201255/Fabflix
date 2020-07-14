## Fabflix Project

### Members: 

​		Qi He (heq4@uci.edu), Youan Lu (youanl@uci.edu)

### Part 1 Demo Video URL:  https://youtu.be/LCVxxR6jmOk

### Part 2 Demo Video URL: https://youtu.be/a5NNy3zP1j8

### Part 3 Demo Video URL: https://youtu.be/YeDsWiWxFFc

### Part 4 Demo Video URL: https://youtu.be/HlxYq-pPW9Y

### Part 5 Demo Video URL:  https://youtu.be/WvFB3SE39gQ

### Deployment Instruction:

- Require JDK 8 and Tomcat 8.
- Open "context.xml" under META-INF folder.
- Change the "username" and "password" to your own mysql username and password for testing, make sure you have moviedb and all data inserted correctly in your mysql database.
- Build the project artifact, you will see a war file under the target folder.
- Start Tomcat, and deploy the war file.
- Open the fabflix app.


### Contributions:

We also debug each other's code to implement the functionality.

Qi He:
- Search
- Multi search
- Shopping Cart
- Payment
- Demo Video
- PreparedStatements
- XML Parsing
- Android App


Youan Lu:
- HTTPS
- Stored Procedure
- Employee Login
- Metadata
- dashboard
- Full-text Search
- Autocomplete


- # Connection Pooling
    - Include the filename/path of all code/configuration files in GitHub of using JDBC Connection Pooling.
    - All servlets are changed to Connection Pooling and PreparedStatement.
    - AddmovieServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-29/blob/master/src/AddMovieServlet.java
    - AddStarServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-29/blob/master/src/AddstarServlet.java
    - AutoCompleteServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-29/blob/master/cs122b-spring20-team-29/src/AutoCompleteServlet.java
    - BrowseGenreServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-29/blob/master/src/BrowseGenreServlet.java
    - BrowseTitleServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-29/blob/master/src/BrowseTitleServlet.java
    - CartServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-29/blob/master/src/CartServlet.java
    - DashboardServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-29/blob/master/cs122b-spring20-team-29/src/DashboardServlet.java
    - GenreServlet: https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-29/blob/master/cs122b-spring20-team-29/src/GenreServlet.java
    - EmployeeLoginServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-29/blob/master/src/EmployeeLoginServlet.java
    - LoginServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-29/blob/master/src/LoginServlet.java
    - MultisearchServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-29/blob/master/src/MultipleSearchServlet.java
    - NewSearchServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-29/blob/master/cs122b-spring20-team-29/src/NewSearchServlet.java
    - PaymentAuthServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-29/blob/master/src/PaymentAuthServlet.java
    - PaymentServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-29/blob/master/src/PaymentServlet.java
    - SingleMovieServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-29/blob/master/src/SingleMovieServlet.java
    - SingleSearchServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-29/blob/master/src/SingleSearchServlet.java
    - SingleStarServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-29/blob/master/src/SingleStarServlet.java
    
    - Explain how Connection Pooling is utilized in the Fabflix code.
    -       Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource dataSource = (DataSource) envContext.lookup("jdbc/masterdb");
            Connection dbcon = dataSource.getConnection();
            or 
            DataSource dataSource = (DataSource) envContext.lookup(RouteChoice.getDatasource());
            Connection dbcon = dataSource.getConnection();
            
    - Explain how Connection Pooling works with two backend SQL.
    - The DataSource is defined in Context.xml. It also define the location of the SQL database Java will do a look up and use the name a dataSource and get a connection from the source of the datasource. In the Datasource, it also defines a set of connection when the program starts. Whenever the servlet asks for a connection, it will get a precreated connection from the pool and can be reused at the different time.
    
- # Master/Slave
    - #### Include the filename/path of all code/configuration files in GitHub of routing queries to Master/Slave SQL.
    - The RouteChoice.java helps randomly route to instance Master/Slave when there's read request. https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-29/blob/master/cs122b-spring20-team-29/src/RouteChoice.java

    - How read/write requests were routed to Master/Slave SQL?
    - We use two datasource, one is the master instance and the other is the slave instance. It's specified in context.xml. All the write requests are sent to master instance and read requests are randomly sent to master or slave instances. We use Random.nextBoolean() to handle randomness. 
    
- # JMeter TS/TJ Time Logs
    - #### Instructions of how to use the `log_processing.*` script to process the JMeter logs.
    
    - The script is `log_processing.java` Path: https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-29/blob/master/src/log_processing.java
    
    - The log files will be generated under tomcat's subfolder `webapps/cs122b-spring20-team-29/`, we have provided the generated log files for each case in  https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-29/blob/master/logs, there are two subfolders `Scaled` and `Single` which contains log files for scaled version test plan and single-instance version test plan respectively. Each case has its own folder named with the case number. The case folders contains the log file. 
    
    - For cases under Single folder, each case has one log file called `timeLog.txt`.
    
    - For cases under Scaled folder, each case has two log files `timeLogM.txt` and `timeLogS.txt`, which are from master server and slave server respectively.
    
    - To run the script, if it's a 'single-instance' case, just replace the path of variable `f` in line 10 to specify the log file. Then, run the java program and the results will be displayed on your terminal window.
    
    - If it's a 'scaled' case, you need to first change the `boolean scale` to `true` in line 8, then replace the path of variable `f` in line 10 to specify one log file, then replace the path of the variable `f2` in line 26 to specify the other log file. Lastly, run the java program and the results will be displayed on your terminal window.
    
    - The `screenshots` of graph results for each case are stored in `img` folder under root folder: https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-29/blob/master/img, the sub-folder format is same as `log` folder.
    
    - The JMeter config file is named `search Request.jmx` under root folder.


- # JMeter TS/TJ Time Measurement Report

| **Single-instance Version Test Plan**         | **Graph Results Screenshot**                  | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis**                                                 |
| --------------------------------------------- | --------------------------------------------- | -------------------------- | ----------------------------------- | ------------------------- | ------------------------------------------------------------ |
| Case 1: HTTP/1 thread                         | ![](cs122b-spring20-team-29/img/Single/1.PNG) | 14                         | 2.567011                            | 2.493622                  | 1 thread, time is relatively short.                          |
| Case 2: HTTP/10 threads                       | ![](cs122b-spring20-team-29/img/Single/2.PNG) | 26                         | 14.330229                           | 14.273250                 | All three time are longer than 1 thread situation. The server has heavier workload. |
| Case 3: HTTPS/10 threads                      | ![](cs122b-spring20-team-29/img/Single/3.PNG) | 34                         | 21.995874                           | 21.915524                 | Highest in TQ, TS, and TJ. Guess: the encryption  process for Https took the extra time. TQ and TJ should be close to Case #2 but the result is higher than which of Case #2, the reason might be the network. |
| Case 4: HTTP/10 threads/No connection pooling | ![](cs122b-spring20-team-29/img/Single/4.PNG) | 27                         | 16.123943                           | 16.054378                 | Without connection pooling, the performance is very close to Case #2, but it still adds a little more time because the connection is not pre-defined. |

| **Scaled Version Test Plan**                  | **Graph Results Screenshot**                  | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis**                                                 |
| --------------------------------------------- | --------------------------------------------- | -------------------------- | ----------------------------------- | ------------------------- | ------------------------------------------------------------ |
| Case 1: HTTP/1 thread                         | ![](cs122b-spring20-team-29/img/Scaled/1.PNG) | 76                         | 3.845515                            | 3.725063                  | Longer time compared to single-instance, because the server need to choose between master&slave first, and then route to it. |
| Case 2: HTTP/10 threads                       | ![](cs122b-spring20-team-29/img/Scaled/2.PNG) | 75                         | 3.504912                            | 3.439293                  | very close to Case #1, but with shorter time, the scaled version works great when the server get heavier workload compared to the single-instance version, because the workload is distributed between to master and slave servers. |
| Case 3: HTTP/10 threads/No connection pooling | ![](cs122b-spring20-team-29/img/Scaled/3.PNG) | 76                         | 3.960356                            | 3.890850                  | Still very close to previous two cases, but TJ and TS is higher because there is no pre-defined connections. |


### Substring matching design
Browse By Title:
- if it's 0-9 and a-z, we use the substring matching by "title like 'x%'".
- if it's *, we use the regex expression m.title regexp '^[a-z0-9]'.

Single search:
- It searchs either title, year, director or stars by user input. We find any values that have search-item in any position
where m.title like %search-item% or m.year= search-item or m.director like %search-item% or s.name like %search-item% 
- Notice that if you search by "Tom" for example. The movie list may display a movie that both title and star name do not include "Tom" sometimes. It's because we only need to display three star names. "Tom" is the actor in that movie but is not displayed in main movie page. If you click the movie name and go to single movie page, you can see "Tom" as the actor.

Advanced search/ Multiple search:
- Title, year, director and stars can be searched together by user input. We allow user to type in these information by typing in text box. We find any values that include user input in any position
- where m.title like '%title%' and m.year like '%year%' and m.director like '%director%' and s.name like '%star-name%'

### Prepared Statement

We have changed all the statements of our servlets to prepared statements.
- AddmovieServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-29/blob/master/src/AddMovieServlet.java
- AddStarServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-29/blob/master/src/AddstarServlet.java
- BrowseGenreServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-29/blob/master/src/BrowseGenreServlet.java
- BrowseTitleServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-29/blob/master/src/BrowseTitleServlet.java
- CartServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-29/blob/master/src/CartServlet.java
- EmployeeLoginServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-29/blob/master/src/EmployeeLoginServlet.java
- LoginServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-29/blob/master/src/LoginServlet.java
- MultisearchServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-29/blob/master/src/MultipleSearchServlet.java
- PaymentAuthServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-29/blob/master/src/PaymentAuthServlet.java
- PaymentServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-29/blob/master/src/PaymentServlet.java
- SingleMovieServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-29/blob/master/src/SingleMovieServlet.java
- SingleSearchServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-29/blob/master/src/SingleSearchServlet.java
- SingleStarServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-29/blob/master/src/SingleStarServlet.java


### XML Parsers

Parsers:

`MovieParser.java` -- `mains243.xml`
`CastParser.java` -- `casts124.xml`, `actors63.xml`

Insertion:

`InsertParsedData.sql`

**The parsing process and insertion process are separated**, the two .java files are for parsing, and they generate .txt files. We write the LOAD DATA query inside another file inside the root folder called **`InsertParsedData.sql`**.

### XML Parsing Performance Tuning

1. We put all existed useful data from Movies table, Genres table, Stars table, Genres_in_movies table, Stars_in_movies table into separated in-memory HashMaps before parsing, in order to filter out the duplicate data entry.
2. We put all the parsed data into separated .txt files inside the self-generated `ParsedFiles` folder, each .txt file contains the data of a correlated table that will be inserted into the database later, each line in the .txt represents the values of an entry. The parsing process is approximately 1~2 seconds.
3. We use the 'LOAD DATA' function of mysql to load the data from the .txt files, time is about 1~2 seconds, it is much faster compare to the naive approach.

### XML Inconsistency Data

​	During the parsing process, if the program find the data is already existed in the database or some value are missing, the program will notify the user by printing a line of report in the terminal window, and the program also writes all the inconsistency data report and duplicate data report into a .txt file inside the `ParsedFiles` folder. After running `MovieParser` and `CastParser`, there will be two .txt files named `moviesInconsistentDataReport.txt` and `castsInconsistentDataReport.txt` shows all the inconsistency and duplicate report. The format is like this:

```
MovieParser at Movie No.1 ID=H1: Genre 'Drama' already exist
MovieParser at Movie No.2 ID=H2: Missing Movie Genre
ActorParser at Actor No.11 Name=Maury Abram: Missing Birth Year
ActorParser at Actor No.11 Name=Victoria Abril: Star 'Victoria Abril' already exist
CastParser at Index No.26649: Star 'Francisco Rabal' already exist
CastParser at Index No.26650: Star 's a' already exist
```

Note: The `CastParser.java` actually consist of both CastParser and ActorParser, and the inconsistency data report of these two parsers are in the same `castsInconsistentDataReport.txt` file.


