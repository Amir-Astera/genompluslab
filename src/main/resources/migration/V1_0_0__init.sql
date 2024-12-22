-- noinspection SqlNoDataSourceInspectionForFile
CREATE ROLE read_only;
CREATE USER notry WITH PASSWORD 'BolshoiRozhok24123!&_' IN ROLE read_only;
CREATE DATABASE notrybro OWNER notry;

GRANT SELECT, INSERT, UPDATE, DELETE ON
    users, user_authority, modules, module_lessons, lessons, files, authority TO read_only;


CREATE TABLE IF NOT EXISTS USERS (
    ID VARCHAR (50) NOT NULL PRIMARY KEY,
    NAME VARCHAR (50) NOT NULL,
    SURNAME VARCHAR (50),
    PHONE VARCHAR (50),
    iin VARCHAR (50),
    EMAIL VARCHAR (50),
    LOGIN VARCHAR (50) NOT NULL,
    LOGO VARCHAR (50),
    VERSION BIGINT DEFAULT 1,
    CREATED_AT TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UPDATED_AT TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT UC_USER_LOGIN UNIQUE (LOGIN)
);

CREATE TABLE IF NOT EXISTS ANALYSIS (
        ID VARCHAR (50) NOT NULL PRIMARY KEY,
        CODE VARCHAR (255) NOT NULL,
        NAME VARCHAR (255) NOT NULL,
        MATERIAL VARCHAR (255) NOT NULL,
        DEADLINE VARCHAR (255) NOT NULL,
        PRICE FLOAT NOT NULL DEFAULT 0.0,
        DESCRIPTION VARCHAR (255) NOT NULL,
        VERSION BIGINT DEFAULT 1,
        CREATED_AT TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        UPDATED_AT TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        CONSTRAINT UC_ANALYSIS_NAME UNIQUE (NAME)
);

CREATE TABLE IF NOT EXISTS FILES
(
    ID        VARCHAR(255) NOT NULL PRIMARY KEY,
    DIRECTORY  VARCHAR(255) NOT NULL,
    FORMAT     VARCHAR(255) NOT NULL,
    URL        VARCHAR(255) NOT NULL,
    VERSION BIGINT DEFAULT 1,
    CREATED_AT TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

CREATE TABLE IF NOT EXISTS TOPIC (
     ID VARCHAR (50) NOT NULL PRIMARY KEY,
     NAME VARCHAR (50) NOT NULL,
     VERSION BIGINT DEFAULT 1,
     CREATED_AT TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
     UPDATED_AT TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
     CONSTRAINT UC_TOPIC_NAME UNIQUE (NAME)
);

CREATE TABLE IF NOT EXISTS TOPIC_ANALYSIS (
    ID VARCHAR (100) NOT NULL PRIMARY KEY,
    TOPIC_ID VARCHAR (100) NOT NULL REFERENCES TOPIC (ID) ON DELETE CASCADE,
    ANALYSIS_ID VARCHAR (100) NOT NULL REFERENCES ANALYSIS (ID) ON DELETE CASCADE,
    VERSION BIGINT DEFAULT 1,
    CREATED_AT TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UPDATED_AT TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS CITY (
     ID VARCHAR (50) NOT NULL PRIMARY KEY,
     NAME VARCHAR (50) NOT NULL,
     VERSION BIGINT DEFAULT 1,
     CREATED_AT TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
     UPDATED_AT TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
     CONSTRAINT UC_CITY_NAME UNIQUE (NAME)
);

CREATE TABLE IF NOT EXISTS CITY_ANALYSIS (
      ID VARCHAR (100) NOT NULL PRIMARY KEY,
      CITY_ID VARCHAR (100) NOT NULL REFERENCES CITY (ID) ON DELETE CASCADE,
      ANALYSIS_ID VARCHAR (100) NOT NULL REFERENCES ANALYSIS (ID) ON DELETE CASCADE,
      VERSION BIGINT DEFAULT 1,
      CREATED_AT TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
      UPDATED_AT TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS CART (
     ID VARCHAR (100) NOT NULL PRIMARY KEY,
     USER_ID VARCHAR (100) NOT NULL REFERENCES USERS (ID) ON DELETE CASCADE,
     ANALYSIS_ID VARCHAR (100) NOT NULL REFERENCES ANALYSIS (ID) ON DELETE CASCADE,
     VERSION BIGINT DEFAULT 1,
     CREATED_AT TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
     UPDATED_AT TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS ANALYTICS_POPULAR_ANALYSIS (
    ID VARCHAR (100) NOT NULL PRIMARY KEY,
    ANALYSIS_ID VARCHAR (100) NOT NULL REFERENCES ANALYSIS (ID) ON DELETE CASCADE,
    CITY_ID VARCHAR (100) NOT NULL REFERENCES CITY (ID) ON DELETE CASCADE,
    SALES_COUNT FLOAT NOT NULL DEFAULT 0.0,
    VIEWS_COUNT FLOAT NOT NULL DEFAULT 0.0,
    CART_COUNT FLOAT NOT NULL DEFAULT 0.0,
    LAST_SALE_DATE TIMESTAMP,
    VERSION BIGINT DEFAULT 1,
    CREATED_AT TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UPDATED_AT TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS ANALYTICS_POPULAR_TOPICS (
    ID VARCHAR (100) NOT NULL PRIMARY KEY,
    TOPIC_ID VARCHAR (100) NOT NULL REFERENCES TOPIC (ID) ON DELETE CASCADE,
    CITY_ID VARCHAR (100) NOT NULL REFERENCES CITY (ID) ON DELETE CASCADE,
    POPULAR_SCORE FLOAT NOT NULL DEFAULT 0.0,
    VERSION BIGINT DEFAULT 1,
    CREATED_AT TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UPDATED_AT TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS AUTHORITY (
    ID VARCHAR (50) NOT NULL PRIMARY KEY,
    NAME VARCHAR (50) NOT NULL,
    DESCRIPTION VARCHAR (255) NOT NULL,
    VERSION BIGINT DEFAULT 1,
    CREATED_AT TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UPDATED_AT TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT UC_AUTHORITY_NAME UNIQUE (NAME)
);

CREATE TABLE IF NOT EXISTS USER_AUTHORITY (
  ID VARCHAR (100) NOT NULL PRIMARY KEY,
  USER_ID VARCHAR (100) NOT NULL REFERENCES USERS (ID) ON DELETE CASCADE,
  AUTHORITY_ID VARCHAR (100) NOT NULL REFERENCES AUTHORITY (ID) ON DELETE CASCADE,
  VERSION BIGINT DEFAULT 1,
  CREATED_AT TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UPDATED_AT TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
