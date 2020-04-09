package com.openrsc.server.database.struct;

public class PlayerRecoveryQuestions {
	public String username;
	public String question1;
	public String question2;
	public String question3;
	public String question4;
	public String question5;
	public String[] answers = new String[5];
	public long dateSet;
	public String ipSet;
	public String previousPass;
	public String earlierPass;
}
