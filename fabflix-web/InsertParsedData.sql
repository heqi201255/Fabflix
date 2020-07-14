load data local infile '/home/ubuntu/cs122b-spring20-team-29/ParsedFiles/genres.txt'
ignore into table genres
fields terminated by ','
enclosed by "'"
lines terminated by '\n'
(id, name);

load data local infile '/home/ubuntu/cs122b-spring20-team-29/ParsedFiles/movies.txt'
ignore into table movies
fields terminated by ','
enclosed by "'"
lines terminated by '\n'
(id, title, year, director);

load data local infile '/home/ubuntu/cs122b-spring20-team-29/ParsedFiles/gim.txt'
ignore into table genres_in_movies
fields terminated by ','
enclosed by "'"
lines terminated by '\n'
(genreId, movieId);

load data local infile '/home/ubuntu/cs122b-spring20-team-29/ParsedFiles/stars.txt'
ignore into table stars
fields terminated by ','
enclosed by "'"
lines terminated by '\n'
(id, name, birthYear);

load data local infile '/home/ubuntu/cs122b-spring20-team-29/ParsedFiles/sim.txt'
ignore into table stars_in_movies
fields terminated by ','
enclosed by "'"
lines terminated by '\n'
(starId, movieId);

