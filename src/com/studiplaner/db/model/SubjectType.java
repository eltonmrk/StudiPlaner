package com.studiplaner.db.model;

public class SubjectType extends CRUD {
	public static final String tableName = "subject_type";
	
	public static String columnTitle = "title";
	public static String columnUserId = "user_id";

	public SubjectType() {
		super(tableName);
	}
}
