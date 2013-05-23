-- Creator:       MySQL Workbench 5.2.31/ExportSQLite plugin 2009.12.02
-- Author:        Elton Marku
-- Caption:       New Model
-- Project:       Name of the project
-- Changed:       2011-07-24 18:54
-- Created:       2011-06-11 17:28
PRAGMA foreign_keys = OFF;

-- Schema: entwicklerherz_
BEGIN;
CREATE TABLE "user"(
  "id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  "login" VARCHAR(255) NOT NULL,
  "pass" VARCHAR(255) NOT NULL
);
CREATE TABLE "semester"(
  "id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  "begin" TIMESTAMP NOT NULL,
  "end" TIMESTAMP NOT NULL,
  "title" VARCHAR(255) NOT NULL,
  "user_id" INTEGER NOT NULL,
  "deleted" INTEGER,
  "updated" TIMESTAMP,
  "synchronized" TIMESTAMP,
  "created" TIMESTAMP,
  "cs_reference" INTEGER,
  CONSTRAINT "fk_semester_user1"
    FOREIGN KEY("user_id")
    REFERENCES "user"("id")
);
CREATE INDEX "semester.fk_semester_user1" ON "semester"("user_id");
CREATE TABLE "lecturer"(
  "id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  "firstname" VARCHAR(255) NOT NULL,
  "lastname" VARCHAR(255),
  "place" VARCHAR(255),
  "email" VARCHAR(255),
  "deleted" INTEGER,
  "updated" TIMESTAMP,
  "synchronized" TIMESTAMP,
  "created" TIMESTAMP,
  "cs_reference" INTEGER
);
CREATE TABLE "subject_type"(
  "id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  "title" VARCHAR(45) NOT NULL,
  "user_id" INTEGER NOT NULL,
  "deleted" INTEGER,
  "updated" TIMESTAMP,
  "synchronized" TIMESTAMP,
  "created" TIMESTAMP DEFAULT NOW(),
  "cs_reference" INTEGER,
  CONSTRAINT "fk_subject_type_user1"
    FOREIGN KEY("user_id")
    REFERENCES "user"("id")
);
CREATE INDEX "subject_type.fk_subject_type_user1" ON "subject_type"("user_id");
CREATE TABLE "task_category"(
  "id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  "title" VARCHAR(255) NOT NULL,
  "created" TIMESTAMP,
  "user_id" INTEGER NOT NULL,
  "position" INTEGER NOT NULL,
  "deleted" INTEGER,
  "updated" TIMESTAMP,
  "synchronized" TIMESTAMP,
  "cs_reference" INTEGER,
  CONSTRAINT "fk_task_category_user1"
    FOREIGN KEY("user_id")
    REFERENCES "user"("id")
);
CREATE INDEX "task_category.fk_task_category_user1" ON "task_category"("user_id");
CREATE TABLE "subject"(
  "id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  "title" VARCHAR(255),
  "begin" TIME NOT NULL,
  "end" TIME NOT NULL,
  "day" INTEGER NOT NULL,
  "place" VARCHAR(255),
  "subject_type_id" INTEGER,
  "lecturer_id" INTEGER,
  "semester_id" INTEGER NOT NULL,
  "deleted" INTEGER,
  "updated" TIMESTAMP,
  "synchronized" TIMESTAMP,
  "created" TIMESTAMP,
  "cs_reference" INTEGER,
  CONSTRAINT "fk_subject_subject_type"
    FOREIGN KEY("subject_type_id")
    REFERENCES "subject_type"("id"),
  CONSTRAINT "fk_subject_lecturer1"
    FOREIGN KEY("lecturer_id")
    REFERENCES "lecturer"("id"),
  CONSTRAINT "fk_subject_semester1"
    FOREIGN KEY("semester_id")
    REFERENCES "semester"("id")
);
CREATE INDEX "subject.fk_subject_subject_type" ON "subject"("subject_type_id");
CREATE INDEX "subject.fk_subject_lecturer1" ON "subject"("lecturer_id");
CREATE INDEX "subject.fk_subject_semester1" ON "subject"("semester_id");
CREATE TABLE "task"(
  "id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  "title" VARCHAR(45) NOT NULL,
  "note" TEXT,
  "archived" VARCHAR(45),
  "due" TIMESTAMP,
  "task_category_id" INTEGER NOT NULL,
  "position" INTEGER NOT NULL,
  "deleted" INTEGER,
  "updated" TIMESTAMP,
  "synchronized" TIMESTAMP,
  "created" TIMESTAMP,
  "cs_reference" INTEGER,
  CONSTRAINT "fk_task_task_category1"
    FOREIGN KEY("task_category_id")
    REFERENCES "task_category"("id")
);
CREATE INDEX "task.fk_task_task_category1" ON "task"("task_category_id");
CREATE TABLE "task_tags"(
  "id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  "tag" VARCHAR(255),
  "task_id" INTEGER NOT NULL,
  "deleted" INTEGER,
  "updated" TIMESTAMP,
  "synchronized" TIMESTAMP,
  "created" TIMESTAMP,
  "cs_reference" INTEGER,
  CONSTRAINT "fk_task_tags_task1"
    FOREIGN KEY("task_id")
    REFERENCES "task"("id")
);
CREATE INDEX "task_tags.fk_task_tags_task1" ON "task_tags"("task_id");
COMMIT;
