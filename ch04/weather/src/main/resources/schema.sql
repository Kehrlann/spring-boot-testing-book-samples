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

CREATE TABLE IF NOT EXISTS preferred_city
(
    id      int PRIMARY KEY AUTO_INCREMENT,
    city_id int UNIQUE NOT NULL,
    FOREIGN KEY (city_id) REFERENCES city(id) ON DELETE CASCADE
);
