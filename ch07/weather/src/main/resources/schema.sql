CREATE TABLE IF NOT EXISTS city
(
    id        int PRIMARY KEY AUTO_INCREMENT,
    name      text   NOT NULL,
    country   text   NOT NULL,
    latitude  double NOT NULL,
    longitude double NOT NULL
);

INSERT INTO city(name, country, latitude, longitude)
    DIRECT (SELECT "city",
                   "country",
                   CAST("lat" as DOUBLE),
                   CAST("lng" as DOUBLE)
            FROM CSVREAD(
                    'classpath:/cities.csv',
                    null, 'charset=UTF-8 lineComment=# caseSensitiveColumnNames=true'));

-- Note: user_id is a plain column, not a foreign key. Users are owned by the security
-- module, which keeps its own store, so the database cannot enforce the reference (nor
-- cascade deletes: the city module listens for UserDeleted instead).
CREATE TABLE IF NOT EXISTS preferred_city
(
    id      int PRIMARY KEY AUTO_INCREMENT,
    user_id varchar(255) NOT NULL,
    city_id int NOT NULL,
    date_added timestamp,
    UNIQUE (user_id, city_id),
    FOREIGN KEY (city_id) REFERENCES city(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS preferences
(
    id        int PRIMARY KEY AUTO_INCREMENT,
    user_id   varchar(255) UNIQUE NOT NULL,
    dark_mode boolean NOT NULL,
    units     varchar(255),
    sort_by   varchar(255)
);
