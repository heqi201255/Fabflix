use moviedb;

delimiter $$
drop function if exists get_sid;
create function get_sid()
returns char(10)
deterministic
begin
declare sid int;
SELECT cast(substring(max(id),3, length(max(id))-2) as unsigned) into sid from stars;
set sid = sid + 1;
return concat("nm", sid);
end
$$



drop procedure if exists add_star;
create procedure add_star(sname varchar(100), syear int)
begin
  declare id varchar(10);
  select get_sid() into id;
  insert into stars value(id, sname, syear);
  select id as sid, sname as name, syear as year;
end
$$



drop function if exists same_movie;
create function same_movie(mtitle varchar(100), myear int, mdirector varchar(100))
returns boolean
deterministic
begin
  declare movie char(100);
  select title into movie from movies where title = mtitle and year = myear and director = mdirector;
  if(movie is null) then
    return 0;
  else
    return 1;
  end if;
end
$$



drop function if exists get_mid;
create function get_mid()
returns char(10)
deterministic
begin
declare mid int;
SELECT count(*) into mid from movies;
set mid = mid + 1;
return mid;
end
$$



drop function if exists get_gid;
create function get_gid(mgenre varchar(32))
returns int
deterministic
begin
  declare gid int;
  declare new_gid int;
  select id into gid from genres where name = mgenre;
  if(gid is null) then
    select max(id) into new_gid from genres;
    set new_gid = new_gid + 1;
    insert into genres value(new_gid, mgenre);
	return new_gid;
  else
    return gid;
  end if;
end
$$


drop function if exists star_exist;
create function star_exist(mstar varchar(100))
returns char(10)
deterministic
begin
  declare sid varchar(10);
  declare new_sid varchar(10);
  select id into sid from stars where name = mstar limit 1;
  if (sid is null) then
    select get_sid() into new_sid;
    insert into stars value(new_sid, mstar, null);
    return new_sid;
  else
    return sid;
  end if;
end
$$


drop procedure if exists add_movie;
create procedure add_movie(mtitle varchar(100), myear int, mdirector varchar(100), mgenre varchar(32), mstar varchar(100))
begin
  declare new_mid char(10);
  declare new_gid int;
  declare new_sid char(10);
  if(same_movie(mtitle, myear, mdirector)) then
    select "same" as mid, null as title, null as year, null as director, null as genre, null as star;
  else
    select get_mid() into new_mid;
    select get_gid(mgenre) into new_gid;
    select star_exist(mstar) into new_sid;
    insert into movies value(new_mid, mtitle, myear, mdirector);
    insert into genres_in_movies value(new_gid,new_mid);
    insert into stars_in_movies value(new_sid, new_mid);
    select new_mid as mid, mtitle as title, myear as year, mdirector as director, new_gid as genre, new_sid as star;
  end if;
end
$$

delimiter ;
  