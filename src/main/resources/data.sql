insert into rating_MPA (mpa_name)
select 'G'
where not exists (
    select 1 from rating_MPA where mpa_name = 'G'
);
insert into rating_MPA (mpa_name)
select 'PG'
where not exists (
    select 1 from rating_MPA where mpa_name = 'PG'
);
insert into rating_MPA (mpa_name)
select 'PG-13'
where not exists (
    select 1 from rating_MPA where mpa_name = 'PG-13'
);
insert into rating_MPA (mpa_name)
select 'R'
where not exists (
    select 1 from rating_MPA where mpa_name = 'R'
);
insert into rating_MPA (mpa_name)
select 'NC-17'
where not exists (
    select 1 from rating_MPA where mpa_name = 'NC-17'
);

insert into GENRES (name)
select 'Комедия'
where not exists (
    select 1 from GENRES where NAME = 'Комедия'
);
insert into GENRES (name)
select 'Драма'
where not exists (
    select 1 from GENRES where NAME = 'Драма'
);
insert into GENRES (name)
select 'Мультфильм'
where not exists (
    select 1 from GENRES where NAME = 'Мультфильм'
);
insert into GENRES (name)
select 'Триллер'
where not exists (
    select 1 from GENRES where NAME = 'Триллер'
);
insert into GENRES (name)
select 'Документальный'
where not exists (
    select 1 from GENRES where NAME = 'Документальный'
);
insert into GENRES (name)
select 'Боевик'
where not exists (
    select 1 from GENRES where NAME = 'Боевик'
);