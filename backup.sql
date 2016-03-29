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
CREATE TABLE approved_users(
approved_org_id INTEGER,
approved_user_id INTEGER,
FOREIGN KEY(approved_org_id) references organizations(org_id),
FOREIGN KEY(approved_user_id) references users(id));
INSERT INTO "approved_users" VALUES(1,1);
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
COMMIT;
