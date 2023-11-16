CREATE TABLE users
(
    username varchar(50) PRIMARY KEY,
    password varchar(100) NOT NULL,
    enabled  boolean      NOT NULL DEFAULT TRUE
);

CREATE TABLE authorities
(
    username  varchar(50) NOT NULL REFERENCES users (username),
    authority varchar(50) NOT NULL
);

INSERT INTO users(username, password)
VALUES ('root', '$2a$04$h2h/1yUrMk/1s1z8qVLTouoW./Mgd10Yr2.XBGhNDuVRsDCh756km');
INSERT INTO users(username, password)
VALUES ('wakeup', '$2a$04$h2h/1yUrMk/1s1z8qVLTouoW./Mgd10Yr2.XBGhNDuVRsDCh756km');
INSERT INTO users(username, password)
VALUES ('beza', '$2a$04$h2h/1yUrMk/1s1z8qVLTouoW./Mgd10Yr2.XBGhNDuVRsDCh756km');

INSERT INTO authorities(username, authority)
VALUES ('root', 'ROLE_ADMIN');
INSERT INTO authorities(username, authority)
VALUES ('root', 'ROLE_TIME');
INSERT INTO authorities(username, authority)
VALUES ('root', 'ROLE_SECURITY');
INSERT INTO authorities(username, authority)
VALUES ('wakeup', 'ROLE_TIME');
INSERT INTO authorities(username, authority)
VALUES ('beza', 'ROLE_SECURITY');