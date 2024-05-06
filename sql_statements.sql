CREATE TABLE users (
	user_id INTEGER PRIMARY KEY AUTOINCREMENT,
	username VARCHAR(50) UNIQUE NOT NULL ,
	password VARCHAR(50) NOT NULL
	totalPoints INT 
);

INSERT INTO users (username, password) VALUES ('elizabeth','password');
INSERT INTO users (username, password) VALUES ('odette','test123');
INSERT INTO users (username, password) VALUES ('joe','pass');
INSERT INTO users (username, password) VALUES ('john','mypass');
INSERT INTO users (username, password) VALUES ("anne","rabbit");

CREATE TABLE games (
	game_id INTEGER PRIMARY KEY AUTOINCREMENT,
	user0_id INTEGER,
	user1_id INTEGER,
	winner INTEGER,
	 FOREIGN KEY(user0_id) REFERENCES users(user_id),
	 FOREIGN KEY(user1_id) REFERENCES users(user_id),
	 FOREIGN KEY(winner) REFERENCES users(user_id)
);

INSERT INTO games (user0_id, user1_id, winner) VALUES (1,2,2);
INSERT INTO games (user0_id, user1_id, winner) VALUES (2,3,3);
INSERT INTO games (user0_id, user1_id, winner) VALUES (3,4,3);
INSERT INTO games (user0_id, user1_id, winner) VALUES (5,1,5);
INSERT INTO games (user0_id, user1_id, winner) VALUES (3,5,3);
INSERT INTO games (user0_id, user1_id, winner) VALUES (1,4,1);


