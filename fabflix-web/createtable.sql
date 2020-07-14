use moviedb;
CREATE TABLE IF NOT EXISTS stars(
               id varchar(10) DEFAULT '',
               `name` varchar(100) DEFAULT '',
               birthYear integer,
               primary key (id)
		);
           
CREATE TABLE if not exists movies(
       	    id VARCHAR(10) DEFAULT '',
       	    title VARCHAR(100) DEFAULT '',
       	    `year` INTEGER NOT NULL,
       	    director VARCHAR(100) DEFAULT '',
       	    PRIMARY KEY (id)
       );

CREATE TABLE IF NOT EXISTS stars_in_movies(
       	    starId VARCHAR(10) DEFAULT '',
       	    movieId VARCHAR(10) DEFAULT '',
       	    FOREIGN KEY (starId) REFERENCES stars(id),
       	    FOREIGN KEY (movieId) REFERENCES movies(id)
       );
       
CREATE TABLE IF NOT EXISTS genres(
			id INTEGER NOT NULL auto_increment,
            `name` VARCHAR(32) DEFAULT '',
            PRIMARY KEY (id)
		);

CREATE TABLE IF NOT EXISTS genres_in_movies(
			genreId INTEGER NOT NULL,
            movieId varchar(10) DEFAULT '',
            FOREIGN KEY (genreId) REFERENCES genres(id),
            FOREIGN KEY (movieId) REFERENCES movies(id)
		);

CREATE TABLE IF NOT EXISTS creditcards(
		id VARCHAR(20) DEFAULT '',
        firstName varchar(50) default '',
        lastName varchar(50) default '',
        expiration date not null,
        primary key(id)
        );

CREATE TABLE IF NOT EXISTS customers(
		id INTEGER NOT NULL auto_increment,
        firstName VARCHAR(50) default '',
        lastName VARCHAR(50) default '',
        ccId VARCHAR(20) default '',
        address VARCHAR(200) default '',
        email VARCHAR(50) default '',
        `password` VARCHAR(20) default '',
        PRIMARY KEY (id),
        foreign key (ccID) REFERENCES creditcards(id)
        );
        
create table if not exists sales(
		id integer not null auto_increment,
        customerId integer not null,
        movieId varchar(10) default '',
        saleDate date not null,
        primary key(id),
        foreign key (customerId) references customers(id),
        foreign key (movieId) references movies(id)
        );

create table if not exists ratings(
		movieId varchar(10) default '',
        rating float not null,
        numVotes integer not null,
        foreign key (movieId) references movies(id)
        );

create table if not exists employees(
email varchar(50) primary key,
password varchar(20) not null,
fullname varchar(100));

insert into employees values("classta@email.com", "classta", "TA CS122B");
        
