package com.openrsc.server.util;

import com.openrsc.server.Server;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RandomUsername {
	private final static String[] shakespeareNames = {"Aaron", "Abbot", "Abraham", "Achilles", "Adam", "Adrian", "Adriana", "Aedile", "Aegeon", "Aemilia", "Aemilius", "Aeneas", "Agamemnon", "Agrippa", "Aguecheek", "Ajax", "Albany", "Alcibiades", "Alencon", "Alexander", "Alexas", "Alice", "Alltheppl", "Alonso", "Ambassador", "Amiens", "Andromache", "Andronicus", "Angelo", "Angus", "Anne", "Another", "Antigonus", "Antiochus", "Antipholus", "Antonio", "Antony", "Apemantus", "Apothecary", "Apparition", "Archidamus", "Ariel", "Armado", "Arragon", "Arthur", "Arviragus", "Athenian", "Attendant", "Attendants", "Audrey", "Aufidius", "Aumerle", "Autolycus", "Auvergne", "Balthasar", "Balthazar", "Bandit", "Banditti", "Banquo", "Baptista", "Bardolph", "Barnardine", "Bassanio", "Basset", "Bassianus", "Bates", "Bawd", "Beadle", "Beatrice", "Bedford", "Belarius", "Benedick", "Benvolio", "Berkeley", "Bernardo", "Bertram", "Bianca", "Biondello", "Biron", "Blanch", "Blossom", "Blunt", "Boatswain", "Bolingbrok", "Bona", "Borachio", "Boult", "Bourchier", "Brabantio", "Brakenbury", "Brandon", "Brutus", "Buckingham", "Bullcalf", "Bullen", "Burgundy", "Caesar", "Caithness", "Calchas", "Caliban", "Calpurnia", "Cambridge", "Camillo", "Campeius", "Canidius", "Canterbury", "Caphis", "Captain", "Capucius", "Capulet", "Carlisle", "Carrier", "Casca", "Cassandra", "Cassio", "Cassius", "Catesby", "Cato", "Celia", "Ceres", "Cerimon", "Chancellor", "Character", "Charles", "Charmian", "Chatham", "Chatillon", "Chiron", "Chorus", "Cicero", "Cinna", "Citizen", "Citizens", "Claudio", "Claudius", "Cleomenes", "Cleon", "Cleopatra", "Clerk", "Clifford", "Cloten", "Cobweb", "Colville", "Cominius", "Commons", "Conrade", "Constable", "Constance", "Cordelia", "Corin", "Coriolanus", "Cornelius", "Cornwall", "Costard", "Countess", "Court", "Courtezan", "Cranmer", "Cressida", "Cromwell", "Curan", "Curio", "Curtis", "Cymbeline", "Dardanius", "Dauphin", "Davy", "Deiphobus", "Demetrius", "Dennis", "Denny", "Dercetas", "Desdemona", "Diana", "Diomedes", "Dion", "Dionyza", "Doctor", "Dogberry", "Dolabella", "Domitius", "Donadriano", "Donalbain", "Donjohn", "Dorcas", "Dorset", "Douglas", "Drcaius", "Dromio", "Duke", "Dumain", "Duncan", "Earloxford", "Edgar", "Edmond", "Edmund", "Edward", "Egeus", "Eglamour", "Egyptian", "Elbow", "Eleanor", "Elinor", "Elizabeth", "Ely", "Emilia", "Enobarus", "Ephesus", "Erpingham", "Escalus", "Escanes", "Euphronius", "Exeter", "Fabian", "Falstaff", "Fang", "Fastolfe", "Father", "Fenton", "Ferdinand", "Feste", "Fisherman", "Fitzwater", "Flaminius", "Flavius", "Fleance", "Florence", "Florizel", "Fluellen", "Flute", "Ford", "Forester", "Fortinbras", "Francis", "Francisca", "Francisco", "Frederick", "French", "Gadshill", "Gallus", "Gaoler", "Gardener", "Gardiner", "Gargrave", "Garter", "General", "George", "Gertrude", "Ghost", "Glansdale", "Glendower", "Gloucester", "Gobbo", "Goneril", "Gonzalo", "Gower", "Grandpre", "Gratiano", "Green", "Gregory", "Gremio", "Grey", "Griffith", "Groom", "Grumio", "Guard", "Guiderius", "Guildford", "Guildstern", "Haberdash", "Hamlet", "Harcourt", "Harfleur", "Hastings", "Hecate", "Hector", "Helen", "Helena", "Helenus", "Helicanus", "Henry", "Henryiv", "Henryv", "Henryvi", "Henryviii", "Herald", "Hermia", "Hermione", "Hero", "Hippolyta", "Holofernes", "Horatio", "Hortensio", "Hortensius", "Hubert", "Hughevan", "Humphrey", "Iachimo", "Iago", "Imogen", "Iras", "Iris", "Isabel", "Isabella", "Jackcade", "James", "Jamy", "Jaquenetta", "Jaques", "Jessica", "Jeweller", "Joan", "John", "Johnhume", "Joseph", "Jourdain", "Julia", "Juliet", "Junius", "Juno", "Jupiter", "Justice", "Katharine", "Katherina", "Keeper", "Kent", "Kingedward", "Kingjohn", "Kinglewis", "Knight", "Knights", "Laertes", "Lafeu", "Langley", "Lartius", "Launce", "Launcelot", "Laurence", "Lavinia", "Lawyer", "Lear", "Lebeau", "Legate", "Lennox", "Leonardo", "Leonato", "Leonatus", "Leonine", "Leontes", "Lepidus", "Lewis", "Lieutenant", "Ligarius", "Lincoln", "Lodovico", "London", "Longaville", "Lordgrey", "Lordrivers", "Lorenzo", "Lovel", "Lovell", "Luce", "Lucentio", "Lucetta", "Luciana", "Lucilius", "Lucio", "Lucius", "Lucullus", "Lychorida", "Lymoges", "Lysander", "Lysimachus", "Macbeth", "Macduff", "Macmorris", "Malcolm", "Malvolio", "Mamillius", "Marcellus", "Marcus", "Mardian", "Margarelon", "Margaret", "Maria", "Mariana", "Marina", "Mariner", "Mariners", "Marshal", "Martext", "Martius", "Marullus", "Mecaenas", "Melun", "Menas", "Menecrates", "Menelaus", "Menenius", "Menteith", "Mercade", "Merchant", "Mercutio", "Messala", "Messenger", "Metellus", "Michael", "Milan", "Minola", "Miranda", "Montague", "Montano", "Montjoy", "Mopsa", "Morocco", "Mortimer", "Morton", "Moth", "Mowbray", "Musician", "Mustard", "Mutius", "Myrmidons", "Nathaniel", "Neighbour", "Nerissa", "Nestor", "Nicholas", "Norfolk", "Nurse", "Nym", "Oberon", "Octavia", "Octavius", "Officer", "Oliver", "Olivia", "Ophelia", "Orlando", "Orleans", "Orsino", "Osric", "Ostler", "Oswald", "Othello", "Outlaws", "Page", "Painter", "Pandar", "Pandarus", "Pandulph", "Panthino", "Paris", "Parolles", "Patience", "Patrician", "Patroclus", "Paulina", "Pedant", "Pembroke", "Percy", "Perdita", "Pericles", "Peter", "Petruchio", "Phebe", "Philario", "Philemon", "Philip", "Phillip", "Philo", "Philostrat", "Philotus", "Phrynia", "Pinch", "Pindarus", "Pirate", "Pisanio", "Player", "Players", "Poet", "Polixenes", "Polonius", "Pomfret", "Pompey", "Popilius", "Porter", "Portia", "Post", "Posthumus", "Priam", "Priest", "Proculeius", "Prospero", "Proteus", "Provost", "Publius", "Pursuivant", "Quince", "Quintus", "Ralph", "Rambures", "Ratcliff", "Regan", "Reignier", "Reynaldo", "Richard", "Richardii", "Richmond", "Robert", "Robin", "Roderigo", "Roman", "Romeo", "Rosalind", "Rosaline", "Ross", "Rotherham", "Rugby", "Rumour", "Sailor", "Salanio", "Salarino", "Salerio", "Salisbury", "Sampson", "Sands", "Saturninus", "Scales", "Scarus", "Scout", "Scribe", "Scrivener", "Scroop", "Sebastian", "Secretary", "Seleucus", "Sempronius", "Senator", "Sentinel", "Sentinels", "Sergeant", "Servilius", "Seyton", "Shadow", "Shepherd", "Sheriff", "Shrewsbury", "Shylock", "Sicilius", "Sicinius", "Silence", "Silius", "Silvia", "Silvius", "Simon", "Simonides", "Sirtoby", "Sirwalter", "Siward", "Smith", "Snare", "Snout", "Soldier", "Soldiers", "Solinus", "Somerset", "Soothsayer", "Speed", "Spirit", "Stafford", "Stanley", "Stephano", "Stephen", "Steward", "Stranger", "Strato", "Suffolk", "Surrey", "Surveyor", "Syracuse", "Tailor", "Talbot", "Tamora", "Taurus", "Thaisa", "Thaliard", "Thebutcher", "Thepoet", "Thersites", "Theseus", "Thomas", "Thurio", "Thyreus", "Timandra", "Time", "Timon", "Tintinius", "Titania", "Titus", "Touchstone", "Townsman", "Tranio", "Traveller", "Travellers", "Travers", "Trebonius", "Tribune", "Tribunes", "Trinculo", "Troilus", "Tubal", "Tullus", "Tutor", "Tybalt", "Tyrian", "Tyrrel", "Ulysses", "Ursula", "Urswick", "Valentine", "Valeria", "Varrius", "Varro", "Vaughan", "Vaux", "Velutus", "Venice", "Ventidius", "Verges", "Vernon", "Vincentio", "Vintner", "Viola", "Virgilia", "Volsce", "Voltemand", "Volumnia", "Volumnius", "Walter", "Warder", "Warwick", "Watch", "Watchman", "Weaver", "Whitmore", "William", "Williams", "Willoughby", "Wiltshire", "Winchester", "Witch", "Wolsey", "Woodvile", "Worcester", "York"};
	private final static String numbers = "0123456789";

	public static String getRandomUnusedUsername(Server server) {
		for (int i = 0; i < 256; i++) {
			String baseWord = shakespeareNames[(int) (shakespeareNames.length * Math.random())];
			StringBuilder candidateUsername = new StringBuilder(baseWord);
			StringBuilder endingNumbersBuilder = new StringBuilder(4);

			int baseWordLength = baseWord.length();
			for (int j = baseWordLength; j < 12 && j < baseWordLength + 4; j++) {
				int randomNumberCharIndex = (int) (numbers.length() * Math.random());
				endingNumbersBuilder.append(numbers.charAt(randomNumberCharIndex));
			}
			String endingNumbers = endingNumbersBuilder.toString();

			// Adjust inappropriate numbers
			candidateUsername.append(endingNumbers.equals("1488") ? "1289" : endingNumbers);

			if (candidateUsername.length() <= 12 && !server.getDatabase().playerExists(candidateUsername.toString())) {
				return candidateUsername.toString();
			}
		}

		// Very unlikely to reach this point, but if choosing a random shakespeare name fails enough times,
		// fall back to choosing completely random names.
		Logger LOGGER = LogManager.getLogger();
		LOGGER.warn("You have probably run out of Shakespeare character names somehow...!");
		String validUsernameCharacters = "abcdefghijklmnopqrstuvxyz0123456789";
		while (true) {
			StringBuilder candidateUsername = new StringBuilder(12);
			for (int i = 0; i < 12; i++) {
				int index = (int) (validUsernameCharacters.length() * Math.random());
				candidateUsername.append(validUsernameCharacters.charAt(index));
			}
			if (!server.getDatabase().playerExists(candidateUsername.toString())) {
				return candidateUsername.toString();
			}
		}
	}
}
