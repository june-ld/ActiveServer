PRAGMA foreign_keys=OFF;
BEGIN TRANSACTION;
CREATE TABLE users(
id INTEGER PRIMARY KEY,
username TEXT,
password TEXT,
email_addr TEXT,
security_ans TEXT,
currentEXP INTEGER NOT NULL DEFAULT 0);
INSERT INTO "users" VALUES(0,'NULL','NULL','NULL','NULL',0);
INSERT INTO "users" VALUES(1,'June','pass','june@live.com','yes',1350);
INSERT INTO "users" VALUES(2,'Admin','pass','admin@live.com','yes',1350);

CREATE TABLE comments(
comment_id INTEGER PRIMARY KEY,
comment_challenge_id INTEGER,
comment_host_id INTEGER,
comment_text TEXT,
FOREIGN KEY(comment_challenge_id) references challenge(challenge_id),
FOREIGN KEY(comment_host_id) references users(id));

CREATE TABLE checkedIn(
checkin_id INTEGER PRIMARY KEY,
checkin_user_id INTEGER,
checkin_ch_id INTEGER,
FOREIGN KEY(checkin_user_id) references users(id),
FOREIGN KEY(checkin_ch_id) references challenge(challenge_id));

CREATE TABLE organizations(
org_id INTEGER PRIMARY KEY,
org_name TEXT,
org_passcode INTEGER);
INSERT INTO "organizations" VALUES(0,'NULL',0);
INSERT INTO "organizations" VALUES(1,'UTA Rotaract',686164);
INSERT INTO "organizations" VALUES(2,'International Student Org',612846);
INSERT INTO "organizations" VALUES(3,'Japanese Culture Society',612846);

CREATE TABLE approved_users(
approved_org_id INTEGER,
approved_user_id INTEGER,
FOREIGN KEY(approved_org_id) references organizations(org_id),
FOREIGN KEY(approved_user_id) references users(id));
INSERT INTO "approved_users" VALUES(1,1);
INSERT INTO "approved_users" VALUES(2,1);
INSERT INTO "approved_users" VALUES(3,1);

CREATE TABLE challenges(
challenge_id INTEGER PRIMARY KEY,
challenge_name TEXT,
challenge_date_event TEXT,
challenge_date_created TEXT,
challenge_location TEXT,
challenge_host_id INTEGER,
challenge_host_isorg INTEGER NOT NULL DEFAULT 0,
challenge_details TEXT,
challenge_exp_value INTEGER NOT NULL DEFAULT 25,
challenge_upvotes INTEGER NOT NULL DEFAULT 0,
challenge_checkin_code INTEGER,
challenge_org_id INTEGER,
challenge_end_time TEXT,
active INTEGER DEFAULT 1,
FOREIGN KEY(challenge_host_id) references users(id),
FOREIGN KEY(challenge_org_id) references organizations(org_id));
INSERT INTO "challenges" VALUES(1,'Soccer Tournaments','04-02-2016 03:30','03-20-2016 17:09','Test location',0,1,'No details given',50,0,942137,2,'04-02-2016 04:30',1);
INSERT INTO "challenges" VALUES(2,'Grand Opening Ceremony & Parade of Banners','04-04-2016 17:30','03-20-2016 17:11','Test location',0,1,'No details given',50,0,622074,2,'04-04-2016 18:30',1);
INSERT INTO "challenges" VALUES(3,'International Food Fair','04-05-2016 17:30','03-20-2016 17:11','Test location',0,1,'Delicious food from all over the world prepared by our students national organizations. ',50,0,622074,2,'04-05-2016 18:30',1);
INSERT INTO "challenges" VALUES(4,'Fashion Show','04-06-2016 17:30','03-20-2016 17:11','Test location',0,1,'No details given',50,0,622074,2,'04-06-2016 18:30',1);
INSERT INTO "challenges" VALUES(5,'School Visits','04-07-2016 17:30','03-20-2016 17:11','Test location',0,1,'No details given',50,0,622074,2,'04-07-2016 18:30',1);
INSERT INTO "challenges" VALUES(6,'Global Extravaganza','04-08-2016 17:30','03-20-2016 17:11','Test location',0,1,'No details given',50,0,622074,2,'04-08-2016 18:30',1);

INSERT INTO "challenges" VALUES(7,'4th General Body Meeting','03-23-2016 17:30','03-20-2016 17:11','Test location',0,1,'No details given',50,0,622074,1,'03-23-2016 18:30',1);
INSERT INTO "challenges" VALUES(8,'JCS Meeting S2 Episode 5','03-25-2016 17:30','03-20-2016 17:11','Test location',0,1,'Welcome back everyone! Were starting stuff up again for this meeting and well let you know what were gonna do for this month! :D',50,0,622074,3,'03-25-2016 18:30',1);
INSERT INTO "challenges" VALUES(9,'H-Mart/Carrollton Adventure!','03-26-2016 17:30','03-20-2016 17:11','Test location',0,1,'Because we havent seen anyone in forever we want to hang out with you! Join us as we eat and walk around the H-Mart area. Daisos also an option~! :D',50,0,622074,3,'03-26-2016 18:30',1);



COMMIT;
