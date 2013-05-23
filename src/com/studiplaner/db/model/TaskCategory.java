package com.studiplaner.db.model;

public class TaskCategory extends CRUD {
	public static final String tableName = "task_category";
	
	public static String columnTitle = "title";
	public static String columnUserId = "user_id";
	public static String columnPosition = "position";

	public TaskCategory() {
		super(tableName);
	}
}
