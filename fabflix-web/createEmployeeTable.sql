create table if not exists employees(
    email varchar(50) primary key,
    password varchar(20) not null,
    fullname varchar(100)
);

insert into employees values('classta@email.edu', 'classta', 'TA CS122B');
