CREATE TABLE IF NOT EXISTS people (
    name VARCHAR(50),
    width INT,
    height INT,
    birthday DATE
);

INSERT INTO people (name, width, height, birthday)
VALUES
    ('Gal', 65, 178, '1991-06-17'),
    ('Shlomo', 75, 195, '1992-09-17'),
    ('Dan', 65, 185, '1990-10-17'),
    ('Tom', 90, 191, '1994-11-17');
    
SELECT * FROM people;

SELECT AVG(height) AS average_height
FROM people
WHERE birthday BETWEEN '1990-01-01' AND '1993-01-01';

CREATE VIEW IF NOT EXISTS proportion AS
SELECT name, width, height, birthday, height / AVG(height) OVER () AS proportion_height
FROM people
WHERE birthday BETWEEN '1990-01-01' AND '1993-01-01';

SELECT p.name, p.width, p.height, p.birthday, pr.proportion_height
FROM people p
JOIN proportion pr ON p.name = pr.name
WHERE p.width > 75 AND p.height > 185;