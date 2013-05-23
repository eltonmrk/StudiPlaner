package com.studiplaner.db.model;

public class TaskTags extends CRUD {
	public static final String tableName = "task_tags";
	
	public static String columnTag = "tag";
	public static String columnTaskId = "task_id";

	public TaskTags() {
		super(tableName);
	}
}
