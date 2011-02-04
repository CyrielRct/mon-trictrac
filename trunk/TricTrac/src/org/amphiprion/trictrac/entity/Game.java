/*
 * @copyright 2010 Gerald Jacobson
 * @license GNU General Public License
 * 
 * This file is part of MyTricTrac.
 *
 * MyTricTrac is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MyTricTrac is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with My Accounts.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.amphiprion.trictrac.entity;

/**
 * This entity represente a Game.
 * 
 * @author amphiprion
 * 
 */
public class Game extends Entity {
	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 1L;
	/** The game name. */
	private String name;
	/** The image name. */
	private String imageName;

	private String type;
	private String families;
	private String mechanisms;
	private String themes;
	private int minPlayer;
	private int maxPlayer;
	private int minAge;
	private int maxAge;
	private int duration;
	private int difficulty = -1;
	private int luck = -1;
	private int strategy = -1;
	private int diplomaty = -1;
	// auto computed
	private int nbParty;
	private double happyness;
	private int numberOfRatings;
	private double adverageRating;

	public enum DbField {
		ID, NAME, IMAGE_NAME, TYPE, FAMILIES, MECHANISMS, THEMES, MIN_PLAYER, MAX_PLAYER, MIN_AGE, MAX_AGE, DURATION, DIFFICULTY, LUCK, STRATEGY, DIPLOMATY, NB_RATING, ADV_RATING
	}

	/**
	 * Default constructor.
	 * 
	 * @param id
	 *            the identifier
	 */
	public Game(String id) {
		super(id);
	}

	/**
	 * @return the game name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the new game name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the image name
	 */
	public String getImageName() {
		return imageName;
	}

	/**
	 * @param imageName
	 *            the new image name
	 */
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public int getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}

	public int getDiplomaty() {
		return diplomaty;
	}

	public void setDiplomaty(int diplomaty) {
		this.diplomaty = diplomaty;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getFamilies() {
		return families;
	}

	public void setFamilies(String families) {
		this.families = families;
	}

	public int getLuck() {
		return luck;
	}

	public void setLuck(int luck) {
		this.luck = luck;
	}

	public int getMaxAge() {
		return maxAge;
	}

	public void setMaxAge(int maxAge) {
		this.maxAge = maxAge;
	}

	public int getMaxPlayer() {
		return maxPlayer;
	}

	public void setMaxPlayer(int maxPlayer) {
		this.maxPlayer = maxPlayer;
	}

	public String getMechanisms() {
		return mechanisms;
	}

	public void setMechanisms(String mechanisms) {
		this.mechanisms = mechanisms;
	}

	public int getMinAge() {
		return minAge;
	}

	public void setMinAge(int minAge) {
		this.minAge = minAge;
	}

	public int getMinPlayer() {
		return minPlayer;
	}

	public void setMinPlayer(int minPlayer) {
		this.minPlayer = minPlayer;
	}

	public int getStrategy() {
		return strategy;
	}

	public void setStrategy(int strategy) {
		this.strategy = strategy;
	}

	public String getThemes() {
		return themes;
	}

	public void setThemes(String themes) {
		this.themes = themes;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getNbParty() {
		return nbParty;
	}

	public void setNbParty(int nbParty) {
		this.nbParty = nbParty;
	}

	public double getHappyness() {
		return happyness;
	}

	public void setHappyness(double happyness) {
		this.happyness = happyness;
	}

	/**
	 * @return the numberOfRatings
	 */
	public int getNumberOfRatings() {
		return numberOfRatings;
	}

	/**
	 * @param numberOfRatings
	 *            the numberOfRatings to set
	 */
	public void setNumberOfRatings(int numberOfRatings) {
		this.numberOfRatings = numberOfRatings;
	}

	/**
	 * @return the adverageRating
	 */
	public double getAdverageRating() {
		return adverageRating;
	}

	/**
	 * @param adverageRating
	 *            the adverageRating to set
	 */
	public void setAdverageRating(double adverageRating) {
		this.adverageRating = adverageRating;
	}

}
