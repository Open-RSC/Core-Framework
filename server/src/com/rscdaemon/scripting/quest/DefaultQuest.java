package com.rscdaemon.scripting.quest;

final class DefaultQuest
	extends
		AbstractQuest
{

	private static final long serialVersionUID = -3676407865236486132L;

	private final int id, questPoints;
	private final String name;
	private final QuestReward[] rewards;

	DefaultQuest(Quest quest)
	{
		this.id = quest.getID();
		this.name = quest.getName();
		this.questPoints = quest.getQuestPoints();
		this.rewards = quest.getRewards();
	}

	@Override
	public int getID()
	{
		return id;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public QuestReward[] getRewards()
	{
		return rewards;
	}

	@Override
	public int getQuestPoints()
	{
		return questPoints;
	}
	
	@Override
	public final String toString()
	{
		return name;
	}
	
	DefaultQuest(int id, String name, int questPoints, QuestReward[] rewards)
	{
		this.id = id;
		this.name = name;
		this.questPoints = questPoints;
		this.rewards = rewards;
	}
}