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

CREATE TABLE games (
	game_id INTEGER PRIMARY KEY AUTOINCREMENT;
	user0_id INTEGER,
	user1_id INTEGER,
	score0 INTEGER,
	score1 INTEGER,
	winner INTEGER,
	 FOREIGN KEY(user0_id) REFERENCES users(user_id),
	 FOREIGN KEY(user1_id) REFERENCES users(user_id)
);

INSERT INTO games VALUES (1,1,2,0,15,1);
INSERT INTO games VALUES (2,1,2,17,15,0);
INSERT INTO games VALUES (3,1,3,5,17,1);
INSERT INTO games VALUES (4,3,4,17,13,0);


